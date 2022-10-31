package cn.evolvefield.mods.pvz.common.item.display;

import cn.evolvefield.mods.pvz.api.PVZAPI;
import cn.evolvefield.mods.pvz.api.interfaces.raid.IChallengeComponent;
import cn.evolvefield.mods.pvz.common.world.challenge.ChallengeManager;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.init.registry.PVZItemGroups;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

import java.util.List;
import java.util.Optional;

public class ChallengeEnvelopeItem extends Item {

    private static final String CHALLENGE_TYPE = "challenge_type";

    public ChallengeEnvelopeItem() {
        super(new Properties().tab(PVZItemGroups.PVZ_ENVELOPE).stacksTo(1));
    }

    public static ResourceLocation getChallengeType(ItemStack stack) {
        return new ResourceLocation(stack.getOrCreateTag().getString(CHALLENGE_TYPE));
    }

    public static Optional<IChallengeComponent> getRaidComponent(ItemStack stack) {
        return Optional.ofNullable(PVZAPI.get().getRaidTypes().getOrDefault(getChallengeType(stack), null));
    }

    public static ItemStack setChallengeType(ItemStack stack, ResourceLocation res) {
        stack.getOrCreateTag().putString(CHALLENGE_TYPE, res.toString());
        return stack;
    }

    public static ItemStack getChallengeEnvelope(ResourceLocation res) {
    	final ItemStack stack = new ItemStack(ItemRegister.CHALLENGE_ENVELOPE.get());
        return setChallengeType(stack, res);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowedIn(group)) {
            PVZAPI.get().getRaidTypes().forEach((res, com) -> {
                items.add(setChallengeType(new ItemStack(ItemRegister.CHALLENGE_ENVELOPE.get()), res));
            });
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("tooltip.pvz.challenge_envelope1").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.pvz.challenge_envelope2").withStyle(ChatFormatting.GREEN));
        getRaidComponent(stack).ifPresent(com -> {
            tooltip.add(Component.literal(getChallengeType(stack).toString()).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
            tooltip.add(com.getChallengeName().withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD));
        });
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (playerIn.getItemInHand(handIn).getItem() instanceof ChallengeEnvelopeItem) {
            if (worldIn.isClientSide) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    ChallengeEnvelopeItem.getRaidComponent(playerIn.getItemInHand(handIn)).ifPresent(challengeComponent -> {
                        if(handIn == InteractionHand.MAIN_HAND){
                            Minecraft.getInstance().setScreen(new ChallengeEnvelopeScreen(challengeComponent));
                        } else{
                            Minecraft.getInstance().setScreen(new ChallengeInfoScreen(challengeComponent));
                        }
                    });
                });
            }
            return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
        }
        return InteractionResultHolder.fail(playerIn.getItemInHand(handIn));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (! context.getLevel().isClientSide && context.getItemInHand().getItem() instanceof ChallengeEnvelopeItem && context.getClickedFace() == Direction.UP) {
            final ResourceLocation res = getChallengeType(context.getItemInHand());
            final IChallengeComponent challengeComponent = ChallengeManager.getChallengeByResource(res);
            if(challengeComponent == null){
                PlayerUtil.sendMsgTo(context.getPlayer(), Component.translatable("help.pvz.no_challenge").withStyle(ChatFormatting.RED));
            } else{
                if(ChallengeManager.hasChallengeNearby((ServerLevel) context.getLevel(), context.getClickedPos().above())){
                    PlayerUtil.sendMsgTo(context.getPlayer(), Component.translatable("help.pvz.full_challenge").withStyle(ChatFormatting.RED));
                } else{
                    if(ChallengeManager.createChallenge((ServerLevel) context.getLevel(), getChallengeType(context.getItemInHand()), context.getClickedPos().above())) {
                        if (PlayerUtil.isPlayerSurvival(context.getPlayer())) {
                            context.getItemInHand().shrink(1);
                        }
                    } else{
                        PlayerUtil.sendMsgTo(context.getPlayer(), Component.translatable("help.pvz.wrong_challenge").withStyle(ChatFormatting.RED));
                    }
                }
            }
        }
        return InteractionResult.CONSUME;
    }

}
