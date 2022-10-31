package cn.evolvefield.mods.pvz.common.impl.plant;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.PVZAPI;
import cn.evolvefield.mods.pvz.api.interfaces.paz.IPlantEntity;
import cn.evolvefield.mods.pvz.api.interfaces.paz.IPlantInfo;
import cn.evolvefield.mods.pvz.api.interfaces.paz.IPlantModel;
import cn.evolvefield.mods.pvz.api.interfaces.types.*;
import cn.evolvefield.mods.pvz.common.impl.*;
import cn.evolvefield.mods.pvz.utils.StringUtil;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 18:34
 * Description:
 */
public abstract class PlantType extends PAZType implements IPlantType {

    /* all registered plants */
    private static final List<IPlantType> PLANTS = new ArrayList<>();
    /* category -> plant type list */
    private static final Map<String, List<IPlantType>> CATEGORY_MAP = new HashMap<>();
    /* used to confirm no duplicate */
    private static final Set<IPlantType> PLANT_SET = new HashSet<>();
    /* plant type -> unique id.(dynamic each loading) */
    private static final Map<IPlantType, Integer> BY_ID = new HashMap<>();
    /* get type by name */
    private static final Map<String, IPlantType> BY_NAME = new HashMap<>();
    /* entity type -> plant type */
    private static final Map<EntityType<? extends PathfinderMob>, IPlantType> BY_ENTITY_TYPE = new HashMap<>();
    /* other data */
    protected IEssenceType plantEssence = EssenceTypes.APPEASE;
    protected Supplier<IPlantModel<? extends IPlantEntity>> plantModelSupplier;
    protected Supplier<IPlantType> upgradeFrom;
    protected Supplier<IPlantType> upgradeTo;
    protected Supplier<Block> plantBlock;
    protected Supplier<IPlantInfo> outerPlant;
    protected ICardPlacement cardPlacement = Placements.COMMON;
    protected boolean isShroomPlant;
    protected boolean isWaterPlant;

    protected PlantType(String name, PlantFeatures features) {
        super(name);
        // common.
        this.sunCost = features.sunCost;
        this.requiredLevel = features.requiredLevel;
        this.xpPoint = features.xpPoint;
        this.renderScale = features.renderScale;
        this.coolDown = features.coolDown;
        this.rankType = features.rankType;
        this.lootTable = features.lootTable;
        this.entitySup = features.entitySup;
        this.summonCardSup = features.summonCardSup;
        this.enjoyCardSup = features.enjoyCardSup;
        this.skills = features.skillTypes;
        // unique.
        this.plantEssence = features.plantEssence;
        this.plantModelSupplier = features.plantModelSupplier;
        this.upgradeFrom = features.upgradeFrom;
        this.upgradeTo = features.upgradeTo;
        this.plantBlock = features.plantBlock;
        this.outerPlant = features.outerPlant;
        this.cardPlacement = features.cardPlacement;
        this.isShroomPlant = features.isShroomPlant;
        this.isWaterPlant = features.isWaterPlant;
        // last.
        this.entityRenderResource = this.genEntityResource();
    }

    @Override
    public IEssenceType getEssence() {
        return this.plantEssence;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Optional<IPlantModel<? extends IPlantEntity>> getPlantModel() {
        return Optional.ofNullable(this.plantModelSupplier.get());
    }

    @Override
    public Optional<IPlantType> getUpgradeFrom() {
        return this.upgradeFrom == null ? Optional.empty() : Optional.ofNullable(this.upgradeFrom.get());
    }

    @Override
    public Optional<IPlantType> getUpgradeTo() {
        return this.upgradeTo == null ? Optional.empty() : Optional.ofNullable(this.upgradeTo.get());
    }

    @Override
    public ICardPlacement getPlacement() {
        return this.cardPlacement;
    }

    @Override
    public boolean isShroomPlant() {
        return this.isShroomPlant;
    }

    @Override
    public Optional<Block> getPlantBlock() {
        return this.plantBlock == null ? Optional.empty() : Optional.ofNullable(this.plantBlock.get());
    }

    @Override
    public boolean isWaterPlant() {
        return this.isWaterPlant;
    }

    @Override
    public boolean isOuterPlant() {
        return this.outerPlant != null;
    }

    @Override
    public Optional<IPlantInfo> getOuterPlant() {
        return Optional.ofNullable(this.outerPlant.get());
    }

    /**
     * the resource to save entity render picture.
     */
    protected ResourceLocation genEntityResource() {
        final String sep = this.getEssence().toString();
        return new ResourceLocation(this.getModID(), "textures/entity/plant/" + sep + "/" + this.toString() + ".png");
    }

    @Override
    public final int getId() {
        if(BY_ID.containsKey(this)) {
            return BY_ID.get(this);
        }
        return 0;
    }

    public static void initPlants() {
        Static.LOGGER.debug("PlantManager : registered " + PLANT_SET.size() + " plants.");
        PAZType.initPAZs(PLANTS, CATEGORY_MAP, BY_ID, BY_NAME);
    }

    /**
     * {@link EntityRegister#addEntityAttributes(net.minecraftforge.event.entity.EntityAttributeCreationEvent)}
     */
    public static void postInitPlants() {
        postInit(PLANTS, BY_ENTITY_TYPE);
    }

    /**
     * {@link PVZAPIImpl#registerPlantType(IPlantType)}
     */
    public static void registerPlant(IPlantType plant) {
        registerPAZType(PLANT_SET, CATEGORY_MAP, plant);
    }

    public static void registerPlants(Collection<IPlantType> plants) {
        plants.forEach(type -> registerPlant(type));
    }

    public static List<IPlantType> getPlants(){
        return Collections.unmodifiableList(PLANTS);
    }

    public static Optional<IPlantType> getPlantByName(String name){
        if(BY_NAME.containsKey(name)) {
            return Optional.ofNullable(BY_NAME.get(name));
        }
        return Optional.empty();
    }

    public static int size(){
        return PLANTS.size();
    }

    /* modify methods */

    private void sunCost(int cost) {
        this.sunCost = cost;
    }

    private void requiredLevel(int lvl) {
        this.requiredLevel = lvl;
    }

    private void cd(ICoolDown cd) {
        this.coolDown = cd;
    }

    /* builder */

    public static final class PlantFeatures{
        //common.
        protected int sunCost = 9999;
        protected int requiredLevel = 100;
        protected int xpPoint = 0;
        protected float renderScale = 0.5F;
        protected ICoolDown coolDown = CoolDowns.DEFAULT;
        protected IRankType rankType = RankTypes.GRAY;
        protected ResourceLocation entityRenderResource;
        protected ResourceLocation lootTable;
        protected Supplier<EntityType<? extends PathfinderMob>> entitySup;
        protected Supplier<? extends Item> summonCardSup;
        protected Supplier<? extends Item> enjoyCardSup;
        protected List<ISkillType> skillTypes = new ArrayList<>();
        //unique.
        protected IEssenceType plantEssence = EssenceTypes.APPEASE;
        protected Supplier<IPlantModel<? extends IPlantEntity>> plantModelSupplier;
        protected Supplier<IPlantType> upgradeFrom;
        protected Supplier<IPlantType> upgradeTo;
        protected Supplier<Block> plantBlock;
        protected Supplier<IPlantInfo> outerPlant;
        protected ICardPlacement cardPlacement = Placements.COMMON;
        protected boolean isShroomPlant;
        protected boolean isWaterPlant;

        public PlantFeatures cost(int cost) {
            this.sunCost = cost;
            return this;
        }

        public PlantFeatures requiredLevel(int lvl) {
            this.requiredLevel = lvl;
            return this;
        }

        public PlantFeatures xp(int point) {
            this.xpPoint = point;
            return this;
        }

        public PlantFeatures cd(ICoolDown cd) {
            this.coolDown = cd;
            return this;
        }

        public PlantFeatures essence(IEssenceType e) {
            this.plantEssence = e;
            return this;
        }

        public PlantFeatures rank(IRankType r) {
            this.rankType = r;
            return this;
        }

        public PlantFeatures entityType(Supplier<EntityType<? extends PathfinderMob>> sup) {
            this.entitySup = sup;
            return this;
        }

        public PlantFeatures summonCard(Supplier<? extends Item> sup) {
            this.summonCardSup = sup;
            return this;
        }

        public PlantFeatures enjoyCard(Supplier<? extends Item> sup) {
            this.enjoyCardSup = sup;
            return this;
        }

        public PlantFeatures skill(Collection<ISkillType> skills){
            this.skillTypes.addAll(skills);
            return this;
        }

        public PlantFeatures cdSkill(Collection<ISkillType> skills){
            this.skillTypes.addAll(Arrays.asList(SkillTypes.FAST_CD));
            return this.skill(skills);
        }

        public PlantFeatures commonSkill(Collection<ISkillType> skills){
            this.skillTypes.addAll(Arrays.asList(SkillTypes.PLANT_MORE_LIFE, SkillTypes.FAST_CD));
            return this.skill(skills);
        }

        public PlantFeatures commonSunSkill(Collection<ISkillType> skills){
            this.skillTypes.addAll(Arrays.asList(SkillTypes.LESS_SUN));
            return this.commonSkill(skills);
        }

        public PlantFeatures plantModel(Supplier<Callable<IPlantModel<? extends IPlantEntity>>> sup) {
            this.plantModelSupplier = () -> DistExecutor.unsafeCallWhenOn(Dist.CLIENT, sup);
            return this;
        }

        public PlantFeatures scale(float scale) {
            this.renderScale = scale;
            return this;
        }

        public PlantFeatures upgradeFrom(Supplier<IPlantType> sup) {
            this.upgradeFrom = sup;
            return this;
        }

        public PlantFeatures upgradeTo(Supplier<IPlantType> sup) {
            this.upgradeTo = sup;
            return this;
        }

        public PlantFeatures placement(ICardPlacement placement) {
            this.cardPlacement = placement;
            return this;
        }

        public PlantFeatures loot(ResourceLocation loot){
            this.lootTable = loot;
            return this;
        }

        public PlantFeatures plantBlock(Supplier<Block> plantBlock) {
            this.plantBlock = plantBlock;
            return this;
        }

        public PlantFeatures outerPlant(Supplier<IPlantInfo> info) {
            this.outerPlant = info;
            this.cardPlacement = Placements.NONE;
            return this;
        }

        public PlantFeatures isShroomPlant() {
            this.isShroomPlant = true;
            this.cardPlacement = Placements.SHROOM;
            return this;
        }

        public PlantFeatures isWaterPlant() {
            this.isWaterPlant = true;
            this.cardPlacement = Placements.NONE;
            return this;
        }

    }

    public static class PlantTypeLoader extends SimpleJsonResourceReloadListener {

        public static final String NAME = "plant";
        private static final Gson GSON = (new GsonBuilder()).create();

        public PlantTypeLoader() {
            super(GSON, NAME + "s");
        }


        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
            map.forEach((res, jsonElement) -> {
                try {
                    JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, NAME);
                    jsonObject.entrySet().forEach(entry -> {
                        //find modId.
                        final String modId = entry.getKey();
                        final JsonElement element = entry.getValue();
                        if(element.isJsonObject()){
                            element.getAsJsonObject().entrySet().forEach(entry1 -> {
                                //find plant type identification.
                                final String name = entry1.getKey();
                                final JsonElement element1 = entry1.getValue();
                                final Optional<IPlantType> opt = PVZAPI.get().getPlantTypeByID(modId + ":" + name);
                                if(opt.get() instanceof PlantType && element1.isJsonObject()){
                                    //modify attributes.
                                    final JsonObject obj = element1.getAsJsonObject();
                                    final PlantType type = (PlantType) opt.get();
                                    //sun amount.
                                    final int sunCost = GsonHelper.getAsInt(obj, StringUtil.JSON_SUN_COST, -1);
                                    if(sunCost >= 0){
                                        type.sunCost(sunCost);
                                    }
                                    //cool down.
                                    final String coolDown = GsonHelper.getAsString(obj, StringUtil.JSON_COOL_DOWN, "");
                                    final ICoolDown cd = CoolDowns.getCDByName(coolDown);
                                    if(cd != null){
                                        type.cd(cd);
                                    }
                                    //required level.
                                    final int requiredLevel = GsonHelper.getAsInt(obj, StringUtil.JSON_REQUIRE_LEVEL, 0);
                                    if(requiredLevel > 0){
                                        type.requiredLevel(requiredLevel);
                                    }
                                } else{
                                    Static.LOGGER.error("Skipping loading {}:{} in {}", modId, name, res);
                                }
                            });
                        } else{
                            Static.LOGGER.error("Skipping loading {} in {}", modId, res);
                        }
                    });
                } catch (IllegalArgumentException | JsonParseException e) {
                    Static.LOGGER.error("Parsing error loading custom raid {}: {}", res, e.getMessage());
                }
            });

            Static.LOGGER.info("Loaded {} custom plants' information", map.size());


        }
    }
}
