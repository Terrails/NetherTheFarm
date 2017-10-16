package terrails.netherthefarm.blocks;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import terrails.netherthefarm.Constants;
import terrails.netherthefarm.NetherTheFarm;
import terrails.netherthefarm.config.ConfigHandler;
import terrails.netherthefarm.world.data.CustomWorldData;
import terrails.terracore.block.BlockBase;

import javax.annotation.Nullable;
import java.util.List;

public class BlockSpawnPoint extends BlockBase {

    public BlockSpawnPoint(String name) {
        super(Material.IRON, name);
        setBlockUnbreakable();
        setCreativeTab(Constants.NTF_TAB);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        if (!ConfigHandler.spawnPoint)
            tooltip.add(ChatFormatting.RED + "The Block is disabled in Config");
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosionIn) {
        return false;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        CustomWorldData worldData = CustomWorldData.get(worldIn);
        worldData.setPosX(pos.getX());
        worldData.setPosY(pos.getY());
        worldData.setPosZ(pos.getZ());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        CustomWorldData worldData = CustomWorldData.get(worldIn);
        worldData.setPosX(pos.getX());
        worldData.setPosY(pos.getY());
        worldData.setPosZ(pos.getZ());
        worldData.spawnPointDim(placer.world.provider.getDimension());
        if (!ConfigHandler.spawnPoint && placer instanceof EntityPlayer && !worldIn.isRemote){
            Constants.playerMessage((EntityPlayer) placer, "Obelisks and Spawn Points are disabled");
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        CustomWorldData worldData = CustomWorldData.get(world);
        worldData.setPosX(0);
        worldData.setPosY(0);
        worldData.setPosZ(0);
        worldData.spawnPointDim(0);
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @SuppressWarnings("deprecation")
    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.IGNORE;
    }
}
