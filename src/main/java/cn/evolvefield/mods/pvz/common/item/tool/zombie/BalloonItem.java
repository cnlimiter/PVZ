package cn.evolvefield.mods.pvz.common.item.tool.zombie;

import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class BalloonItem extends Item {

    private static final int SUN_COST = 25;
    private static final int EFFECT_CD = 200;

    public BalloonItem() {
        super(new Properties().stacksTo(1).tab(PVZItemGroups.PVZ_USEFUL).durability(100));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("tooltip.pvz.balloon").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.pvz.sun_cost", SUN_COST).withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if(PlayerUtil.getResource(playerIn, Resources.SUN_NUM) >= SUN_COST){
            if(! playerIn.level.isClientSide){
                PlayerUtil.addResource(playerIn, Resources.SUN_NUM, - SUN_COST);
                playerIn.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, EFFECT_CD, 10));
                if(PlayerUtil.isPlayerSurvival(playerIn)) {
                    playerIn.getCooldowns().addCooldown(playerIn.getItemInHand(handIn).getItem(), EFFECT_CD + 80);
                    playerIn.getItemInHand(handIn).hurtAndBreak(1, playerIn, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
                }
            }
            return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
        }
        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }

}
