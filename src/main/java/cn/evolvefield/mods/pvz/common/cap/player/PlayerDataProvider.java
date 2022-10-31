package cn.evolvefield.mods.pvz.common.cap.player;

import cn.evolvefield.mods.pvz.common.cap.CapabilityHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Project: pvz
 * Author: cnlimiter
 * Date: 2022/10/31 14:23
 * Description:
 */
public class PlayerDataProvider extends CapabilityProvider<PlayerDataProvider> implements INBTSerializable<CompoundTag> {
    private final LazyOptional<IPlayerDataCapability> capability;
    public PlayerDataProvider(Player player) {
        super(PlayerDataProvider.class);
        capability = LazyOptional.of(PlayerDataCapability::new);
        if(player!=null) {
            capability.ifPresent(iPlayerDataCapability -> iPlayerDataCapability.init(player));
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CapabilityHandler.PLAYER_DATA_CAPABILITY.orEmpty(cap, capability);

    }

    @Override
    public CompoundTag serializeNBT() {
        return capability.resolve().isPresent() ? capability.resolve().get().getPlayerData().saveToNBT() : new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        capability.ifPresent(cap -> cap.getPlayerData().loadFromNBT(nbt));

    }
}
