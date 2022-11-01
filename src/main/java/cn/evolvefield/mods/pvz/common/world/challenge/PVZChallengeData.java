package cn.evolvefield.mods.pvz.common.world.challenge;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.interfaces.raid.IChallengeComponent;
import cn.evolvefield.mods.pvz.utils.ConfigUtil;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class PVZChallengeData extends SavedData {

	private static final String DATA_NAME = "ChallengeData";
	private final Map<Integer, Challenge> challengeMap = Maps.newHashMap();
	private final ServerLevel world;
	private int currentChallengeId = 1;
	private int tick = 0;

	public PVZChallengeData(ServerLevel world) {
		super();
		this.world = world;

	}
	public static PVZChallengeData load(ServerLevel world, CompoundTag nbt) {
		var data = new PVZChallengeData(world);
		if (nbt.contains("current_id")) {
			data.currentChallengeId = nbt.getInt("current_id");
		}
		final ListTag raidList = nbt.getList("challenges", 10);
		for (int i = 0; i < raidList.size(); ++i) {
			final CompoundTag tmp = raidList.getCompound(i);
			final Challenge raid = new Challenge(world, tmp);
			data.challengeMap.put(raid.getId(), raid);
		}
		return data;
	}


		/**
         * tick all raid in running.
         * {@link ChallengeManager#tickChallenges(Level)}
         */
	public void tick() {
		Iterator<Challenge> iterator = this.challengeMap.values().iterator();
		while (iterator.hasNext()) {
			Challenge raid = iterator.next();
			if (! ConfigUtil.isRaidEnable()) {
				raid.remove();
			}
			if (raid.isRemoving()) {
				iterator.remove();
				this.setDirty();
			} else {
				this.world.getProfiler().push("Challenge Tick");
				raid.tick();
				this.world.getProfiler().pop();
			}
		}

		if (++ this.tick % 200 == 0) {
			this.setDirty();
		}
	}

	public Optional<Challenge> createChallenge(ServerLevel world, ResourceLocation res, BlockPos pos) {
		final int id = this.getUniqueId();
		IChallengeComponent tmp = ChallengeManager.getChallengeByResource(res);
		if(tmp != null && tmp.isSuitableDimension(world.dimension())){
			final Challenge challenge = new Challenge(id, world, res, pos);
			this.addChallenge(id, challenge);
			return Optional.ofNullable(challenge);
		}
		Static.LOGGER.error("Challenge Create : Wrong ResourceLocation or Dimension for {} !", res);
		return Optional.empty();
	}

	public void addChallenge(int id, Challenge challenge) {
		this.challengeMap.put(id, challenge);
		this.setDirty();
	}

	public int getUniqueId() {
		this.setDirty();
		return this.currentChallengeId++;
	}

	public List<Challenge> getChallenges() {
		return new ArrayList<>(this.challengeMap.values());
	}



	@Override
	public CompoundTag save(CompoundTag nbt) {
		nbt.putInt("current_id", this.currentChallengeId);

		final ListTag raidList = new ListTag();
		for (Challenge raid : this.challengeMap.values()) {
			final CompoundTag tmp = new CompoundTag();
			raid.save(tmp);
			raidList.add(tmp);
		}
		nbt.put("challenges", raidList);

		return nbt;
	}

	public static PVZChallengeData getInvasionData(Level worldIn) {
		if (worldIn instanceof ServerLevel level) {
			return level.getDataStorage().computeIfAbsent((compoundTag -> PVZChallengeData.load(level, compoundTag)), () -> new PVZChallengeData(level), DATA_NAME);
		}
		throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
	}

}
