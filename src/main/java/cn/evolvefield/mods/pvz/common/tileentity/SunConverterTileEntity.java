package cn.evolvefield.mods.pvz.common.tileentity;

import cn.evolvefield.mods.pvz.common.container.SunConverterContainer;
import cn.evolvefield.mods.pvz.common.enchantment.misc.SunMendingEnchantment;
import cn.evolvefield.mods.pvz.common.entity.misc.drop.DropEntity;
import cn.evolvefield.mods.pvz.common.entity.misc.drop.SunEntity;
import cn.evolvefield.mods.pvz.common.item.tool.plant.SunStorageSaplingItem;
import cn.evolvefield.mods.pvz.utils.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;

import java.util.HashSet;
import java.util.Set;

public class SunConverterTileEntity extends BlockEntity implements BlockEntityTicker<SunConverterTileEntity>, MenuProvider {

	public final ItemStackHandler handler = new ItemStackHandler(9);
	public final ContainerData array = new SimpleContainerData(1);
	private final Set<SunEntity> sunSet = new HashSet<>();
	private final int MaxSearchTick = 60;
	private final double MaxSearchRange = 10;
	private int absorbPos = - 1;
	public int tickExist = 0;

	public SunConverterTileEntity() {
		super(TileEntityRegister.SUN_CONVERTER.get());
	}


	@Override
	public void tick(Level pLevel, BlockPos pPos, BlockState pState, SunConverterTileEntity pBlockEntity) {
		++ pBlockEntity.tickExist;
		pBlockEntity.tickSunSet();
		if(! level.isClientSide) {
			pBlockEntity.array.set(0, pBlockEntity.checkCanWorkNow() ? 1: 0);
		}
	}


	@SuppressWarnings("deprecation")
	private void tickSunSet() {
		if (! level.isClientSide) {
			//maintain the set.
			Set<SunEntity> tmp = new HashSet<>();
			this.sunSet.forEach((sun) -> {
				if(sun != null && ! sun.isRemoved() && sun.getDropState() == DropEntity.DropStates.ABSORB) {
					tmp.add(sun);
				}
			});
			this.sunSet.clear();
			this.sunSet.addAll(tmp);
			tmp.clear();
			//if the set is full, then release the sun.
			if(! this.checkCanWorkNow()) {
				this.sunSet.forEach((sun) -> {
					sun.setDropState(DropEntity.DropStates.NORMAL);
				});
				this.sunSet.clear();
				return ;
			}
			//find new sun.
			if(this.tickExist % this.MaxSearchTick == 0) {
			    level.getEntitiesOfClass(SunEntity.class, MathUtil.getAABBWithPos(worldPosition, MaxSearchRange), (sun) -> {
						return sun.getDropState() == DropEntity.DropStates.NORMAL && ! this.sunSet.contains(sun);
			    }).forEach((sun) -> {
			    	sun.setDropState(DropEntity.DropStates.ABSORB);
				    this.sunSet.add(sun);
			    });
			}
			//absorb suns in the set.
			this.sunSet.forEach((sun) -> {
				if(! this.checkCanWorkNow()) return ;
				double speed = 0.15D;
				var now = new Vec3(worldPosition.getX() + 0.5D, worldPosition.getY() + 1D, worldPosition.getZ() + 0.5D);
				var vec = now.subtract(sun.position());
				if(vec.length() <= 1) {
				    this.onCollectSun(sun);
				} else {
				    sun.setDeltaMovement(vec.normalize().scale(speed));
				}
			});
		}
	}

	/**
	 * collect when sun is close.
	 */
	private void onCollectSun(SunEntity sun) {
		int amount = sun.getAmount();
		while(this.checkCanWorkNow() && amount > 0) {
			final ItemStack stack = this.handler.getStackInSlot(this.absorbPos);
			if(SunStorageSaplingItem.isNotOnceSapling(stack)) {
				final SunStorageSaplingItem item = (SunStorageSaplingItem) stack.getItem();
				final int max = item.MAX_STORAGE_NUM;
			    int now = SunStorageSaplingItem.getStorageSunAmount(stack);
			    if(now + amount > max) {
				    amount -= max - now;
				    now = max;
			    } else {
				    now += amount;
				    amount = 0;
			    }
			    SunStorageSaplingItem.setStorageSunAmount(stack, now);
			} else {
				SunMendingEnchantment.repairItem(stack, amount);
				amount = 0;
			}
		}
		if(amount > 0) {
			sun.setAmount(amount);
		} else {
			sun.remove(Entity.RemovalReason.KILLED);
		}
	}

	private boolean checkCanWorkNow() {
		for(int i = 0; i < this.handler.getSlots(); ++ i) {
			final ItemStack stack = this.handler.getStackInSlot(i);
			// sapling absorb.
			if(SunStorageSaplingItem.isNotOnceSapling(stack) && ! SunStorageSaplingItem.isSunStorageFull(stack)) {
				this.absorbPos = i;
				return true;
			}
			// repair tools.
			if(stack.isDamaged()) {
				this.absorbPos = i;
				return true;
			}
		}
		return false;
	}

	/**
	 * Don't rename this method to canInteractWith due to conflicts with Container
	 */
	public boolean isUsableByPlayer(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		}
		return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void load(CompoundTag compound) {
    	super.load(compound);
		this.handler.deserializeNBT(compound.getCompound("itemstack_list"));
		this.tickExist = compound.getInt("exist_tick");
	}



	@Override
	public void saveAdditional(CompoundTag compound) {
		compound.put("itemstack_list", this.handler.serializeNBT());
		compound.putInt("exist_tick", this.tickExist);
		super.saveAdditional(compound);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new SunConverterContainer(id, player, this.worldPosition);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("block.pvz.sun_converter");
	}


}
