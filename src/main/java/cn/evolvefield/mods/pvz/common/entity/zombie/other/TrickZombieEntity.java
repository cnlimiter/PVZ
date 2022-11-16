package cn.evolvefield.mods.pvz.common.entity.zombie.other;

import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.impl.zombie.CustomZombies;
import cn.evolvefield.mods.pvz.common.impl.zombie.ZombieType;
import cn.evolvefield.mods.pvz.init.misc.PVZLoot;
import cn.evolvefield.mods.pvz.init.registry.EffectRegister;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.ZombieUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TrickZombieEntity extends PVZZombieEntity {

	public static final int EXPLOSION_CHANCE = 8;
	public static final int SUMMON_CHACNE = 5;
	private int lastSummonTick = 0;
	private final int summonGap = 40;

	public TrickZombieEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(ZombieUtil.WALK_LITTLE_FAST);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ZombieUtil.NORMAL_DAMAGE);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(amount >= 1.1F && ! level.isClientSide && this.tickCount - this.lastSummonTick >= this.summonGap && this.getRandom().nextInt(SUMMON_CHACNE) == 0) {
			this.lastSummonTick = this.tickCount;
			TrickZombieEntity zombie = EntityRegister.TRICK_ZOMBIE.get().create(level);
			BlockPos pos = blockPosition().offset(this.getRandom().nextInt(5) - 2, this.getRandom().nextInt(2), this.getRandom().nextInt(5) - 2);
			ZombieUtil.copySummonZombieData(this, zombie);
			EntityUtil.onEntitySpawn(level, zombie, pos);
		}
		return super.hurt(source, amount);
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 vec3d, InteractionHand hand) {
		if(! level.isClientSide && player.getItemInHand(hand).getItem() == ItemRegister.CANDY.get() && ! this.isCharmed()) {
			if(this.getRandom().nextInt(3) == 0) {
				player.getItemInHand(hand).shrink(1);
				this.setCharmed(true);
				return InteractionResult.CONSUME;
			}
		}
		return super.interactAt(player, vec3d, hand);	}



	@Override
	protected void dropAllDeathLoot(DamageSource damageSourceIn) {
		if(! this.hasEffect(EffectRegister.COLD_EFFECT.get()) && ! this.isCharmed()) {
			if(this.getRandom().nextInt(EXPLOSION_CHANCE) == 0) {
				var mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
				this.level.explode(this, getX(), getY(), getZ(), 0.5f, mode);
			}
		}
		super.dropAllDeathLoot(damageSourceIn);
	}

	@Override
	public boolean ignoreExplosion() {
		return true;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		if(this.isMiniZombie()) return EntityDimensions.scalable(0.3F, 0.5F);
		return EntityDimensions.scalable(0.6f, 1.2f);
	}

	@Override
	public float getLife() {
		return 10;
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return PVZLoot.TRICK_ZOMBIE;
	}

	@Override
	public ZombieType getZombieType() {
		return CustomZombies.TRICK_ZOMBIE;
	}

}
