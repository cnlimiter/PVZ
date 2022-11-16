package cn.evolvefield.mods.pvz.common.entity.misc.drop;

import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.common.enchantment.misc.SunMendingEnchantment;
import cn.evolvefield.mods.pvz.common.world.invasion.MissionManager;
import cn.evolvefield.mods.pvz.common.world.invasion.SpawnType;
import cn.evolvefield.mods.pvz.init.config.PVZConfig;
import cn.evolvefield.mods.pvz.init.registry.EnchantmentRegister;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import com.hungteen.pvz.common.event.events.PlayerCollectDropEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map.Entry;
import java.util.Random;

public class SunEntity extends DropEntity {

	private float fall_speed = 0.03f;

	public SunEntity(EntityType<? extends Mob> type, Level worldIn) {
		super(type, worldIn);
		this.setAmount(25);//default sun amount (nature spawn)
		this.setNoGravity(true);
	}

	@Override
	public void tick() {
		super.tick();
		if(! this.onGround && ! this.isInWater()) {
			this.setDeltaMovement(this.getDeltaMovement().x(), - fall_speed, this.getDeltaMovement().z());
		}
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		//25 0.6
		int amount = this.getAmount();
		float w = amount * 1f / 200 + 0.3f, h = amount * 1f / 75 + 0.1f;
		return new EntityDimensions(w, h, false); //max (0.8w,1.5h) min(0.4w,0.3h)
	}

	@Override
	public void playerTouch(Player entityIn) {
		if(!this.level.isClientSide && this.isAlive() && this.getDropState() != DropStates.STEAL) {
			this.onCollectedByPlayer(entityIn);
		}
	}

	@Override
	public void onCollectedByPlayer(Player player) {
		if(! level.isClientSide && ! MinecraftForge.EVENT_BUS.post(new PlayerCollectDropEvent.PlayerCollectSunEvent(player, this))) {
		    Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomItemWith(EnchantmentRegister.SUN_MENDING.get(), player);
		    if(entry != null) {
                SunMendingEnchantment.repairItem(entry.getValue(), this.getAmount());
		    } else {
		    	PlayerUtil.addResource(player, Resources.SUN_NUM, this.getAmount());
				if(MissionManager.getPlayerMission(player) == MissionManager.MissionType.COLLECT_SUN){
					PlayerUtil.addResource(player, Resources.MISSION_VALUE, this.getAmount());
				}
		    }
		    PlayerUtil.playClientSound(player, SoundRegister.SUN_PICK.get());
		}
		this.remove(RemovalReason.KILLED);
	}

	public static void spawnSunsByAmount(Level world, BlockPos pos, int amount) {
		spawnSunsByAmount(world, pos, amount, 75, 1);
	}


	public static void spawnSunsByAmount(Level world, BlockPos pos, int amount, int each, int range) {
		while(amount >= each) {
			amount -= each;
			spawnSunRandomly(world, pos, each, range);
		}
		if(amount != 0) {
			spawnSunRandomly(world, pos, amount, range);
			amount = 0;
		}
	}

	/**
	 * spawn sun entity in range randomly with specific amount.
	 */
	public static void spawnSunRandomly(Level world, BlockPos pos, int amount, int dis) {
		SunEntity sun = EntityRegister.SUN.get().create(world);
		sun.setAmount(amount);
		EntityUtil.onEntityRandomPosSpawn(world, sun, pos, dis);
	}



	public static boolean canSunSpawn(EntityType<? extends SunEntity> zombieType, LevelReader worldIn, SpawnType reason, BlockPos pos, Random rand) {
		if(worldIn instanceof ServerLevel) {
			return ! ((ServerLevel)worldIn).isRainingAt(pos) && ((ServerLevel)worldIn).isDay() && worldIn.getBrightness(LightLayer.SKY, pos) >= 15;
		}
		return worldIn.getBrightness(LightLayer.SKY, pos) >= 15;
	}

	@Override
	protected int getMaxLiveTick() {
		return PVZConfig.COMMON_CONFIG.EntitySettings.EntityLiveTick.SunLiveTick.get();
	}

}
