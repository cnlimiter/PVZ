package cn.evolvefield.mods.pvz.common.entity.plant.magic;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.misc.drop.CoinEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantProducerEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
public class MariGoldEntity extends PlantProducerEntity {

	public MariGoldEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public void genSomething() {
		CoinEntity coin = EntityRegister.COIN.get().create(level);
		coin.setAmount(this.getRandomAmount());
		EntityUtil.onEntityRandomPosSpawn(level, coin, blockPosition(), 3);
	}

	protected void genSpecCoin(CoinEntity.CoinType type) {
		CoinEntity coin = EntityRegister.COIN.get().create(level);
		coin.setAmountByType(type);
		EntityUtil.onEntityRandomPosSpawn(level, coin, blockPosition(), 3);
	}

	@Override
	public void genSuper() {
		for (int i = 0; i < this.getSuperGenCnt(); ++i) {
			this.genSomething();
		}
		this.genSpecCoin(CoinEntity.CoinType.GOLD);
	}

	private int getRandomAmount() {
		final int num = this.getRandom().nextInt(100);
		final int silverNum = this.getSilverChance();
		final int goldNum = this.getGoldChance();
		if (num < goldNum) {
			return CoinEntity.CoinType.GOLD.money;
		}
		if (num < silverNum + goldNum) {
			return CoinEntity.CoinType.SILVER.money;
		}
		return CoinEntity.CoinType.COPPER.money;
	}

	public int getSilverChance() {
		return 10;
	}

	public int getGoldChance() {
		return 1;
	}

	public int getSuperGenCnt() {
		return 5;
	}

	@Override
	public int getGenCD() {
		return 1200;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.8f, 1.6f);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.MARIGOLD;
	}

}
