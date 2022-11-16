package cn.evolvefield.mods.pvz.init.event.handler;

import cn.evolvefield.mods.pvz.init.config.PVZConfig;
import cn.evolvefield.mods.pvz.init.registry.BlockRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;

public class BlockEventHandler {

	/**
	 * trigger endermans around when dig amethyst ore.
	 */
	public static void triggerAmethystAround(BlockEvent.BreakEvent ev){
		if(! ev.getLevel().isClientSide() && ev.getState().getBlock().equals(BlockRegister.AMETHYST_ORE.get()) && PlayerUtil.isValidPlayer(ev.getPlayer())){
			final float range = 10;
			final var aabb = EntityUtil.getEntityAABB(ev.getPlayer(), range, range);
			EntityUtil.getPredicateEntities(ev.getPlayer(), aabb, EnderMan.class, (e) -> true).forEach(enderman ->{
				if(ev.getLevel().getRandom().nextFloat() < 0.4){
					enderman.setTarget(ev.getPlayer());
				}
			});
		}
	}

	public static void checkAndDropSeeds(BlockEvent.BreakEvent ev) {
		Player player = ev.getPlayer();
		BlockState state = ev.getState();
		BlockPos pos = ev.getPos();
		if(! player.level.isClientSide) {
			if(state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.TALL_GRASS) {//break grass
				if(player.getRandom().nextDouble() < PVZConfig.COMMON_CONFIG.BlockSettings.PeaDropChance.get()) {
					player.level.addFreshEntity(new ItemEntity(player.level,pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemRegister.PEA.get(), 1)));
				} else if(player.getRandom().nextDouble() < PVZConfig.COMMON_CONFIG.BlockSettings.CabbageDropChance.get()) {
					player.level.addFreshEntity(new ItemEntity(player.level,pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemRegister.CABBAGE_SEEDS.get(), 1)));
				}
			}
		}
	}
}
