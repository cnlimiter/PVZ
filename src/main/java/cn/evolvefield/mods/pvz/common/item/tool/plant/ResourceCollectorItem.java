package cn.evolvefield.mods.pvz.common.item.tool.plant;

import cn.evolvefield.mods.pvz.api.interfaces.base.ICollectible;
import cn.evolvefield.mods.pvz.common.enchantment.misc.RangeReachEnchantment;
import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class ResourceCollectorItem extends Item {

	private static final int SINGLE_COLLECT_COOL_DOWN = 8;
	private static final int RANGE_COLLECT_COOL_DOWN = 200;
	private static final int SINGLE_COLLECT_RANGE = 25;
	private static final int RANGE_COLLECT_RANGE = 5;

	public ResourceCollectorItem() {
		super(new Properties().tab(PVZItemGroups.PVZ_USEFUL).stacksTo(1).durability(1200));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		if(! worldIn.isClientSide) {
			final ItemStack stack = playerIn.getItemInHand(handIn);
			if(playerIn.isShiftKeyDown()) {//range collect
				final float range = RangeReachEnchantment.getReachDistance(stack, RANGE_COLLECT_RANGE);
				final List<Entity> list = EntityUtil.getPredicateEntities(playerIn, EntityUtil.getEntityAABB(playerIn, range, range), Entity.class, e -> {
					return e instanceof ICollectible && ((ICollectible) e).canCollectBy(playerIn);
				});
				if(! list.isEmpty()){
					playerIn.awardStat(Stats.ITEM_USED.get(this));
					playerIn.getCooldowns().addCooldown(this, RANGE_COLLECT_COOL_DOWN);
				} else{
					playerIn.getCooldowns().addCooldown(this, SINGLE_COLLECT_COOL_DOWN);
				}
				list.forEach(e -> {
					((ICollectible) e).onCollect(playerIn);
				});
			} else {
				final var entityRay = EntityUtil.rayTraceEntities(worldIn, playerIn, playerIn.getLookAngle(), RangeReachEnchantment.getReachDistance(stack, SINGLE_COLLECT_RANGE), e -> e instanceof ICollectible);
				if(entityRay != null && entityRay.getType() == HitResult.Type.ENTITY) {
					if(entityRay.getEntity() instanceof ICollectible) {
						((ICollectible) entityRay.getEntity()).onCollect(playerIn);
						playerIn.awardStat(Stats.ITEM_USED.get(this));
						playerIn.getCooldowns().addCooldown(this, SINGLE_COLLECT_COOL_DOWN);
					}
				}
			}
		}
		return super.use(worldIn, playerIn, handIn);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(Component.translatable("tooltip.pvz.resource_collector.use").withStyle(ChatFormatting.YELLOW));
		tooltip.add(Component.translatable("tooltip.pvz.resource_collector.info", (int) RangeReachEnchantment.getReachDistance(stack, SINGLE_COLLECT_RANGE), (int) RangeReachEnchantment.getReachDistance(stack, RANGE_COLLECT_RANGE)).withStyle(ChatFormatting.GREEN));
	}

}
