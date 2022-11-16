package cn.evolvefield.mods.pvz.common.entity.npc;

import cn.evolvefield.mods.pvz.api.enums.PVZGroupType;
import cn.evolvefield.mods.pvz.common.container.provider.PVZContainerProvider;
import cn.evolvefield.mods.pvz.common.container.shop.DaveShopContainer;
import cn.evolvefield.mods.pvz.utils.StringUtil;
import com.hungteen.pvz.common.container.provider.PVZContainerProvider;
import com.hungteen.pvz.common.container.shop.DaveShopContainer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class CrazyDaveEntity extends AbstractDaveEntity {

	public CrazyDaveEntity(EntityType<? extends PathfinderMob> type, Level worldIn) {
		super(type, worldIn);
		this.transactionResource = StringUtil.prefix("crazy_dave");
	}

	@Override
	protected void openContainer(ServerPlayer player) {
		NetworkHooks.openScreen(player, new PVZContainerProvider() {

			@Override
			public AbstractContainerMenu createMenu(int id, Inventory inventory,
													Player playerEntity) {
				return new DaveShopContainer(id, playerEntity, CrazyDaveEntity.this.getId());
			}

		}, buffer -> {
			buffer.writeInt(CrazyDaveEntity.this.getId());
		});
	}

	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return EntityDimensions.scalable(0.9f, 2.6f);
	}

	@Override
	public PVZGroupType getEntityGroupType() {
		return PVZGroupType.PLANTS;
	}

}
