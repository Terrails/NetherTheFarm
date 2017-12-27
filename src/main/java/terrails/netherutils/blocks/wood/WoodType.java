package terrails.netherutils.blocks.wood;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import terrails.netherutils.Constants;
import terrails.netherutils.init.ModBlocks;

public enum WoodType implements IStringSerializable {
    HELL(0, "hell"),
    ASH(1, "ash"),
    SOUL(2, "soul");

    private static final WoodType[] META_LOOKUP = new WoodType[values().length];
    private int meta;
    private String name;

    WoodType(int meta, String name) {
        this.meta = meta;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getMetadata() {
        return meta;
    }

    public String toString()
    {
        return this.name;
    }

    public static WoodType byMetadata(int meta) {
        if (meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
        }

        return META_LOOKUP[meta];
    }

    static {
        for (WoodType enumType : values()) {
            META_LOOKUP[enumType.getMetadata()] = enumType;
        }
    }

    public static class Init {

        public static void initModel() {
            for (WoodType enumType : WoodType.values()) {
                ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.PLANKS), new ResourceLocation(Constants.MOD_ID, enumType.getName() + "_planks"));
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.PLANKS), enumType.getMetadata(), new ModelResourceLocation(new ResourceLocation(Constants.MOD_ID, enumType.getName() + "_planks"), "inventory"));

                ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.LOG), new ResourceLocation(Constants.MOD_ID, enumType.getName() + "_log"));
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.LOG), enumType.getMetadata(), new ModelResourceLocation(new ResourceLocation(Constants.MOD_ID, enumType.getName() + "_log"), "inventory"));

                ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.LEAVES), new ResourceLocation(Constants.MOD_ID, enumType.getName() + "_leaves"));
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.LEAVES), enumType.getMetadata(), new ModelResourceLocation(new ResourceLocation(Constants.MOD_ID, enumType.getName() + "_leaves"), "inventory"));

                ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.SAPLING), new ResourceLocation(Constants.MOD_ID, enumType.getName() + "_sapling"));
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.SAPLING), enumType.getMetadata(), new ModelResourceLocation(new ResourceLocation(Constants.MOD_ID, enumType.getName() + "_sapling"), "inventory"));
            }
        }
    }
}