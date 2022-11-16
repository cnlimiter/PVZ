package cn.evolvefield.mods.pvz.common.entity.plant.assist;

import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.misc.drop.CoinEntity;
import cn.evolvefield.mods.pvz.common.entity.misc.drop.DropEntity;
import cn.evolvefield.mods.pvz.common.entity.misc.drop.JewelEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GoldMagnetEntity extends PVZPlantEntity {

	private final Set<DropEntity> coinSet = new HashSet<>();
	private static final int SEARCH_CD = 60;

	public GoldMagnetEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if (! level.isClientSide) {
			this.tickCoinSet();
			this.setAttackTime(this.coinSet.size());
		}
	}

	private void tickCoinSet() {
		// maintain the set. keep absorbed exist coin.
		Set<DropEntity> tmp = new HashSet<>();
		this.coinSet.forEach(coin -> {
			if (EntityUtil.isEntityValid(coin) && coin.getDropState() == DropEntity.DropStates.ABSORB) {
				tmp.add(coin);
			}
		});
		this.coinSet.clear();
		this.coinSet.addAll(tmp);
		tmp.clear();
		// if it can not work, release the coins.
		if (! this.checkCanWorkNow()) {
			this.coinSet.forEach(coin -> {
				coin.setDropState(DropEntity.DropStates.NORMAL);
			});
			this.coinSet.clear();
			return;
		}
		// find new coin regularly.
		if (this.getExistTick() % SEARCH_CD == 0) {
			final float range = this.getSearchRange();
			level.getEntitiesOfClass(DropEntity.class, EntityUtil.getEntityAABB(this, range, range), drop -> {
				return (drop instanceof CoinEntity || drop instanceof JewelEntity) && drop.getDropState() == DropEntity.DropStates.NORMAL && ! this.coinSet.contains(drop);
			}).forEach(coin -> {
				coin.setDropState(DropEntity.DropStates.ABSORB);
				this.coinSet.add(coin);
			});
			if(! this.coinSet.isEmpty()) {
				EntityUtil.playSound(this, SoundRegister.MAGNET.get());
			}
		}
		// absorb all coins in the set.
		this.coinSet.forEach(coin -> {
			final double speed = 0.35D;
			var now = new Vec3(this.getX(), this.getY() + this.getBbHeight(), this.getZ());
			var vec = now.subtract(coin.position());
			if (vec.length() <= 1) {
				this.onCollectCoin(coin);
			} else {
				coin.setDeltaMovement(vec.normalize().scale(speed));
			}
		});
	}

	/**
	 * {@link #tickCoinSet()}
	 */
	protected void onCollectCoin(DropEntity drop) {
		this.getOwnerPlayer().ifPresent(player -> {
			if(drop instanceof CoinEntity) {
				PlayerUtil.addResource(player, Resources.MONEY, drop.getAmount());
				EntityUtil.playSound(this, SoundRegister.COIN_PICK.get());
			} else if(drop instanceof JewelEntity) {
				PlayerUtil.addResource(player, Resources.GEM_NUM, drop.getAmount());
				EntityUtil.playSound(this, SoundRegister.JEWEL_PICK.get());
			}
		});
		drop.remove(RemovalReason.KILLED);
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.add(Pair.of(PAZAlmanacs.WORK_RANGE, this.getSearchRange()));
	}

	public int getSearchRange() {
		return 8;
	}

	protected boolean checkCanWorkNow() {
		return this.getOwnerPlayer().isPresent();
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.5f, 1.3f);
	}

	@Override
	public int getSuperTimeLength() {
		return 0;
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.GOLD_MAGNET;
	}

}
