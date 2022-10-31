package cn.evolvefield.mods.pvz.common.item.spawn.card;

import cn.evolvefield.mods.pvz.api.interfaces.types.ICoolDown;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPAZType;
import cn.evolvefield.mods.pvz.common.impl.CoolDowns;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.item.PVZRarity;
import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import cn.evolvefield.mods.pvz.utils.StringUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.List;

public abstract class SummonCardItem extends Item {

	public final IPAZType type;
	public final boolean isEnjoyCard;

	public SummonCardItem(IPAZType type, boolean isEnjoyCard) {
		this(new Properties().tab(PVZItemGroups.PVZ_PLANT_CARD).stacksTo(isEnjoyCard ? 16 : 1), type, isEnjoyCard);
	}

	public SummonCardItem(Properties properties, IPAZType type, boolean isEnjoyCard) {
		super(properties);
		this.type = type;
		this.isEnjoyCard = isEnjoyCard;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return ! this.isEnjoyCard && super.canApplyAtEnchantingTable(stack, enchantment);//enjoy card have no enchant
	}

	/**
	 * get base sun cost.
	 * {@link #appendHoverText(ItemStack, World, List, ITooltipFlag)}
	 */
	public static int getCardSunCost(ItemStack stack) {
		if(stack.getItem() instanceof PlantCardItem) {
			return ((PlantCardItem) stack.getItem()).getBasisSunCost(stack);
		}
		return 1;
	}

	/**
	 * get base card cd.
	 * {@link #appendHoverText(ItemStack, World, List, ITooltipFlag)}
	 */
	public static ICoolDown getCardCoolDown(ItemStack stack) {
		if(stack.getItem() instanceof PlantCardItem) {
			return ((PlantCardItem) stack.getItem()).getBasisCoolDown(stack);
		}
		return CoolDowns.DEFAULT;
	}

	/**
	 * {@link #appendHoverText(ItemStack, World, List, ITooltipFlag)}
	 */
	public static int getCardRequiredLevel(ItemStack stack) {
		if(stack.getItem() instanceof PlantCardItem) {
			return ((PlantCardItem) stack.getItem()).plantType.getRequiredLevel();
		}
		return 100;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(Component.translatable("tooltip.pvz.card_sun_cost", getCardSunCost(stack)).withStyle(ChatFormatting.YELLOW));
		tooltip.add(Component.translatable("tooltip.pvz.card_cd", Component.translatable(getCardCoolDown(stack).getTranslateKey()).getString()).withStyle(TextFormatting.AQUA));
		PlayerUtil.getOptManager(PVZMod.PROXY.getPlayer()).ifPresent(m -> {
			//this paz type is locked.
			if (m.isPAZLocked(this.type) && ! this.isEnjoyCard) {
				tooltip.add(Component.translatable("tooltip.pvz.card_required_level", getCardRequiredLevel(stack)).withStyle(ChatFormatting.RED));
			}
		});
	}

	public static void appendSkillToolTips(ItemStack stack, List<Component> tooltip){
		if(stack.getItem() instanceof SummonCardItem){
			final IPAZType type = ((SummonCardItem) stack.getItem()).type;
			type.getSkills().forEach(skill -> {
				final int lvl = SkillTypes.getSkillLevel(stack, skill);
				if(lvl > 0){
					tooltip.add(skill.getText().append(StringUtil.getRomanString(lvl)).withStyle(ChatFormatting.DARK_PURPLE));
				}
			});
		}
	}

	@Override
	public Rarity getRarity(ItemStack itemStack) {
		if(itemStack.getItem() instanceof SummonCardItem){
	    	return PVZRarity.getRarityByRank(((SummonCardItem) itemStack.getItem()).type.getRank());
		}
		return super.getRarity(itemStack);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return this.getMaxStackSize(stack) == 1 && ! this.isEnjoyCard;
	}



	@Override
	public int getEnchantmentValue(ItemStack stack) {
		return 20;
	}

	@Override
	public int getEnchantmentValue() {
		return 20;//0 ~ 45
	}

	public void notifyPlayerAndCD(Player player, ItemStack stack, PlacementErrors error) {
		this.notifyPlayerAndCD(player, stack, error, 0);
	}

	/**
	 * send helpful info.
	 */
	public void notifyPlayerAndCD(Player player, ItemStack stack, PlacementErrors error, int arg) {
		if(! player.level.isClientSide) {
			PlayerUtil.sendMsgTo(player, error.getTextByArg(arg, ChatFormatting.RED));
			PlayerUtil.setItemStackCD(player, stack, 10);
			PlayerUtil.playClientSound(player, SoundRegister.NO.get());
		}
	}

	protected enum PlacementErrors{
		SUN_ERROR("sun"),
		CD_ERROR("cd"),
		LOCK_ERROR("lock"),
		UPGRADE_ERROR("upgrade"),
		GROUND_ERROR("ground"),
		OUTER_ERROR("outer"),
		OUTER_FULL("outer_full");

		private final String info;

		PlacementErrors(String s){
			this.info = s;
		}

		public MutableComponent getTextByArg(int arg, ChatFormatting color){
			return Component.translatable("help.pvz."+ this.info, arg).withStyle(color);
		}
	}

}
