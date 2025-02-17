package com.github.standobyte.jojo.init;

import com.github.standobyte.jojo.JojoMod;
import com.github.standobyte.jojo.entity.AfterimageEntity;
import com.github.standobyte.jojo.entity.CrimsonBubbleEntity;
import com.github.standobyte.jojo.entity.HamonBlockChargeEntity;
import com.github.standobyte.jojo.entity.HamonProjectileShieldEntity;
import com.github.standobyte.jojo.entity.LeavesGliderEntity;
import com.github.standobyte.jojo.entity.MRDetectorEntity;
import com.github.standobyte.jojo.entity.PillarmanTempleEngravingEntity;
import com.github.standobyte.jojo.entity.RoadRollerEntity;
import com.github.standobyte.jojo.entity.damaging.LightBeamEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.HGEmeraldEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.HamonBubbleBarrierEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.HamonBubbleCutterEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.HamonBubbleEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.HamonCutterEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.MRCrossfireHurricaneEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.MRFireballEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.MRFlameEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.SCRapierEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.HGBarrierEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.HGGrapplingStringEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.HGStringEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.MRRedBindEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.SPStarFingerEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.SatiporojaScarfBindingEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.SatiporojaScarfEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.SnakeMufflerEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.SpaceRipperStingyEyesEntity;
import com.github.standobyte.jojo.entity.damaging.projectile.ownerbound.ZoomPunchEntity;
import com.github.standobyte.jojo.entity.itemprojectile.BladeHatEntity;
import com.github.standobyte.jojo.entity.itemprojectile.ClackersEntity;
import com.github.standobyte.jojo.entity.itemprojectile.KnifeEntity;
import com.github.standobyte.jojo.entity.itemprojectile.StandArrowEntity;
import com.github.standobyte.jojo.entity.mob.HamonMasterEntity;
import com.github.standobyte.jojo.entity.mob.HungryZombieEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityStats;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.entity.stand.stands.HierophantGreenEntity;
import com.github.standobyte.jojo.entity.stand.stands.MagiciansRedEntity;
import com.github.standobyte.jojo.entity.stand.stands.SilverChariotEntity;
import com.github.standobyte.jojo.entity.stand.stands.StarPlatinumEntity;
import com.github.standobyte.jojo.entity.stand.stands.TheWorldEntity;
import com.github.standobyte.jojo.network.packets.fromserver.TrStandSoundPacket.StandSoundType;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, JojoMod.MOD_ID);
    
//  public static final RegistryObject<EntityType<___Entity>> ___ = ENTITIES.register("___", 
//          () -> EntityType.Builder.<___Entity>of(___Entity::new, EntityClassification.MISC).sized(, )//.noSummon().noSave()
//          .build(new ResourceLocation(JojoMod.MOD_ID, "___").toString()));

    public static final RegistryObject<EntityType<HungryZombieEntity>> HUNGRY_ZOMBIE = ENTITIES.register("hungry_zombie", 
            () -> EntityType.Builder.<HungryZombieEntity>of(HungryZombieEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F)
            .build(new ResourceLocation(JojoMod.MOD_ID, "hungry_zombie").toString()));
    
    public static final RegistryObject<EntityType<HamonMasterEntity>> HAMON_MASTER = ENTITIES.register("hamon_master", 
            () -> EntityType.Builder.<HamonMasterEntity>of(HamonMasterEntity::new, EntityClassification.MISC).sized(0.6F, 1.95F)
            .build(new ResourceLocation(JojoMod.MOD_ID, "hamon_teacher").toString()));

    public static final RegistryObject<EntityType<BladeHatEntity>> BLADE_HAT = ENTITIES.register("blade_hat", 
            () -> EntityType.Builder.<BladeHatEntity>of(BladeHatEntity::new, EntityClassification.MISC).sized(0.6F, 0.375F).setUpdateInterval(20)
            .build(new ResourceLocation(JojoMod.MOD_ID, "blade_hat").toString()));
    
    public static final RegistryObject<EntityType<SpaceRipperStingyEyesEntity>> SPACE_RIPPER_STINGY_EYES = ENTITIES.register("space_ripper_stingy_eyes", 
            () -> EntityType.Builder.<SpaceRipperStingyEyesEntity>of(SpaceRipperStingyEyesEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).noSummon()
            .build(new ResourceLocation(JojoMod.MOD_ID, "space_ripper_stingy_eyes").toString()));
    
    public static final RegistryObject<EntityType<ZoomPunchEntity>> ZOOM_PUNCH = ENTITIES.register("zoom_punch", 
            () -> EntityType.Builder.<ZoomPunchEntity>of(ZoomPunchEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).noSummon()
            .build(new ResourceLocation(JojoMod.MOD_ID, "zoom_punch").toString()));
    
    public static final RegistryObject<EntityType<AfterimageEntity>> AFTERIMAGE = ENTITIES.register("afterimage", 
            () -> EntityType.Builder.<AfterimageEntity>of(AfterimageEntity::new, EntityClassification.MISC).sized(0.6F, 1.8F).noSummon()
            .build(new ResourceLocation(JojoMod.MOD_ID, "afterimage").toString()));
    
    public static final RegistryObject<EntityType<HamonProjectileShieldEntity>> HAMON_PROJECTILE_SHIELD = ENTITIES.register("hamon_projectile_shield", 
            () -> EntityType.Builder.<HamonProjectileShieldEntity>of(HamonProjectileShieldEntity::new, EntityClassification.MISC).sized(3.0F, 3.0F).noSummon().noSave()
            .build(new ResourceLocation(JojoMod.MOD_ID, "hamon_projectile_shield").toString()));
    
    public static final RegistryObject<EntityType<LeavesGliderEntity>> LEAVES_GLIDER = ENTITIES.register("leaves_glider", 
            () -> EntityType.Builder.<LeavesGliderEntity>of(LeavesGliderEntity::new, EntityClassification.MISC).sized(2.5F, 0.125F)
            .build(new ResourceLocation(JojoMod.MOD_ID, "leaves_glider").toString()));
    
    public static final RegistryObject<EntityType<HamonBlockChargeEntity>> HAMON_BLOCK_CHARGE = ENTITIES.register("hamon_block_charge", 
            () -> EntityType.Builder.<HamonBlockChargeEntity>of(HamonBlockChargeEntity::new, EntityClassification.MISC).sized(1.0F, 1.0F).setUpdateInterval(Integer.MAX_VALUE).setShouldReceiveVelocityUpdates(false)
            .build(new ResourceLocation(JojoMod.MOD_ID, "hamon_block_charge").toString()));
    
    public static final RegistryObject<EntityType<LightBeamEntity>> AJA_STONE_BEAM = ENTITIES.register("aja_stone_beam", 
            () -> EntityType.Builder.<LightBeamEntity>of(LightBeamEntity::new, EntityClassification.MISC).sized(0.125F, 0.125F).noSummon().setUpdateInterval(1)
            .build(new ResourceLocation(JojoMod.MOD_ID, "aja_stone_beam").toString()));
    
    public static final RegistryObject<EntityType<HamonCutterEntity>> HAMON_CUTTER = ENTITIES.register("hamon_cutter", 
            () -> EntityType.Builder.<HamonCutterEntity>of(HamonCutterEntity::new, EntityClassification.MISC).sized(0.5F, 0.125F).setUpdateInterval(10)
            .build(new ResourceLocation(JojoMod.MOD_ID, "hamon_cutter").toString()));
    
    public static final RegistryObject<EntityType<ClackersEntity>> CLACKERS = ENTITIES.register("clackers", 
            () -> EntityType.Builder.<ClackersEntity>of(ClackersEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).setUpdateInterval(20)
            .build(new ResourceLocation(JojoMod.MOD_ID, "clackers").toString()));
    
    public static final RegistryObject<EntityType<HamonBubbleEntity>> HAMON_BUBBLE = ENTITIES.register("hamon_bubble", 
            () -> EntityType.Builder.<HamonBubbleEntity>of(HamonBubbleEntity::new, EntityClassification.MISC).sized(0.15F, 0.15F).setUpdateInterval(10)
            .build(new ResourceLocation(JojoMod.MOD_ID, "hamon_bubble").toString()));
    
    public static final RegistryObject<EntityType<HamonBubbleBarrierEntity>> HAMON_BUBBLE_BARRIER = ENTITIES.register("hamon_bubble_barrier", 
            () -> EntityType.Builder.<HamonBubbleBarrierEntity>of(HamonBubbleBarrierEntity::new, EntityClassification.MISC).sized(2.0F, 2.0F)
            .build(new ResourceLocation(JojoMod.MOD_ID, "hamon_bubble_barrier").toString()));
    
    public static final RegistryObject<EntityType<HamonBubbleCutterEntity>> HAMON_BUBBLE_CUTTER = ENTITIES.register("hamon_bubble_cutter", 
            () -> EntityType.Builder.<HamonBubbleCutterEntity>of(HamonBubbleCutterEntity::new, EntityClassification.MISC).sized(0.325F, 0.0875F).setUpdateInterval(10)
            .build(new ResourceLocation(JojoMod.MOD_ID, "hamon_bubble_cutter").toString()));
    
    public static final RegistryObject<EntityType<CrimsonBubbleEntity>> CRIMSON_BUBBLE = ENTITIES.register("crimson_bubble", 
            () -> EntityType.Builder.<CrimsonBubbleEntity>of(CrimsonBubbleEntity::new, EntityClassification.MISC).sized(0.625F, 0.625F).noSummon().setUpdateInterval(20)
            .build(new ResourceLocation(JojoMod.MOD_ID, "crimson_bubble").toString()));
    
    public static final RegistryObject<EntityType<SatiporojaScarfEntity>> SATIPOROJA_SCARF = ENTITIES.register("satiporoja_scarf", 
            () -> EntityType.Builder.<SatiporojaScarfEntity>of(SatiporojaScarfEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).noSummon()
            .build(new ResourceLocation(JojoMod.MOD_ID, "satiporoja_scarf").toString()));
    
    public static final RegistryObject<EntityType<SatiporojaScarfBindingEntity>> SATIPOROJA_SCARF_BINDING = ENTITIES.register("satiporoja_scarf_binding", 
            () -> EntityType.Builder.<SatiporojaScarfBindingEntity>of(SatiporojaScarfBindingEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).noSummon()
            .build(new ResourceLocation(JojoMod.MOD_ID, "satiporoja_scarf_binding").toString()));
    
    public static final RegistryObject<EntityType<SnakeMufflerEntity>> SNAKE_MUFFLER = ENTITIES.register("snake_muffler", 
            () -> EntityType.Builder.<SnakeMufflerEntity>of(SnakeMufflerEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).noSummon()
            .build(new ResourceLocation(JojoMod.MOD_ID, "snake_muffler").toString()));
    
    public static final RegistryObject<EntityType<KnifeEntity>> KNIFE = ENTITIES.register("knife", 
            () -> EntityType.Builder.<KnifeEntity>of(KnifeEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).setUpdateInterval(20)
            .build(new ResourceLocation(JojoMod.MOD_ID, "knife").toString()));
    
    public static final RegistryObject<EntityType<StandArrowEntity>> STAND_ARROW = ENTITIES.register("stand_arrow", 
            () -> EntityType.Builder.<StandArrowEntity>of(StandArrowEntity::new, EntityClassification.MISC).sized(0.75F, 0.75F).clientTrackingRange(4).updateInterval(20)
            .build(new ResourceLocation(JojoMod.MOD_ID, "stand_arrow").toString()));
    
    public static final RegistryObject<EntityType<PillarmanTempleEngravingEntity>> PILLARMAN_TEMPLE_ENGRAVING = ENTITIES.register("pillarman_temple_engraving", 
            () -> EntityType.Builder.<PillarmanTempleEngravingEntity>of(PillarmanTempleEngravingEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).noSummon().setUpdateInterval(Integer.MAX_VALUE).setShouldReceiveVelocityUpdates(false)
            .build(new ResourceLocation(JojoMod.MOD_ID, "pillarman_temple_engraving").toString()));
    
    public static final RegistryObject<EntityType<SPStarFingerEntity>> SP_STAR_FINGER = ENTITIES.register("sp_star_finger", 
            () -> EntityType.Builder.<SPStarFingerEntity>of(SPStarFingerEntity::new, EntityClassification.MISC).sized(0.125F, 0.125F).noSummon().noSave()
            .build(new ResourceLocation(JojoMod.MOD_ID, "sp_star_finger").toString()));
    
    public static final RegistryObject<EntityType<HGStringEntity>> HG_STRING = ENTITIES.register("hg_string", 
            () -> EntityType.Builder.<HGStringEntity>of(HGStringEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).noSummon().noSave()
            .build(new ResourceLocation(JojoMod.MOD_ID, "hg_string").toString()));
    
    public static final RegistryObject<EntityType<HGEmeraldEntity>> HG_EMERALD = ENTITIES.register("hg_emerald", 
            () -> EntityType.Builder.<HGEmeraldEntity>of(HGEmeraldEntity::new, EntityClassification.MISC).sized(0.5F, 0.25F).noSummon().noSave().setUpdateInterval(10)
            .build(new ResourceLocation(JojoMod.MOD_ID, "hg_emerald").toString()));
    
    public static final RegistryObject<EntityType<HGGrapplingStringEntity>> HG_GRAPPLING_STRING = ENTITIES.register("hg_grappling_string", 
            () -> EntityType.Builder.<HGGrapplingStringEntity>of(HGGrapplingStringEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).noSummon().noSave()
            .build(new ResourceLocation(JojoMod.MOD_ID, "hg_grappling_string").toString()));
    
    public static final RegistryObject<EntityType<HGBarrierEntity>> HG_BARRIER = ENTITIES.register("hg_barrier", 
            () -> EntityType.Builder.<HGBarrierEntity>of(HGBarrierEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).noSummon().noSave()
            .build(new ResourceLocation(JojoMod.MOD_ID, "hg_barrier").toString()));
    
    public static final RegistryObject<EntityType<SCRapierEntity>> SC_RAPIER = ENTITIES.register("sc_rapier", 
            () -> EntityType.Builder.<SCRapierEntity>of(SCRapierEntity::new, EntityClassification.MISC).sized(0.125F, 0.125F).noSummon().noSave().setUpdateInterval(10)
            .build(new ResourceLocation(JojoMod.MOD_ID, "sc_rapier").toString()));
    
    public static final RegistryObject<EntityType<RoadRollerEntity>> ROAD_ROLLER = ENTITIES.register("road_roller", 
            () -> EntityType.Builder.<RoadRollerEntity>of(RoadRollerEntity::new, EntityClassification.MISC).sized(4.0F, 2.0F).setUpdateInterval(10)
            .build(new ResourceLocation(JojoMod.MOD_ID, "road_roller").toString()));
    
    public static final RegistryObject<EntityType<MRFlameEntity>> MR_FLAME = ENTITIES.register("mr_flame", 
            () -> EntityType.Builder.<MRFlameEntity>of(MRFlameEntity::new, EntityClassification.MISC).sized(0.0625F, 0.0625F).noSummon().noSave().setUpdateInterval(10)
            .build(new ResourceLocation(JojoMod.MOD_ID, "mr_flame").toString()));
    
    public static final RegistryObject<EntityType<MRFireballEntity>> MR_FIREBALL = ENTITIES.register("mr_fireball", 
            () -> EntityType.Builder.<MRFireballEntity>of(MRFireballEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).noSummon().noSave().setUpdateInterval(10)
            .build(new ResourceLocation(JojoMod.MOD_ID, "mr_fireball").toString()));
    
    public static final RegistryObject<EntityType<MRCrossfireHurricaneEntity>> MR_CROSSFIRE_HURRICANE = ENTITIES.register("mr_crossfire_hurricane", 
            () -> EntityType.Builder.<MRCrossfireHurricaneEntity>of(MRCrossfireHurricaneEntity::new, EntityClassification.MISC).sized(1.25F, 1.8F).noSummon().noSave().setUpdateInterval(10)
            .build(new ResourceLocation(JojoMod.MOD_ID, "mr_crossfire_hurricane").toString()));
    
    public static final RegistryObject<EntityType<MRCrossfireHurricaneEntity>> MR_CROSSFIRE_HURRICANE_SPECIAL = ENTITIES.register("mr_crossfire_hurricane_special", 
            () -> EntityType.Builder.<MRCrossfireHurricaneEntity>of(MRCrossfireHurricaneEntity::new, EntityClassification.MISC).sized(0.625F, 0.9F).noSummon().noSave().setUpdateInterval(10)
            .build(new ResourceLocation(JojoMod.MOD_ID, "mr_crossfire_hurricane_special").toString()));
    
    public static final RegistryObject<EntityType<MRRedBindEntity>> MR_RED_BIND = ENTITIES.register("mr_red_bind", 
            () -> EntityType.Builder.<MRRedBindEntity>of(MRRedBindEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).noSummon().noSave()
            .build(new ResourceLocation(JojoMod.MOD_ID, "mr_red_bind").toString()));
    
    public static final RegistryObject<EntityType<MRDetectorEntity>> MR_DETECTOR = ENTITIES.register("mr_detector", 
            () -> EntityType.Builder.<MRDetectorEntity>of(MRDetectorEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).noSummon().noSave().setUpdateInterval(2)
            .build(new ResourceLocation(JojoMod.MOD_ID, "mr_detector").toString()));

    /*
     * default stand stats:
     * .damage(5.0)                         // 0.0 ~ 2048.0
     * .attackKnockback(1.0)                // 0.0 ~ 5.0
     * .movementSpeed(0.6)                  // 0.0 ~ 1024.0
     * .attackSpeed(10.0)                   // 0.0 ~ 1024.0
     * .summonTicks(10)                     // 0 ~ 20
     * .maxRange(4.0)                       // 4.0 ~ 1000.0
     * .armor(0)                            // 0.0 ~ 30.0
     * .armorToughness(0)                   // 0.0 ~ 20.0
     * .knockbackResistance(0)              // 0.0 ~ 1.0
     * .blockDmgFactor(0.5)                 // 0.0 ~ 1.0
     * .blockStaminaCostForDmgPoint(10.0)   // 0.0 ~ 100.0
     * .precision(0.0)                      // 0.0 ~ 1.0 
     */
    
    public static final RegistryObject<StandEntityType<StarPlatinumEntity>> STAR_PLATINUM = ENTITIES.register("star_platinum", 
            () -> new StandEntityType<StarPlatinumEntity>(StarPlatinumEntity::new, false, 0.7F, 2.1F, 
                    new StandEntityStats.Builder()
                    .damage(10)
                    .attackSpeed(100)
                    .summonTicks(0)
                    .armor(20)
                    .knockbackResistance(0.2)
                    .precision(1.0)
                    .build())
            .addStandSound(StandSoundType.SUMMON, ModSounds.STAR_PLATINUM_SUMMON)
            .addStandSound(StandSoundType.UNSUMMON, ModSounds.STAR_PLATINUM_UNSUMMON)
            .addStandSound(StandSoundType.MELEE_ATTACK, ModSounds.STAR_PLATINUM_ORA)
            .addStandSound(StandSoundType.MELEE_BARRAGE, ModSounds.STAR_PLATINUM_ORA_ORA_ORA)
            .addStandSound(StandSoundType.RANGED_ATTACK, ModSounds.STAR_PLATINUM_STAR_FINGER));
    
    public static final RegistryObject<StandEntityType<TheWorldEntity>> THE_WORLD = ENTITIES.register("the_world", 
            () -> new StandEntityType<TheWorldEntity>(TheWorldEntity::new, false, 0.7F, 2.1F,
                    new StandEntityStats.Builder()
                    .damage(10.5)
                    .attackSpeed(100)
                    .summonTicks(0)
                    .maxRange(10)
                    .armor(20)
                    .knockbackResistance(0.2)
                    .precision(0.6)
                    .build())
            .addStandSound(StandSoundType.SUMMON, ModSounds.THE_WORLD_SUMMON)
            .addStandSound(StandSoundType.MELEE_ATTACK, ModSounds.THE_WORLD_MUDA)
            .addStandSound(StandSoundType.MELEE_BARRAGE, ModSounds.THE_WORLD_MUDA_MUDA_MUDA));
    
    public static final RegistryObject<StandEntityType<HierophantGreenEntity>> HIEROPHANT_GREEN = ENTITIES.register("hierophant_green", 
            () -> new StandEntityType<HierophantGreenEntity>(HierophantGreenEntity::new, false, 0.55F, 1.75F, 
                    new StandEntityStats.Builder()
                    .damage(1.25)
                    .maxRange(100)
                    .build())
            .addStandSound(StandSoundType.SUMMON, ModSounds.HIEROPHANT_GREEN_SUMMON)
            .addStandSound(StandSoundType.RANGED_ATTACK, ModSounds.HIEROPHANT_GREEN_EMERALD_SPLASH));
    
    public static final RegistryObject<StandEntityType<SilverChariotEntity>> SILVER_CHARIOT = ENTITIES.register("silver_chariot", 
            () -> new StandEntityType<SilverChariotEntity>(SilverChariotEntity::new, false, 0.6F, 1.8F, 
                    new StandEntityStats.Builder()
                    .damage(8)
                    .attackKnockback(0.1)
                    .attackSpeed(75)
                    .summonTicks(3)
                    .maxRange(10)
                    .armor(15)
                    .armorToughness(8)
                    .precision(1.0)
                    .build())
            .addStandSound(StandSoundType.SUMMON, ModSounds.SILVER_CHARIOT_SUMMON)
            .addStandSound(StandSoundType.UNSUMMON, ModSounds.SILVER_CHARIOT_UNSUMMON)
            .addStandSound(StandSoundType.RANGED_ATTACK, ModSounds.SILVER_CHARIOT_RAPIER_SHOT));
    
    public static final RegistryObject<StandEntityType<MagiciansRedEntity>> MAGICIANS_RED = ENTITIES.register("magicians_red", 
            () -> new StandEntityType<MagiciansRedEntity>(MagiciansRedEntity::new, false, 0.6F, 1.8F,
                    new StandEntityStats.Builder()
                    .damage(7)
                    .maxRange(8)
                    .armor(7)
                    .build())
            .addStandSound(StandSoundType.SUMMON, ModSounds.MAGICIANS_RED_SUMMON));
}
