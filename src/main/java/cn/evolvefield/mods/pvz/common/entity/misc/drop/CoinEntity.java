package cn.evolvefield.mods.pvz.common.entity.misc.drop;

import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.init.config.PVZConfig;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CoinEntity extends DropEntity {

	public static final int COIN_TYPES = 3;

	public CoinEntity(EntityType<? extends Mob> type, Level worldIn) {
		super(type, worldIn);
		this.setAmountByType(this.getRandomType());
	}

	@Override
	protected void onDropped() {
		super.onDropped();
		if (!level.isClientSide) {
			EntityUtil.playSound(this, SoundRegister.COIN_DROP.get());
		}
	}

	@Override
	public void onCollectedByPlayer(Player player) {
		if (! this.level.isClientSide) {
			PlayerUtil.addResource(player, Resources.MONEY, this.getAmount());
			PlayerUtil.playClientSound(player, SoundRegister.COIN_PICK.get());
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		// 1000 <-> 1 1 <-> 0.5
		final float t = (float) Math.log10(this.getAmount());// 0 1 2 3
		final float w = t * 0.18f + 0.4f;
		return new EntityDimensions(w, w, false); // max (0.4w,0.4h) min(0.9w,0.9h)
	}

	/**
	 * spawn coin by type.
	 */
	public static void spawnCoin(Level world, BlockPos pos, CoinType type) {
		final CoinEntity coin = EntityRegister.COIN.get().create(world);
		coin.setAmount(type.money);
		EntityUtil.onEntitySpawn(world, coin, pos);
	}

	public void setAmountByType(CoinType type) {
		this.setAmount(type.money);
	}

	protected CoinType getRandomType() {
		return CoinType.values()[this.random.nextInt(COIN_TYPES)];
	}

	@Override
	protected int getMaxLiveTick() {
		return PVZConfig.COMMON_CONFIG.EntitySettings.EntityLiveTick.CoinLiveTick.get();
	}

	public static enum CoinType {
		COPPER(1), SILVER(10), GOLD(100);

		public final int money;

		private CoinType(int money) {
			this.money = money;
		}
	}

}
