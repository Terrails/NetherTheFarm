package terrails.netherthefarm.crafting;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

public class IngredientNBT extends Ingredient {

    private final ItemStack[] matchingStacks;
    private IntList matchingStacksPacked;

    protected IngredientNBT(final ItemStack... stacks) {
        super(0);
        matchingStacks = stacks;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return matchingStacks;
    }
    @Override
    public boolean apply(@Nullable final ItemStack itemStack) {
        if (itemStack == null) return false;

        for (final ItemStack stack : matchingStacks) {
            if (stack.getItem() == itemStack.getItem()) {
                final int metadata = stack.getMetadata();

                if ((metadata == OreDictionary.WILDCARD_VALUE || metadata == itemStack.getMetadata()) &&
                        ItemStack.areItemStackTagsEqual(itemStack, stack)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public IntList getValidItemStacksPacked() {
        if (matchingStacksPacked == null) {
            matchingStacksPacked = new IntArrayList(this.matchingStacks.length);

            for (final ItemStack itemstack : this.matchingStacks) {
                matchingStacksPacked.add(RecipeItemHelper.pack(itemstack));
            }

            matchingStacksPacked.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return matchingStacksPacked;
    }
    @Override
    protected void invalidate() {
        matchingStacksPacked = null;
    }

    public static Ingredient fromStacks(final ItemStack... stacks) {
        if (stacks.length > 0) {
            for (final ItemStack itemstack : stacks) {
                if (!itemstack.isEmpty()) {
                    return new IngredientNBT(stacks);
                }
            }
        }

        return EMPTY;
    }

    public static class Factory implements IIngredientFactory {

        @Override
        public Ingredient parse(final JsonContext context, final JsonObject json) {
            final ItemStack stack = CraftingHelper.getItemStack(json, context);

            return IngredientNBT.fromStacks(stack);
        }
    }
}
