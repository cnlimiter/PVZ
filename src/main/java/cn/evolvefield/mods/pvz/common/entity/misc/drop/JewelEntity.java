package cn.evolvefield.mods.pvz.common.entity.misc.drop;

import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.init.config.PVZConfig;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class JewelEntity extends DropEntity{

	public JewelEntity(EntityType<? extends Mob> type, Level worldIn) {
		super(type, worldIn);
		this.setAmount(1);
	}

	@Override
	protected void onDropped() {
		super.onDropped();
		if(! level.isClientSide) {
			EntityUtil.playSound(this, SoundRegister.JEWEL_DROP.get());
		}
	}

	@Override
	public void onCollectedByPlayer(Player player) {
		if(! this.level.isClientSide) {
			PlayerUtil.addResource(player, Resources.GEM_NUM, this.getAmount());
			PlayerUtil.playClientSound(player, SoundRegister.JEWEL_PICK.get());
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.9f, 0.9f);
	}

	@Override
	protected int getMaxLiveTick() {
		return PVZConfig.COMMON_CONFIG.EntitySettings.EntityLiveTick.JewelLiveTick.get();
	}

}
