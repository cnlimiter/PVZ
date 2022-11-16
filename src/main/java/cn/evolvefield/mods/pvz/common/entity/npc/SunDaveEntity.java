package cn.evolvefield.mods.pvz.common.entity.npc;

import cn.evolvefield.mods.pvz.utils.StringUtil;
import com.hungteen.pvz.common.container.provider.PVZContainerProvider;
import com.hungteen.pvz.common.container.shop.SunShopContainer;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class SunDaveEntity extends AbstractDaveEntity {

	public SunDaveEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.transactionResource = StringUtil.prefix("sun_dave");
	}

	@Override
	protected void openContainer(ServerPlayer player) {
		NetworkHooks.openGui(player, new PVZContainerProvider() {

			@Override
			public Container createMenu(int id, PlayerInventory inventory,
										Player playerEntity) {
				return new SunShopContainer(id, playerEntity, SunDaveEntity.this.getId());
			}

		}, buffer -> {
			buffer.writeInt(SunDaveEntity.this.getId());
		});
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.9f, 2.5f);
	}

}
