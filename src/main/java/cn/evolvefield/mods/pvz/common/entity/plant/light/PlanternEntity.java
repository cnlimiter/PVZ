package cn.evolvefield.mods.pvz.common.entity.plant.light;

import cn.evolvefield.mods.pvz.api.enums.Colors;
import cn.evolvefield.mods.pvz.api.enums.PAZAlmanacs;
import cn.evolvefield.mods.pvz.api.interfaces.base.IAlmanacEntry;
import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.api.interfaces.util.ILightEffect;
import cn.evolvefield.mods.pvz.common.entity.effect.OriginEffectEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.world.challenge.ChallengeManager;
import cn.evolvefield.mods.pvz.init.registry.EffectRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EffectUtil;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
public class PlanternEntity extends PVZPlantEntity implements ILightEffect {

	private static final int EFFECT_CD = 100;

	public PlanternEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if(! level.isClientSide) {
			if(this.getExistTick() % EFFECT_CD == 10) {
				this.giveLightToPlayers();
			}
		}
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 vec3d, InteractionHand hand) {
		if(hand == InteractionHand.MAIN_HAND && player.getItemInHand(hand).isEmpty() && this.isInSuperState()){
			if(! this.level.isClientSide){
				OriginEffectEntity.create(this.level, this.blockPosition().above(), Colors.YELLOW);

				this.displayAllRaider();

				EntityUtil.playSound(this, this.getSpawnSound().get());
			}
			return InteractionResult.SUCCESS;
		}
		return super.interactAt(player, vec3d, hand);
	}

	/**
	 * {@link #normalPlantTick()}
	 */
	private void giveLightToPlayers() {
		final float range = this.getEffectRange();
		EntityUtil.getFriendlyLivings(this, EntityUtil.getEntityAABB(this, range, range)).forEach(entity -> {
			entity.addEffect(this.getLightEyeEffect());
			final int nightVisionTime = this.getNightVisionTime();
			if(nightVisionTime > 0){
				entity.addEffect(EffectUtil.viewEffect(MobEffects.NIGHT_VISION, nightVisionTime, 0));
			}
		});
	}

	private void displayAllRaider(){
		if(this.level instanceof ServerLevel){
			ChallengeManager.getChallengeNearBy((ServerLevel) this.level, this.blockPosition()).ifPresent(challenge -> {
				challenge.getRaiders().forEach(raider -> {
					if(raider instanceof LivingEntity) {
						((LivingEntity) raider).addEffect(EffectUtil.viewEffect(MobEffects.GLOWING, 200, 0));
					} else {
						raider.setGlowingTag(true);
					}
				});
			});
		}
	}

	public float getEffectRange(){
		return this.getSkillValue(SkillTypes.MORE_LIGHT_RANGE);
	}

	public int getNightVisionTime(){
		return (int) this.getSkillValue(SkillTypes.NIGHT_VISION);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.75f, 1.7f);
	}

	@Override
	public MobEffectInstance getLightEyeEffect() {
		return EffectUtil.viewEffect(EffectRegister.LIGHT_EYE_EFFECT.get(), this.getLightEyeTime(), 0);
	}

	@Override
	public void addAlmanacEntries(List<Pair<IAlmanacEntry, Number>> list) {
		super.addAlmanacEntries(list);
		list.add(Pair.of(PAZAlmanacs.EFFECT_TIME, this.getLightEyeTime()));
	}

	public int getLightEyeTime() {
		return 1800;
	}

	@Override
	public int getSuperTimeLength() {
		return 20;
	}

	@Override
	public Optional<SoundEvent> getSpawnSound() {
		return Optional.ofNullable(SoundRegister.PLANTERN.get());
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.PLANTERN;
	}

}
