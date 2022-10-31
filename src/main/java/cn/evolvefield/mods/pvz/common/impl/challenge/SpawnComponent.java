package cn.evolvefield.mods.pvz.common.impl.challenge;

import cn.evolvefield.mods.pvz.api.interfaces.raid.IAmountComponent;
import cn.evolvefield.mods.pvz.api.interfaces.raid.IPlacementComponent;
import cn.evolvefield.mods.pvz.api.interfaces.raid.ISpawnComponent;
import cn.evolvefield.mods.pvz.common.impl.challenge.amount.ConstantAmount;
import cn.evolvefield.mods.pvz.common.world.challenge.ChallengeManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.hungteen.pvz.PVZMod;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map.Entry;

public class SpawnComponent implements ISpawnComponent {

	public static final String NAME = "default";
	private EntityType<?> entityType;
	private IAmountComponent spawnAmount = new ConstantAmount();
	private CompoundTag nbt = new CompoundTag();
	private IPlacementComponent placement;
	private int spawnTick;

	@Override
	public boolean readJson(JsonObject json) {

		/* entity type */
		this.entityType = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(GsonHelper.getAsString(json, "entity_type", "")));
		if(this.entityType == null) {
			throw new JsonSyntaxException("entity type cannot be empty or wrong format");
		}

		/* spawn amount */
		{
			JsonObject obj = GsonHelper.getAsJsonObject(json, "spawn_amount");
	        if(obj != null && ! obj.entrySet().isEmpty()) {
	    	    for(Entry<String, JsonElement> entry : obj.entrySet()) {
	    		    final IAmountComponent tmp = ChallengeManager.getAmountComponent(entry.getKey());
	    		    if(tmp != null) {
	    			    tmp.readJson(entry.getValue());
	    			    this.spawnAmount = tmp;
	    		    } else {
	    			    PVZMod.LOGGER.warn("Amount Component : Read Spawn Amount Wrongly");
	    		    }
	    		    break;
	    	    }
	        }
		}

	    /* spawn placement */
		this.placement = ChallengeManager.readPlacement(json, false);

		/* spawn tick */
		this.spawnTick = GsonHelper.getAsInt(json, "spawn_tick", 0);

		/* nbt */
		if(json.has("nbt")) {
			try {
			    nbt = TagParser.parseTag(GsonHelper.convertToString(json.get("nbt"), "nbt"));
		    } catch (CommandSyntaxException e) {
			    throw new JsonSyntaxException("Invalid nbt tag: " + e.getMessage());
		    }
		}

		return true;
	}

	@Override
	public int getSpawnTick() {
		return this.spawnTick;
	}

	@Override
	public int getSpawnAmount() {
		return this.spawnAmount.getSpawnAmount();
	}

	@Override
	public IPlacementComponent getPlacement() {
		return this.placement;
	}

	@Override
	public CompoundTag getNBT() {
		return this.nbt;
	}

	@Override
	public EntityType<?> getSpawnType() {
		return this.entityType;
	}

}
