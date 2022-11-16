package cn.evolvefield.mods.pvz.init.proxy;

import cn.evolvefield.mods.pvz.common.block.others.SteelLadderBlock;
import cn.evolvefield.mods.pvz.common.item.armor.BucketArmorItem;
import cn.evolvefield.mods.pvz.common.item.armor.ConeArmorItem;
import cn.evolvefield.mods.pvz.common.item.armor.FootballArmorItem;
import cn.evolvefield.mods.pvz.common.item.armor.GigaArmorItem;
import cn.evolvefield.mods.pvz.init.registry.BlockRegister;
import cn.evolvefield.mods.pvz.init.registry.ItemRegister;
import cn.evolvefield.mods.pvz.utils.StringUtil;
import com.hungteen.pvz.client.render.layer.fullskin.ColdLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy{

	public static final Minecraft MC = Minecraft.getInstance();

	@Override
	public void init() {
	};

	@Override
	public void postInit() {
		this.addLayersForRender();
	};

	@Override
	public void setUpClient() {
		ConeArmorItem.initArmorModel();
		BucketArmorItem.initArmorModel();
		FootballArmorItem.initArmorModel();
		GigaArmorItem.initArmorModel();
		KeyBindRegister.init();
		ItemProperties.register(ItemRegister.SCREEN_DOOR.get(), StringUtil.prefix("blocking"),
				(stack, world, entity, e) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addLayersForRender() {
		MC.getEntityRenderDispatcher().renderers.values().forEach(r -> {
			if (r instanceof LivingEntityRenderer) {
				((LivingEntityRenderer) r).addLayer(new ColdLayer<>((LivingEntityRenderer) r));
			}
		});
	}

	@Override
	public void climbUp() {
		final var player = this.getPlayer();
		if(player != null && player.horizontalCollision && player.onClimbable()){
			//is on steel ladder.
			if(player.level.getBlockState(player.blockPosition()).getBlock().defaultBlockState().is(BlockRegister.STEEL_LADDER.get())) {
				ladderSpeed = Math.min(SteelLadderBlock.MAX_SPEED_UP, ladderSpeed + SteelLadderBlock.UP_SPEED * 0.8F);
				final var vec = player.getDeltaMovement();
				player.setDeltaMovement(vec.x, ladderSpeed, vec.z);
			} else {
				ladderSpeed = Math.max(0, ladderSpeed - SteelLadderBlock.UP_SPEED);
			}
		} else {
			ladderSpeed = 0.06F;
		}
	}

	@Override
	public Player getPlayer() {
		return MC.player;
	}
}
