package cn.evolvefield.mods.pvz.common.entity.plant.light;

import cn.evolvefield.mods.pvz.api.interfaces.types.IPlantType;
import cn.evolvefield.mods.pvz.common.block.special.GoldTileBlock;
import cn.evolvefield.mods.pvz.common.entity.plant.base.PlantBomberEntity;
import cn.evolvefield.mods.pvz.common.impl.SkillTypes;
import cn.evolvefield.mods.pvz.common.impl.plant.OtherPlants;
import cn.evolvefield.mods.pvz.init.registry.BlockRegister;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
public class GoldLeafEntity extends PlantBomberEntity {

	public static final int GOLD_GEN_CD = 400;

	public GoldLeafEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.canCollideWithPlant = false;
		this.hasBombAlamancs = false;
	}

	@Override
	public void startBomb(boolean server) {
		Block block = level.getBlockState(this.blockPosition().below()).getBlock();
		final int lvl = getBlockGoldLevel(block);
		if(server && lvl >= 0 && lvl < this.getTileLevel()) {
			level.setBlockAndUpdate(blockPosition().below(), getGoldTileByLvl(this.getTileLevel()).defaultBlockState());
		}
	}

	public static int getBlockGoldLevel(Block block) {
		if(block instanceof GoldTileBlock) {
			return ((GoldTileBlock) block).lvl;
		}
		if(block == Blocks.GOLD_BLOCK) return 0;
		return - 1;
	}

	public static int getGoldGenAmount(int lvl) {
		return lvl == 1 ? 25 : lvl == 2 ? 35 : 50;
	}

	/**
	 * {@link #startBomb(boolean)}
	 */
	public static Block getGoldTileByLvl(int lvl) {
		return lvl == 1 ? BlockRegister.GOLD_TILE1.get() : lvl == 2 ? BlockRegister.GOLD_TILE2.get() : BlockRegister.GOLD_TILE3.get();
	}

	public int getTileLevel() {
		return (int) this.getSkillValue(SkillTypes.ADVANCE_GOLD);
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.6F, 1F);
	}

	@Override
	public int getReadyTime() {
		return 60;
	}

	@Override
	public IPlantType getPlantType() {
		return OtherPlants.GOLD_LEAF;
	}

}
