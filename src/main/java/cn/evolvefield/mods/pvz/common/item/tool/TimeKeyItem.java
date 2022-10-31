package cn.evolvefield.mods.pvz.common.item.tool;

import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * @program: pvzmod-1.16.5
 * @author: HungTeen
 * @create: 2022-02-05 22:03
 **/
public class TimeKeyItem extends Item {

    public TimeKeyItem() {
        super(new Properties().tab(PVZItemGroups.PVZ_USEFUL));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("tooltip.pvz.wait_for_update").withStyle(ChatFormatting.DARK_RED));
    }
}
