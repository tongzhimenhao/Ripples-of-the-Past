package com.github.standobyte.jojo.power.nonstand.type;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.capability.entity.ClientPlayerUtilCapProvider;
import com.github.standobyte.jojo.capability.entity.PlayerUtilCap;
import com.github.standobyte.jojo.capability.entity.PlayerUtilCapProvider;
import com.github.standobyte.jojo.capability.entity.PlayerUtilCap.OneTimeNotification;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.client.sound.ClientTickingSoundsHelper;
import com.github.standobyte.jojo.entity.CrimsonBubbleEntity;
import com.github.standobyte.jojo.entity.HamonBlockChargeEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.SnakeMufflerEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.ModEffects;
import com.github.standobyte.jojo.init.ModItems;
import com.github.standobyte.jojo.init.ModNonStandPowers;
import com.github.standobyte.jojo.init.ModParticles;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.network.PacketManager;
import com.github.standobyte.jojo.network.packets.fromclient.ClRunAwayPacket;
import com.github.standobyte.jojo.network.packets.fromserver.TrHamonParticlesPacket;
import com.github.standobyte.jojo.power.IPower;
import com.github.standobyte.jojo.power.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.nonstand.type.HamonData.Exercise;
import com.github.standobyte.jojo.power.nonstand.type.HamonSkill.HamonStat;
import com.github.standobyte.jojo.power.nonstand.type.HamonSkill.Technique;
import com.github.standobyte.jojo.util.JojoModUtil;
import com.github.standobyte.jojo.util.damage.ModDamageSources;
import com.google.common.collect.ImmutableMap;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TripWireBlock;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TieredItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.KeybindTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class HamonPowerType extends NonStandPowerType<HamonData> {

    public HamonPowerType(int color, Action[] startingAttacks, Action[] startingAbilities, float manaRegenPoints) {
        super(color, startingAttacks, startingAbilities, manaRegenPoints, HamonData::new);
    }
    
    @Override
    public void onClear(INonStandPower power) {
        power.getTypeSpecificData(this).ifPresent(hamon -> hamon.setBreathingLevel(0));
    }

    @Override
    public int getExpRewardMultiplier() {
        return 4;
    }
    
    @Override
    public float reduceManaConsumed(float amount, INonStandPower power, LivingEntity user) {
        return user.getItemBySlot(EquipmentSlotType.HEAD).getItem() == ModItems.SATIPOROJA_SCARF.get() ? amount * 0.6F : super.reduceManaConsumed(amount, power, user);
    }
    
    @Override
    public boolean isLeapUnlocked(INonStandPower power) {
        return power.getTypeSpecificData(this).get().isSkillLearned(HamonSkill.JUMP);
    }
    
    @Override
    public void onLeap(INonStandPower power) {
        power.getTypeSpecificData(this).get().hamonPointsFromAction(HamonStat.CONTROL, getLeapManaCost());
//        createHamonSparkParticles(power.getUser().level, null, power.getUser().position(), getLeapStrength(power) * 0.15F);
    }
    
    @Override
    public float getLeapStrength(INonStandPower power) {
        HamonData hamon = power.getTypeSpecificData(this).get();
        float strength = (float) 1.5F + (float) hamon.getHamonControlLevel() / (float) HamonData.MAX_STAT_LEVEL;
        return hamon.isSkillLearned(HamonSkill.AFTERIMAGES) ? strength + 1.5F : strength;
    }
    
    @Override
    public int getLeapCooldownPeriod() {
        return 100;
    }
    
    @Override
    public float getLeapManaCost() {
        return 100;
    }
    
    @Override
    public boolean canTickMana(LivingEntity user, IPower<?> power) {
        return user.getAirSupply() == user.getMaxAirSupply() && !user.hasEffect(ModEffects.FREEZE.get()) &&
                !(user.getItemBySlot(EquipmentSlotType.HEAD).getItem() == ModItems.BREATH_CONTROL_MASK.get() && user.tickCount % 3 == 0);
    }

    @Override
    public void tickUser(LivingEntity user, IPower<?> power) {
        HamonData hamon = ((INonStandPower) power).getTypeSpecificData(this).get();
        float breathing = hamon.getBreathingLevel();
        World world = user.level;
        if (!world.isClientSide()) {
            hamon.tick();
            int air = user.getAirSupply();
            if (air < user.getMaxAirSupply() && user.tickCount % 200 < (int) breathing * 2 - 1) {
                user.setAirSupply(air + 1);
            }
            if (user instanceof PlayerEntity) {
                ServerPlayerEntity player = (ServerPlayerEntity) user;
                float multiplier = player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == ModItems.BREATH_CONTROL_MASK.get() ? 2F : 0;
                if (player.swinging) {
                    hamon.incExerciseTicks(Exercise.MINING, multiplier);
                }
                if (player.isSwimming()) {
                    hamon.incExerciseTicks(Exercise.SWIMMING, multiplier);
                }
                else if (player.isSprinting() && player.isOnGround()) {
                    hamon.incExerciseTicks(Exercise.RUNNING, multiplier);
                }
                if (player.hasEffect(ModEffects.MEDITATION.get())) {
                    if (hamon.updateMeditation(player.position(), player.yHeadRot, player.xRot)) {
                        if (player.tickCount % 800 == 400) {
                            JojoModUtil.sayVoiceLine(player, hamon.getBreathingSound(), 0.75F, 1.0F);
                        }
                        player.addEffect(new EffectInstance(ModEffects.MEDITATION.get(), Math.max(Exercise.MEDITATION.maxTicks - hamon.getExerciseTicks(Exercise.MEDITATION), 210)));
                        hamon.incExerciseTicks(Exercise.MEDITATION, multiplier);
                        if (power.getType().canTickMana(user, power)) {
                            power.addMana(power.getManaRegenPoints());
                        }
                        player.getFoodData().addExhaustion(-0.001F);
                        if (player.tickCount % 200 == 0 && player.isHurt() && player.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
                            player.heal(1.0F);
                        }
                    }
                    else {
                        player.removeEffect(ModEffects.MEDITATION.get());
                    }
                }
                hamon.newDayCheck(player.level);
                if (hamon.isSkillLearned(HamonSkill.ROPE_TRAP)) {
                    if (player.isOnGround() && player.isShiftKeyDown()) {
                        BlockPos pos = player.blockPosition();
                        if (player.level.isEmptyBlock(pos)) {
                            PlayerInventory inventory = player.inventory;
                            for (int i = 8; i >= 0; i--) {
                                ItemStack stack = inventory.items.get(i);
                                if (!stack.isEmpty() && stack.getItem() == Items.STRING) {
                                    BlockItemUseContext ctx = new BlockItemUseContext(player, Hand.OFF_HAND, stack, new BlockRayTraceResult(Vector3d.atCenterOf(pos), Direction.UP, pos, false));
                                    BlockState state = Blocks.TRIPWIRE.getStateForPlacement(ctx);
                                    if (state != null && !ForgeEventFactory.onBlockPlace(player, BlockSnapshot.create(world.dimension(), world, pos.below()), Direction.UP) && world.setBlock(pos, state, 3) && !player.abilities.instabuild) {
                                        stack.shrink(1);
                                    }
                                    break;
                                }
                            }
                        }
                    }
//                    if (player.fishing != null) {
//                        ItemStack mainHandItem = player.getMainHandItem();
//                        if (mainHandItem.getItem() instanceof FishingRodItem) {
//                            Entity hooked = player.fishing.getHookedIn();
//                            if (hooked != null) {
//                                float manaCost = 30;
//                                if (power.consumeMana(manaCost)) {
//                                    ModDamageSources.dealHamonDamage(hooked, 0.0125F, player.fishing, player);
//                                    hamon.hamonPointsFromAction(HamonStat.STRENGTH, manaCost);
//                                }
//                                else {
//                                    player.fishing.retrieve(mainHandItem);
//                                }
//                            }
//                        }
//                    }
                }
            }
        }
        else {
            user.getCapability(ClientPlayerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                boolean prevTickSound = cap.syoSound;
                cap.syoSound = canPerformSunlightYellowOverdrive(user, power, hamon, null);
                if (!prevTickSound && cap.syoSound) {
                    ClientTickingSoundsHelper.playHamonConcentrationSound(user, entity -> !cap.syoSound);
                }
            });
        }
        if (hamon.getTechnique() == Technique.JOSEPH) {
            if (user.isSprinting()) {
                Vector3d vecBehind = Vector3d.directionFromRotation(0, 180F + user.yRot).scale(8D);
                AxisAlignedBB aabb = new AxisAlignedBB(user.position().subtract(0, 2D, 0), user.position().add(vecBehind.x, 2D, vecBehind.z));
                List<LivingEntity> entitiesBehind = world.getEntitiesOfClass(LivingEntity.class, aabb, entity -> entity != user)
                        .stream().filter(entity -> !(entity instanceof StandEntity)).collect(Collectors.toList());
                if (!entitiesBehind.isEmpty()) {
                    if (world.isClientSide()) {
                        if (user instanceof ClientPlayerEntity && ((ClientPlayerEntity) user).sprintTime == 0) {
                            PacketManager.sendToServer(new ClRunAwayPacket());
                        }
                    }
                    else {
                        EffectInstance speed = user.getEffect(Effects.MOVEMENT_SPEED);
                        int speedAmplifier = speed != null && speed.getDuration() > 100 ? speed.getAmplifier() + 2 : 1;
                        user.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 100, speedAmplifier, false, false, true));
                    }
                }
            }
        }
    }

    @Override
    public boolean isReplaceableWith(NonStandPowerType<?> newType) {
        return newType == ModNonStandPowers.VAMPIRISM.get();
    }
    
    public static boolean ropeTrap(LivingEntity user, BlockPos pos, BlockState blockState, World world, INonStandPower power, HamonData hamon) {
        if (hamon.isSkillLearned(HamonSkill.ROPE_TRAP)) {
            createChargedCobweb(user, pos, blockState, world, 64, null, power, 
                    40 + (int) ((float) (160 * hamon.getHamonStrengthLevel()) / (float) HamonData.MAX_STAT_LEVEL), 0.02F * hamon.getHamonDamageMultiplier(), hamon);
            return true;
        }
        return false;
    }
    
    private static final Map<BooleanProperty, Direction> PROPERTY_TO_DIRECTION = ImmutableMap.of(
            TripWireBlock.EAST, Direction.EAST, 
            TripWireBlock.SOUTH, Direction.SOUTH, 
            TripWireBlock.WEST, Direction.WEST, 
            TripWireBlock.NORTH, Direction.NORTH);
    private static final float STRING_CHARGE_COST = 10;
    private static void createChargedCobweb(LivingEntity user, BlockPos pos, BlockState blockState, World world, 
            int range, @Nullable Direction from, INonStandPower power, int chargeTicks, float charge, HamonData hamon) {
        if (range > 0 && blockState.getBlock() == Blocks.TRIPWIRE && power.consumeMana(STRING_CHARGE_COST)) {
            hamon.hamonPointsFromAction(HamonStat.CONTROL, STRING_CHARGE_COST / 2);
            Map<Property<?>, Comparable<?>> values = blockState.getValues();
            List<Direction> directions = new ArrayList<Direction>();
            for (Map.Entry<BooleanProperty, Direction> entry: PROPERTY_TO_DIRECTION.entrySet()) {
                if (entry.getValue() != from && (Boolean) values.get(entry.getKey())) {
                    directions.add(entry.getValue());
                }
            }
            world.setBlock(pos, Blocks.COBWEB.defaultBlockState(), 3);
            HamonBlockChargeEntity chargeEntity = new HamonBlockChargeEntity(world, pos);
            chargeEntity.setCharge(charge, chargeTicks, user, STRING_CHARGE_COST / 2);
            world.addFreshEntity(chargeEntity);
            for (Direction direction : directions) {
                BlockPos nextPos = pos.relative(direction);
                createChargedCobweb(user, nextPos, world.getBlockState(nextPos), world, 
                        range - 1, direction.getOpposite(), power, chargeTicks, charge, hamon);
            }
        }
    }
    
    private static final Map<Technique, Supplier<SoundEvent>> SYO_VOICE_LINES = new ImmutableMap.Builder<Technique, Supplier<SoundEvent>>()
            .put(Technique.JONATHAN, ModSounds.JONATHAN_SUNLIGHT_YELLOW_OVERDRIVE)
            .put(Technique.ZEPPELI, ModSounds.ZEPPELI_SUNLIGHT_YELLOW_OVERDRIVE)
            .put(Technique.JOSEPH, ModSounds.JOSEPH_SUNLIGHT_YELLOW_OVERDRIVE)
            .put(Technique.CAESAR, ModSounds.CAESAR_SUNLIGHT_YELLOW_OVERDRIVE)
//            .put(Technique.LISA_LISA, ModSounds.LISA_LISA_SUNLIGHT_YELLOW_OVERDRIVE)
            .build();
    public static final float OVERDRIVE_DAMAGE = 0.5F;
    public static final float OVERDRIVE_COST = 750F;
    public static void overdriveAttack(LivingEntity user, LivingEntity target, INonStandPower power, HamonData hamon) {
        if (!user.level.isClientSide()) {
            SoundEvent overdriveVoiceLine = null;
            Technique technique = hamon.getTechnique();
            
            float damage = OVERDRIVE_DAMAGE;
            float manaCost = OVERDRIVE_COST;
            float knockback = 0;
            
            ItemStack heldItemStack = user.getMainHandItem();
            if (!heldItemStack.isEmpty()) {
                if (hamon.isSkillLearned(HamonSkill.METAL_SILVER_OVERDRIVE) && heldItemStack.getItem() instanceof TieredItem) {
                    TieredItem item = (TieredItem) heldItemStack.getItem();
                    damage *= 0.5F;
                    manaCost += 250;
                    if (!user.isShiftKeyDown() && item instanceof SwordItem && heldItemStack.hasCustomHoverName() && "pluck".equals(heldItemStack.getHoverName().getString().toLowerCase())) {
                        overdriveVoiceLine = ModSounds.JONATHAN_PLUCK_SWORD.get();
                    }
                }
                else {
                    return;
                }
            }
            else if (hamon.isSkillLearned(HamonSkill.TURQUOISE_BLUE_OVERDRIVE) && user.isInWater() && target.isInWater()) {
                damage *= 2F;
                knockback = 1F;
            }
            else {
                boolean sunlightYellowOverdrive = canPerformSunlightYellowOverdrive(user, power, hamon, target);
                if (sunlightYellowOverdrive) {
                    float SYOFactor = power.getMana() / manaCost;
                    manaCost = power.getMana();
                    damage *= SYOFactor * 1.2F;
                }
                if (sunlightYellowOverdrive && technique != null) {
                    Supplier<SoundEvent> syoVoiceLine =  SYO_VOICE_LINES.get(technique);
                    if (syoVoiceLine != null) {
                        overdriveVoiceLine = syoVoiceLine.get();
                    }
                }
            }
            
            if (!power.consumeMana(manaCost)) {
                return;
            }
            float dmgScale = 1;
            if (user instanceof PlayerEntity) {
                float swingStrengthScale = ((PlayerEntity) user).getAttackStrengthScale(0.5F);
                dmgScale = (0.2F + swingStrengthScale * swingStrengthScale * 0.8F);
                damage *= dmgScale;
            }
            if (ModDamageSources.dealHamonDamage(target, damage, user, null)) {
                hamon.hamonPointsFromAction(HamonStat.STRENGTH, manaCost * dmgScale);
                if (knockback > 0) {
                    target.knockback(knockback, 
                            (double) MathHelper.sin(user.yRot * ((float) Math.PI / 180F)), 
                            (double) (-MathHelper.cos(user.yRot * ((float) Math.PI / 180F))));
                }
                if (overdriveVoiceLine != null) {
                    JojoModUtil.sayVoiceLine(user, overdriveVoiceLine);
                }
            }
        }
    }
    
    public static boolean canPerformSunlightYellowOverdrive(LivingEntity user, IPower<?> power, HamonData hamon, @Nullable LivingEntity target) {
        if (user.isShiftKeyDown() && power.hasMana(OVERDRIVE_COST * 2) && hamon.isSkillLearned(HamonSkill.SUNLIGHT_YELLOW_OVERDRIVE)) {
            if (target == null && user.level.isClientSide()) {
                Entity crosshairEntity = ClientUtil.getCrosshairPickEntity();
                target = crosshairEntity instanceof LivingEntity ? (LivingEntity) crosshairEntity : null;
            }
            if (target != null) {
                return !target.isInvulnerableTo(ModDamageSources.HAMON) && target.getBoundingBox().inflate(user.getBbWidth() + 0.1F).contains(user.getEyePosition(1.0F));
            }
        }
        return false;
    }
    
    public static void updateCheatDeathEffect(LivingEntity user) {
        user.addEffect(new EffectInstance(ModEffects.CHEAT_DEATH.get(), 120000, 0, false, false, true));
    }
    
    public static void snakeMuffler(LivingAttackEvent event) {
        LivingEntity target = event.getEntityLiving();
        if (!target.level.isClientSide()) {
            DamageSource dmgSource = event.getSource();
            Entity attacker = dmgSource.getEntity();
            if (attacker != null && dmgSource.getDirectEntity() == attacker && attacker instanceof LivingEntity
                    && target instanceof PlayerEntity && target.getItemBySlot(EquipmentSlotType.HEAD).getItem() == ModItems.SATIPOROJA_SCARF.get()) {
                LivingEntity livingAttacker = (LivingEntity) attacker;
                PlayerEntity playerTarget = (PlayerEntity) target;
                if (!playerTarget.getCooldowns().isOnCooldown(ModItems.SATIPOROJA_SCARF.get())) {
                    INonStandPower power = INonStandPower.getPlayerNonStandPower(playerTarget);
                    float manaCost = 500F;
                    if (power.hasMana(manaCost)) {
                        power.getTypeSpecificData(ModNonStandPowers.HAMON.get()).ifPresent(hamon -> {
                            if (hamon.isSkillLearned(HamonSkill.SNAKE_MUFFLER)) {
                                playerTarget.getCooldowns().addCooldown(ModItems.SATIPOROJA_SCARF.get(), 80);
                                JojoModUtil.sayVoiceLine(target, ModSounds.LISA_LISA_SNAKE_MUFFLER.get());
                                power.consumeMana(manaCost);
                                ModDamageSources.dealHamonDamage(attacker, 0.75F, target, null);
                                livingAttacker.addEffect(new EffectInstance(Effects.GLOWING, 200));
                                event.setCanceled(true);
                                SnakeMufflerEntity snakeMuffler = new SnakeMufflerEntity(target.level, target);
                                snakeMuffler.setEntityToJumpOver(attacker);
                                target.level.addFreshEntity(snakeMuffler);
                                snakeMuffler.attachToBlockPos(target.blockPosition());
                            }
                        });
                    }
                }
            }
        }
    }
    

    @Nullable
    public static EnumSet<HamonSkill> nearbyTeachersSkills(LivingEntity learner) {
        EnumSet<HamonSkill> skills = EnumSet.noneOf(HamonSkill.class);
        JojoModUtil.entitiesAround(LivingEntity.class, learner, 3D, false, 
                entity -> INonStandPower.getNonStandPowerOptional((LivingEntity) entity).map(power -> 
                power.getTypeSpecificData(ModNonStandPowers.HAMON.get()).map(hamon -> {
                    for (HamonSkill learnedSkill : hamon.getSkillSetImmutable()) {
                        skills.add(learnedSkill);
                    }
                    return true;
                }).orElse(false)).orElse(false));
        return skills;
    }
    
    public static void interactWithHamonTeacher(World world, PlayerEntity player, LivingEntity teacher, HamonData teacherHamon) {
        INonStandPower.getNonStandPowerOptional(player).ifPresent(power -> {
            Optional<HamonData> hamonOptional = power.getTypeSpecificData(ModNonStandPowers.HAMON.get());
            if (!hamonOptional.isPresent() && !world.isClientSide()) {
                if (teacher instanceof PlayerEntity) {
                    teacherHamon.addNewPlayerLearner(player);
                }
                else {
                    startLearningHamon(world, player, power, teacher, teacherHamon);
                }
            }
            hamonOptional.ifPresent(hamon -> {
                if (world.isClientSide()) {
                    ClientUtil.openHamonTeacherUi();
                }
                else {
                    if (player.abilities.instabuild) {
                        hamon.setBreathingLevel(HamonData.MAX_BREATHING_LEVEL);
                        hamon.setHamonStatPoints(HamonSkill.HamonStat.STRENGTH, HamonData.MAX_HAMON_POINTS, true, true);
                        hamon.setHamonStatPoints(HamonSkill.HamonStat.CONTROL, HamonData.MAX_HAMON_POINTS, true, true);
                    }
                }
            });
        });
    }
    
    public static void startLearningHamon(World world, PlayerEntity player, INonStandPower playerPower, LivingEntity teacher, HamonData teacherHamon) {
        if (playerPower.givePower(ModNonStandPowers.HAMON.get())) {
            if (teacherHamon.getTechnique() == Technique.ZEPPELI) {
                JojoModUtil.sayVoiceLine(teacher, ModSounds.ZEPPELI_FORCE_BREATH.get());
                teacher.swing(Hand.MAIN_HAND, true);
                if (player.getRandom().nextFloat() <= 0.01F) {
                    player.hurt(DamageSource.GENERIC, 4.0F);
                    player.setAirSupply(0);
                    return;
                }
                else {
                    player.hurt(DamageSource.GENERIC, 0.1F);
                }
            }
            playerPower.getTypeSpecificData(ModNonStandPowers.HAMON.get()).ifPresent(hamon -> {
                if (player.abilities.instabuild) {
                    hamon.setBreathingLevel(HamonData.MAX_BREATHING_LEVEL);
                    hamon.setHamonStatPoints(HamonSkill.HamonStat.STRENGTH, HamonData.MAX_HAMON_POINTS, true, true);
                    hamon.setHamonStatPoints(HamonSkill.HamonStat.CONTROL, HamonData.MAX_HAMON_POINTS, true, true);
                }
                player.sendMessage(new TranslationTextComponent("jojo.chat.message.learnt_hamon"), Util.NIL_UUID);
                PlayerUtilCap utilCap = player.getCapability(PlayerUtilCapProvider.CAPABILITY).orElseThrow(() -> new IllegalStateException());
                utilCap.sendNotification(OneTimeNotification.HAMON_WINDOW, 
                        new TranslationTextComponent("jojo.chat.message.hamon_window_hint", new KeybindTextComponent("jojo.key.hamon_skills_window")));
            });
        }
        else {
            player.sendMessage(new TranslationTextComponent("jojo.chat.message.cant_learn_hamon"), Util.NIL_UUID);
        }
        return;
    }
    

    
    public static void cancelCactusDamage(LivingAttackEvent event) {
        if (event.getSource() == DamageSource.CACTUS) {
            LivingEntity entity = event.getEntityLiving();
            INonStandPower.getNonStandPowerOptional(entity).ifPresent(power -> {
                power.getTypeSpecificData(ModNonStandPowers.HAMON.get()).ifPresent(hamon -> {
                    if (hamon.isSkillLearned(HamonSkill.WATER_WALKING) && power.consumeMana(event.getAmount() * 0.5F)) {
                        HamonPowerType.createHamonSparkParticles(entity.level, null, entity.getX(), entity.getY(0.5), entity.getZ(), 0.1F);
                        event.setCanceled(true);
                    }
                });
            });
        }
    }

    public static void hamonPerksOnDeath(LivingDeathEvent event) {
        LivingEntity dead = event.getEntityLiving();
        if (JojoModConfig.COMMON.keepNonStandOnDeath.get()) return;
        INonStandPower.getNonStandPowerOptional(dead).ifPresent(power -> {
            power.getTypeSpecificData(ModNonStandPowers.HAMON.get()).ifPresent(hamon -> {
                if (hamon.getTechnique() != null) {
                    switch (hamon.getTechnique()) {
                    case CAESAR:
                        if (hamon.isSkillLearned(HamonSkill.CRIMSON_BUBBLE)) {
                            CrimsonBubbleEntity bubble = new CrimsonBubbleEntity(dead.level);
                            ItemStack heldItem = dead.getMainHandItem();
                            if (!heldItem.isEmpty()) {
                                dead.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                                ItemEntity item = new ItemEntity(dead.level, dead.getX(), dead.getEyeY() - 0.3D, dead.getZ(), heldItem);
                                item.setPickUpDelay(2);
                                dead.level.addFreshEntity(item);
                                bubble.putItem(item);
                            }
                            dead.level.playSound(null, dead.getX(), dead.getY(), dead.getZ(), 
                                    ModSounds.CAESAR_LAST_HAMON.get(), dead.getSoundSource(), 1.0F, 1.0F);
                            bubble.moveTo(dead.getX(), dead.getEyeY(), dead.getZ(), dead.yRot, dead.xRot);
                            bubble.setHamonPoints(hamon.getHamonStrengthPoints(), hamon.getHamonControlPoints());
                            dead.level.addFreshEntity(bubble);
                        }
                        break;
                    case ZEPPELI:
                        if (hamon.isSkillLearned(HamonSkill.DEEP_PASS)) {
                            PlayerEntity closestHamonUser = JojoModUtil.entitiesAround(PlayerEntity.class, dead, 8, false, player -> 
                            INonStandPower.getNonStandPowerOptional(player).map(pwr -> pwr.getType() == ModNonStandPowers.HAMON.get()).orElse(false))
                                    .stream()
                                    .min(Comparator.comparingDouble(player -> player.distanceToSqr(dead)))
                                    .orElse(null);
                            if (closestHamonUser != null) {
                                dead.level.playSound(null, dead.getX(), dead.getY(), dead.getZ(), 
                                        ModSounds.ZEPPELI_DEEP_PASS.get(), dead.getSoundSource(), 1.0F, 1.0F);
                                HamonData receiverHamon = INonStandPower.getPlayerNonStandPower(closestHamonUser).getTypeSpecificData(ModNonStandPowers.HAMON.get()).get();
                                if (receiverHamon.getTechnique() == Technique.JONATHAN) {
                                    JojoModUtil.sayVoiceLine(closestHamonUser, ModSounds.JONATHAN_DEEP_PASS_REACTION.get());
                                }
                                receiverHamon.setHamonStatPoints(HamonSkill.HamonStat.STRENGTH, 
                                        receiverHamon.getHamonStrengthPoints() + hamon.getHamonStrengthPoints(), true, false);
                                receiverHamon.setHamonStatPoints(HamonSkill.HamonStat.CONTROL, 
                                        receiverHamon.getHamonControlPoints() + hamon.getHamonControlPoints(), true, false);
                                createHamonSparkParticlesEmitter(closestHamonUser, 1.0F);
                            }
                        }
                        break;
                    default:
                        break;
                    }
                }
            });
        });
    }

    public static void createHamonSparkParticles(World world, @Nullable PlayerEntity clientHandled, double x, double y, double z, float intensity) {
        if (intensity > 0) {
            intensity = Math.min(intensity, 4F);
            int count = Math.max((int) (intensity * 16.5F), 1);
            if (!world.isClientSide()) {
                ((ServerWorld) world).sendParticles(ModParticles.HAMON_SPARK.get(), x, y, z, count, 0.05, 0.05, 0.05, 0.25);
            }
            else if (clientHandled == ClientUtil.getClientPlayer()) {
                ClientUtil.createHamonSparkParticles(x, y, z, count);
            }
            world.playSound(clientHandled, x, y, z, ModSounds.HAMON_SPARK.get(), 
                    SoundCategory.AMBIENT, intensity * 2, 1.0F + (world.random.nextFloat() - 0.5F) * 0.15F);
        }
    }
    
    public static void createHamonSparkParticles(World world, @Nullable PlayerEntity clientHandled, Vector3d vec, float intensity) {
        createHamonSparkParticles(world, clientHandled, vec.x, vec.y, vec.z, intensity);
    }
    
    public static void createHamonSparkParticlesEmitter(Entity entity, float intensity) {
        if (intensity > 0) {
            intensity = Math.min(intensity, 4F);
            World world = entity.level;
            if (!world.isClientSide()) {
                PacketManager.sendToClientsTrackingAndSelf(new TrHamonParticlesPacket(entity.getId(), intensity), entity);
            }
            else {
                for (int i = (int) (intensity * 9.5F); i >= 0; i--) {
                    ClientUtil.createHamonSparksEmitter(entity, Math.max(1, (int) (intensity * 9.5) - i));
                    if (i % 2 == 0) {
                        ClientTickingSoundsHelper.playHamonSparksSound(entity, intensity * 2, 1.0F + (world.random.nextFloat() - 0.5F) * 0.15F);
                    }
                }
            }
        }
    }
}
