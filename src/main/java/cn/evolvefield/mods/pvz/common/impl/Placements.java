package cn.evolvefield.mods.pvz.common.impl;

import cn.evolvefield.mods.pvz.api.interfaces.types.ICardPlacement;
import cn.evolvefield.mods.pvz.init.misc.PVZBlockTags;
import cn.evolvefield.mods.pvz.init.registry.BlockRegister;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 18:28
 * Description:
 */
public class Placements {
    public static final ICardPlacement NONE = block -> false;

    public static final ICardPlacement ANY = block -> true;

    public static final ICardPlacement COMMON = (block) -> {
        return block.defaultBlockState().is(PVZBlockTags.PLANT_SUIT_BLOCKS.registry());
    };

    public static final ICardPlacement STABLE = (block) -> {
        return block.defaultBlockState().is(PVZBlockTags.PLANT_SUIT_BLOCKS) && ! block.defaultBlockState().is(BlockRegister.LILY_PAD.get());
    };

    public static final ICardPlacement GOLD = (block) -> {
        return block.defaultBlockState().is(PVZBlockTags.GOLD_TILES) || block.defaultBlockState().is(Blocks.GOLD_BLOCK);
    };

    public static final ICardPlacement SAND = (block) -> {
        return block.defaultBlockState().is(PVZBlockTags.PLANT_SUIT_BLOCKS) || block.defaultBlockState().is(BlockTags.SAND);
    };

    public static final ICardPlacement SHROOM = (block) -> {
        return block.defaultBlockState().is(PVZBlockTags.PLANT_SUIT_BLOCKS) || block.defaultBlockState().is(Blocks.MYCELIUM);
    };

    public static final ICardPlacement LILY_PAD = (block) -> {
        return block.defaultBlockState().is(BlockRegister.LILY_PAD.get());
    };

    public static final ICardPlacement WATER = (block) -> {
        return block.defaultBlockState().is(Blocks.WATER);
    };

}
