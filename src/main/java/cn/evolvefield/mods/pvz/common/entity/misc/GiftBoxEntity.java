package cn.evolvefield.mods.pvz.common.entity.misc;

import cn.evolvefield.mods.pvz.common.entity.PVZEntityBase;
import cn.evolvefield.mods.pvz.init.registry.SoundRegister;
import cn.evolvefield.mods.pvz.utils.EntityUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;

/**
 * @program: pvzmod-1.16.5
 * @author: HungTeen
 * @create: 2022-02-01 18:45
 **/
public class GiftBoxEntity extends PVZEntityBase {

    private ItemStackHandler handler;

    public GiftBoxEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.setGlowingTag(true);
        this.setInvulnerable(true);
    }

    public void setDrops(NonNullList<ItemStack> items){
        handler = new ItemStackHandler(items);
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vector3d, InteractionHand hand) {
        if(! this.level.isClientSide) {
        	if(this.handler != null){
                for(int i = 0; i < this.handler.getSlots(); ++ i){
                    Containers.dropItemStack(this.level, this.blockPosition().getX(), this.blockPosition().getY(), this.blockPosition().getZ(),
                        this.handler.getStackInSlot(i));
                }
                EntityUtil.playSound(this, SoundRegister.PRIZE_DROP.get());
        	}
            this.remove(RemovalReason.KILLED);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if(nbt.contains("reward_items")){
            final CompoundTag tmp = nbt.getCompound("reward_items");
            this.handler = new ItemStackHandler();
            this.handler.deserializeNBT(tmp);
        }

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if(this.handler != null){
            nbt.put("reward_items", this.handler.serializeNBT());
        }
    }
}
