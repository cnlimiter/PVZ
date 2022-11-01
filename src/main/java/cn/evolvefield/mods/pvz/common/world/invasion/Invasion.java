package cn.evolvefield.mods.pvz.common.world.invasion;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.enums.PVZPacketTypes;
import cn.evolvefield.mods.pvz.api.enums.Resources;
import cn.evolvefield.mods.pvz.common.net.PVZPacketHandler;
import cn.evolvefield.mods.pvz.common.net.toclient.OtherStatsPacket;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.*;
import cn.evolvefield.mods.pvz.utils.misc.WeightList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 13:43
 * Description:
 */
public class Invasion {
    private final Level world;
    private final Player player;
    //what will spawn in this invasion. (update)
    private final WeightList<SpawnType> spawnList = new WeightList<>();
    //the set of every spawn type, used to check invader. (update)
    private final Set<EntityType<?>> spawnTypes = new HashSet<>();
    //current invasion events resources (for storage).
    private final Set<ResourceLocation> activeResources = new HashSet<>();
    //current spawn resource (for storage).
    private ResourceLocation spawnResource;
    private InvasionType spawnInvasion;
    private Set<InvasionType> activeInvasions;
    private int invasionLvl;
    private boolean isRunning = false;
    private static int tick = 0;
    private int currentCount = 0;
    /* wave */
    private int[] waveTime = new int[InvasionManager.MAX_WAVE_NUM];
    private boolean[] waveTriggered = new boolean[InvasionManager.MAX_WAVE_NUM];
    private int currentWave = 0;
    private int totalWaveCount = 0;
    /* mission */
    private int[] killQueue = new int[MissionManager.KILL_IN_SECOND];
    public int killInSecond = 0;
    public int killPos = 0;
    /* misc */
    private BlockPos availableSpawnPos;

    public Invasion(Player player) {
        this.player = player;
        this.world = player.level;
        this.invasionLvl = PlayerUtil.getResource(player, Resources.TREE_LVL);
        for (int i = 0; i < InvasionManager.MAX_WAVE_NUM; ++i) {
            this.waveTime[i] = 0;
            this.waveTriggered[i] = false;
        }
        for (int i = 0; i < MissionManager.KILL_IN_SECOND; ++i) {
            killQueue[i] = 0;
        }
    }

    /**
     * {@link InvasionManager#tick(TickEvent.LevelTickEvent)}
     */
    public void tick() {
        // wait for data pack or peaceful mode.
        if (this.isRunning() && this.getSpawnInvasion() != null && this.world.getDifficulty() != Difficulty.PEACEFUL) {
            world.getProfiler().push("Invasion Spawn Tick");
            if (++tick >= this.getSpawnCD() && !this.getSpawnList().isEmpty()) {
                this.spawnInvaders();
                tick = 0;
            }
            world.getProfiler().pop();

            world.getProfiler().push("Invasion Wave Tick");
            final int dayTime = (int) world.getDayTime();
            if (this.currentWave < this.getTotalWaveCount() && dayTime == this.getWaveTime(this.currentWave)) {
                this.spawnWaveInvaders();
                this.setWaveTriggered(this.currentWave ++, this.spawnWaveInvaders());//wave spawn.
            }
            world.getProfiler().pop();

            world.getProfiler().push("Invasion Mission Tick");
            MissionManager.tickMission(this);
            world.getProfiler().pop();
        }
    }

    public void spawnInvaders() {
        final int range = PVZConfig.COMMON_CONFIG.InvasionSettings.MaxSpawnRange.get();
        final int maxCount = PVZConfig.COMMON_CONFIG.InvasionSettings.MaxSpawnEachPlayer.get();
        final int current = EntityUtil
                .getPredicateEntities(player, EntityUtil.getEntityAABB(player, range, range), Mob.class, e -> {
                    return isInvasionEntity(e.getType());
                }).size();

        if (current < maxCount) {
            for (int i = 0; i < this.getSpawnCount(); ++i) {
                final SpawnType type = getSpawnList().getRandomItem(world.random).get();
                final BlockPos pos = LevelUtil.findRandomSpawnPos(world, player.blockPosition(), 10, 12, range,
                        b -> InvasionManager.suitableInvasionPos(world, b) && type.checkPos(world, b));

                if (pos != null) {
                    this.availableSpawnPos = pos;
                    this.spawnInvader(type, pos);
                }
            }
        }
        this.currentCount = Math.min(current, maxCount);
    }

    public boolean spawnWaveInvaders() {
        //can only spawn in overworld, and peaceful, and wave enable.
        if (!PlayerUtil.isPlayerSurvival(player) || !world.dimension().equals(Level.OVERWORLD) || world.getDifficulty() == Difficulty.PEACEFUL || !ConfigUtil.enableHugeWave()) {
            return false;
        }

        if (getSpawnList().isEmpty()) {
            Static.LOGGER.warn("WaveManager : Why cause spawn list empty ?");
            return false;
        }

        int cnt = this.getSpawnCount(this.currentWave);
        boolean spawned = false;
        while (cnt >= 15) {//split whole zombie to serveral zombie teams.
            final int teamCnt = (cnt < 20 ? cnt : 10);
            spawned |= this.spawnZombieTeam(teamCnt);
            cnt -= teamCnt;
        }
        if (cnt > 0) {
            spawned |= this.spawnZombieTeam(cnt);
        }
        if (spawned) {
            PlayerUtil.playClientSound(player, SoundRegister.HUGE_WAVE.get());
            PlayerUtil.sendSubTitleToPlayer(player, InvasionManager.HUGE_WAVE);
            // TODO extra summon a-huge-wave
//		    PVZFlagData data = PVZFlagData.getGlobalFlagData(world);
//		    if(data.isZombossDefeated()) {
//		        this.activateTombStone();
//		        this.checkAndSummonBungee();
//		    }
        }
        return spawned;
    }

    /**
     * spawn a zombie invade team.
     */
    private boolean spawnZombieTeam(int cnt) {
        BlockPos mid = LevelUtil.findRandomSpawnPos(world, player.blockPosition(), 20, 16, 48,
                b -> suitableInvasionPos(world, b) && world.getBlockState(b.below()).getFluidState().isEmpty());

        //improve wave spawn position finding.
        if(mid == null && this.availableSpawnPos != null && MathUtil.getPosDisToVec(this.availableSpawnPos, this.player.position()) < 1600) {
            mid = this.availableSpawnPos;

        }

        boolean flag = false;
        if (mid != null) {//find spawn position.
            for (int i = 0; i < cnt; ++i) {
                final SpawnType type = getSpawnList().getRandomItem(world.random).get();
                final BlockPos pos = LevelUtil.findRandomSpawnPos(world, mid, 4, 1, 7, b -> type.checkPos(world, b));
                if (pos != null) {
                    flag = true;
                    this.spawnInvader(type, pos);
                }
            }
            if (flag) {
                //spawn team leader -- flag zombie.
                EntityUtil.onEntitySpawn(world, EntityRegister.FLAG_ZOMBIE.get().create(world), mid.offset(0, 1, 0));

                //spawn yeti zombie when it's Yeti Invasion.
                if (world.random.nextFloat() < InvasionManager.getYetiSpawnChance(this)) {
                    EntityUtil.onEntitySpawn(world, EntityRegister.YETI_ZOMBIE.get().create(world), mid.offset(0, 1, 0));
                }
            }
        }
        return flag;
    }


    private void spawnInvader(SpawnType spawnType, BlockPos pos){
        Entity entity = EntityUtil.createWithNBT(world, spawnType.getSpawnType(), spawnType.getNbt(), pos);
        if(InvasionManager.enableSkills(this.world) && entity instanceof AbstractPAZEntity){
            AbstractPAZEntity.randomInitSkills((AbstractPAZEntity) entity, Math.max(0, this.invasionLvl - spawnType.getInvasionLevel()));
        }
        if(entity instanceof LivingEntity) {
            if(InvasionManager.hasInvisInvasion(this)){
                ((LivingEntity) entity).addEffect(EffectUtil.viewEffect(MobEffects.INVISIBILITY, 1000000, 1));
            }
        }
        if(entity instanceof PVZZombieEntity){
            if(InvasionManager.hasMiniInvasion(this)){
                ((PVZZombieEntity) entity).setMiniZombie(true);
                ((PVZZombieEntity) entity).updatePAZStates();
            }
        }
        //already add to world.
//        world.addFreshEntity(entity);
    }

    /**
     * calculate how many zombies will spawn each wave.
     */
    private int getSpawnCount(int currentWave) {
        final int maxCnt = InvasionManager.SPAWN_COUNT_EACH_WAVE[currentWave];
        final int minCnt = maxCnt / 2;
        return MathUtil.getRandomMinMax(world.random, minCnt, maxCnt);
    }

    public void load(CompoundTag baseTag) {
        this.invasionLvl = baseTag.getInt("invasion_level");
        this.isRunning = baseTag.getBoolean("invasion_running");
        if (baseTag.contains("wave_nbt")) {//wave.
            final CompoundTag nbt = baseTag.getCompound("wave_nbt");
            for (int i = 0; i < InvasionManager.MAX_WAVE_NUM; ++i) {
                this.waveTime[i] = nbt.getInt("wave_time_" + i);
                this.waveTriggered[i] = nbt.getBoolean("wave_triggered_" + i);
            }
            this.totalWaveCount = nbt.getInt("wave_count");
            this.currentWave = nbt.getInt("current_wave");
        }
        if (baseTag.contains("mission_nbt")) {
            final CompoundTag nbt = baseTag.getCompound("mission_nbt");
            for (int i = 0; i < MissionManager.KILL_IN_SECOND; ++i) {
                if (nbt.contains("kill_count" + i)) {
                    this.killQueue[i] = nbt.getInt("kill_count" + i);
                }
            }
            this.killPos = nbt.getInt("kill_pos");
            this.killInSecond = nbt.getInt("kill_in_second");
        }
        if (baseTag.contains("invasion_resources")) {
            this.activeResources.clear();
            final ListTag list = (ListTag) baseTag.get("invasion_resources");
            for (net.minecraft.nbt.Tag tag : list) {
                final CompoundTag tmp = (CompoundTag) tag;
                this.activeResources.add(new ResourceLocation(tmp.getString("type")));
            }
        }
        if (baseTag.contains("spawn_resource")) {
            this.spawnResource = new ResourceLocation(baseTag.getString("spawn_resource"));
        }
    }

    public void save(CompoundTag baseTag) {
        baseTag.putInt("invasion_level", this.invasionLvl);
        baseTag.putBoolean("invasion_running", this.isRunning);
        {//wave.
            final CompoundTag nbt = new CompoundTag();
            for (int i = 0; i < InvasionManager.MAX_WAVE_NUM; ++i) {
                nbt.putInt("wave_time_" + i, this.waveTime[i]);
                nbt.putBoolean("wave_triggered_" + i, this.waveTriggered[i]);
            }
            nbt.putInt("wave_count", this.totalWaveCount);
            nbt.putInt("current_wave", this.currentWave);
            baseTag.put("wave_nbt", nbt);
        }
        {//mission.
            final CompoundTag nbt = new CompoundTag();
            for (int i = 0; i < MissionManager.KILL_IN_SECOND; ++i) {
                nbt.putInt("kill_count" + i, killQueue[i]);
            }
            nbt.putInt("kill_pos", this.killPos);
            nbt.putInt("kill_in_second", killInSecond);
            baseTag.put("mission_nbt", nbt);
        }
        {
            ListTag list = new ListTag();
            this.activeResources.forEach(res -> {
                final CompoundTag tmp = new CompoundTag();
                tmp.putString("type", res.toString());
                list.add(tmp);
            });
            baseTag.put("invasion_resources", list);
        }
        if (this.spawnResource != null) {
            baseTag.putString("spawn_resource", this.spawnResource.toString());
        }
    }

    /**
     * start invasion.
     * send random mission to player.
     * {@link InvasionManager#enableInvasion(Collection)}
     */
    public void enable() {
        this.invasionLvl = PlayerUtil.getResource(player, Resources.TREE_LVL);
        this.resetWaveTime();
        this.isRunning = true;
        this.resetMission(MissionManager.getMission(player.getRandom()));
        /* choose random spawn event & assist events */
        InvasionManager.setSpawnEvent(this);
        InvasionManager.setAssistEvent(this);
        /* send msg */
        if (PVZConfig.COMMON_CONFIG.InvasionSettings.ShowEventMessages.get()) {
            this.getActiveInvasions().forEach(type -> PlayerUtil.sendMsgTo(player, type.getText()));
            if (!getSpawnList().isEmpty()) {
                final MutableComponent msg = Component.literal("");
                for (int i = 0; i < getSpawnList().getLen(); ++i) {
                    final EntityType<?> type = getSpawnList().getItem(i).getSpawnType();
                    final MutableComponent component = Component.translatable("entity."
                            + ForgeRegistries.ENTITY_TYPES.getKey(type).getNamespace() + "." + ForgeRegistries.ENTITY_TYPES.getKey(type).getPath());
                    msg.append(i == 0 ? component : Component.literal(",").append(component));
                }
                PlayerUtil.sendMsgToAll(world, msg);
            }
        }
    }

    /**
     * stop invasion.
     * clear mission of player.
     * {@link InvasionManager#disableInvasion(Collection, boolean)}
     */
    public void disable() {
        this.setTotalWaveCount(0);
        this.isRunning = false;
        this.clearMission();

        this.spawnResource = null;
        this.activeResources.clear();
        this.activeInvasions = null;
    }

    public void setInvasionType(InvasionType invasionType) {
        if (!invasionType.isAssistInvasion()) {
            this.setSpawnInvasion(invasionType.resourceLocation);
            this.updateSpawns(invasionType);
        } else {
            this.addAssistInvasion(invasionType.resourceLocation);
        }
    }

    public void updateSpawns(@Nullable InvasionType type) {
        this.spawnList.clear();
        this.spawnTypes.clear();
        if (type != null) {
            type.getSpawns().forEach(spawn -> {
                if (spawn.getInvasionLevel() <= this.invasionLvl) {
                    this.spawnList.addItem(spawn, spawn.getSpawnWeight());
                    this.spawnTypes.add(spawn.getSpawnType());
                }
            });
        }
    }

    /**
     * reset the huge wave time of each player.
     */
    public void resetWaveTime() {
        final int cnt = getPlayerWaveCount(player.getRandom(), this.invasionLvl);
        //not happen at the first 2000 ticks and the last 2000 ticks of the day.
        final int eachTime = 20000 / cnt;
        for (int i = 0; i < cnt; ++i) {
            final int offset = 2000 + i * eachTime + player.getRandom().nextInt(eachTime);
            final int pos = i;
            this.setWaveTime(pos, offset);
            this.setWaveTriggered(pos, false);
        }
        this.currentWave = 0;
        this.setTotalWaveCount(cnt);
    }

    public void setSpawnInvasion(ResourceLocation resourceLocation) {
        if (this.spawnResource != null) {
            this.activeResources.remove(this.spawnResource);
        }
        this.spawnResource = resourceLocation;
        this.activeResources.add(this.spawnResource);
        this.updateSpawns(InvasionManager.getInvasion(resourceLocation));
    }

    public void addAssistInvasion(ResourceLocation resourceLocation) {
        if (!this.activeResources.contains(resourceLocation)) {
            this.activeResources.add(resourceLocation);
        }
    }

    public void removeAssistInvasion(ResourceLocation resourceLocation) {
        if (this.activeResources.contains(resourceLocation)) {
            this.activeResources.remove(resourceLocation);
        }
    }

    public void clearInvasion() {
        this.spawnInvasion = null;
        this.activeResources.clear();
    }

    public ResourceLocation getSpawnResource() {
        return this.spawnResource;
    }

    public Set<ResourceLocation> getActiveResources() {
        return this.activeResources;
    }

    public InvasionType getSpawnInvasion() {
        return this.spawnInvasion == null ? this.spawnInvasion = InvasionManager.getInvasion(this.spawnResource) : this.spawnInvasion;
    }

    public WeightList<SpawnType> getSpawnList() {
        if (this.spawnList.isEmpty()) {
            this.updateSpawns(this.getSpawnInvasion());
        }
        return this.spawnList;
    }

    public Set<InvasionType> getActiveInvasions() {
        if (this.activeInvasions == null) {
            this.activeInvasions = new HashSet<>();
            this.activeResources.forEach(res -> {
                final InvasionType type = InvasionManager.getInvasion(res);
                if (type != null) {
                    this.activeInvasions.add(type);
                }
            });
            if (isRunning && this.activeInvasions.isEmpty()) {
                this.activeInvasions = null;
            }
        }
        return this.activeInvasions == null ? new HashSet<>() : this.activeInvasions;
    }

    public void setWaveTime(int pos, int data) {
        if (pos >= 0 && pos < InvasionManager.MAX_WAVE_NUM) {
            this.waveTime[pos] = data;
            this.sendWavePacket(player, pos, data);
        }
    }

    public void setTotalWaveCount(int cnt) {
        this.totalWaveCount = cnt;
        this.sendWavePacket(player, -1, cnt);
    }

    public void setWaveTriggered(int pos, boolean is) {
        if (pos >= 0 && pos < InvasionManager.MAX_WAVE_NUM) {
            this.waveTriggered[pos] = is;
            this.sendWaveFlagPacket(player, pos, is);
        }
    }

    public int getTotalWaveCount() {
        return totalWaveCount;
    }

    public boolean getWaveTriggered(int pos) {
        return waveTriggered[pos];
    }

    public int getWaveTime(int pos) {
        return waveTime[pos];
    }

    public int getInvasionLvl() {
        return invasionLvl;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public Level getLevel() {
        return this.world;
    }

    public Player getPlayer() {
        return this.player;
    }

    public RandomSource getRandom() {
        return this.player.getRandom();
    }

    public boolean isInvasionEntity(EntityType<?> entityType) {
        if (this.spawnTypes.isEmpty()) {
            updateSpawns(getSpawnInvasion());
        }
        return this.spawnTypes.contains(entityType);
    }

    private void sendWavePacket(Player player, int pos, int data) {
        if (player instanceof ServerPlayer) {
            PVZPacketHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> {
                        return (ServerPlayer) player;
                    }),
                    new OtherStatsPacket(PVZPacketTypes.WAVE, pos, data)
            );
        }
    }

    private void sendWaveFlagPacket(Player player, int pos, boolean flag) {
        if (player instanceof ServerPlayer) {
            PVZPacketHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> {
                        return (ServerPlayer) player;
                    }),
                    new OtherStatsPacket(PVZPacketTypes.WAVE_FLAG, pos, flag)
            );
        }
    }

    public void sendAllWavePacket(Player player) {
        for (int i = 0; i < InvasionManager.MAX_WAVE_NUM; ++i) {
            sendWavePacket(player, i, this.waveTime[i]);
            sendWaveFlagPacket(player, i, this.waveTriggered[i]);
        }
        sendWavePacket(player, -1, this.totalWaveCount);
    }

    public void resetMission(MissionManager.MissionType type) {
        PlayerUtil.setResource(player, Resources.MISSION_TYPE, type.ordinal());
        PlayerUtil.setResource(player, Resources.MISSION_VALUE, 0);
        PlayerUtil.setResource(player, Resources.MISSION_STAGE, 0);
        for (int i = 0; i < MissionManager.KILL_IN_SECOND; ++i) {
            this.killQueue[i] = 0;
        }
        this.killInSecond = 0;
    }

    public void updateKillQueue() {
        final int next = (killPos + 1) % MissionManager.KILL_IN_SECOND;
//        for(int i = 0; i < 10; ++ i) {
//        	System.out.print(this.killQueue[i] + ", ");
//        }
//        System.out.print("Kills : " + PlayerUtil.getResource(player, Resources.MISSION_VALUE) + ", sub : " + this.killQueue[next]);
//        System.out.println();
        PlayerUtil.addResource(player, Resources.MISSION_VALUE, - this.killQueue[next]);
        this.killQueue[next] = this.killInSecond;
        this.killPos = next;
        this.killInSecond = 0;
    }

    public void clearMission() {
        this.resetMission(MissionManager.MissionType.EMPTY);
    }

    /**
     * max : 10 20 35 50 65 80 100 ...
     * min : 20 40 60 80 100
     */
    private static int getPlayerWaveCount(RandomSource random, int lvl) {
        final int max = Math.min(lvl <= 20 ? (lvl + 9) / 10 : lvl <= 80 ? (lvl - 6) / 15 + 2 : lvl / 20 + 3, InvasionManager.MAX_WAVE_NUM);
        final int min = Math.max(1, Math.min(lvl <= 40 ? (lvl + 19) / 20 : (lvl + 39) / 40 + 1, max - 1));
        return MathUtil.getRandomMinMax(random, min, max);
    }

    /**
     * hard : 3s normal : 6s easy : 10s
     * 20 min = 1200 s
     */
    private int getSpawnCD() {
        if(world.getDifficulty() == Difficulty.PEACEFUL) {
            return 2000;
        }
        final int mid = world.getDifficulty() == Difficulty.HARD ? 60 : world.getDifficulty() == Difficulty.NORMAL ? 120 : 200;
        final int base = this.invasionLvl / 3;
        final int extra = (this.currentCount < base ? 0 : (this.currentCount - base) * 5);
        return MathUtil.getRandomMinMax(world.random, -10, 10) + mid + extra;
    }

    /**
     * hard : 2 - 4 normal : 1 - 4 easy : 1 - 3
     */
    private int getSpawnCount() {
        final int max = world.getDifficulty() == Difficulty.EASY ? 3 : 4;
        final int min = world.getDifficulty() == Difficulty.HARD ? 2 : 1;
        return MathUtil.getRandomMinMax(world.random, min, max);
    }
}
