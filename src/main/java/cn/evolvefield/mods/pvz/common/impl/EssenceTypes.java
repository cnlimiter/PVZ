package cn.evolvefield.mods.pvz.common.impl;

import cn.evolvefield.mods.pvz.api.PVZAPI;
import cn.evolvefield.mods.pvz.api.interfaces.types.IEssenceType;
import cn.evolvefield.mods.pvz.init.misc.PVZBlockTags;
import cn.evolvefield.mods.pvz.init.registry.BlockRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 18:12
 * Description:
 */
public class EssenceTypes {
    private static final List<IEssenceType> ESSENCES = new ArrayList<>();

    /**
     * {@link PVZAPIImpl#registerEssenceType(IEssenceType)}
     */
    public static void registerEssence(IEssenceType e){
        if(! ESSENCES.contains(e)){
            ESSENCES.add(e);
        }
    }

    public static List<IEssenceType> getEssences(){
        return EssenceTypes.ESSENCES;
    }

    public static final IEssenceType ORIGIN = new EssenceType(
            "origin",
            ItemRegister.ORIGIN_ESSENCE::get,
            () -> BlockRegister.ORIGIN_ORE.get(),
            () -> null,
            null
    );

    public static final IEssenceType APPEASE = new EssenceType(
            "appease",
            () -> ItemRegister.APPEASE_ESSENCE.get(),
            () -> BlockRegister.APPEASE_ORE.get(),
            () -> Blocks.GRASS_BLOCK,
            PVZBlockTags.TO_APPEASE_ORES
    );

    public static final IEssenceType LIGHT = new EssenceType(
            "light",
            () -> ItemRegister.LIGHT_ESSENCE.get(),
            () -> BlockRegister.LIGHT_ORE.get(),
            () -> Blocks.GLOWSTONE,
            PVZBlockTags.TO_LIGHT_ORES
    );

    public static final IEssenceType EXPLOSION = new EssenceType(
            "explosion",
            () -> ItemRegister.EXPLOSION_ESSENCE.get(),
            () -> BlockRegister.EXPLOSION_ORE.get(),
            () -> Blocks.REDSTONE_BLOCK,
            PVZBlockTags.TO_EXPLOSION_ORES
    );

    public static final IEssenceType DEFENCE = new EssenceType(
            "defence",
            () -> ItemRegister.DEFENCE_ESSENCE.get(),
            () -> BlockRegister.DEFENCE_ORE.get(),
            () -> Blocks.GRANITE,
            PVZBlockTags.TO_DEFENCE_ORES
    );

    public static final IEssenceType ICE = new EssenceType(
            "ice",
            () -> ItemRegister.ICE_ESSENCE.get(),
            () -> BlockRegister.ICE_ORE.get(),
            () -> Blocks.BLUE_ICE,
            PVZBlockTags.TO_ICE_ORES
    );

    public static final IEssenceType ENFORCE = new EssenceType(
            "enforce",
            () -> ItemRegister.ENFORCE_ESSENCE.get(),
            () -> BlockRegister.ENFORCE_ORE.get(),
            () -> Blocks.ANDESITE,
            PVZBlockTags.TO_ENFORCE_ORES
    );

    public static final IEssenceType TOXIC = new EssenceType(
            "toxic",
            () -> ItemRegister.TOXIC_ESSENCE.get(),
            () -> BlockRegister.TOXIC_ORE.get(),
            () -> Blocks.MYCELIUM,
            PVZBlockTags.TO_TOXIC_ORES
    );

    public static final IEssenceType ASSIST = new EssenceType(
            "assist",
            () -> ItemRegister.ASSIST_ESSENCE.get(),
            () -> BlockRegister.ASSIST_ORE.get(),
            () -> Blocks.DIORITE,
            PVZBlockTags.TO_ASSIST_ORES
    );

    public static final IEssenceType MAGIC = new EssenceType(
            "magic",
            () -> ItemRegister.MAGIC_ESSENCE.get(),
            () -> BlockRegister.MAGIC_ORE.get(),
            () -> Blocks.SOUL_SAND,
            PVZBlockTags.TO_MAGIC_ORES
    );

    public static final IEssenceType FLAME = new EssenceType(
            "flame",
            () -> ItemRegister.FLAME_ESSENCE.get(),
            () -> BlockRegister.FLAME_ORE.get(),
            () -> Blocks.MAGMA_BLOCK,
            PVZBlockTags.TO_FLAME_ORES
    );

    public static final IEssenceType SPEAR = new EssenceType(
            "spear",
            () -> ItemRegister.SPEAR_ESSENCE.get(),
            () -> BlockRegister.SPEAR_ORE.get(),
            () -> Blocks.GRAVEL,
            PVZBlockTags.TO_SPEAR_ORES
    );

    public static final IEssenceType ARMA = new EssenceType(
            "arma",
            () -> ItemRegister.ARMA_ESSENCE.get(),
            () -> BlockRegister.ARMA_ORE.get(),
            () -> Blocks.SANDSTONE,
            PVZBlockTags.TO_ARMA_ORES
    );

    public static final IEssenceType ELECTRIC = new EssenceType(
            "electric",
            () -> ItemRegister.ELECTRIC_ESSENCE.get(),
            () -> BlockRegister.ELECTRIC_ORE.get(),
            () -> null,
            PVZBlockTags.TO_ELECTRIC_ORES
    );

    public static final IEssenceType SHADOW = new EssenceType(
            "shadow",
            () -> ItemRegister.SHADOW_ESSENCE.get(),
            () -> BlockRegister.SHADOW_ORE.get(),
            () -> null,
            PVZBlockTags.TO_SHADOW_ORES
    );

    public static class EssenceType implements IEssenceType{

        private static final List<IEssenceType> ESSENCE_TYPES = new ArrayList<>();
        private final Supplier<Item> itemSupplier;
        private final Supplier<Block> oreSupplier;
        private final Supplier<Block> blockSupplier;
        private final TagKey<Block> essenceTag;
        private final String essenceName;

        public EssenceType(@Nonnull String name, @Nonnull Supplier<Item> itemSup, @Nonnull Supplier<Block> oreSup, @Nonnull Supplier<Block> blockSup, TagKey<Block> tag) {
            this.essenceName = name;
            this.itemSupplier = itemSup;
            this.oreSupplier = oreSup;
            this.blockSupplier = blockSup;
            this.essenceTag = tag;
            ESSENCE_TYPES.add(this);
        }

        /**
         * {@link CoreRegister#register()}
         */
        public static void register(){
            PVZAPI.get().registerEssenceTypes(ESSENCE_TYPES);
        }

        public Item getEssenceItem() {
            return this.itemSupplier.get();
        }

        @Override
        public Block getEssenceOre() {
            return this.oreSupplier.get();
        }

        @Override
        public Optional<Block> getRadiationBlock() {
            return Optional.ofNullable(this.blockSupplier.get());
        }

        public Optional<TagKey<Block>> getRadiationBlockTag() {
            return Optional.ofNullable(this.essenceTag);
        }

        @Override
        public String toString() {
            return this.essenceName;
        }
    }

}
