package cn.evolvefield.mods.pvz.common.world.structure.zombie;

import com.hungteen.pvz.common.entity.EntityRegister;
import com.hungteen.pvz.common.entity.zombie.pool.YetiZombieEntity;
import com.hungteen.pvz.common.misc.PVZLoot;
import com.hungteen.pvz.common.world.structure.PVZTemplateComponent;
import com.hungteen.pvz.common.world.structure.StructureRegister;
import com.hungteen.pvz.utils.EntityUtil;
import com.hungteen.pvz.utils.StringUtil;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

public class YetiHouseComponents {

	public static final ResourceLocation res1 = StringUtil.prefix("zombie_house/yeti_house");

	public static void generate(TemplateManager manager, BlockPos pos1, Rotation rotation, List<StructurePiece> list, Random rand) {
	      list.add(new YetiHouseComponent(manager, res1, pos1, rotation));
    }

	public static class YetiHouseComponent extends PVZTemplateComponent{

		private static final IStructurePieceType type = StructureRegister.YETI_HOUSE_PIECE;

		public YetiHouseComponent(TemplateManager manager, CompoundTag nbt) {
			super(type, manager, nbt);
		}

		public YetiHouseComponent(TemplateManager manager, ResourceLocation res,BlockPos pos, Rotation rotation) {
			super(type, manager, res, pos, rotation);
		}

		@Override
		public boolean postProcess(ISeedReader worldIn, StructureManager p_230383_2_, ChunkGenerator chunkGeneratorIn, Random randomIn,
				MutableBoundingBox mutableBoundingBoxIn, ChunkPos chunkPosIn, BlockPos blockPos) {
			int height = chunkGeneratorIn.getFirstOccupiedHeight(this.templatePosition.getX(), this.templatePosition.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
			this.templatePosition = new BlockPos(this.templatePosition.getX(), height - 1, this.templatePosition.getZ());
			return super.postProcess(worldIn, p_230383_2_, chunkGeneratorIn, randomIn, mutableBoundingBoxIn, chunkPosIn, blockPos);
		}

		@Override
		protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand,
				MutableBoundingBox sbb) {
			if(function.equals("chest1")){
				this.createChest(worldIn, sbb, rand, pos, PVZLoot.YETI_HOUSE_CHEST, null);
			} else if(function.equals("chest2")) {
				this.createChest(worldIn, sbb, rand, pos, PVZLoot.YETI_HOUSE_CHEST, null);
			} else if(function.equals("chest3")) {
				this.createChest(worldIn, sbb, rand, pos, PVZLoot.YETI_HOUSE_CHEST, null);
			} else if(function.equals("spawn")) {
				if(rand.nextInt(3) == 0) {
					YetiZombieEntity yeti = EntityRegister.YETI_ZOMBIE.get().create(worldIn.getLevel());
					EntityUtil.onEntitySpawn(worldIn, yeti, pos);
					worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
				}
			}
		}
	}
}
