package cn.evolvefield.mods.pvz.common.datapack;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.api.enums.Colors;
import cn.evolvefield.mods.pvz.api.interfaces.raid.IChallengeComponent;
import cn.evolvefield.mods.pvz.common.world.challenge.ChallengeManager;
import com.google.common.collect.Maps;
import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChallengeTypeLoader extends SimpleJsonResourceReloadListener {

	public static final String NAME = "challenge";
	public static final Map<ResourceLocation, IChallengeComponent> CHALLENGE_MAP = Maps.newHashMap();
	public static final Map<IChallengeComponent, ResourceLocation> RES_MAP = Maps.newHashMap();
	public static final Map<ResourceLocation, JsonElement> JSONS = new HashMap<>();
	private static final Gson GSON = (new GsonBuilder()).create();

	public ChallengeTypeLoader() {
		super(GSON, NAME + "s");
	}


	@Override
	public void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller profiler) {
		/* refresh */
		CHALLENGE_MAP.clear();
		RES_MAP.clear();

		/* load */
		map.forEach((res, jsonElement) -> {
			updateResource(res, jsonElement);

			JSONS.put(res, jsonElement);
		});

		Static.LOGGER.info("Loaded {} challenges", CHALLENGE_MAP.size());
	}

	public static void updateResource(ResourceLocation res, JsonElement jsonElement) {
		try {
			JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, NAME);
			String type = GsonHelper.getAsString(jsonObject, "type", "");
			IChallengeComponent challengeType = ChallengeManager.getChallengeComponent(type);
			if(! challengeType.readJson(jsonObject)) {
				Static.LOGGER.debug("Skipping loading challenge {} as it's conditions were not met", res);
				return;
			}
			/* messages */
			{
				final List<Pair<MutableComponent, Integer>> messages = new ArrayList<>();
				final JsonArray jsonMsgs = GsonHelper.getAsJsonArray(jsonObject, "messages", null);
				if(jsonMsgs == null){//no msg, use default.
					for(int i = 0; i < 6; ++ i){
						final var component = Component.translatable("challenge." + res.getNamespace() + "." + res.getPath() + ".msg" + (i + 1));
						messages.add(Pair.of(component, Colors.BLACK));
					}
				} else{
					for(int i = 0; i < jsonMsgs.size(); ++i) {
						final JsonElement e = jsonMsgs.get(i);
						if (e.isJsonObject()) {
							final JsonObject obj = e.getAsJsonObject();
							final String name = GsonHelper.getAsString(obj, "title", null);
							final int color = GsonHelper.getAsInt(obj, "color", Colors.BAT_BLACK);
							if (name != null) {
								messages.add(Pair.of(Component.translatable(name), color));
							}
						}
					}
				}
				challengeType.setMessages(messages);
			}
			CHALLENGE_MAP.put(res, challengeType);
			RES_MAP.put(challengeType, res);
		} catch (IllegalArgumentException | JsonParseException e) {
			Static.LOGGER.error("Parsing error loading challenge {}: {}", res, e.getMessage());
		}
	}

}
