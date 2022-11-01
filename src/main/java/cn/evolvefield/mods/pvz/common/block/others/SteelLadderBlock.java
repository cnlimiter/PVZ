package cn.evolvefield.mods.pvz.common.block.others;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class SteelLadderBlock extends LadderBlock {

	public static final float MAX_SPEED_UP = 0.5F;
	public static final float UP_SPEED = 0.01F;

	public SteelLadderBlock() {
		super(Properties.copy(Blocks.LADDER).strength(6F).harvestTool(ToolType.PICKAXE));
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockReader, List<Component> textComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, blockReader, textComponents, tooltipFlag);
		textComponents.add(Component.translatable("tooltip.pvz.steel_ladder").withStyle(ChatFormatting.ITALIC));
	}

}
