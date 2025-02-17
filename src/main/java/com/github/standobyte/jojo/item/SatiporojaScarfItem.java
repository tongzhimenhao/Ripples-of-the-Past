package com.github.standobyte.jojo.item;

import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.SatiporojaScarfBindingEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.SatiporojaScarfEntity;
import com.github.standobyte.jojo.init.ModEffects;
import com.github.standobyte.jojo.init.ModNonStandPowers;
import com.github.standobyte.jojo.power.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.nonstand.type.HamonSkill;
import com.github.standobyte.jojo.power.nonstand.type.HamonSkill.HamonStat;
import com.github.standobyte.jojo.util.damage.ModDamageSources;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;

public class SatiporojaScarfItem extends CustomModelArmorItem {

    public SatiporojaScarfItem(IArmorMaterial material, EquipmentSlotType slot, Properties builder) {
        super(material, slot, builder);
    }

    public static final float SCARF_SWING_MANA_COST = 600;
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        INonStandPower power = INonStandPower.getPlayerNonStandPower(player);
        if (power.getTypeSpecificData(ModNonStandPowers.HAMON.get()).map(hamon -> {
            if (hamon.isSkillLearned(HamonSkill.SATIPOROJA_SCARF)) {
                if (!world.isClientSide()) {
                    if (power.consumeMana(SCARF_SWING_MANA_COST)) {
                        SatiporojaScarfEntity scarf = new SatiporojaScarfEntity(world, player, 
                                hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm() == HandSide.RIGHT ? HandSide.LEFT : HandSide.RIGHT);
                        world.addFreshEntity(scarf);
                        player.getCooldowns().addCooldown(this, scarf.ticksLifespan());
                        return true;
                    }
                    return false;
                }
                return power.hasMana(SCARF_SWING_MANA_COST);
            }
            return false;
        }).orElse(false)) {
            return ActionResult.success(stack);
        }
        return ActionResult.pass(stack);
    }

    @Override
    public boolean hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity user) {
        if (user instanceof PlayerEntity && ((PlayerEntity) user).getCooldowns().isOnCooldown(itemStack.getItem())) {
            return false;
        }
        return INonStandPower.getNonStandPowerOptional(user).map(power -> 
        power.getTypeSpecificData(ModNonStandPowers.HAMON.get()).map(hamon -> {
            if (!user.level.isClientSide()) {
                if (power.consumeMana(500) && ModDamageSources.dealHamonDamage(target, 0.6F, user, null)) {
                    if (user.isShiftKeyDown() && hamon.isSkillLearned(HamonSkill.SNAKE_MUFFLER) && power.consumeMana(100)) {
                        SatiporojaScarfBindingEntity scarf = new SatiporojaScarfBindingEntity(user.level, user);
                        scarf.attachToEntity(target);
                        target.addEffect(new EffectInstance(ModEffects.STUN.get(), scarf.ticksLifespan()));
                        user.level.addFreshEntity(scarf);
                        if (user instanceof PlayerEntity) {
                            ((PlayerEntity) user).getCooldowns().addCooldown(this, scarf.ticksLifespan());
                        }
                    }
                    hamon.hamonPointsFromAction(HamonStat.STRENGTH, 500);
                    return true;
                }
                return false;
            }
            return true;
        }).orElse(false)).orElse(false);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }

}
