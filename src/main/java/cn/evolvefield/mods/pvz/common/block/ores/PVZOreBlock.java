package cn.evolvefield.mods.pvz.common.block.ores;

import cn.evolvefield.mods.pvz.init.registry.BlockRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PVZOreBlock extends DropExperienceBlock {

    public PVZOreBlock(Properties properties) {
        super(properties);
    }


    @Override
    public int getExpDrop(BlockState state, LevelReader level, RandomSource random, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        if(this instanceof EssenceOreBlock){// essence ore drop xp.
            if(this.equals(BlockRegister.ORIGIN_ORE.get())){
                return Mth.nextInt(random, 3, 7);
            } else{
                return Mth.nextInt(random, 1, 4);
            }
        }
        return super.getExpDrop(state, level, random, pos, fortuneLevel, silkTouchLevel);
    }


}
