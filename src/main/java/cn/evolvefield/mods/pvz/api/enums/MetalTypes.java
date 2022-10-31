package cn.evolvefield.mods.pvz.api.enums;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * Project: PVZ-fabric
 * Author: cnlimiter
 * Date: 2022/10/30 20:13
 * Description:僵尸所穿的装备种类
 */
public enum MetalTypes {
    EMPTY,
    BUCKET_HEAD,
    SCREEN_DOOR,
    FOOTBALL_HELMET,
    GIGA_HELMET,
    JACK_BOX,
    IRON_PICKAXE,
    POGO,
    LADDER;

    public static Item getMetalItem(MetalTypes type) {
        return switch (type) {
            case BUCKET_HEAD -> ItemRegister.BUCKET_HEAD.get();
            case SCREEN_DOOR -> ItemRegister.SCREEN_DOOR.get();
            case FOOTBALL_HELMET -> ItemRegister.FOOTBALL_HELMET.get();
            case GIGA_HELMET -> ItemRegister.GIGA_HELMET.get();
            case JACK_BOX -> ItemRegister.JACK_BOX.get();
            case IRON_PICKAXE -> Items.IRON_PICKAXE;
            case LADDER -> BlockRegister.STEEL_LADDER.get().asItem();
            default -> Items.IRON_INGOT;
        };
    }
}
