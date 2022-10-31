package cn.evolvefield.mods.pvz.utils;

import cn.evolvefield.mods.pvz.Static;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class StringUtil {

	private static final List<String> ROMAN_NUMBERS = Arrays.asList("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X");
	public static final MutableComponent EMPTY = Component.literal("");
	public static final ResourceLocation WIDGETS = StringUtil.prefix("textures/gui/widgets.png");
	public static final String TE_TAG = "BlockEntityTag";
	public static final String ARMOR_PREFIX = Static.MOD_ID + ":textures/models/armor/";
	public static final String INIT_VERSION = "0.0.0";
	public static final String JSON_SUN_COST = "sun_cost";
	public static final String JSON_COOL_DOWN = "cool_down";
	public static final String JSON_REQUIRE_LEVEL = "require_level";
	public static final String JSON_OCCUR_DIFFICULTY = "occur_difficulty";

	/*
	 * for raid level.
	 */
	public static final String PRE_CD = "pre_tick";
	public static final String WIN_CD = "win_tick";
	public static final String LOSS_CD = "loss_tick";
	public static final String BAR_COLOR = "bar_color";
	public static final String SOUNDS = "sounds";
	public static final String PRE_SOUND = "pre_sound";
	public static final String WAVE_SOUND = "wave_sound";
	public static final String WIN_SOUND = "win_sound";
	public static final String LOSS_SOUND = "loss_sound";
	public static final String SPAWN_PLACEMENT = "placement";
	public static final String WAVES = "waves";
	public static final String REWARDS = "rewards";

	/*
	 * for wave level
	 */
	public static final String WAVE_DURATION = "duration";
	public static final String SPAWNS = "spawns";

	/*
	 * for spawn level
	 */
	public static final String ENTITY_TYPE = "entity_type";
	public static final String ENTITY_NBT = "nbt";
	public static final String SPAWN_AMOUNT = "spawn_amount";
	public static final String SPAWN_TICK = "spawn_tick";


	public static ResourceLocation prefix(String a) {
		return new ResourceLocation(Static.MOD_ID, a);
	}

	public static String identify(String modId, String name){
		return modId + ":" + name;
	}

	public static void drawScaledString(PoseStack stack, Font render, String string, int x, int y, int color, float scale) {
		stack.pushPose();
		stack.scale(scale, scale, scale);
		render.draw(stack, string, x / scale, y / scale, color);
		stack.popPose();
	}

	public static void drawCenteredString(PoseStack stack, Font render, String string, int x, int y, int color) {
		final int width = render.width(string);
		render.draw(stack, string, x - width / 2, y, color);
	}

	public static void drawCenteredScaledString(PoseStack stack, Font render, String string, int x, int y, int color,
			float scale) {
		int width = render.width(string);
		stack.pushPose();
		stack.scale(scale, scale, scale);
		render.draw(stack, string, (x - width / 2 * scale) / scale, y / scale, color);
		stack.popPose();
	}

	public static String getRandomLangText(Minecraft mc, Random rand, String name) {
		if(mc == null) {
			mc = Minecraft.getInstance();
		}
		List<String> texts = StringUtil.getLangTextList(mc, name);
		return texts.get(rand.nextInt(texts.size()));
	}

	/**
	 * get lang from resource.
	 */
	public static List<String> getLangTextList(Minecraft mc, String name) {

		try
		{
			var iresource = StringUtil.getTxtResource(mc, name).get();
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.open(), StandardCharsets.UTF_8));
			return bufferedreader.lines().map(String::trim).filter((p_215277_0_) -> p_215277_0_.hashCode() != 125780783).collect(Collectors.toList());
		} catch (IOException var36) {
			return Collections.emptyList();
		}
	}

	public static Optional<Resource> getTxtResource(Minecraft mc, String name) {
		ResourceLocation fileLoc = StringUtil.prefix("lang/others/" + mc.options.languageCode + "/" + name + ".txt");
        ResourceLocation backupLoc = StringUtil.prefix("lang/others/en_us/" + name + ".txt");
		Optional<Resource> resource = null;
        try {
            resource = mc.getResourceManager().getResource(fileLoc);
        } catch (Exception e) {
            try {
                resource = mc.getResourceManager().getResource(backupLoc);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return resource;
	}

	public static String getRomanString(int num){
		if(num > 0 && num <= 10){
			return ROMAN_NUMBERS.get(num - 1);
		}
		return "";
	}

}
