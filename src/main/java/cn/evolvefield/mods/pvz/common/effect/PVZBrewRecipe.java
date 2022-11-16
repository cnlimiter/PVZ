package cn.evolvefield.mods.pvz.common.effect;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import javax.annotation.Nonnull;

public class PVZBrewRecipe implements IBrewingRecipe{

	@Nonnull private final Ingredient input;
    @Nonnull private final Ingredient ingredient;
    @Nonnull private final ItemStack output;

	public PVZBrewRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
		this.input = input;
        this.ingredient = ingredient;
        this.output = output;
	}

	@Override
	public boolean isInput(ItemStack stack) {
		Potion now = PotionUtils.getPotion(stack);
		for(ItemStack itemstack : this.input.getItems()) {
			if(itemstack.getItem() != stack.getItem()) continue;
			Potion std = PotionUtils.getPotion(itemstack);
			if(std == now) {
				return true;
			}
		}
		return false;
	}

	@Override
    public boolean isIngredient(ItemStack ingredient){
        return this.ingredient.test(ingredient);
    }

	@Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient){
		return isInput(input) && isIngredient(ingredient) ? getOutput().copy() : ItemStack.EMPTY;
    }

	public Ingredient getInput(){
        return input;
    }

    public Ingredient getIngredient(){
        return ingredient;
    }

    public ItemStack getOutput(){
        return output;
    }

}
