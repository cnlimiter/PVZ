package cn.evolvefield.mods.pvz.utils;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.enums.PVZGroupType;
import cn.evolvefield.mods.pvz.api.interfaces.base.ICanBeCharmed;
import cn.evolvefield.mods.pvz.api.interfaces.base.IHasGroup;
import cn.evolvefield.mods.pvz.api.interfaces.base.IHasOwner;
import cn.evolvefield.mods.pvz.api.interfaces.util.IHasMultiPart;
import cn.evolvefield.mods.pvz.common.entity.AbstractPAZEntity;
import cn.evolvefield.mods.pvz.common.entity.EntityGroupHander;
import cn.evolvefield.mods.pvz.common.entity.PVZMultiPartEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.PVZZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.base.AbstractBossZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.grass.PoleZombieEntity;
import cn.evolvefield.mods.pvz.common.entity.zombie.pool.BalloonZombieEntity;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.common.net.PVZPacketHandler;
import cn.evolvefield.mods.pvz.common.net.toclient.SpawnParticlePacket;
import cn.evolvefield.mods.pvz.init.registry.EffectRegister;
import cn.evolvefield.mods.pvz.init.registry.PVZAttributes;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EntityUtil {

	public static final Random RAND = new Random();
	public static final float LIMITED_DAMAGE = 100;

	public static Vec3 getNormalisedVector2d(@Nonnull Entity a, @Nonnull Entity b) {
		final double dx = b.getX() - a.getX();
		final double dz = b.getZ() - a.getZ();
		final double dis = Math.sqrt(dx * dx + dz * dz);
		return new Vec3(dx / dis, 0, dz / dis);
	}

	@Nullable
	public static Entity createWithNBT(Level world, EntityType<?> entityType, CompoundTag nbt, BlockPos pos){
		if(! Level.isInSpawnableBounds(pos)) {
			Static.LOGGER.error("Invalid position when trying summon entity !");
			return null;
		}
		final CompoundTag compound = nbt.copy();
		compound.putString("id", Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(entityType)).toString());
		Entity entity = EntityType.loadEntityRecursive(compound, world, e -> {
			e.moveTo(pos, e.xRotO, e.yRotO);
			return e;
		});
		if(entity == null) {
			Static.LOGGER.error("summon entity failed !");
			return null;
		} else {
			if(world instanceof ServerLevel && ! ((ServerLevel) world).tryAddFreshEntityWithPassengers(entity)) {
				Static.LOGGER.error("summon entity duplicated uuid !");
				return null;
			}
		}
		return entity;
	}

	/**
	 * can zombie be instant remove by lawn mower.
	 * {@link LawnMowerEntity#checkAndRemoveEntity(Entity)}
	 */
	public static boolean canEntityBeRemoved(Entity entity) {
		if(entity instanceof PVZZombieEntity) {
			return ((PVZZombieEntity) entity).canZombieBeRemoved();
		}
		if(!ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).getNamespace().equals(Static.MOD_ID)
				&& (entity instanceof LivingEntity)) {
			return ((LivingEntity) entity).getMaxHealth() <= 20F;
		}
		return true;
	}

	public static boolean canHelpAttackOthers(@Nonnull Entity entity) {
		if(entity instanceof PVZZombieEntity) {
			return ((PVZZombieEntity) entity).canHelpAttack();
		}
		if(entity instanceof PVZPlantEntity) {
			return ((PVZPlantEntity) entity).canHelpAttack();
		}
		return true;
	}

	/**
	 * spawn particle from server side to client side.
	 */
	public static void spawnParticle(Entity entity, int type) {
		PVZPacketHandler.CHANNEL.send(PacketDistributor.NEAR.with(() -> {
			return new PacketDistributor.TargetPoint(entity.getX(), entity.getY(), entity.getZ(), 40, entity.level.dimension());
		}), new SpawnParticlePacket(type, entity.getX(), entity.getY(), entity.getZ()));
	}

	/**
	 * spawn no speed particle on client side.
	 */
	public static void spawnStaticParticle(Entity entity, ParticleOptions type) {
		spawnSpeedParticle(entity, type, 0);
	}

	/**
	 * spawn speed particle on client side.
	 */
	public static void spawnSpeedParticle(Entity entity, ParticleOptions type, float speed) {
		LevelUtil.spawnRandomSpeedParticle(entity.level, type, entity.position().add(0, entity.getBbHeight(), 0), speed);
	}

	public static boolean canDestroyBlock(Level world, BlockPos pos, Entity entity) {
		return canDestroyBlock(world, pos, world.getBlockState(pos), entity);
	}

	public static void playSound(Entity entity, SoundEvent ev) {
		if(ev != null) {
			entity.playSound(ev, 1.0F, RAND.nextFloat() * 0.2F + 0.9F);
		}
	}

	public static boolean isEntityValid(Entity target) {
		return target != null && target.isAlive();
	}

	/**
	 * {@link StrangeCatEntity#startSuperMode(boolean)}
	 */
	public static List<LivingEntity> getRandomLivingInRange(Level world, LivingEntity attacker, AABB aabb, int cnt) {
		List<LivingEntity> list = new ArrayList<>();
		for(LivingEntity living : EntityUtil.getTargetableLivings(attacker, aabb)){
			list.add(living);
			if(-- cnt <= 0) {
				break;
			}
		}
		return list;
	}

	/**
	 * get how many health the target has currently.
	 * {@link #getCurrentDefenceHealth(LivingEntity)}
	 */
	public static float getCurrentHealth(LivingEntity target) {
		if(target instanceof AbstractPAZEntity) {
			return (float) ((AbstractPAZEntity) target).getCurrentHealth();
		}
		return target.getHealth();
	}

	/**
	 * get entity's defence health.
	 * use to show on jade mod.
	 * {@link PVZEntityProvider}
	 */
	public static float getCurrentDefenceHealth(LivingEntity entity) {
		float health = 0;
		if(entity.getAttribute(PVZAttributes.INNER_DEFENCE_HP.get()) != null) {
			health += entity.getAttribute(PVZAttributes.INNER_DEFENCE_HP.get()).getValue();
		}
		if(entity.getAttribute(PVZAttributes.OUTER_DEFENCE_HP.get()) != null) {
			health += entity.getAttribute(PVZAttributes.OUTER_DEFENCE_HP.get()).getValue();
		}
		return health;
	}

	/**
	 * get the max health the target has currently.
	 */
	public static float getCurrentMaxHealth(LivingEntity target) {
		if(target instanceof AbstractPAZEntity) {
			return (float) ((AbstractPAZEntity) target).getCurrentMaxHealth();
		}
		return target.getMaxHealth();
	}

	public static float getMaxHealthDamage(LivingEntity target) {
		return getMaxHealthDamage(target, 1);
	}

	public static float getMaxHealthDamage(LivingEntity target, float multiple) {
		return (float) (EntityUtil.getCurrentMaxHealth(target) * multiple);
	}

	/**
	 * entity's health more than 100 is consider as other mod's boss.
	 */
	public static boolean isEntityBoss(@Nonnull LivingEntity entity) {
		if(entity instanceof AbstractPAZEntity) {
			if(entity instanceof PVZZombieEntity mo) {
			    return (entity instanceof AbstractBossZombieEntity);
		    }
		    if(entity instanceof PVZPlantEntity) {
			    return false;
		    }
		}
		return entity.getHealth() > 100F;
	}

	public static boolean canSeeEntity(Entity entity, Entity target) {
		Vec3 start = entity.position().add(0, entity.getEyeHeight(), 0);
		Vec3 lowerEnd = target.position();
		Vec3 upperEnd = lowerEnd.add(0, target.getBbHeight(), 0);
		ClipContext ray1 = new ClipContext(start, lowerEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
		ClipContext ray2 = new ClipContext(start, upperEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
		return entity.level.clip(ray1).getType() != BlockHitResult.Type.BLOCK || entity.level.clip(ray2).getType() != BlockHitResult.Type.BLOCK;
	}

	/**
	 * can entity pass without hit block when it has a motion vec.
	 * {@link PoleZombieEntity}
	 */
	public static boolean canEntityPass(Entity entity, Vec3 vec, float length) {
		Vec3 lowStart = entity.position();
		Vec3 upperStart = entity.position().add(0, entity.getBbHeight(), 0);
		Vec3 lowerEnd = lowStart.add(vec.scale(length));
		Vec3 upperEnd = upperStart.add(vec.scale(length));
		ClipContext ray1 = new ClipContext(lowStart, lowerEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
		ClipContext ray2 = new ClipContext(upperStart, upperEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
		return entity.level.clip(ray1).getType() != BlockHitResult.Type.BLOCK && entity.level.clip(ray2).getType() != BlockHitResult.Type.BLOCK;
	}

	/**
	 * check can entity destroy the specific block.
	 */
	public static boolean canDestroyBlock(Level world, BlockPos pos, BlockState state, Entity entity) {
		float hardness = state.getDestroySpeed(world, pos);
		return hardness >= 0f && hardness < 50f && !state.getBlock().defaultBlockState().isAir()
				&& state.getBlock().canEntityDestroy(state, world, pos, entity) && (!(entity instanceof LivingEntity)
				|| ForgeEventFactory.onEntityDestroyBlock((LivingEntity) entity, pos, state));
	}

	/**
	 * use to create entity and spawn it in world.
	 */
	public static void createEntityAndSpawn(Level world, EntityType<?> type, BlockPos pos) {
		Entity entity = type.create(world);
		onEntitySpawn(world, entity, pos);
	}

	/**
	 * use to spawn entity in world.
	 */
	public static void onEntitySpawn(LevelAccessor world, Entity entity, BlockPos pos) {
		entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		if(entity instanceof Mob mob) {
			mob.finalizeSpawn((ServerLevelAccessor) world, world.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, null, null);
		}
		world.addFreshEntity(entity);
	}

	/**
	 * spawn in random range position.
	 */
	public static void onEntityRandomPosSpawn(LevelAccessor world, Entity entity, BlockPos pos, int dis) {
		pos = pos.offset(MathUtil.getRandomInRange(world.getRandom(), dis), world.getRandom().nextInt(dis) + 1, MathUtil.getRandomInRange(world.getRandom(), dis));
		onEntitySpawn(world, entity, pos);
	}

	/**
	 * check if entity is on ground
	 */
	public static boolean isOnGround(Entity entity){
		BlockPos pos = entity.blockPosition().below();
		if(!entity.level.isEmptyBlock(pos) && (entity.getY() - pos.getY()) <= 1.00001) {
			return true;
		}
		return false;
	}

	/**
	 * check if entity is on snow.
	 * {@link BobsleTeamEntity#zombieTick()}
	 */
	public static boolean isOnSnow(Entity entity) {
		BlockPos pos = entity.blockPosition();
		return entity.level.getBlockState(pos).is(Blocks.SNOW) || entity.level.getBlockState(pos.below()).is(Blocks.SNOW_BLOCK);
	}

	/**
	 * check if entity is on ice.
	 * {@link BobsleTeamEntity#zombieTick()}
	 */
	public static boolean isOnIce(Entity entity) {
		BlockPos pos = entity.blockPosition();
		return entity.level.getBlockState(pos).is(BlockTags.ICE) || entity.level.getBlockState(pos.below()).is(BlockTags.ICE);
	}

	/**
	 * use for squash to check can attack
	 */
	public static boolean isSuitableTargetInRange(Mob entity, @Nonnull LivingEntity target, double range) {
		return entity.distanceToSqr(target) > getAttackRange(entity, target, range) && canTargetEntity(entity, target);
	}

	/**
	 * get entity attack range by width and r
	 */
	public static double getAttackRange(Entity a, Entity b, double r) {
		double two = Math.sqrt(2);
		double dis = (a.getBbWidth() / two + b.getBbWidth() / two + r);
		return dis * dis;
	}

	/**
	 * get the nearest distance of two entities.
	 * {@link PVZZombieAttackGoal}
	 */
	public static double getNearestDistance(Entity a, Entity b) {
		double dx = a.getX() - b.getX();
		double dz = a.getZ() - b.getZ();
		double dy = 0;
		if(a.getY() > b.getY() + b.getBbHeight()) {
			dy = a.getY() - b.getY() - b.getBbHeight();
		} else if(b.getY() > a.getY() + a.getBbHeight()) {
			dy = b.getY() - a.getY() - a.getBbHeight();
		}
		return dx * dx + dy * dy + dz * dz;
	}

	/**
	 * get the owner of entity
	 */
	@Nullable
	public static Player getEntityOwner(Level world, @Nullable Entity entity) {
		UUID uuid = null;
		if(entity instanceof IHasOwner owner) {
			uuid = owner.getOwnerUUID().orElse(null);
		}
		return uuid == null ? null : world.getPlayerByUUID(uuid);
	}

	/**
	 * use for TargetGoal to check if attacker can set target as AttackTarget.
	 */
	public static boolean canTargetEntity(Entity attacker, Entity target) {
		if(attacker instanceof AbstractPAZEntity abstractPAZEntity) {
			return abstractPAZEntity.checkCanPAZTarget(target);
		}
		return checkCanEntityBeTarget(attacker, target);
	}

	public static boolean canAttackEntity(Entity attacker, Entity target) {
		if(attacker instanceof AbstractPAZEntity abstractPAZEntity) {
			return abstractPAZEntity.checkCanPAZAttack(target);
		}
		return checkCanEntityBeAttack(attacker, target);
	}

	/**
	 * check can TargetGoal select target as attackTarget.
	 */
	public static boolean checkCanEntityBeTarget(Entity attacker, Entity target) {
		if(attacker == null || target == null || EntityUtil.isOpEntity(target)) {//prevent crash
			return false;
		}
		if(target instanceof PVZMultiPartEntity) {//can attack its owner then can attack it
			return checkCanEntityBeTarget(attacker, ((PVZMultiPartEntity) target).getOwner());
		}
		if(ConfigUtil.isTeamAttackEnable()) {//enable team attack
			final Team team1 = getEntityTeam(attacker.level, attacker);
		    final Team team2 = getEntityTeam(attacker.level, target);
			if(team1 != null && team2 != null) {
				return (isEntityCharmed(attacker) ^ isEntityCharmed(target)) == team1.isAlliedTo(team2);
			}
		}
		if(attacker instanceof LivingEntity) {//target the entity who attack it before.
			if(target.is(((LivingEntity) attacker).getLastHurtMob()) && checkCanEntityBeAttack(attacker, target)) {
				return true;
			}
		}
		return EntityGroupHander.checkCanTarget(getEntityGroup(attacker), getEntityGroup(target));
	}

	/**
	 * check can AttackGoal continue to attack target.
	 */
	public static boolean checkCanEntityBeAttack(Entity attacker, Entity target) {
		if(attacker == null || target == null) {//prevent crash
			return false;
		}
		if(target instanceof PVZMultiPartEntity) {//can attack its owner then can attack it
			return checkCanEntityBeAttack(attacker, ((PVZMultiPartEntity) target).getOwner());
		}
		if(target instanceof Player && !PlayerUtil.isPlayerSurvival((Player) target)) {
			return false;
		}
		if(ConfigUtil.isTeamAttackEnable()) {//enable team attack
			final Team team1 = getEntityTeam(attacker.level, attacker);
			final Team team2 = getEntityTeam(attacker.level, target);
			if(team1 != null && team2 != null) {
				return isEntityCharmed(attacker) ^ isEntityCharmed(target) ? team1.isAlliedTo(team2) : ! team1.isAlliedTo(team2);
			}
		}
		return EntityGroupHander.checkCanAttack(getEntityGroup(attacker), getEntityGroup(target));
	}

	/**
	 * both entity belong to the same side of group.
	 */
	public static boolean isFriendly(Entity a, Entity b){
		return (a == null || b == null) ? false : ! EntityGroupHander.checkCanTarget(getEntityGroup(a), getEntityGroup(b));
	}

	/**
	 * both entity belong to different side of group.
	 */
	public static boolean isEnemy(Entity a, Entity b){
		return (a == null || b == null) ? false : EntityGroupHander.checkCanTarget(getEntityGroup(a), getEntityGroup(b));
	}

	public static PVZGroupType getEntityGroup(Entity entity) {
		if(entity instanceof Player){
			return EntityGroupHander.getPlayerGroup((Player) entity);
		}
		return (entity instanceof IHasGroup) ? ((IHasGroup) entity).getEntityGroupType() : EntityGroupHander.getEntityGroupType(entity);
	}

	/**
	 * get team of the entity.
	 * used at {@link #checkCanEntityBeAttack(Entity, Entity)}
	 */
	@Nullable
	public static Team getEntityTeam(Level world, Entity entity){
		if(entity instanceof Player) {
			return entity.getTeam();
		}
		if(entity instanceof IHasOwner && ((IHasOwner) entity).getOwnerUUID().isPresent()) {
			Player player = world.getPlayerByUUID(((IHasOwner) entity).getOwnerUUID().get());
			return player == null ? null : player.getTeam();
		}
	    return entity.getTeam();
	}

	/**
	 * check if entity is charmed
	 */
	public static boolean isEntityCharmed(Entity entity){
		if(entity instanceof ICanBeCharmed) {
			return ((ICanBeCharmed) entity).isCharmed();
		}
		return false;
	}

	/**
	 * get targetable livingentity in range.
	 */
	public static List<LivingEntity> getTargetableLivings(@Nonnull Entity attacker, AABB aabb){
		return getPredicateEntities(attacker, aabb, LivingEntity.class, target -> canTargetEntity(attacker, target));
	}

	/**
	 * get viewable targetable livingentity in range.
	 */
	public static List<LivingEntity> getViewableTargetableEntity(@Nonnull Entity attacker, AABB aabb){
		return getTargetableLivings(attacker, aabb).stream().filter(target -> {
			return canSeeEntity(attacker, target);
		}).collect(Collectors.toList());
	}

	/**
	 * get targetable entities with details.
	 * often use for normal attack check.
	 */
	public static List<Entity> getTargetableEntities(@Nonnull Entity attacker, AABB aabb){
		return getPredicateEntities(attacker, aabb, Entity.class, target -> canTargetEntity(attacker, target));
	}

	/**
	 * get friendly entities.
	 */
	public static List<LivingEntity> getFriendlyLivings(@Nonnull Entity attacker, AABB aabb){
		return getPredicateEntities(attacker, aabb, LivingEntity.class, target -> isFriendly(attacker, target));
	}

	/**
	 * get targetable entities by original check function.
	 * {@link #getWholeTargetableEntities(Entity, AABB)}
	 */
	private static List<Entity> getTargetableEntitiesIngoreCheck(@Nonnull Entity attacker, AABB aabb){
		return getPredicateEntities(attacker, aabb, Entity.class, target -> checkCanEntityBeTarget(attacker, target));
	}

	/**
	 * {@link #getTargetableEntities(Entity, AABB)}
	 * {@link #getTargetableEntitiesIngoreCheck(Entity, AABB)}
	 */
	public static <T extends Entity> List<T> getPredicateEntities(@Nonnull Entity attacker, AABB aabb, Class<T> tClass, Predicate<T> predicate){
		if(attacker == null) {
			return new ArrayList<>();
		}
		return attacker.level.getEntitiesOfClass(tClass, aabb).stream().filter(target -> {
			return ! attacker.equals(target) && predicate.test(target);
		}).collect(Collectors.toList());
	}

	/**
	 * get final attack entities for explosion or other range attack.
	 * use for every range attack condition.
	 */
	public static List<Entity> getWholeTargetableEntities(@Nonnull Entity attacker, AABB aabb) {
		IntOpenHashSet set = new IntOpenHashSet();
		List<Entity> list = new ArrayList<>();
		if(attacker == null) return list;//prevent crash.
		List<Entity> targets = getTargetableEntitiesIngoreCheck(attacker, aabb);
		//choose owner first.
		targets.stream().filter(target -> ! (target instanceof PVZMultiPartEntity)
				&& ! set.contains(target.getId())).forEach(target -> {
			set.addAll(getOwnerAndPartsID(target));
			list.add(target);
		});
		//deal with part.
		targets.stream().filter(target -> target instanceof PVZMultiPartEntity
				&& ! set.contains(target.getId())).forEach(target -> {
			set.addAll(getOwnerAndPartsID(target));
			list.add(target);
		});
		return list;
	}

	/**
	 * get all parts of a entity.
	 * {@link AbstractBulletEntity#addHitEntity(Entity)}
	 */
	public static List<Integer> getOwnerAndPartsID(Entity entity){
		List<Integer> list = new ArrayList<>();
		if(entity instanceof PVZMultiPartEntity p) {//the entity is a part.
			LivingEntity owner = p.getOwner();
			if(owner == null) {// no owner
				list.add(entity.getId());
			} else {// get all id for owner's parts
				IHasMultiPart parent = p.getParent();
				for(Entity target : parent.getMultiParts()) {
				    if(target != null) {
				    	list.add(target.getId());
				    }
				}
				list.add(owner.getId());
			}
		} else if(entity instanceof IHasMultiPart multiPart){
			for(Entity target : multiPart.getMultiParts()) {
			    if(target != null) {
			    	list.add(target.getId());
			    }
			}
			list.add(entity.getId());
		} else {
			list.add(entity.getId());
		}
		return list;
	}

	/**
	 * add entity potion effect.
	 * {@link LivingEventHandler#handleHurtEffects(LivingEntity, PVZEntityDamageSource)}
	 */
	public static void addPotionEffect(Entity entity, MobEffectInstance effect) {
		if(entity instanceof PVZMultiPartEntity) {
			addPotionEffect(((PVZMultiPartEntity) entity).getOwner(), effect);
		} else if(entity instanceof PVZZombieEntity) {
			((PVZZombieEntity) entity).checkAndAddPotionEffect(effect);
		} else if(entity instanceof PVZPlantEntity){
			((PVZPlantEntity) entity).checkAndAddPotionEffect(effect);
		} else if(entity instanceof LivingEntity) {
			((LivingEntity) entity).addEffect(effect);
		}
	}

	/**
	 * is entity has cold effect.
	 */
	public static boolean isEntityCold(LivingEntity entity) {
		return entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(EffectRegister.COLD_EFFECT_UUID) != null;
	}

	/**
	 * is entity has frozen effect.
	 */
	public static boolean isEntityFrozen(LivingEntity entity) {
		return entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(EffectRegister.FROZEN_EFFECT_UUID) != null;
	}

	/**
	 * is entity has butter effect.
	 */
	public static boolean isEntityButter(LivingEntity entity) {
		return entity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(EffectRegister.BUTTER_EFFECT_UUID) != null;
	}

	/**
	 * {@link BloverEntity#blow()}
	 */
	public static boolean isEntityInSky(Entity entity) {
		if(entity instanceof FlyingMob || entity instanceof Bat) {
			return true;
		}
		if(entity instanceof BalloonZombieEntity entity1 && entity1.hasBalloon()) {
			return true;
		}
		return ! entity.isOnGround() && ! entity.isInWater() && ! entity.isInLava();
	}

	public static boolean hasNearBy(Level world, BlockPos pos, double r, Predicate<Entity> pre) {
		return world.getEntitiesOfClass(Entity.class, BlockUtil.getAABB(pos, r, r)).stream().anyMatch(pre);
	}

	/**
	 * set max health and heal.
	 */
	public static void setLivingMaxHealthAndHeal(LivingEntity living, float maxHealth) {
		living.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
		living.heal(maxHealth);
	}

	/**
	 * used for bullets hit check.
	 * get predicate entity between start to end.
	 */
	public static EntityHitResult rayTraceEntities(Level world, Entity entity, Vec3 startVec, Vec3 endVec, Predicate<Entity> predicate) {
		return ProjectileUtil.getEntityHitResult(world, entity, startVec, endVec,
				entity.getBoundingBox().expandTowards(entity.getDeltaMovement()).inflate(1.0D), predicate);
	}

	/**
	 * used for item ray trace.
	 * get predicate entity with dis and vec.
	 */
	public static EntityHitResult rayTraceEntities(Level world, Entity entity, Vec3 lookVec, double distance, Predicate<Entity> predicate) {
	    final Vec3 startVec = entity.position().add(0, entity.getEyeHeight(), 0);
	    Vec3 endVec = startVec.add(lookVec.normalize().scale(distance));
	    ClipContext ray = new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
		BlockHitResult result = world.clip(ray);
		if(result.getType() != BlockHitResult.Type.MISS) {// hit something
			endVec = result.getLocation();
		}
	    return ProjectileUtil.getEntityHitResult(world, entity, startVec, endVec,
				entity.getBoundingBox().inflate(distance), predicate);
	}

	/**
	 * get AABB by entity's width and height.
	 */
	public static AABB getEntityAABB(Entity entity, double w, double h){
		return BlockUtil.getAABB(entity.blockPosition(), w, h);
	}

	/**
	 * no need to target or attack an op entity.
	 */
	public static boolean isOpEntity(Entity entity){
		if(entity instanceof Player && ! PlayerUtil.isPlayerSurvival((Player) entity)){
			return true;
		}
		return entity.isInvulnerable();
	}

}
