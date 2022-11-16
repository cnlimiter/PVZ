package cn.evolvefield.mods.pvz.init.registry;

import cn.evolvefield.mods.pvz.Static;
import cn.evolvefield.mods.pvz.common.recipe.FragmentRecipe;
import cn.evolvefield.mods.pvz.common.recipe.FusionRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeRegister {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Static.MOD_ID);

    static {
        ShapedRecipe.setCraftingSize(5, 5);
    }
    //recipe type.
    public static final RecipeType<FusionRecipe> FUSION_RECIPE_TYPE = RecipeType.register("pvz:card_fusion");
    public static final RecipeType<FragmentRecipe> FRAGMENT_RECIPE_TYPE = RecipeType.register("pvz:fragment_splice");
//    registerType(FragmentRecipe.TYPE);

    //serializer.
    public static final RegistryObject<FusionRecipe.Serializer> FUSION_SERIALIZER = RECIPE_SERIALIZERS.register("card_fusion", FusionRecipe.Serializer::new);
    public static final RegistryObject<FragmentRecipe.Serializer> FRAGMENT_SERIALIZER = RECIPE_SERIALIZERS.register("fragment_splice", FragmentRecipe.Serializer::new);

//    public static class RecipeType<T extends IRecipe<?>> implements IRecipeType<T> {
//        @Override
//        public String toString() {
//            return Registry.RECIPE_TYPE.getKey(this).toString();
//        }
//    }
//
//    public static <T extends IRecipe<?>> T registerType(ResourceLocation resourceLocation){
////        IRecipeType
//        return (T) Registry.register(Registry.RECIPE_TYPE, resourceLocation, new RecipeType<>());
//    }

}
