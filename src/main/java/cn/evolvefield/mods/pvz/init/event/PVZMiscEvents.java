package cn.evolvefield.mods.pvz.init.event;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.common.block.cubes.OriginBlock;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.BasicTrade;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Static.MOD_ID)
public class PVZMiscEvents {

    @SubscribeEvent
    public static void onTagsChange(TagsUpdatedEvent event){
        OriginBlock.updateRadiationMap();
    }

    @SubscribeEvent
    public static void addTrades(WandererTradesEvent event){
        event.getGenericTrades().add(new BasicTrade(8, new ItemStack(ItemRegister.SPORE.get()), 8, 5));
        event.getRareTrades().add(new BasicTrade(24, new ItemStack(Items.MYCELIUM), 4, 15));
    }

}
