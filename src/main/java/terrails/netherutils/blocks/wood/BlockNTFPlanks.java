package terrails.netherutils.blocks.wood;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import terrails.netherutils.Constants;
import terrails.netherutils.NetherUtils;
import terrails.netherutils.init.ModBlocks;
import terrails.terracore.block.BlockBase;
import terrails.terracore.registry.IItemBlock;
import terrails.terracore.registry.client.ICustomModel;

public class BlockNTFPlanks extends BlockBase implements IItemBlock, ICustomModel {

    public static final PropertyEnum<WoodType> VARIANT = PropertyEnum.create("variant", WoodType.class);

    public BlockNTFPlanks(String name) {
        super(Material.WOOD);
        this.setRegistryName(new ResourceLocation(NetherUtils.MOD_ID, name));
        this.setCreativeTab(NetherUtils.TAB_NETHER_UTILS);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, WoodType.HELL));
    }

    @Override
    public void initModel() {
        for (WoodType enumType : WoodType.values()) {
            ModelBakery.registerItemVariants(Item.getItemFromBlock(this), new ResourceLocation(NetherUtils.MOD_ID, enumType.getName() + "_planks"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), enumType.getMetadata(), new ModelResourceLocation(new ResourceLocation(NetherUtils.MOD_ID, enumType.getName() + "_planks"), "inventory"));
        }
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockPlanks(this);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (WoodType enumType : WoodType.values()) {
            items.add(new ItemStack(this, 1, enumType.getMetadata()));
        }
    }
    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, WoodType.byMetadata(meta));
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }
    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }
}
