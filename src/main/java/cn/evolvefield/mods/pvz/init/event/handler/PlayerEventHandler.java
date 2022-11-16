package cn.evolvefield.mods.pvz.init.event.handler;

import cn.evolvefield.mods.pvz.api.PVZAPI;
import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.common.cap.CapabilityHandler;
import cn.evolvefield.mods.pvz.common.entity.AbstractPAZEntity;
import cn.evolvefield.mods.pvz.common.entity.misc.drop.SunEntity;
import cn.evolvefield.mods.pvz.common.entity.plant.PVZPlantEntity;
import cn.evolvefield.mods.pvz.common.item.display.ChallengeEnvelopeItem;
import cn.evolvefield.mods.pvz.common.item.tool.mc.OriginShovelItem;
import cn.evolvefield.mods.pvz.common.world.invasion.InvasionManager;
import cn.evolvefield.mods.pvz.common.world.invasion.MissionManager;
import cn.evolvefield.mods.pvz.init.config.PVZConfig;
import cn.evolvefield.mods.pvz.init.event.PVZLivingEvents;
import cn.evolvefield.mods.pvz.init.event.PVZPlayerEvents;
import cn.evolvefield.mods.pvz.init.registry.EffectRegister;
import cn.evolvefield.mods.pvz.init.registry.EnchantmentRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EnchantmentUtil;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import cn.evolvefield.mods.pvz.utils.PlayerUtil;
import cn.evolvefield.mods.pvz.utils.StringUtil;
import com.hungteen.pvz.PVZMod;
import com.hungteen.pvz.common.entity.zombie.PVZZombieEntity;
import com.hungteen.pvz.compat.patchouli.PVZPatchouliHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PlayerEventHandler {

    /**
     * run when player right click plantEntity with shovel.
     * {@link PVZPlayerEvents#onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific)}
     */
    public static void quickRemoveByPlayer(Player player, Entity entity, ItemStack stack) {
        if(! PlayerUtil.isPlayerSurvival(player) || ((entity instanceof AbstractPAZEntity) && player.getUUID().equals(((AbstractPAZEntity) entity).getOwnerUUID().get()))){
            boolean removed = false;
            if(entity instanceof PVZPlantEntity && stack.getItem() instanceof ShovelItem) {
                final PVZPlantEntity plantEntity = (PVZPlantEntity) entity;
                if (plantEntity.getOuterPlantInfo().isPresent()) {//has outer plant, shovel outer plant.
                    SunEntity.spawnSunsByAmount(player.level, plantEntity.blockPosition(), EnchantmentUtil.getSunShovelAmount(stack, plantEntity.getOuterPlantInfo().get().getSunCost()));
                    plantEntity.removeOuterPlant();
                } else if (plantEntity.getPlantInfo().isPresent()) {
                    SunEntity.spawnSunsByAmount(player.level, plantEntity.blockPosition(), EnchantmentUtil.getSunShovelAmount(stack, plantEntity.getPlantInfo().get().getSunCost()));
                    plantEntity.remove(Entity.RemovalReason.KILLED);
                }
                removed = ! (stack.getItem() instanceof OriginShovelItem);
                EntityUtil.playSound(plantEntity, SoundRegister.PLACE_PLANT_GROUND.get());

            } else if(entity instanceof PVZZombieEntity) {
                //TODO fast remove zombie.
            }
            if(removed && PlayerUtil.isPlayerSurvival(player)){
                stack.hurtAndBreak(3, player, (p) -> {
                    p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                });
            }
        }
    }

    /**
     * {@link PVZPlayerEvents#onPlayerInteract(PlayerInteractEvent.EntityInteractSpecific)}
     */
    public static void makeSuperMode(Player player, Entity entity, ItemStack heldStack) {
        if (entity instanceof PVZPlantEntity && EntityUtil.isEntityValid(entity)) {//target must still alive.
            //origin tools or item enchanted with [Energy Transfer] can make this.
            if (heldStack.getItem().equals(ItemRegister.ORIGIN_SWORD.get()) || EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegister.ENERGY_TRANSFER.get(), heldStack) > 0) {
                //this plant can be super and player has enough energy.
                if (((PVZPlantEntity) entity).canStartSuperMode() && (! PlayerUtil.isPlayerSurvival(player) || PlayerUtil.getResource(player, Resources.ENERGY_NUM) > 0)) {
                    if(PlayerUtil.isPlayerSurvival(player)) {
                        PlayerUtil.addResource(player, Resources.ENERGY_NUM, -1);
                    }
                    ((PVZPlantEntity) entity).startSuperMode(true);
                    //player gain plant food effect.
                    final int treeLevel = PlayerUtil.getResource(player, Resources.TREE_LVL);
                    player.addEffect(new MobEffectInstance(EffectRegister.ENERGETIC_EFFECT.get(), 100 + (treeLevel + 1) / 2, 0));
                }
            }
        }
    }

    /**
     * server side only.
     * {@link PVZLivingEvents#onLivingDeath(LivingDeathEvent)}
     */
    public static void onPlayerKillEntity(Player player, DamageSource source, LivingEntity living) {
        if (living instanceof AbstractPAZEntity) {
            if (EntityUtil.isEnemy(player, living)) {
                PlayerUtil.addResource(player, Resources.TREE_XP, ((AbstractPAZEntity) living).getPAZType().getXpPoint());
            }
        }
        if (PlayerUtil.getInvasion(player).isInvasionEntity(living.getType()) && EntityUtil.isEnemy(player, living)) {
            if (MissionManager.getPlayerMission(player) == MissionManager.MissionType.KILL || MissionManager.getPlayerMission(player) == MissionManager.MissionType.INSTANT_KILL) {
                PlayerUtil.addResource(player, Resources.MISSION_VALUE, 1);
            }
        }
    }

    /**
     * send packet from server to client to sync player's data.
     * {@link PVZPlayerEvents#onPlayerLogin(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent)}
     */
    public static void onPlayerLogin(Player player) {
        PlayerUtil.getOptManager(player).ifPresent(l -> {
            l.init();

            if (l.lastVersion.equals(StringUtil.INIT_VERSION)) {//first join world.
                // allow to get beginner reward.
                if (PVZConfig.COMMON_CONFIG.RuleSettings.GiveBeginnerReward.get()) {
                    player.addItem(new ItemStack(ItemRegister.PEA_SHOOTER_CARD.get()));
                    player.addItem(new ItemStack(ItemRegister.SUN_FLOWER_CARD.get()));
                    player.addItem(new ItemStack(ItemRegister.WALL_NUT_CARD.get()));
                    player.addItem(new ItemStack(ItemRegister.POTATO_MINE_CARD.get()));
                }
                // give patchouli guide book to new join player.
                PVZPatchouliHandler.giveInitialGuideBook(player);
                // give challenge envelope to player.
                player.addItem(ChallengeEnvelopeItem.getChallengeEnvelope(StringUtil.prefix("strange_help")));
            } else if (!l.lastVersion.equals(PVZMod.MOD_VERSION)) {//version changed.

            }

            l.lastVersion = PVZMod.MOD_VERSION;
        });

    }

    /**
     * save card cd.
     * {@link PVZPlayerEvents#onPlayerLogout(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent)}
     */
    public static void onPlayerLogout(Player player) {
        //save all summon card cool down.
        PlayerUtil.getOptManager(player).ifPresent(l -> {
            PVZAPI.get().getPAZs().forEach(p -> {
                p.getSummonCard().ifPresent(card -> {
                    l.setPAZCardBar(p, player.getCooldowns().getCooldownPercent(card, 0f));
                });
            });
        });
    }

    /**
     * {@link PVZLivingEvents#onLivingDeath(LivingDeathEvent)}
     */
    public static void handlePlayerDeath(LivingDeathEvent ev, Player player) {
        if (player != null && !player.level.isClientSide && PlayerUtil.isValidPlayer(player)) {
            /* spawn sun around*/
            spawnSunAroundPlayer(player);
        }
    }

    /**
     * {@link PVZPlayerEvents#onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone)}
     */
    public static void clonePlayerData(Player oldPlayer, Player newPlayer, boolean died) {
        oldPlayer.getCapability(CapabilityHandler.PLAYER_DATA_CAPABILITY).ifPresent(l -> {
        	newPlayer.getCapability(CapabilityHandler.PLAYER_DATA_CAPABILITY).ifPresent(r -> {
        		r.getPlayerData().cloneFromExistingPlayerData(l.getPlayerData(), died);

        	});
        });

        InvasionManager.removePlayer(oldPlayer);
        InvasionManager.addPlayer(newPlayer);
    }

    /**
     * {@link #handlePlayerDeath(LivingDeathEvent, Player)}
     */
    private static void spawnSunAroundPlayer(Player player) {
        final int amount = PlayerUtil.getResource(player, Resources.SUN_NUM);
        final int spawn = amount > 50 ? Mth.clamp((amount - 50) / 10, 25, 250) : 0;
        if (amount > 15) {
            SunEntity.spawnSunsByAmount(player.level, player.blockPosition(), spawn, 50, 3);
        }
    }

    /**
     * when tree level is enough, unlock some plants & zombies.
     */
    public static void unLockPAZs(Player player) {
        final int level = PlayerUtil.getResource(player, Resources.TREE_LVL);
        PVZAPI.get().getPAZs().forEach(type -> {
            if (type.getRequiredLevel() <= level) {
                PlayerUtil.getOptManager(player).ifPresent(m -> {
                    m.setPAZLocked(type, false);
                });
            }
        });
    }

}
