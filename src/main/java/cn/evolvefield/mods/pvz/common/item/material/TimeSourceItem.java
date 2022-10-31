package cn.evolvefield.mods.pvz.common.item.material;

import cn.evolvefield.mods.pvz.common.item.PVZMiscItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @program: pvzmod-1.16.5
 * @author: HungTeen
 * @create: 2022-02-05 22:07
 **/
public class TimeSourceItem extends PVZMiscItem {

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> textComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, world, textComponents, tooltipFlag);
        textComponents.add(Component.translatable("tooltip.pvz.time_source").withStyle(ChatFormatting.ITALIC));
        textComponents.add(Component.translatable("tooltip.pvz.wait_for_update").withStyle(ChatFormatting.DARK_RED));
    }
}
