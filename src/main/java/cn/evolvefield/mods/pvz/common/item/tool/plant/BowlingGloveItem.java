package cn.evolvefield.mods.pvz.common.item.tool.plant;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * TODO bowling glove
 * @author 86152
 *
 */
public class BowlingGloveItem extends Item {

	public static final String BOWLING_STRING = "bowling_type";
	private static final Map<String, BowlingType> BOWLINGS = new HashMap<>();

	static {
		registerBowling(PVZPlants.WALL_NUT, () -> EntityRegister.WALL_NUT_BOWLING.get(), 1F);
	}

	public BowlingGloveItem() {
		super(new Properties().tab(PVZItemGroups.PVZ_USEFUL).rarity(Rarity.UNCOMMON).defaultDurability(666).setISTER(() -> BowlingGloveISTER::new));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		final var world = context.getLevel();
		final var player = context.getPlayer();
		final var hand = context.getHand();
		final var stack = player.getItemInHand(hand);
		final var pos = context.getClickedPos();
		Optional<BowlingType> type = getBowlingType(stack);
		if(! type.isPresent()) {
			if(! world.isClientSide) {
				PlayerUtil.sendMsgTo(player, Component.translatable("help.pvz.bowling_glove.empty").withStyle(ChatFormatting.RED));
				player.getCooldowns().addCooldown(this, 20);
			}
			return InteractionResult.FAIL;
		}

		var spawnPos = pos;
		if (!world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()) {
			spawnPos = pos.relative(context.getClickedFace());
		}
		if (context.getClickedFace() == Direction.UP && world.isEmptyBlock(pos.above())) {// can plant here
			final EntityType<? extends Entity> entityType = type.get().getEntity();
			if (entityType == null) {
				Static.LOGGER.error("BowlingGloveItem Error : no such bowling entity !");
				return InteractionResult.FAIL;
			}
			if(! world.isClientSide) {
				final Entity entity = entityType.spawn((ServerLevel) player.level, stack, player, spawnPos, MobSpawnType.SPAWN_EGG, true, true);
			    if (entity == null || ! (entity instanceof AbstractBowlingEntity)) {
			    	Static.LOGGER.error("BowlingGloveItem Error : bowling entity spawn error !");
				    return InteractionResult.FAIL;
			    }
			    ((AbstractBowlingEntity) entity).summonByOwner(player);
			    ((AbstractBowlingEntity) entity).shoot(player);
			    if (PlayerUtil.isPlayerSurvival(player)) {// reset
				    setEmpty(stack);
			    }
			    if(PlayerUtil.isPlayerSurvival(player)) {
			    	player.getCooldowns().addCooldown(this, 100);
			    	stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
				}
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	public static void onPickUp(PlayerInteractEvent.EntityInteractSpecific ev) {
//		if(ev.getItemStack().getItem().equals(ItemRegister.BOWLING_GLOVE.get())) {
//			if(ev.getTarget() instanceof PVZPlantEntity) {
//			    final PVZPlantEntity plantEntity = (PVZPlantEntity) ev.getTarget();
//			    if(isBowlingPlant(plantEntity)) {
//				    setBowlingType(ev.getItemStack(), plantEntity.getPlantType());
//				    ev.getTarget().remove();
//				    return ;
//			    }
//			}
//		    if(! ev.getSide().isClient()) {
//			    PlayerUtil.sendMsgTo(ev.getPlayer(), Component.translatable("help.pvz.bowling_glove.fail").withStyle(ChatFormatting.RED));
//			    ev.getPlayer().getCooldowns().addCooldown(ev.getItemStack().getItem(), 20);
//		    }
//		}
	}

	/**
	 * get bowling type of certain stack.
	 */
	public static Optional<BowlingType> getBowlingType(ItemStack stack) {
		final String type = stack.getOrCreateTag().getString(BOWLING_STRING);
		if(BOWLINGS.containsKey(type)) {
			return Optional.ofNullable(BOWLINGS.get(type));
		}
		return Optional.empty();
	}

	public static ItemStack setBowlingType(ItemStack stack, IPlantType type) {
		stack.getOrCreateTag().putString(BOWLING_STRING, type.getIdentity());
		return stack;
	}

	public static boolean isBowlingPlant(PVZPlantEntity entity) {
		return BOWLINGS.containsKey(entity.getPlantType().getIdentity());
	}

	public static ItemStack setEmpty(ItemStack stack) {
		stack.getOrCreateTag().putString(BOWLING_STRING, "");
		return stack;
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (this.allowedIn(group)) {
			items.add(new ItemStack(this));
			BOWLINGS.forEach((s, type) -> {
				items.add(setBowlingType(new ItemStack(this), type.getType()));
			});
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		final Optional<BowlingType> plant = getBowlingType(stack);
		if(! plant.isPresent()) {
			tooltip.add(Component.translatable("tooltip.pvz.bowling_glove.empty").withStyle(ChatFormatting.GOLD));
		} else {
			tooltip.add(Component.translatable("tooltip.pvz.bowling_glove.full").withStyle(ChatFormatting.GOLD).append(plant.get().getType().getText().withStyle(ChatFormatting.GREEN)));
		}
	}

	/**
	 * make sure the name of key equals to the entity's registry name who can be pick up by this item.
	 */
	public static void registerBowling(IPlantType type, Supplier<EntityType<? extends Entity>> supplier, float size) {
		BOWLINGS.put(type.getIdentity(), new BowlingType(type, supplier, size));
	}

	public static class BowlingType {

		private final Supplier<EntityType<? extends Entity>> supplier;
		private final IPlantType type;
		private final float renderSize;

		public BowlingType(IPlantType type, Supplier<EntityType<? extends Entity>> supplier, float size) {
			this.supplier = supplier;
			this.type = type;
			this.renderSize = size;
		}

		public EntityType<? extends Entity> getEntity(){
			return this.supplier.get();
		}

		public IPlantType getType() {
			return this.type;
		}

		public float getSize() {
			return this.renderSize;
		}

	}

}
