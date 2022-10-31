package cn.evolvefield.mods.pvz.common.item.tool.mc;

import cn.evolvefield.mods.pvz.common.item.PVZItemTier;
import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @program: pvzmod-1.16.5
 * @author: HungTeen
 * @create: 2022-02-03 10:07
 **/
public class OriginShovelItem extends ShovelItem {

    public OriginShovelItem() {
        super(PVZItemTier.ORIGIN, 1.5F, -3.0F, new Properties().tab(PVZItemGroups.PVZ_USEFUL));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> textComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, world, textComponents, tooltipFlag);
        textComponents.add(Component.translatable("tooltip.pvz.origin_shovel").withStyle(ChatFormatting.DARK_GREEN));
    }

}
