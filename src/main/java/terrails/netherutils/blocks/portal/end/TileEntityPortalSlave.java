package terrails.netherutils.blocks.portal.end;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import terrails.netherutils.api.capabilities.IPortal;
import terrails.netherutils.api.portal.IPortalMaster;
import terrails.netherutils.api.portal.IPortalSlave;
import terrails.netherutils.blocks.portal.Counter;
import terrails.netherutils.blocks.portal.PortalRegistry;
import terrails.netherutils.entity.capabilities.portal.CapabilityPortal;
import terrails.netherutils.init.ModFeatures;
import terrails.netherutils.network.CPacketBoolean;
import terrails.netherutils.network.SPacketBoolean;
import terrails.netherutils.world.TeleporterNTF;
import terrails.netherutils.world.data.CustomWorldData;
import terrails.terracore.block.tile.TileEntityBase;

public class TileEntityPortalSlave extends TileEntityBase implements ITickable, IPortalSlave {
    
    private boolean oldActive;
    private boolean status;
    private BlockPos masterPos = BlockPos.ORIGIN;

    //-----------TESR Counters and Booleans-------------\\
    public Counter counterCircle = new Counter();
    public Counter counterTeleport = new Counter();
    public boolean isAtPosTopMiddleCircle;
    public boolean isAtPosSideCircles;

    public boolean isReadyToTeleport;
    //--------------------------------------------------\\

    @Override
    public void update() {
        if (getWorld().isRemote)
            return;

        boolean hasMaster = false;

        if (!CustomWorldData.get(getWorld()).hasRead()) {
            hasMaster = true;
        }

        if (PortalRegistry.LIST.size() > 0) {
            for (IPortalMaster master : PortalRegistry.LIST) {
                if (master.getDimension() == 0) {
                    hasMaster = true;
                    boolean bool = false;
                    for (IPortalMaster master1 : PortalRegistry.LIST) {
                        if (master1.getDimension() == 0 && master1.isActive()) {
                            bool = true;
                            break;
                        }
                    }
                    isActive(bool);
                    if (isActive()) {
                        doTeleportation();
                    }

                    if (oldActive != status) {
                        if (!getWorld().isRemote) {
                            sendStatus();
                            oldActive = status;
                        }
                    }
                }
            }
        }

        if (!hasMaster) {
            world.setBlockToAir(getPos());
            world.removeTileEntity(getPos());
        }
    }

    // == Packets == \\

    public void sendReadyToTeleport() {
        if (!getWorld().isRemote) {
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(isReadyToTeleport, getPos(), 2), getWorld().provider.getDimension());
        } else {
            ModFeatures.Network.WRAPPER.sendToServer(new SPacketBoolean(isReadyToTeleport, getPos(), 2));
        }
    }
    public void sendStatus() {
        if (!getWorld().isRemote) {
            ModFeatures.Network.WRAPPER.sendToDimension(new CPacketBoolean(isActive(), getPos(), 1), getWorld().provider.getDimension());
        } else {
            ModFeatures.Network.WRAPPER.sendToServer(new SPacketBoolean(isActive(), getPos(), 1));
        }
    }

    // == End == \\


    // == IPortalSlave == \\

    @Override
    public BlockPos getBlockPos() {
        return this.getPos();
    }

    @Override
    public void setMasterPos(BlockPos pos) {
        this.masterPos = pos;
    }

    @Override
    public BlockPos getMasterPos() {
        return this.masterPos;
    }

    @Override
    public int getDimension() {
        return world.provider.getDimension();
    }

    // == End == \\


    // == Basic & Rendering == \\

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().add(-5, -5, -5), getPos().add(5, 5, 5));
    }

    private void doTeleportation() {
        if (getWorld().provider.getDimension() != 1 || getWorld().isRemote)
            return;

        EntityPlayer player = getWorld().getClosestPlayer(getPos().getX() + 0.5, getPos().getY() + 0.75, getPos().getZ() + 0.5, 0.5, false);
        if (player instanceof EntityPlayerMP && isReadyToTeleport) {

            IPortal portalItem = player.getCapability(CapabilityPortal.PORTAL_CAPABILITY, null);
            if (portalItem != null) {
                BlockPos pos = BlockPos.ORIGIN;
                for (IPortalMaster master : PortalRegistry.LIST) {
                    if (master.getDimension() == 0) {
                        if (master.getBlockPos().equals(portalItem.lastMasterPos())) {
                            pos = master.getBlockPos();
                            break;
                        } else if (!master.getBlockPos().equals(BlockPos.ORIGIN))
                            pos = master.getBlockPos();
                    }
                }

                if (!pos.equals(BlockPos.ORIGIN)) {
                    TeleporterNTF.teleport((EntityPlayerMP) player, 0, pos.add(1, 0, 0), false);

                    this.isReadyToTeleport = false;
                    sendReadyToTeleport();
                }
            }
        }
    }

    public void isActive(boolean isActive) {
        this.status = isActive;
    }
    public boolean isActive() {
        return this.status;
    }

    // == End == \\


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        isActive(compound.getBoolean("Active"));
   //     setMasterPos(new BlockPos(compound.getInteger("xMaster"), compound.getInteger("yMaster"), compound.getInteger("zMaster")));
        super.readFromNBT(compound);
    }
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("Active", isActive());
    //    compound.setInteger("xMaster", getMasterPos().getX());
   //     compound.setInteger("yMaster", getMasterPos().getY());
    //    compound.setInteger("zMaster", getMasterPos().getZ());
        return super.writeToNBT(compound);
    }
}