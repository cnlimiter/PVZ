package cn.evolvefield.mods.pvz.common.impl.challenge;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.interfaces.raid.*;
import cn.evolvefield.mods.pvz.common.world.challenge.ChallengeManager;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.Map.Entry;

public class ChallengeComponent implements IChallengeComponent {

	public static final String NAME = "default";
	private List<IWaveComponent> waves = new ArrayList<>();
	private List<IRewardComponent> rewards = new ArrayList<>();
	private Set<String> tags = new HashSet<>();
	private Set<String> dimensions = new HashSet<>();
	private List<String> authors = new ArrayList<>();
	private final List<Pair<MutableComponent, Integer>> messages = new ArrayList<>();
	private IPlacementComponent placement;
	private Component title = Component.translatable("challenge.pvz.title");
	private Component winTitle = Component.translatable("challenge.pvz.win_title");
	private Component lossTitle = Component.translatable("challenge.pvz.loss_title");
	private BossEvent.BossBarColor barColor = BossEvent.BossBarColor.WHITE;
	private SoundEvent preSound = SoundRegister.READY.get();
	private SoundEvent waveSound = SoundRegister.HUGE_WAVE.get();
	private SoundEvent winSound = SoundRegister.WIN_MUSIC.get();
	private SoundEvent lossSound = SoundRegister.LOSE_MUSIC.get();
	private int winTick;
	private int lossTick;
	/* for trade */
	private boolean canTrade;
	private int tradePrice;
	private int tradeWeight;
	/* misc */
	private boolean showRound;
	private int recommendLevel;
	private boolean shouldCloseToCenter;

	@Override
	public boolean readJson(JsonObject json) {
		/* titles */
		{
			final Component text = Component.Serializer.fromJson(json.get("title"));
		    if(text != null) {
			    this.title = text;
		    }
		}
		{
			final Component text = Component.Serializer.fromJson(json.get("win_title"));
		    if(text != null) {
			    this.winTitle = text;
		    }
		}
		{
			final Component text = Component.Serializer.fromJson(json.get("loss_title"));
		    if(text != null) {
			    this.lossTitle = text;
		    }
		}
		/* authors */
		{
			final JsonArray array = GsonHelper.getAsJsonArray(json, "authors", new JsonArray());
			if(array != null) {
				for(int i = 0; i < array.size(); ++ i) {
					final JsonElement e = array.get(i);
					if(e.isJsonPrimitive()) {
						this.authors.add(e.getAsString());
					}
				}
			}
		}
		/* tags */
		{
			final JsonArray array = GsonHelper.getAsJsonArray(json, "tags", new JsonArray());
			if(array != null) {
				for(int i = 0; i < array.size(); ++ i) {
					final JsonElement e = array.get(i);
					if(e.isJsonPrimitive()) {
						this.tags.add(e.getAsString());
					}
				}
			}
		}
		/* dimensions */
		{
			final JsonArray array = GsonHelper.getAsJsonArray(json, "dimensions", new JsonArray());
			if(array != null) {
				for(int i = 0; i < array.size(); ++ i) {
					final JsonElement e = array.get(i);
					if(e.isJsonPrimitive()) {
						this.dimensions.add(e.getAsString());
					}
				}
			}
		}
		/* raid cd */
		{
		    this.winTick = GsonHelper.getAsInt(json, "win_tick", 400);
		    this.lossTick = GsonHelper.getAsInt(json, "loss_tick", 200);
		}
		/* bar color */
		{
			this.barColor = BossEvent.BossBarColor.byName(GsonHelper.getAsString(json, "bar_color", "red"));
		}
		{/* trade */
			this.canTrade = GsonHelper.getAsBoolean(json, "can_trade", true);
			this.tradePrice = GsonHelper.getAsInt(json, "trade_price", 100);
			this.tradeWeight = GsonHelper.getAsInt(json, "trade_weight", 100);
		}
		{/* misc */
			this.showRound = GsonHelper.getAsBoolean(json, "show_round", true);
			this.recommendLevel = GsonHelper.getAsInt(json, "recommend_level", 1);
			this.shouldCloseToCenter = GsonHelper.getAsBoolean(json, "close_to_center", true);
		}
		/* sounds */
		{
			JsonObject obj = GsonHelper.getAsJsonObject(json, "sounds", null);
			if(obj != null) {
				{
					final SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(GsonHelper.getAsString(obj, "pre_sound", "")));
					if(sound != null){
						this.preSound = sound;
					}
				}
				{
					final SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(GsonHelper.getAsString(obj, "wave_sound", "")));
					if(sound != null){
						this.waveSound = sound;
					}
				}
				{
					final SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(GsonHelper.getAsString(obj, "win_sound", "")));
					if(sound != null){
						this.winSound = sound;
					}
				}
				{
					final SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(GsonHelper.getAsString(obj, "loss_sound", "")));
					if(sound != null){
						this.lossSound = sound;
					}
				}
			}
		}
		/* spawn placement */
		{
			this.placement = ChallengeManager.readPlacement(json, true);
		}
		/* waves */
		JsonArray jsonWaves = GsonHelper.getAsJsonArray(json, "waves", new JsonArray());
		if(jsonWaves != null) {
			for(int i = 0; i < jsonWaves.size(); ++ i) {
			    JsonObject obj = jsonWaves.get(i).getAsJsonObject();
			    if(obj != null) {
			    	String type = GsonHelper.getAsString(obj, "type", "");
		            IWaveComponent wave = ChallengeManager.getWaveComponent(type);
		            if(! wave.readJson(obj)) {
		            	return false;
		            }
		            //by tick order.
		            wave.getSpawns().sort(new Sorter());
				    this.waves.add(wave);
			    }
			}
		}
	    if(this.waves.isEmpty()) {// mandatory !
		    throw new JsonSyntaxException("Wave list cannot be empty");
	    }

	    /* rewards */
	    {
	    	JsonObject obj = GsonHelper.getAsJsonObject(json, "rewards", null);
		    if(obj != null && ! obj.entrySet().isEmpty()) {
		       for(Entry<String, JsonElement> entry : obj.entrySet()) {
		  		    final IRewardComponent tmp = ChallengeManager.getRewardComponent(entry.getKey());
		    	    if(tmp != null) {
		    		    tmp.readJson(entry.getValue());
		    		    this.rewards.add(tmp);
		    	    } else {
		    		    Static.LOGGER.warn("Placement Component : Read Spawn Placement Wrongly");
		    	    }
		   	    }
		    }
	    }

	    return true;
	}

	@Override
	public List<ISpawnComponent> getSpawns(int wavePos) {
		return this.waves.get(this.wavePos(wavePos)).getSpawns();
	}

	public List<IWaveComponent> getWaves() {
		return waves;
	}

	@Override
	public List<IRewardComponent> getRewards() {
		return this.rewards;
	}

	@Override
	public List<String> getAuthors() {
		return this.authors;
	}

	@Override
	public boolean hasTag(String tag) {
		return this.tags.contains(tag);
	}

	@Override
	public boolean isSuitableDimension(ResourceKey<Level> type) {
		return this.dimensions.isEmpty() || this.dimensions.contains(Registry.DIMENSION_REGISTRY);
	}

	@Override
	public int getPrepareCD(int wavePos) {
		return this.waves.get(this.wavePos(wavePos)).getPrepareCD();
	}

	@Override
	public int getLastDuration(int wavePos) {
		return this.waves.get(this.wavePos(wavePos)).getLastDuration();
	}

	@Override
	public boolean isWaveFinish(int wavePos, int spawnPos) {
		return spawnPos >= this.waves.get(this.wavePos(wavePos)).getSpawns().size();
	}

	@Override
	public int getTotalWaveCount() {
		return this.waves.size();
	}

	@Override
	public int getWinTick() {
		return this.winTick;
	}

	@Override
	public int getLossTick() {
		return this.lossTick;
	}

	@Override
	public SoundEvent getPrepareSound() {
		return this.preSound;
	}

	@Override
	public SoundEvent getStartWaveSound() {
		return this.waveSound;
	}

	@Override
	public SoundEvent getWinSound() {
		return this.winSound;
	}

	@Override
	public SoundEvent getLossSound() {
		return this.lossSound;
	}

	@Override
	public IPlacementComponent getPlacement(int wavePos) {
		final IPlacementComponent p = this.waves.get(this.wavePos(wavePos)).getPlacement();
		return p == null ? this.placement : p;
	}

	@Override
	public Component getTitle() {
		return this.title;
	}

	@Override
	public Component getWinTitle() {
		return this.winTitle;
	}

	@Override
	public Component getLossTitle() {
		return this.lossTitle;
	}

	@Override
	public BossEvent.BossBarColor getBarColor() {
		return this.barColor;
	}

	private int wavePos(int pos) {
		return Mth.clamp(pos, 0, this.waves.size() - 1);
	}

	@Override
	public MutableComponent getChallengeName(){
		final ResourceLocation resourceLocation = ChallengeManager.getResourceByChallenge(this);
		return Component.translatable("challenge." + resourceLocation.getNamespace() + "." + resourceLocation.getPath() + ".name");
	}

	@Override
	public void setMessages(List<Pair<MutableComponent, Integer>> list) {
		this.messages.clear();
		list.forEach(p -> this.messages.add(p));
	}

	@Override
	public List<Pair<MutableComponent, Integer>> getMessages(){
		return Collections.unmodifiableList(this.messages);
	}

	@Override
	public int getRecommendLevel() {
		return recommendLevel;
	}

	@Override
	public boolean canTrade() {
		return canTrade;
	}

	@Override
	public int getTradeWeight() {
		return tradeWeight;
	}

	@Override
	public int getTradePrice() {
		return tradePrice;
	}

	@Override
	public boolean showRoundTitle() {
		return this.showRound;
	}

	@Override
	public boolean shouldCloseToCenter() {
		return this.shouldCloseToCenter;
	}

	private static class Sorter implements Comparator<ISpawnComponent> {

		public int compare(ISpawnComponent a, ISpawnComponent b) {
			final double d0 = a.getSpawnTick();
			final double d1 = b.getSpawnTick();
			return d0 < d1 ? -1 : d0 > d1 ? 1 : 0;
		}

	}

}

