package cn.evolvefield.mods.pvz.common.entity.misc.bowling;

import cn.evolvefield.mods.pvz.common.advancement.trigger.EntityEffectAmountTrigger;
import cn.evolvefield.mods.pvz.common.entity.misc.drop.CoinEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class WallNutBowlingEntity extends AbstractBowlingEntity {

	public WallNutBowlingEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public WallNutBowlingEntity(EntityType<? extends Projectile> type, Level worldIn, Player entity) {
		super(type, worldIn, entity);
	}

	@Override
	protected void dealDamageTo(Entity entity) {
		++ this.hitCount;
		for(int i = 1; i < this.hitCount; ++ i) {
			CoinEntity coin = EntityRegister.COIN.get().create(level);
			coin.setAmount(1);
			EntityUtil.onEntityRandomPosSpawn(level, coin, this.blockPosition(), 1);
		}
		entity.hurt(PVZEntityDamageSource.normal(this, this.getOwner()).setCount(hitCount), 30);
		EntityUtil.playSound(this, SoundRegister.BOWLING_HIT.get());
		var player = (Player) this.getOwner();
		if(player != null && player instanceof ServerPlayer) {
			EntityEffectAmountTrigger.INSTANCE.trigger((ServerPlayer) player, this, hitCount);
		}
	}

}
