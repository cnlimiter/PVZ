package cn.evolvefield.mods.pvz.init.registry;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.api.interfaces.types.IZombieType;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PlantType;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.utils.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 19:06
 * Description:
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegister {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =  DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Static.MOD_ID);

    /**
     * drops
     */
    public static final RegistryObject<EntityType<SunEntity>> SUN = registerEntityType(SunEntity::new, "sun", EntityClassification.AMBIENT);
    public static final RegistryObject<EntityType<CoinEntity>> COIN = registerEntityType(CoinEntity::new, "coin", EntityClassification.MISC);
    public static final RegistryObject<EntityType<JewelEntity>> JEWEL = registerEntityType(JewelEntity::new, "jewel", EntityClassification.MISC);
    public static final RegistryObject<EntityType<EnergyEntity>> ENERGY = registerEntityType(EnergyEntity::new, "energy", EntityClassification.MISC, 0.9f, 2f);
    public static final RegistryObject<EntityType<GiftBoxEntity>> GIFT_BOX = registerEntityType(GiftBoxEntity::new, "gift_box", EntityClassification.MISC, 0.9f, 1f);

    /**
     * bullets
     */
    public static final RegistryObject<EntityType<PeaEntity>> PEA = registerEntityType(PeaEntity::new, "pea", EntityClassification.MISC);
    public static final RegistryObject<EntityType<PotatoEntity>> POTATO = registerEntityType(PotatoEntity::new, "potato", EntityClassification.MISC);
    public static final RegistryObject<EntityType<SporeEntity>> SPORE = registerEntityType(SporeEntity::new, "spore", EntityClassification.MISC);
    public static final RegistryObject<EntityType<FumeEntity>> FUME = registerEntityType(FumeEntity::new, "fume", EntityClassification.MISC);
    public static final RegistryObject<EntityType<MetalItemEntity>> METAL = registerEntityType(MetalItemEntity::new, "metal", EntityClassification.MISC);
    public static final RegistryObject<EntityType<ThornEntity>> THORN = registerEntityType(ThornEntity::new, "thorn", EntityClassification.MISC);
    public static final RegistryObject<EntityType<StarEntity>> STAR = registerEntityType(StarEntity::new, "star", EntityClassification.MISC);
    //	public static final RegistryObject<EntityType<NutEntity>> NUT = registerEntityType(NutEntity::new, "nut", EntityClassification.MISC);
    public static final RegistryObject<EntityType<CabbageEntity>> CABBAGE = registerEntityType(CabbageEntity::new, "cabbage", EntityClassification.MISC);
    public static final RegistryObject<EntityType<KernelEntity>> KERNEL = registerEntityType(KernelEntity::new, "kernel", EntityClassification.MISC);
    public static final RegistryObject<EntityType<ButterEntity>> BUTTER = registerEntityType(ButterEntity::new, "butter", EntityClassification.MISC);
    public static final RegistryObject<EntityType<TargetArrowEntity>> TARGET_ARROW = registerEntityType(TargetArrowEntity::new, "target_arrow", EntityClassification.MISC);
    public static final RegistryObject<EntityType<MelonEntity>> MELON = registerEntityType(MelonEntity::new, "melon", EntityClassification.MISC);
    public static final RegistryObject<EntityType<FireCrackerEntity>> FIRE_CRACKER = registerEntityType(FireCrackerEntity::new, "fire_cracker", EntityClassification.MISC);
    public static final RegistryObject<EntityType<BallEntity>> BALL = registerEntityType(BallEntity::new, "ball", EntityClassification.MISC);
    public static final RegistryObject<EntityType<CornEntity>> CORN = registerEntityType(CornEntity::new, "corn", EntityClassification.MISC);

    /**
     * effects
     */
    public static final RegistryObject<EntityType<OriginEffectEntity>> ORIGIN_EFFECT = registerEntityType(OriginEffectEntity::new, "origin_effect", EntityClassification.MISC);
    public static final RegistryObject<EntityType<DoomFixerEntity>> DOOM_FIXER = registerEntityType(DoomFixerEntity::new, "doom_fixer", EntityClassification.MISC);

    /**
     * misc
     */
    public static final RegistryObject<EntityType<SmallChomperEntity>> SMALL_CHOMPER = registerEntityType(SmallChomperEntity::new, "small_chomper", EntityClassification.MISC);
    public static final RegistryObject<EntityType<BobsleCarEntity>> BOBSLE_CAR = registerEntityType(BobsleCarEntity::new, "bobsle_car", EntityClassification.MISC);
    public static final RegistryObject<EntityType<PVZZombiePartEntity>> ZOMBIE_PART = registerEntityType(PVZZombiePartEntity::new, "zombie_part", EntityClassification.MISC);
    public static final RegistryObject<EntityType<ZombieHandEntity>> ZOMBIE_HAND = registerEntityType(ZombieHandEntity::new, "zombie_hand", EntityClassification.MISC);
    public static final RegistryObject<EntityType<WallNutBowlingEntity>> WALL_NUT_BOWLING = registerEntityType(WallNutBowlingEntity::new, "wall_nut_bowling", EntityClassification.MISC);
    public static final RegistryObject<EntityType<ExplosionBowlingEntity>> EXPLOSION_BOWLING = registerEntityType(ExplosionBowlingEntity::new, "explosion_bowling", EntityClassification.MISC);
    public static final RegistryObject<EntityType<GiantNutBowlingEntity>> GIANT_NUT_BOWLING = registerEntityType(GiantNutBowlingEntity::new, "giant_nut_bowling", EntityClassification.MISC);
    public static final RegistryObject<EntityType<LawnMowerEntity>> LAWN_MOWER = registerEntityType(LawnMowerEntity::new, "lawn_mower", EntityClassification.MISC);
    public static final RegistryObject<EntityType<FireCrackersEntity>> FIRE_CRACKERS = registerEntityType(FireCrackersEntity::new, "fire_crackers", EntityClassification.MISC);
    public static final RegistryObject<EntityType<ElementBallEntity>> ELEMENT_BALL = registerEntityType(ElementBallEntity::new, "element_ball", EntityClassification.MISC);
    public static final RegistryObject<EntityType<DestroyCarEntity>> DESTROY_CAR = registerEntityType(DestroyCarEntity::new, "destroy_car", EntityClassification.MISC);
    public static final RegistryObject<EntityType<GardenRakeEntity>> GARDEN_RAKE = registerEntityType(GardenRakeEntity::new, "garden_rake", EntityClassification.MISC);
    public static final RegistryObject<EntityType<ZombieDropBodyEntity>> ZOMBIE_DROP_BODY = registerEntityType(ZombieDropBodyEntity::new, "zombie_drop_body", EntityClassification.MISC);

    /**
     * animals
     */
    public static final RegistryObject<EntityType<FoodieZombieEntity>> FOODIE_ZOMBIE = registerEntityType(FoodieZombieEntity::new, "foodie_zombie", EntityClassification.WATER_CREATURE);

    /**
     * npc
     */
    public static final RegistryObject<EntityType<CrazyDaveEntity>> CRAZY_DAVE = registerEntityType(CrazyDaveEntity::new, "crazy_dave", EntityClassification.CREATURE);
    public static final RegistryObject<EntityType<PennyEntity>> PANNEY = registerEntityType(PennyEntity::new, "panney", EntityClassification.CREATURE);
    public static final RegistryObject<EntityType<SunDaveEntity>> SUN_DAVE = registerEntityType(SunDaveEntity::new, "sun_dave", EntityClassification.CREATURE);

    /**
     * zombies
     */
    public static final RegistryObject<EntityType<NormalZombieEntity>> NORMAL_ZOMBIE = registerZombieEntityType(NormalZombieEntity::new, "normal_zombie");
    public static final RegistryObject<EntityType<FlagZombieEntity>> FLAG_ZOMBIE = registerZombieEntityType(FlagZombieEntity::new, "flag_zombie");
    public static final RegistryObject<EntityType<ConeHeadZombieEntity>> CONEHEAD_ZOMBIE = registerZombieEntityType(ConeHeadZombieEntity::new, "conehead_zombie");
    public static final RegistryObject<EntityType<PoleZombieEntity>> POLE_ZOMBIE = registerZombieEntityType(PoleZombieEntity::new, "pole_zombie");
    public static final RegistryObject<EntityType<BucketHeadZombieEntity>> BUCKETHEAD_ZOMBIE = registerZombieEntityType(BucketHeadZombieEntity::new, "buckethead_zombie");
    public static final RegistryObject<EntityType<SnorkelZombieEntity>> SNORKEL_ZOMBIE = registerZombieEntityType(SnorkelZombieEntity::new, "snorkel_zombie");
    public static final RegistryObject<EntityType<ZomboniEntity>> ZOMBONI = registerZombieEntityType(ZomboniEntity::new, "zomboni");
    public static final RegistryObject<EntityType<BobsleTeamEntity>> BOBSLE_TEAM = registerZombieEntityType(BobsleTeamEntity::new, "bobsle_team");
    public static final RegistryObject<EntityType<BobsleZombieEntity>> BOBSLE_ZOMBIE = registerZombieEntityType(BobsleZombieEntity::new, "bobsle_zombie");
    public static final RegistryObject<EntityType<ZombieDolphinEntity>> ZOMBIE_DOLPHIN = registerZombieEntityType(ZombieDolphinEntity::new, "zombie_dolphin");
    public static final RegistryObject<EntityType<DolphinRiderEntity>> DOLPHIN_RIDER = registerZombieEntityType(DolphinRiderEntity::new, "dolphin_rider");
    public static final RegistryObject<EntityType<DolphinRiderZombieEntity>> DOLPHIN_RIDER_ZOMBIE = registerZombieEntityType(DolphinRiderZombieEntity::new, "dolphin_rider_zombie");
    public static final RegistryObject<EntityType<LavaZombieEntity>> LAVA_ZOMBIE = registerZombieEntityType(LavaZombieEntity::new, "lava_zombie");
    public static final RegistryObject<EntityType<NewspaperZombieEntity>> NEWSPAPER_ZOMBIE = registerZombieEntityType(NewspaperZombieEntity::new, "newspaper_zombie");
    public static final RegistryObject<EntityType<TombStoneEntity>> TOMB_STONE = registerZombieEntityType(TombStoneEntity::new, "tomb_stone");
    public static final RegistryObject<EntityType<ScreenDoorZombieEntity>> SCREENDOOR_ZOMBIE = registerZombieEntityType(ScreenDoorZombieEntity::new, "screendoor_zombie");
    public static final RegistryObject<EntityType<FootballZombieEntity>> FOOTBALL_ZOMBIE = registerZombieEntityType(FootballZombieEntity::new, "football_zombie");
    public static final RegistryObject<EntityType<DancingZombieEntity>> DANCING_ZOMBIE = registerZombieEntityType(DancingZombieEntity::new, "dancing_zombie");
    public static final RegistryObject<EntityType<BackupDancerEntity>> BACKUP_DANCER = registerZombieEntityType(BackupDancerEntity::new, "backup_dancer");
    public static final RegistryObject<EntityType<GigaFootballZombieEntity>> GIGA_FOOTBALL_ZOMBIE = registerZombieEntityType(GigaFootballZombieEntity::new, "giga_football_zombie");
    public static final RegistryObject<EntityType<PumpkinZombieEntity>> PUMPKIN_ZOMBIE = registerZombieEntityType(PumpkinZombieEntity::new, "pumpkin_zombie");
    public static final RegistryObject<EntityType<TrickZombieEntity>> TRICK_ZOMBIE = registerZombieEntityType(TrickZombieEntity::new, "trick_zombie");
    public static final RegistryObject<EntityType<CoffinEntity>> COFFIN = registerZombieEntityType(CoffinEntity::new, "coffin");
    public static final RegistryObject<EntityType<MournerZombieEntity>> MOURNER_ZOMBIE = registerZombieEntityType(MournerZombieEntity::new, "mourner_zombie");
    public static final RegistryObject<EntityType<NobleZombieEntity>> NOBLE_ZOMBIE = registerZombieEntityType(NobleZombieEntity::new, "noble_zombie");
    public static final RegistryObject<EntityType<OldZombieEntity>> OLD_ZOMBIE = registerZombieEntityType(OldZombieEntity::new, "old_zombie");
    public static final RegistryObject<EntityType<SundayEditionZombieEntity>> SUNDAY_EDITION_ZOMBIE = registerZombieEntityType(SundayEditionZombieEntity::new, "sunday_edition_zombie");
    public static final RegistryObject<EntityType<JackInBoxZombieEntity>> JACK_IN_BOX_ZOMBIE = registerZombieEntityType(JackInBoxZombieEntity::new, "jack_in_box_zombie");
    public static final RegistryObject<EntityType<PogoZombieEntity>> POGO_ZOMBIE = registerZombieEntityType(PogoZombieEntity::new, "pogo_zombie");
    public static final RegistryObject<EntityType<YetiZombieEntity>> YETI_ZOMBIE = registerZombieEntityType(YetiZombieEntity::new, "yeti_zombie");
    public static final RegistryObject<EntityType<DiggerZombieEntity>> DIGGER_ZOMBIE = registerZombieEntityType(DiggerZombieEntity::new, "digger_zombie");
    public static final RegistryObject<EntityType<BalloonZombieEntity>> BALLOON_ZOMBIE = registerZombieEntityType(BalloonZombieEntity::new, "balloon_zombie");
    public static final RegistryObject<EntityType<RaZombieEntity>> RA_ZOMBIE = registerZombieEntityType(RaZombieEntity::new, "ra_zombie");
    public static final RegistryObject<EntityType<BungeeZombieEntity>> BUNGEE_ZOMBIE = registerZombieEntityType(BungeeZombieEntity::new, "bungee_zombie");
    public static final RegistryObject<EntityType<LadderZombieEntity>> LADDER_ZOMBIE = registerZombieEntityType(LadderZombieEntity::new, "ladder_zombie");
    public static final RegistryObject<EntityType<CatapultZombieEntity>> CATAPULT_ZOMBIE = registerZombieEntityType(CatapultZombieEntity::new, "catapult_zombie");
    public static final RegistryObject<EntityType<GargantuarEntity>> GARGANTUAR = registerZombieEntityType(GargantuarEntity::new, "gargantuar");
    public static final RegistryObject<EntityType<ImpEntity>> IMP = registerZombieEntityType(ImpEntity::new, "imp");
    public static final RegistryObject<EntityType<GigaGargantuarEntity>> GIGA_GARGANTUAR = registerZombieEntityType(GigaGargantuarEntity::new, "giga_gargantuar");
    public static final RegistryObject<EntityType<Edgar090505Entity>> EDGAR_090505 = registerZombieEntityType(Edgar090505Entity::new, "edgar_090505");
    public static final RegistryObject<EntityType<Edgar090517Entity>> EDGAR_090517 = registerZombieEntityType(Edgar090517Entity::new, "edgar_090517");
    public static final RegistryObject<EntityType<PeaShooterZombieEntity>> PEASHOOTER_ZOMBIE = registerZombieEntityType(PeaShooterZombieEntity::new, "peashooter_zombie");
    public static final RegistryObject<EntityType<GatlingPeaZombieEntity>> GATLINGPEA_ZOMBIE = registerZombieEntityType(GatlingPeaZombieEntity::new, "gatlingpea_zombie");
    public static final RegistryObject<EntityType<SquashZombieEntity>> SQUASH_ZOMBIE = registerZombieEntityType(SquashZombieEntity::new, "squash_zombie");
    public static final RegistryObject<EntityType<JalapenoZombieEntity>> JALAPENO_ZOMBIE = registerZombieEntityType(JalapenoZombieEntity::new, "jalapeno_zombie");
    public static final RegistryObject<EntityType<WallNutZombieEntity>> WALLNUT_ZOMBIE = registerZombieEntityType(WallNutZombieEntity::new, "wallnut_zombie");
    public static final RegistryObject<EntityType<TallNutZombieEntity>> TALLNUT_ZOMBIE = registerZombieEntityType(TallNutZombieEntity::new, "tallnut_zombie");
    public static final RegistryObject<EntityType<GigaTombStoneEntity>> GIGA_TOMB_STONE = registerZombieEntityType(GigaTombStoneEntity::new, "giga_tomb_stone");

    /**
     * plants
     */
    public static final RegistryObject<EntityType<PeaShooterEntity>> PEA_SHOOTER = registerPlantEntityType(PeaShooterEntity::new, "pea_shooter");
    public static final RegistryObject<EntityType<SunFlowerEntity>> SUN_FLOWER = registerPlantEntityType(SunFlowerEntity::new, "sun_flower");
    public static final RegistryObject<EntityType<CherryBombEntity>> CHERRY_BOMB = registerPlantEntityType(CherryBombEntity::new, "cherry_bomb");
    public static final RegistryObject<EntityType<WallNutEntity>> WALL_NUT = registerPlantEntityType(WallNutEntity::new, "wall_nut");
    public static final RegistryObject<EntityType<PotatoMineEntity>> POTATO_MINE = registerPlantEntityType(PotatoMineEntity::new, "potato_mine");
    public static final RegistryObject<EntityType<SnowPeaEntity>> SNOW_PEA = registerPlantEntityType(SnowPeaEntity::new, "snow_pea");
    public static final RegistryObject<EntityType<ChomperEntity>> CHOMPER = registerPlantEntityType(ChomperEntity::new, "chomper");
    public static final RegistryObject<EntityType<RepeaterEntity>> REPEATER = registerPlantEntityType(RepeaterEntity::new, "repeater");
    public static final RegistryObject<EntityType<LilyPadEntity>> LILY_PAD = registerPlantEntityType(LilyPadEntity::new, "lily_pad");
    public static final RegistryObject<EntityType<SquashEntity>> SQUASH = registerPlantEntityType(SquashEntity::new, "squash");
    public static final RegistryObject<EntityType<ThreePeaterEntity>> THREE_PEATER = registerPlantEntityType(ThreePeaterEntity::new, "three_peater");
    public static final RegistryObject<EntityType<TangleKelpEntity>> TANGLE_KELP = registerPlantEntityType(TangleKelpEntity::new, "tangle_kelp");
    public static final RegistryObject<EntityType<JalapenoEntity>> JALAPENO = registerPlantEntityType(JalapenoEntity::new, "jalapeno");
    public static final RegistryObject<EntityType<SpikeWeedEntity>> SPIKE_WEED = registerPlantEntityType(SpikeWeedEntity::new, "spike_weed");
    public static final RegistryObject<EntityType<TorchWoodEntity>> TORCH_WOOD = registerPlantEntityType(TorchWoodEntity::new, "torch_wood");
    public static final RegistryObject<EntityType<TallNutEntity>> TALL_NUT = registerPlantEntityType(TallNutEntity::new, "tall_nut");
    public static final RegistryObject<EntityType<PuffShroomEntity>> PUFF_SHROOM = registerPlantEntityType(PuffShroomEntity::new, "puff_shroom");
    public static final RegistryObject<EntityType<SunShroomEntity>> SUN_SHROOM = registerPlantEntityType(SunShroomEntity::new, "sun_shroom");
    public static final RegistryObject<EntityType<FumeShroomEntity>> FUME_SHROOM = registerPlantEntityType(FumeShroomEntity::new, "fume_shroom");
    public static final RegistryObject<EntityType<GraveBusterEntity>> GRAVE_BUSTER = registerPlantEntityType(GraveBusterEntity::new, "grave_buster");
    public static final RegistryObject<EntityType<HypnoShroomEntity>> HYPNO_SHROOM = registerPlantEntityType(HypnoShroomEntity::new, "hypno_shroom");
    public static final RegistryObject<EntityType<ScaredyShroomEntity>> SCAREDY_SHROOM = registerPlantEntityType(ScaredyShroomEntity::new, "scaredy_shroom");
    public static final RegistryObject<EntityType<IceShroomEntity>> ICE_SHROOM = registerPlantEntityType(IceShroomEntity::new, "ice_shroom");
    public static final RegistryObject<EntityType<DoomShroomEntity>> DOOM_SHROOM = registerPlantEntityType(DoomShroomEntity::new, "doom_shroom");
    public static final RegistryObject<EntityType<SeaShroomEntity>> SEA_SHROOM = registerPlantEntityType(SeaShroomEntity::new, "sea_shroom");
    public static final RegistryObject<EntityType<SplitPeaEntity>> SPLIT_PEA = registerPlantEntityType(SplitPeaEntity::new, "split_pea");
    public static final RegistryObject<EntityType<CoffeeBeanEntity>> COFFEE_BEAN = registerPlantEntityType(CoffeeBeanEntity::new, "coffee_bean");
    public static final RegistryObject<EntityType<MariGoldEntity>> MARIGOLD = registerPlantEntityType(MariGoldEntity::new, "marigold");
    public static final RegistryObject<EntityType<GatlingPeaEntity>> GATLING_PEA = registerPlantEntityType(GatlingPeaEntity::new, "gatling_pea");
    public static final RegistryObject<EntityType<TwinSunFlowerEntity>> TWIN_SUNFLOWER = registerPlantEntityType(TwinSunFlowerEntity::new, "twin_sunflower");
    public static final RegistryObject<EntityType<WaterGuardEntity>> WATER_GUARD = registerPlantEntityType(WaterGuardEntity::new, "water_guard");
    public static final RegistryObject<EntityType<PumpkinEntity>> PUMPKIN = registerPlantEntityType(PumpkinEntity::new, "pumpkin");
    public static final RegistryObject<EntityType<PlanternEntity>> PLANTERN = registerPlantEntityType(PlanternEntity::new, "plantern");
    public static final RegistryObject<EntityType<MagnetShroomEntity>> MAGNET_SHROOM = registerPlantEntityType(MagnetShroomEntity::new, "magnet_shroom");
    public static final RegistryObject<EntityType<CatTailEntity>> CAT_TAIL = registerPlantEntityType(CatTailEntity::new, "cat_tail");
    public static final RegistryObject<EntityType<StrangeCatEntity>> STRANGE_CAT = registerPlantEntityType(StrangeCatEntity::new, "strange_cat");
    public static final RegistryObject<EntityType<StarFruitEntity>> STAR_FRUIT = registerPlantEntityType(StarFruitEntity::new, "star_fruit");
    public static final RegistryObject<EntityType<AngelStarFruitEntity>> ANGEL_STAR_FRUIT = registerPlantEntityType(AngelStarFruitEntity::new, "angel_star_fruit");
    public static final RegistryObject<EntityType<CactusEntity>> CACTUS = registerPlantEntityType(CactusEntity::new, "cactus");
    public static final RegistryObject<EntityType<BloverEntity>> BLOVER = registerPlantEntityType(BloverEntity::new, "blover");
    public static final RegistryObject<EntityType<GloomShroomEntity>> GLOOM_SHROOM = registerPlantEntityType(GloomShroomEntity::new, "gloom_shroom");
    public static final RegistryObject<EntityType<GoldMagnetEntity>> GOLD_MAGNET = registerPlantEntityType(GoldMagnetEntity::new, "gold_magnet");
    public static final RegistryObject<EntityType<GoldLeafEntity>> GOLD_LEAF = registerPlantEntityType(GoldLeafEntity::new, "gold_leaf");
    public static final RegistryObject<EntityType<FlowerPotEntity>> FLOWER_POT = registerPlantEntityType(FlowerPotEntity::new, "flower_pot");
    public static final RegistryObject<EntityType<CabbagePultEntity>> CABBAGE_PULT = registerPlantEntityType(CabbagePultEntity::new, "cabbage_pult");
    public static final RegistryObject<EntityType<KernelPultEntity>> KERNEL_PULT = registerPlantEntityType(KernelPultEntity::new, "kernel_pult");
    public static final RegistryObject<EntityType<ButterPultEntity>> BUTTER_PULT = registerPlantEntityType(ButterPultEntity::new, "butter_pult");
    public static final RegistryObject<EntityType<GarlicEntity>> GARLIC = registerPlantEntityType(GarlicEntity::new, "garlic");
    public static final RegistryObject<EntityType<UmbrellaLeafEntity>> UMBRELLA_LEAF = registerPlantEntityType(UmbrellaLeafEntity::new, "umbrella_leaf");
    public static final RegistryObject<EntityType<MelonPultEntity>> MELON_PULT = registerPlantEntityType(MelonPultEntity::new, "melon_pult");
    public static final RegistryObject<EntityType<WinterMelonEntity>> WINTER_MELON = registerPlantEntityType(WinterMelonEntity::new, "winter_melon");
    public static final RegistryObject<EntityType<BambooLordEntity>> BAMBOO_LORD = registerPlantEntityType(BambooLordEntity::new, "bamboo_lord");
    public static final RegistryObject<EntityType<IcebergLettuceEntity>> ICEBERG_LETTUCE = registerPlantEntityType(IcebergLettuceEntity::new, "iceberg_lettuce");
    public static final RegistryObject<EntityType<SpikeRockEntity>> SPIKE_ROCK = registerPlantEntityType(SpikeRockEntity::new, "spike_rock");
    public static final RegistryObject<EntityType<BonkChoyEntity>> BONK_CHOY = registerPlantEntityType(BonkChoyEntity::new, "bonk_choy");
    public static final RegistryObject<EntityType<ImitaterEntity>> IMITATER = registerPlantEntityType(ImitaterEntity::new, "imitater");
    public static final RegistryObject<EntityType<CobCannonEntity>> COB_CANNON = registerPlantEntityType(CobCannonEntity::new, "cob_cannon");


    @SubscribeEvent
    public static void registryEntityRenders(EntityRenderersEvent.RegisterRenderers event){
        // drops
        event.registerEntityRenderer(SUN.get(), SunRender::new);
        event.registerEntityRenderer(COIN.get(), CoinRender::new);
        event.registerEntityRenderer(JEWEL.get(),JewelRender::new);
        event.registerEntityRenderer(ENERGY.get(), EnergyRender::new);
        event.registerEntityRenderer(GIFT_BOX.get(), GiftBoxRender::new);

        // bullets
        event.registerEntityRenderer(PEA.get(), PeaRender::new);
        event.registerEntityRenderer(POTATO.get(), PotatoRender::new);
        event.registerEntityRenderer(SPORE.get(), SporeRender::new);
        event.registerEntityRenderer(FUME.get(), FumeRender::new);
        event.registerEntityRenderer(METAL.get(), MetalItemRender::new);
        event.registerEntityRenderer(THORN.get(), ThornRender::new);
        event.registerEntityRenderer(STAR.get(), StarRender::new);
//        event.registerEntityRenderer(NUT.get(), NutRender::new);
        event.registerEntityRenderer(CABBAGE.get(), CabbageRender::new);
        event.registerEntityRenderer(KERNEL.get(), KernelRender::new);
        event.registerEntityRenderer(BUTTER.get(), ButterRender::new);
        event.registerEntityRenderer(TARGET_ARROW.get(), TargetArrowRender::new);
        event.registerEntityRenderer(MELON.get(), MelonRender::new);
        event.registerEntityRenderer(FIRE_CRACKER.get(), FireCrackerRender::new);
        event.registerEntityRenderer(BALL.get(), BallRender::new);
        event.registerEntityRenderer(CORN.get(), CornRender::new);

        // effects
        event.registerEntityRenderer(ORIGIN_EFFECT.get(), OriginEffectRender::new);
        event.registerEntityRenderer(DOOM_FIXER.get(), DoomFixerRender::new);

        // misc
        event.registerEntityRenderer(SMALL_CHOMPER.get(), SmallChomperRender::new);
        event.registerEntityRenderer(BOBSLE_CAR.get(), BobsleCarRender::new);
        event.registerEntityRenderer(ZOMBIE_PART.get(), EmptyRender::new);
        event.registerEntityRenderer(ZOMBIE_HAND.get(), ZombieHandRender::new);
        event.registerEntityRenderer(WALL_NUT_BOWLING.get(), WallNutBowlingRender::new);
        event.registerEntityRenderer(EXPLOSION_BOWLING.get(), ExplosionBowlingRender::new);
        event.registerEntityRenderer(GIANT_NUT_BOWLING.get(), GiantNutBowlingRender::new);
        event.registerEntityRenderer(LAWN_MOWER.get(), LawnMowerRender::new);
        event.registerEntityRenderer(FIRE_CRACKERS.get(), FireCrackersRender::new);
        event.registerEntityRenderer(ELEMENT_BALL.get(), ElementBallRender::new);
        event.registerEntityRenderer(DESTROY_CAR.get(), DestroyCarRender::new);
        event.registerEntityRenderer(GARDEN_RAKE.get(), GardenRakeRender::new);
        event.registerEntityRenderer(ZOMBIE_DROP_BODY.get(), ZombieBodyRender::new);

        // animals
        event.registerEntityRenderer(FOODIE_ZOMBIE.get(), FoodieZombieRender::new);

        // npc
        event.registerEntityRenderer(CRAZY_DAVE.get(), CrazyDaveRender::new);
        event.registerEntityRenderer(PANNEY.get(), PennyRender::new);
        event.registerEntityRenderer(SUN_DAVE.get(), SunDaveRender::new);

        // zombies
        event.registerEntityRenderer(NORMAL_ZOMBIE.get(), NormalZombieRender::new);
        event.registerEntityRenderer(FLAG_ZOMBIE.get(), FlagZombieRender::new);
        event.registerEntityRenderer(CONEHEAD_ZOMBIE.get(), ConeHeadZombieRender::new);
        event.registerEntityRenderer(POLE_ZOMBIE.get(), PoleZombieRender::new);
        event.registerEntityRenderer(BUCKETHEAD_ZOMBIE.get(), BucketHeadZombieRender::new);
        event.registerEntityRenderer(SNORKEL_ZOMBIE.get(), SnorkelZombieRender::new);
        event.registerEntityRenderer(ZOMBONI.get(), ZomboniRender::new);
        event.registerEntityRenderer(BOBSLE_TEAM.get(), BobsleTeamRender::new);
        event.registerEntityRenderer(BOBSLE_ZOMBIE.get(), BobsleZombieRender::new);
        event.registerEntityRenderer(ZOMBIE_DOLPHIN.get(), ZombieDolphinRender::new);
        event.registerEntityRenderer(DOLPHIN_RIDER.get(), DolphinRiderRender::new);
        event.registerEntityRenderer(DOLPHIN_RIDER_ZOMBIE.get(), DolphinRiderZombieRender::new);
        event.registerEntityRenderer(LAVA_ZOMBIE.get(), LavaZombieRender::new);
        event.registerEntityRenderer(NEWSPAPER_ZOMBIE.get(), NewspaperZombieRender::new);
        event.registerEntityRenderer(TOMB_STONE.get(), TombStoneRender::new);
        event.registerEntityRenderer(SCREENDOOR_ZOMBIE.get(), ScreenDoorZombieRender::new);
        event.registerEntityRenderer(FOOTBALL_ZOMBIE.get(), FootballZombieRender::new);
        event.registerEntityRenderer(GIGA_FOOTBALL_ZOMBIE.get(), GigaFootballZombieRender::new);
        event.registerEntityRenderer(DANCING_ZOMBIE.get(),DancingZombieRender::new);
        event.registerEntityRenderer(BACKUP_DANCER.get(), BackupDancerRender::new);
        event.registerEntityRenderer(PUMPKIN_ZOMBIE.get(), PumpkinZombieRender::new);
        event.registerEntityRenderer(TRICK_ZOMBIE.get(), TrickZombieRender::new);
        event.registerEntityRenderer(COFFIN.get(), CoffinRender::new);
        event.registerEntityRenderer(MOURNER_ZOMBIE.get(), MournerZombieRender::new);
        event.registerEntityRenderer(NOBLE_ZOMBIE.get(), NobleZombieRender::new);
        event.registerEntityRenderer(OLD_ZOMBIE.get(), OldZombieRender::new);
        event.registerEntityRenderer(SUNDAY_EDITION_ZOMBIE.get(), SundayEditionZombieRender::new);
        event.registerEntityRenderer(JACK_IN_BOX_ZOMBIE.get(), JackInBoxZombieRender::new);
        event.registerEntityRenderer(POGO_ZOMBIE.get(), PogoZombieRender::new);
        event.registerEntityRenderer(YETI_ZOMBIE.get(), YetiZombieRender::new);
        event.registerEntityRenderer(DIGGER_ZOMBIE.get(), DiggerZombieRender::new);
        event.registerEntityRenderer(BALLOON_ZOMBIE.get(), BalloonZombieRender::new);
        event.registerEntityRenderer(RA_ZOMBIE.get(), RaZombieRender::new);
        event.registerEntityRenderer(BUNGEE_ZOMBIE.get(), BungeeZombieRender::new);
        event.registerEntityRenderer(LADDER_ZOMBIE.get(), LadderZombieRender::new);
        event.registerEntityRenderer(CATAPULT_ZOMBIE.get(), CatapultZombieRender::new);
        event.registerEntityRenderer(GARGANTUAR.get(), GargantuarRender::new);
        event.registerEntityRenderer(IMP.get(), ImpRender::new);
        event.registerEntityRenderer(GIGA_GARGANTUAR.get(), GigaGargantuarRender::new);
        event.registerEntityRenderer(EDGAR_090505.get(), EdgarRobotRender::new);
        event.registerEntityRenderer(PEASHOOTER_ZOMBIE.get(), PeaShooterZombieRender::new);
        event.registerEntityRenderer(GATLINGPEA_ZOMBIE.get(), GatlingPeaZombieRender::new);
        event.registerEntityRenderer(SQUASH_ZOMBIE.get(), SquashZombieRender::new);
        event.registerEntityRenderer(JALAPENO_ZOMBIE.get(), JalapenoZombieRender::new);
        event.registerEntityRenderer(WALLNUT_ZOMBIE.get(), WallNutZombieRender::new);
        event.registerEntityRenderer(TALLNUT_ZOMBIE.get(), TallNutZombieRender::new);
        event.registerEntityRenderer(GIGA_TOMB_STONE.get(), GigaTombStoneRender::new);
        event.registerEntityRenderer(EDGAR_090517.get(), EdgarRobotRender::new);

        // plants
        event.registerEntityRenderer(PEA_SHOOTER.get(), PeaShooterRender::new);
        event.registerEntityRenderer(SUN_FLOWER.get(), SunFlowerRender::new);
        event.registerEntityRenderer(CHERRY_BOMB.get(), CherryBombRender::new);
        event.registerEntityRenderer(WALL_NUT.get(), WallNutRender::new);
        event.registerEntityRenderer(POTATO_MINE.get(), PotatoMineRender::new);
        event.registerEntityRenderer(SNOW_PEA.get(), SnowPeaRender::new);
        event.registerEntityRenderer(CHOMPER.get(), ChomperRender::new);
        event.registerEntityRenderer(REPEATER.get(), RepeaterRender::new);
        event.registerEntityRenderer(LILY_PAD.get(), LilyPadRender::new);
        event.registerEntityRenderer(SQUASH.get(), SquashRender::new);
        event.registerEntityRenderer(THREE_PEATER.get(), ThreePeaterRender::new);
        event.registerEntityRenderer(TANGLE_KELP.get(), TangleKelpRender::new);
        event.registerEntityRenderer(JALAPENO.get(), JalapenoRender::new);
        event.registerEntityRenderer(SPIKE_WEED.get(), SpikeWeedRender::new);
        event.registerEntityRenderer(TORCH_WOOD.get(), TorchWoodRender::new);
        event.registerEntityRenderer(TALL_NUT.get(), TallNutRender::new);

        event.registerEntityRenderer(PUFF_SHROOM.get(), PuffShroomRender::new);
        event.registerEntityRenderer(SUN_SHROOM.get(), SunShroomRender::new);
        event.registerEntityRenderer(FUME_SHROOM.get(), FumeShroomRender::new);
        event.registerEntityRenderer(GRAVE_BUSTER.get(), GraveBusterRender::new);
        event.registerEntityRenderer(HYPNO_SHROOM.get(), HypnoShroomRender::new);
        event.registerEntityRenderer(SCAREDY_SHROOM.get(), ScaredyShroomRender::new);
        event.registerEntityRenderer(ICE_SHROOM.get(), IceShroomRender::new);
        event.registerEntityRenderer(DOOM_SHROOM.get(), DoomShroomRender::new);

        event.registerEntityRenderer(SEA_SHROOM.get(), SeaShroomRender::new);
        event.registerEntityRenderer(PLANTERN.get(), PlanternRender::new);
        event.registerEntityRenderer(CACTUS.get(), CactusRender::new);
        event.registerEntityRenderer(BLOVER.get(), BloverRender::new);
        event.registerEntityRenderer(SPLIT_PEA.get(), SplitPeaRender::new);
        event.registerEntityRenderer(STAR_FRUIT.get(), StarFruitRender::new);
        event.registerEntityRenderer(PUMPKIN.get(), PumpkinRender::new);
        event.registerEntityRenderer(MAGNET_SHROOM.get(), MagnetShroomRender::new);

        event.registerEntityRenderer(CABBAGE_PULT.get(), CabbagePultRender::new);
        event.registerEntityRenderer(FLOWER_POT.get(), FlowerPotRender::new);
        event.registerEntityRenderer(KERNEL_PULT.get(), KernelPultRender::new);
        event.registerEntityRenderer(GARLIC.get(), GarlicRender::new);
        event.registerEntityRenderer(COFFEE_BEAN.get(), CoffeeBeanRender::new);
        event.registerEntityRenderer(UMBRELLA_LEAF.get(), UmbrellaLeafRender::new);
        event.registerEntityRenderer(MARIGOLD.get(), MariGoldRender::new);
        event.registerEntityRenderer(MELON_PULT.get(), MelonPultRender::new);

        event.registerEntityRenderer(GATLING_PEA.get(), GatlingPeaRender::new);
        event.registerEntityRenderer(TWIN_SUNFLOWER.get(), TwinSunFlowerRender::new);
        event.registerEntityRenderer(GLOOM_SHROOM.get(), GloomShroomRender::new);
        event.registerEntityRenderer(CAT_TAIL.get(), CatTailRender::new);
        event.registerEntityRenderer(WINTER_MELON.get(), WinterMelonRender::new);
        event.registerEntityRenderer(GOLD_MAGNET.get(), GoldMagnetRender::new);
        event.registerEntityRenderer(SPIKE_ROCK.get(), SpikeRockRender::new);
        event.registerEntityRenderer(COB_CANNON.get(), CobCannonRender::new);
        event.registerEntityRenderer(IMITATER.get(), ImitaterRender::new);

        event.registerEntityRenderer(WATER_GUARD.get(), WaterGuardRender::new);
        event.registerEntityRenderer(STRANGE_CAT.get(), StrangeCatRender::new);

        event.registerEntityRenderer(ANGEL_STAR_FRUIT.get(), AngelStarFruitRender::new);
        event.registerEntityRenderer(GOLD_LEAF.get(), GoldLeafRender::new);
        event.registerEntityRenderer(BUTTER_PULT.get(), ButterPultRender::new);
        event.registerEntityRenderer(BAMBOO_LORD.get(), BambooLordRender::new);
        event.registerEntityRenderer(ICEBERG_LETTUCE.get(), IcebergLettuceRender::new);
        event.registerEntityRenderer(BONK_CHOY.get(), BonkChoyRender::new);
    }

    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent ev) {
        PlantType.initPlants();
        ZombieType.initZombies();
        PlantType.postInitPlants();
        ZombieType.postInitZombies();
        //init all plants' attributes.
        for(IPlantType p : PlantType.getPlants()) {
            p.getEntityType().ifPresent(obj -> {
                ev.put(obj, PVZPlantEntity.createPlantAttributes());
            });
        }
        //init all zombies' attributes.
        for(IZombieType z : ZombieType.getZombies()) {
            z.getEntityType().ifPresent(obj -> {
                ev.put(obj, PVZZombieEntity.createZombieAttributes());
            });
        }
        Arrays.asList(
                SUN.get(), COIN.get(), JEWEL.get(), ENERGY.get(),
                CRAZY_DAVE.get(), SUN_DAVE.get(), PANNEY.get(),
                FOODIE_ZOMBIE.get()
        ).forEach(obj -> {
            ev.put(obj, PathfinderMob.createMobAttributes().build());
        });
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(IFactory<T> factory, String name, EntityClassification classification){
        return ENTITY_TYPES.register(name, () -> {return EntityType.Builder.of(factory, classification).build(StringUtil.prefix(name).toString());});
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(IFactory<T> factory, String name, EntityClassification classification, float w, float h){
        return ENTITY_TYPES.register(name, () -> {return EntityType.Builder.of(factory, classification).sized(w, h).build(StringUtil.prefix(name).toString());});
    }

    private static <T extends PVZZombieEntity> RegistryObject<EntityType<T>> registerZombieEntityType(IFactory<T> factory, String name){
        return ENTITY_TYPES.register(name, () -> {return EntityType.Builder.of(factory, PVZEntityClassifications.PVZ_ZOMBIE).fireImmune().build(StringUtil.prefix(name).toString());});
    }

    private static <T extends PVZPlantEntity> RegistryObject<EntityType<T>> registerPlantEntityType(IFactory<T> factory, String name){
        return ENTITY_TYPES.register(name, () -> {return EntityType.Builder.of(factory, PVZEntityClassifications.PVZ_PLANT).build(StringUtil.prefix(name).toString());});
    }

}
