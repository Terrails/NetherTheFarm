package terrails.netherutils.blocks;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import terrails.netherutils.Constants;
import terrails.netherutils.NetherUtils;
import terrails.netherutils.api.world.IWorldData;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.world.data.CustomWorldData;
import terrails.terracore.block.BlockBase;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockSpawnPoint extends BlockBase {

    public BlockSpawnPoint(String name) {
        super(Material.ROCK);
        setRegistryName(new ResourceLocation(NetherUtils.MOD_ID, name));
        setUnlocalizedName(NetherUtils.MOD_ID + "." + name);
        setBlockUnbreakable();
        setResistance(6000000.0F);
        setSoundType(SoundType.STONE);
        disableStats();
    }


    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        IWorldData data = CustomWorldData.get(worldIn);
        if (data != null && !worldIn.isRemote) {
            data.setPointPos(BlockPos.ORIGIN);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        IWorldData data = CustomWorldData.get(worldIn);
        if (data != null && !worldIn.isRemote) {
            data.setPointPos(pos);
        }
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {}

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        if (!ConfigHandler.pointRespawn) {
            tooltip.add(ChatFormatting.RED + "The Block is disabled in config");
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }
}
