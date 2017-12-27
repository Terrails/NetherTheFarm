package terrails.netherutils.blocks;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import terrails.netherutils.api.world.IWorldData;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.world.data.CustomWorldData;
import terrails.terracore.block.BlockBase;

import javax.annotation.Nullable;
import java.util.List;

public class BlockSpawnPoint extends BlockBase {

    public BlockSpawnPoint(String name) {
        super(Material.ROCK, name);
        setBlockUnbreakable();
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
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        if (!ConfigHandler.pointRespawn) {
            tooltip.add(ChatFormatting.RED + "The Block is disabled in Config");
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }
}