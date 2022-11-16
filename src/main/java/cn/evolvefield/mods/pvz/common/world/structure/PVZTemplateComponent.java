package cn.evolvefield.mods.pvz.common.world.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public abstract class PVZTemplateComponent extends TemplateStructurePiece {

	private static final BlockPos STRUCTURE_OFFSET = new BlockPos(0, 0, 0);
	protected final Rotation rotation;
	protected final ResourceLocation res;

	public PVZTemplateComponent(StructurePieceType type, StructureTemplateManager manager, ResourceLocation res, BlockPos pos, Rotation rotation) {
		super(type, 0);
		this.templatePosition = pos;
		this.rotation = rotation;
		this.res=res;
		this.setUpTemplate(manager);
	}

	public PVZTemplateComponent(StructurePieceType type, CompoundTag nbt, StructureTemplateManager manager ) {
		super(type, nbt);
		this.res = new ResourceLocation(nbt.getString("Template"));
		this.rotation = Rotation.valueOf(nbt.getString("Rot"));
		this.setUpTemplate(manager);
	}

	private void setUpTemplate(StructureTemplateManager p_204754_1_) {
		var template = p_204754_1_.getOrCreate(this.res);
		var placementsettings = (new PlacementSettings()).setRotation(this.rotation)
				.setMirror(Mirror.NONE).setRotationPivot(STRUCTURE_OFFSET)
				.addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
		this.setUpTemplate(template, this.templatePosition, placementsettings);
	}


	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext pContext, CompoundTag tagCompound) {
		super.addAdditionalSaveData(pContext, tagCompound);
		tagCompound.putString("Template", this.res.toString());
        tagCompound.putString("Rot", this.rotation.name());
	}

}
