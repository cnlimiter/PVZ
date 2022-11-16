package cn.evolvefield.mods.pvz.common.entity.plant.magic;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantBomberEntity;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.item.spawn.card.PlantCardItem;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.PlantUtil;
import cn.evolvefield.mods.pvz.utils.WorldUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;
public class ImitaterEntity extends PlantBomberEntity {

	private static final EntityDataAccessor<ItemStack> IMITATE_CARD = SynchedEntityData.defineId(ImitaterEntity.class, EntityDataSerializers.ITEM_STACK);
	private Entity targetEntity;
	private Direction placeDirection = Direction.NORTH;
	private ImitateType imitateType = ImitateType.SUMMON;
	private Consumer<PVZPlantEntity> consumer = (e) -> {};

	public ImitaterEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.canCollideWithPlant = false;
		this.isImmuneToWeak = true;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IMITATE_CARD, ItemStack.EMPTY);
	}

	@Override
	protected void registerGoals() {
	}

	@Override
	public void startBomb(boolean server) {
		if(server) {
			if(! (this.getImitateCard().getItem() instanceof PlantCardItem)) {// no imitate template plant.
				Static.LOGGER.warn("Imitate Error : Wrong Card !");
				return ;
			}
			final IPlantType plantType = ((PlantCardItem) this.getImitateCard().getItem()).plantType;
			EntityUtil.playSound(this, SoundRegister.WAKE_UP.get());
			if(plantType == PVZPlants.IMITATER) {
				this.imitateRandomly();
			} else {
				this.imitate(plantType);
			}
		} else {
			for(int i = 0; i < 3; ++ i) {
				WorldUtil.spawnRandomSpeedParticle(level, ParticleTypes.EXPLOSION, position(), 0.01F);
			}
		}
	}

	public void imitate(IPlantType plantType) {
		if(this.imitateType == ImitateType.OUTER) {
			if(this.consumer != null && this.targetEntity instanceof PVZPlantEntity) {
				this.consumer.accept(((PVZPlantEntity) this.targetEntity));
			}
		} else if(plantType.getPlantBlock().isPresent()) {
			if(blockPosition().getY() >= 2) {
				BlockState state = PlantCardItem.getBlockState(placeDirection, plantType);
				PlantCardItem.handlePlantBlock(level, plantType, state, blockPosition());
			}
		} else {
			/* available when player is online */
			this.getOwnerPlayer().ifPresent(player -> {
				PlantCardItem.handlePlantEntity(player, plantType, this.getImitateCard(), blockPosition(), plantEntity -> {
				    /* update owner and maxLevel */
					PlantUtil.copyPlantData(plantEntity, this);
					/* enchantment effects */
					PlantCardItem.enchantPlantEntityByCard(plantEntity, this.getImitateCard());
		            /* handle rider */
				    if(this.getVehicle() != null) {
			            this.stopRiding();
			            plantEntity.startRiding(this.getVehicle());
		            }
				    if(this.getTarget() != null) {
				    	plantEntity.setTarget(this.getTarget());
			    	}
				    /* handle misc */
				    consumer.accept(plantEntity);
			    });
			});
		}
	}

	public void imitateRandomly() {

	}

	@Override
	public void tick() {
		this.noPhysics = true;
		super.tick();
	}

	@Override
	public boolean canBeTargetBy(LivingEntity living) {
		return false;
	}

	public void setImitateAction(Consumer<PVZPlantEntity> c) {
		this.consumer = c;
	}

	public void setImitateType(ImitateType type) {
		this.imitateType = type;
	}

	public void setDirection(Direction dir) {
		this.placeDirection = dir;
	}

	public void setTargetEntity(Entity entity) {
		this.targetEntity = entity;
	}

	@Override
	public boolean isNoGravity() {
		return true;
	}

	@Override
	public int getReadyTime() {
		return 30;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.7F, 1.25F);
	}

	public void setImitateCard(ItemStack stack) {
		this.entityData.set(IMITATE_CARD, stack);
	}

	public ItemStack getImitateCard() {
		return this.entityData.get(IMITATE_CARD);
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.IMITATER;
	}

	public static enum ImitateType{
		SUMMON,
		OUTER
	}

}
