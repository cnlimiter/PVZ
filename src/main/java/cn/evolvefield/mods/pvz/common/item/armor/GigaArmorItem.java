package cn.evolvefield.mods.pvz.common.item.armor;

import cn.evolvefield.mods.pvz.utils.StringUtil;
import com.hungteen.pvz.client.model.armor.GigaArmorModel;
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

public class GigaArmorItem extends PVZArmorItem{

	@SuppressWarnings("rawtypes")
	private static final Map<EquipmentSlot, HumanoidModel> modelMap = new EnumMap<>(EquipmentSlot.class);

	public GigaArmorItem(ArmorMaterial materialIn, EquipmentSlot slot) {
		super(materialIn, slot);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		if(slot == EquipmentSlot.HEAD) return StringUtil.ARMOR_PREFIX + "giga_helmet.png";
		if(slot == EquipmentSlot.CHEST) return StringUtil.ARMOR_PREFIX + "giga_chestplate.png";
		if(slot == EquipmentSlot.LEGS) return StringUtil.ARMOR_PREFIX + "giga_layer_2.png";
		return StringUtil.ARMOR_PREFIX + "giga_layer_1.png";
	}

	@SuppressWarnings("unchecked")
	@OnlyIn(Dist.CLIENT)
	@Override
	public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack,
														EquipmentSlot armorSlot, A _default) {
		return (A) modelMap.get(armorSlot);
	}

	@OnlyIn(Dist.CLIENT)
	public static void initArmorModel(){
		modelMap.put(EquipmentSlot.HEAD, new GigaArmorModel(EquipmentSlot.HEAD, 1f));
		modelMap.put(EquipmentSlot.CHEST, new GigaArmorModel(EquipmentSlot.CHEST, 1f));
	}

}
