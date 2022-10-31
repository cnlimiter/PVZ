package cn.evolvefield.mods.pvz.common.impl;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.interfaces.types.ICoolDown;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPAZType;
import cn.evolvefield.mods.pvz.api.interfaces.types.IRankType;
import cn.evolvefield.mods.pvz.api.interfaces.types.ISkillType;
import cn.evolvefield.mods.pvz.utils.AlgorithmUtil;
import cn.evolvefield.mods.pvz.utils.StringUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.Supplier;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 18:25
 * Description:
 */
public abstract class PAZType implements IPAZType {

    private final String name;
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
    protected List<ISkillType> skills;

    protected PAZType(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String getIdentity() {
        return StringUtil.identify(this.getModID(), this.toString());
    }

    @Override
    public MutableComponent getText() {
        return Component.translatable("entity." + this.getModID() + "." + this.toString());
    }

    @Override
    public int getSunCost() {
        return this.sunCost;
    }

    @Override
    public int getRequiredLevel() {
        return this.requiredLevel;
    }

    @Override
    public int getXpPoint() {
        return this.xpPoint;
    }

    @Override
    public ICoolDown getCoolDown() {
        return this.coolDown;
    }

    @Override
    public IRankType getRank() {
        return this.rankType;
    }

    @Override
    public Optional<EntityType<? extends PathfinderMob>> getEntityType() {
        return this.entitySup == null ? Optional.empty() : Optional.ofNullable(this.entitySup.get());
    }

    @Override
    public Optional<? extends Item> getSummonCard() {
        return this.summonCardSup == null ? Optional.empty() : Optional.ofNullable(this.summonCardSup.get());
    }

    @Override
    public Optional<? extends Item> getEnjoyCard() {
        return this.enjoyCardSup == null ? Optional.empty() : Optional.ofNullable(this.enjoyCardSup.get());
    }

    @Override
    public List<ISkillType> getSkills() {
        return skills;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getRenderScale() {
        return this.renderScale;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getRenderResource() {
        return this.entityRenderResource;
    }

    @Override
    public ResourceLocation getLootTable() {
        return lootTable;
    }

    /**
     * register type.
     */
    public static <T extends IPAZType> void registerPAZType(Set<T> set, Map<String, List<T>> map, T plant) {
        if(! set.contains(plant)) {
            set.add(plant);
            if(map.containsKey(plant.getCategoryName())) {
                map.get(plant.getCategoryName()).add(plant);
            } else {
                map.put(plant.getCategoryName(), new ArrayList<>(Arrays.asList(plant)));
            }
        } else {
            Static.LOGGER.warn("PAZTypeRegister : already add {}.", plant.toString());
        }
    }

    /**
     * sort plants or zombies type by priority.
     */
    public static <T extends IPAZType> void initPAZs(List<T> list, Map<String, List<T>> categoryMap, Map<T, Integer> byId, Map<String, T> byName) {
        //clear list.
        list.clear();
        //get priority category list.
        final List<Pair<String, Integer>> categoryList = new ArrayList<>();
        categoryMap.keySet().forEach(l -> {
            final T tmp = categoryMap.get(l).get(0);
            categoryList.add(Pair.of(l, tmp.getSortPriority()));
        });
        //sort category by priority.
        Collections.sort(categoryList, new AlgorithmUtil.PairSorter<>());
        //deal with each category list one by one.
        for(Pair<String, Integer> category : categoryList) {
            //get priority category list.
            final List<Pair<T, Integer>> tmp = new ArrayList<>();
            categoryMap.get(category.getFirst()).forEach(l -> tmp.add(Pair.of(l, l.getSortPriority())));
            //sort list by priority.
            Collections.sort(tmp, new AlgorithmUtil.PairSorter<>());
            Static.LOGGER.debug("PAZTypeRegister : sort category [{}] found {} {}.", category.getFirst(), tmp.size(), "types");
            //add to the final result list.
            tmp.forEach(pair -> list.add(pair.getFirst()));
        }
        for(int i = 0; i < list.size(); ++ i) {
            byId.put(list.get(i), i);
            byName.put(list.get(i).getIdentity(), list.get(i));
        }
    }

    /**
     * to update the map from entity type to type.
     */
    public static <T extends IPAZType> void postInit(List<T> list, Map<EntityType<? extends PathfinderMob>, T>  byEntityType) {
        list.forEach(type -> {
            type.getEntityType().ifPresent(l -> {
                byEntityType.put(l, type);
            });
        });
    }
}
