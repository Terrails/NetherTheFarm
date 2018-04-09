package terrails.netherutils.blocks.wood;

import net.minecraft.block.properties.IProperty;
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
import terrails.terracore.registry.IItemBlock;
import terrails.terracore.registry.client.ICustomModel;

public class BlockNTFLog extends net.minecraft.block.BlockLog implements IItemBlock, ICustomModel {

    public static final PropertyEnum<WoodType> VARIANT = PropertyEnum.create("variant", WoodType.class, predicate -> {
        assert predicate != null;
        return predicate.getMetadata() < 4;
    });

    public BlockNTFLog(String name) {
        setRegistryName(name);
        this.setCreativeTab(NetherUtils.TAB_NETHER_UTILS);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, WoodType.HELL).withProperty(LOG_AXIS, EnumAxis.Y));
    }

    @Override
    public void initModel() {
        for (WoodType enumType : WoodType.values()) {
            ModelBakery.registerItemVariants(Item.getItemFromBlock(this), new ResourceLocation(NetherUtils.MOD_ID, enumType.getName() + "_log"));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), enumType.getMetadata(), new ModelResourceLocation(new ResourceLocation(NetherUtils.MOD_ID, enumType.getName() + "_log"), "inventory"));
        }
    }

    @Override
    public ItemBlock getItemBlock() {
        return new ItemBlockLog(this);
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
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, WoodType.byMetadata((meta & 3) % 4));

        switch (meta & 12)
        {
            case 0:
                iblockstate = iblockstate.withProperty(LOG_AXIS, EnumAxis.Y);
                break;
            case 4:
                iblockstate = iblockstate.withProperty(LOG_AXIS, EnumAxis.X);
                break;
            case 8:
                iblockstate = iblockstate.withProperty(LOG_AXIS, EnumAxis.Z);
                break;
            default:
                iblockstate = iblockstate.withProperty(LOG_AXIS, EnumAxis.NONE);
        }

        return iblockstate;
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(VARIANT).getMetadata();

        switch (state.getValue(LOG_AXIS))
        {
            case X:
                i |= 4;
                break;
            case Z:
                i |= 8;
                break;
            case NONE:
                i |= 12;
        }

        return i;
    }
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {VARIANT, LOG_AXIS});
    }
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(VARIANT).getMetadata());
    }
    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }
}
