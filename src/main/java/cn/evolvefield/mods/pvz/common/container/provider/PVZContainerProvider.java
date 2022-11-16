package cn.evolvefield.mods.pvz.common.container.provider;

import cn.evolvefield.mods.pvz.utils.StringUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

/**
 * @program: pvzmod-1.16.5
 * @author: HungTeen
 * @create: 2022-02-07 10:04
 **/
public abstract class PVZContainerProvider implements MenuProvider {

    @Override
    public Component getDisplayName() {
        return StringUtil.EMPTY;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return null;
    }
}
