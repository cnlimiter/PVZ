package cn.evolvefield.mods.pvz.common.item.armor;

import cn.evolvefield.mods.pvz.utils.StringUtil;
import com.hungteen.pvz.client.model.armor.BucketHeadModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.EnumMap;
import java.util.Map;

public class BucketArmorItem extends PVZArmorItem{

	@SuppressWarnings("rawtypes")
	private static final Map<EquipmentSlot, HumanoidModel> modelMap = new EnumMap<>(EquipmentSlot.class);

	public BucketArmorItem(ArmorMaterial materialIn, EquipmentSlot slot) {
		super(materialIn, slot);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return StringUtil.ARMOR_PREFIX + "bucket_head.png";
	}

	@SuppressWarnings("unchecked")
	@OnlyIn(Dist.CLIENT)
	@Override
	public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack,
														EquipmentSlot armorSlot, A _default) {
		return (A) modelMap.get(armorSlot);
	}

	@OnlyIn(Dist.CLIENT)
	public static void initArmorModel() {
		modelMap.put(EquipmentSlot.HEAD, new BucketHeadModel(1f));
	}
}
