package terrails.netherutils.blocks.wood;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import terrails.netherutils.Constants;
import terrails.terracore.block.BlockBase;

public class BlockNTFPlanks extends BlockBase {

    public static final PropertyEnum<WoodType> VARIANT = PropertyEnum.create("variant", WoodType.class);

    public BlockNTFPlanks(String name) {
        super(Material.WOOD, Constants.MOD_ID);
        this.setRegistryName(new ResourceLocation(Constants.MOD_ID, name));
        this.setCreativeTab(Constants.CreativeTab.NetherUtils);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, WoodType.HELL));
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
