package cn.evolvefield.mods.pvz.common.tileentity;

import cn.evolvefield.mods.pvz.common.container.FragmentSpliceContainer;
import cn.evolvefield.mods.pvz.common.item.tool.plant.SunStorageSaplingItem;
import com.hungteen.pvz.common.container.FragmentSpliceContainer;
import com.hungteen.pvz.common.item.tool.plant.SunStorageSaplingItem;
import net.minecraft.block.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.MenuProvider;
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
import net.minecraftforge.items.ItemStackHandler;

public class FragmentSpliceTileEntity extends BlockEntity implements BlockEntityTicker<FragmentSpliceTileEntity>, MenuProvider {

	public static final int CRAFT_COST = 10000;
	public final ItemStackHandler handler = new ItemStackHandler(1 + 1 + 25);
	public ContainerData array = new SimpleContainerData(2);
	public int sunAmount = 0;

	public FragmentSpliceTileEntity() {
		super(TileEntityRegister.FRAGMENT_SPLICE.get());
	}
	@Override
	public void tick(Level pLevel, BlockPos pPos, BlockState pState, FragmentSpliceTileEntity pBlockEntity) {
		if(! level.isClientSide) {
			pBlockEntity.absorbSunAmount();
			pBlockEntity.array.set(0, sunAmount);
		}
	}

    private void absorbSunAmount() {
    	ItemStack stack = this.handler.getStackInSlot(0);
    	if(! stack.isEmpty() && stack.getItem() instanceof SunStorageSaplingItem) {
    		int amount = SunStorageSaplingItem.getStorageSunAmount(stack);
    		int decAmount = Math.min(CRAFT_COST - this.sunAmount, Math.min(100, amount));
    		amount -= decAmount;
    		this.sunAmount += decAmount;
    		SunStorageSaplingItem.setStorageSunAmount(stack, amount);
    	}
    }

	public void clearCraftingSlots(){
		for(int i = 0; i < 25; ++ i){
			this.handler.getStackInSlot(i + 2).shrink(1);
//			this.handler.setStackInSlot(i + 2, ItemStack.EMPTY);
		}
	}

    @Override
    public void load(CompoundTag compound) {
    	super.load(compound);
    	this.handler.deserializeNBT(compound.getCompound("item_stack_list"));
    	this.sunAmount = compound.getInt("sun_amount");
    }


	@Override
    public void saveAdditional(CompoundTag compound) {
    	compound.put("item_stack_list", this.handler.serializeNBT());
    	compound.putInt("sun_amount", this.sunAmount);
    	super.saveAdditional(compound);
    }

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		return new FragmentSpliceContainer(id, player, this.worldPosition);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("gui.pvz.fragment_splice");
	}


}
