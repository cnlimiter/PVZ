package cn.evolvefield.mods.pvz.common.entity.bullet.itembullet;

import cn.evolvefield.mods.pvz.api.interfaces.util.IIceEffect;
import cn.evolvefield.mods.pvz.common.entity.bullet.AbstractShootBulletEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.flame.TorchWoodEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EffectRegister;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.utils.EffectUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT, _interface = ItemSupplier.class)
public class PeaEntity extends AbstractShootBulletEntity implements ItemSupplier {

	private static final EntityDataAccessor<Integer> PEA_STATE = SynchedEntityData.defineId(PeaEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> PEA_TYPE = SynchedEntityData.defineId(PeaEntity.class, EntityDataSerializers.INT);
	public TorchWoodEntity torchWood = null;
	private int power = 0;

	public PeaEntity(EntityType<? extends Projectile> type, Level worldIn) {
		super(type, worldIn);
	}

	public PeaEntity(Level worldIn, LivingEntity shooter, Type peaType, State peaState) {
		super(EntityRegister.PEA.get(), worldIn, shooter);
		this.setPeaState(peaState);
		this.setPeaType(peaType);
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(PEA_STATE, State.NORMAL.ordinal());
		entityData.define(PEA_TYPE, Type.NORMAL.ordinal());
	}

	@Override
	protected void onImpact(HitResult result) {
		boolean flag = false;
		if (result.getType() == HitResult.Type.ENTITY) {
			Entity target = ((EntityHitResult) result).getEntity();
			if (this.shouldHit(target)) {
				target.invulnerableTime = 0;
				this.dealPeaDamage(target); // attack
				flag = true;
			}
		}
		this.level.broadcastEntityEvent(this, (byte) 3);
		if (flag || !this.checkLive(result)) {
			this.remove(RemovalReason.KILLED);
		}
	}

	/**
	 * {@link TorchWoodEntity#heatPeas()}
	 */
	public void heatBy(TorchWoodEntity wood) {
		if (this.torchWood == null || !this.torchWood.is(wood)) {// don't fire twice by the same torchwood
			this.torchWood = wood;
			if (this.torchWood.getFlameType() == TorchWoodEntity.FlameTypes.BLUE) {// blue fire
				if (this.getPeaState() == State.ICE) {//ice to fire
					this.setPeaState(State.FIRE);
				} else if (this.getPeaState().ordinal() < State.BLUE_FIRE.ordinal()) {// pea and fire to blue fire
					this.setPeaState(State.BLUE_FIRE);
				}
			} else {// fire
				if (this.getPeaState() == State.ICE) {//ice to normal
					this.setPeaState(State.NORMAL);
				} else if (this.getPeaState() == State.NORMAL) {//normal to fire
					this.setPeaState(State.FIRE);
				}
			}
		}
	}

	private void dealPeaDamage(Entity target) {
		final float damage = this.getAttackDamage();
		if (this.getPeaState() == State.NORMAL) {// normal pea attack
			target.hurt(PVZEntityDamageSource.pea(this, this.getThrower()), damage);
		} else if (this.getPeaState() == State.ICE) {// snow pea attack
			PVZEntityDamageSource source = PVZEntityDamageSource.snowPea(this, this.getThrower());
			LivingEntity owner = this.getThrower();
			if (owner instanceof IIceEffect) {
				((IIceEffect) owner).getColdEffect().ifPresent(e -> source.addEffect(e));
				((IIceEffect) owner).getFrozenEffect().ifPresent(e -> source.addEffect(e));
			} else if(owner instanceof Player) {
				source.addEffect(EffectUtil.effect(EffectRegister.COLD_EFFECT.get(), 100, 5));
			}
			target.hurt(source, damage);
		} else if (this.getPeaState() == State.FIRE || this.getPeaState() == State.BLUE_FIRE) {
			target.hurt(PVZEntityDamageSource.flamePea(this, this.getThrower()), damage);
		}
	}

	@Override
	public float getAttackDamage() {
		float damage = this.attackDamage;
		damage *= (1 + this.power * 1.0f / 5);
		// size
		if (this.getPeaType() == Type.BIG) {
			damage += 20f;
		} else if (this.getPeaType() == Type.HUGE) {
			damage += 75f;
		}
		// fire
		if (this.getPeaState() == State.FIRE) {
			damage *= 1.5F;
		} else if (this.getPeaState() == State.BLUE_FIRE) {
			damage *= 1.75F;
		}
		return damage;
	}

	@Override
	protected int getMaxLiveTick() {
		return 40;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		if (this.getPeaType() == Type.NORMAL) {
			return new EntityDimensions(0.2f, 0.2f, false);
		}
		if (this.getPeaType() == Type.BIG) {
			return new EntityDimensions(0.4f, 0.4f, false);
		}
		if (this.getPeaType() == Type.HUGE) {
			return new EntityDimensions(0.6f, 0.6f, false);
		}
		return new EntityDimensions(0.2f, 0.2f, false);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("peaState", this.getPeaState().ordinal());
		compound.putInt("peaType", this.getPeaType().ordinal());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if(compound.contains("peaState")) {
			this.setPeaState(State.values()[compound.getInt("peaState")]);
		}
		if(compound.contains("peaType")) {
			this.setPeaType(Type.values()[compound.getInt("peaType")]);
		}
	}

	public State getPeaState() {
		return State.values()[entityData.get(PEA_STATE)];
	}

	public void setPeaState(State state) {
		entityData.set(PEA_STATE, state.ordinal());
	}

	public Type getPeaType() {
		return Type.values()[entityData.get(PEA_TYPE)];
	}

	public void setPeaType(Type type) {
		entityData.set(PEA_TYPE, type.ordinal());
	}

	@Override
	public ItemStack getItem() {
		if (this.getPeaState() == State.NORMAL) {
			return new ItemStack(ItemRegister.PEA.get());
		}
		if (this.getPeaState() == State.ICE) {
			return new ItemStack(ItemRegister.SNOW_PEA.get());
		}
		if (this.getPeaState() == State.FIRE) {
			return new ItemStack(ItemRegister.FLAME_PEA.get());
		}
//		if(this.getPeaState() == State.BLUE_FIRE) {
//			return new ItemStack(ItemRegister.BLUE_FLAME_PEA.get());
//		}
		return new ItemStack(ItemRegister.PEA.get());
	}

	public void setPower(int lvl) {
		this.power = lvl;
	}

	public enum Type {
		NORMAL,
		BIG,
		HUGE,
	}

	public enum State {
		ICE,
		NORMAL,
		FIRE,
		BLUE_FIRE,
		ELECTRICITY,
	}

}
