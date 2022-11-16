package cn.evolvefield.mods.pvz.common.entity.plant.explosion;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.entity.misc.DoomFixerEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantBomberEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.PVZPlants;
import cn.evolvefield.mods.pvz.common.misc.PVZEntityDamageSource;
import cn.evolvefield.mods.pvz.init.registry.EntityRegister;
import cn.evolvefield.mods.pvz.init.registry.ParticleRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.WorldUtil;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.loot.LootParameters;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
public class DoomShroomEntity extends PlantBomberEntity {

	public static final float MAX_EXPLOSION_LEVEL = 500;

	public DoomShroomEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void normalPlantTick() {
		super.normalPlantTick();
		if(! this.level.isClientSide) {
			if(this.getAttackTime() == this.getReadyTime() - 2) {
				DoomFixerEntity fixer = EntityRegister.DOOM_FIXER.get().create(level);
				EntityUtil.onEntitySpawn(level, fixer, this.blockPosition());
			}
		}
	}

	@Override
	public void startBomb(boolean server) {
		if(server) {
			//deal damage to targets.
			final float range = this.getExplodeRange();
			final AABB aabb = EntityUtil.getEntityAABB(this, range, range);
			EntityUtil.getWholeTargetableEntities(this, aabb).forEach(target -> {
				if(target instanceof EnderDragon) {//make ender_dragon can be damaged by doom shroom.
					target.hurt(((EntityDamageSource) DamageSource.mobAttack(this)).setThorns().setExplosion(), this.getExplodeDamage() * 2);
				} else {
					target.hurt(PVZEntityDamageSource.explode(this), this.getExplodeDamage());
				}
			});
			PVZPlantEntity.clearLadders(this, aabb);
			EntityUtil.playSound(this, SoundRegister.DOOM_SHROOM.get());
			//destroy block and spawn drops
			if(net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
				this.destroyBlocks();
			}
		} else {
			for(int i = 0; i < 300; ++ i) {
				WorldUtil.spawnRandomSpeedParticle(level, ParticleRegister.SPORE.get(), this.position().add(0, 1, 0), 0.8F);
			}
		}
	}

	@SuppressWarnings("deprecation")
	protected void destroyBlocks() {
		ObjectArrayList<Pair<ItemStack, BlockPos>> list = new ObjectArrayList<>();
		List<BlockPos> posList = new ArrayList<>();
		//lower block positions.
		final int len = 2;
		for(int i = - len;i <= len; ++ i) {
			for(int j = - len;j <= len; ++ j) {
				for(int k = - 2; k < 0; ++ k) {
					posList.add(this.blockPosition().offset(i, k, j));
				}
			}
		}
		//upper block positions.
		final int range = 10;
		for(int h = 0; h <= range + 8; ++ h) {
		    for(int i = - range; i <= range; ++ i) {
			    for(int j = - range; j <= range; ++ j) {
			    	if(new Vec3(i, h - 5, j).lengthSqr() <= range * range) {
			    		posList.add(this.blockPosition().offset(i, h, j));
			    	}
			    }
		    }
		}
		posList.forEach(pos -> {
			BlockState state = level.getBlockState(pos);
			if (state.isAir() || state.getBlock().getExplosionResistance() > MAX_EXPLOSION_LEVEL) {
				return ;
			}
			var tileentity = state.hasBlockEntity() ? this.level.getBlockEntity(pos) : null;
			LootContext.Builder loot = (new LootContext.Builder((ServerLevel) this.level)).withRandom(this.level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootParameters.BLOCK_ENTITY, tileentity).withOptionalParameter(LootParameters.THIS_ENTITY, this);
			loot.withParameter(LootContextParams.EXPLOSION_RADIUS, (float)len);
			state.getDrops(loot).forEach((stack)->{
				for(int l = 0; l < list.size(); ++l) {
                    Pair<ItemStack, BlockPos> pair = list.get(l);
                    ItemStack itemstack = pair.getFirst();
                    if (ItemEntity.areMergable(itemstack, stack)) {
                        ItemStack itemstack1 = ItemEntity.merge(itemstack, stack, 16);
                        list.set(l, Pair.of(itemstack1, pair.getSecond()));
                        if (list.isEmpty()) {
                           return;
                        }
                    }
				}
				list.add(Pair.of(stack, pos));
			});
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		});
		for(Pair<ItemStack, BlockPos> pair : list) {
            Block.popResource(this.level, pair.getSecond(), pair.getFirst());
        }
	}

	@Override
	public float getExplodeDamage() {
		return this.getSkillValue(SkillTypes.HIGH_EXPLODE_DAMAGE);
	}

	@Override
	public float getExplodeRange(){
		return 10.5F;
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.8f, 1.5f);
	}

	@Override
	public int getReadyTime() {
		return 50;
	}

	@Override
	public IPlantType getPlantType() {
		return PVZPlants.DOOM_SHROOM;
	}

}
