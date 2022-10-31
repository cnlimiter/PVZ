package cn.evolvefield.mods.pvz.common.world.invasion;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 13:45
 * Description:
 */
public class InvasionType {
    protected final ResourceLocation resourceLocation;
    protected final List<SpawnType> spawns = new ArrayList<>();
    private final boolean isAssistInvasion;
    protected int triggerChance = 100;
    protected ResourceLocation bonusResource;
    protected ChatFormatting displayColor;
    private int requireDifficulty = 0x7fffffff;

    public InvasionType(ResourceLocation resourceLocation, boolean is){
        this.resourceLocation = resourceLocation;
        this.isAssistInvasion = is;
    }

    public void addSpawn(SpawnType type){
        this.spawns.add(type);
        this.requireDifficulty = Math.min(this.requireDifficulty, type.getInvasionLevel());
    }

    public List<SpawnType> getSpawns() {
        return spawns;
    }

    public void setTriggerChance(int triggerChance) {
        this.triggerChance = triggerChance;
    }

    public int getTriggerChance() {
        return triggerChance;
    }

    public void setBonusResource(ResourceLocation bonusResource) {
        this.bonusResource = bonusResource;
    }

    @Nullable
    public ResourceLocation getBonusResource() {
        return bonusResource;
    }

    public void setDisplayColor(ChatFormatting displayColor) {
        this.displayColor = displayColor;
    }

    public ChatFormatting getDisplayColor() {
        return displayColor;
    }

    public boolean isAssistInvasion() {
        return isAssistInvasion;
    }

    public int getRequireDifficulty() {
        return requireDifficulty;
    }

    public void setRequireDifficulty(int requireDay) {
        this.requireDifficulty = requireDay;
    }

    public Component getText(){
        return Component.translatable("invasion.pvz." + this.resourceLocation.getPath()).withStyle(this.getDisplayColor());
    }
}
