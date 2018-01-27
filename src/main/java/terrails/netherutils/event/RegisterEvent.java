package terrails.netherutils.event;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherutils.blocks.obelisk.BlockObelisk;
import terrails.netherutils.blocks.pedestal.BlockPedestal;
import terrails.netherutils.blocks.pedestal.ItemBlockPedestal;
import terrails.netherutils.blocks.portal.end.BlockPortalOverride;
import terrails.netherutils.blocks.portal.nether.BlockPortal;
import terrails.netherutils.blocks.portal.nether.BlockPortalSlave;
import terrails.netherutils.blocks.portal.nether.ItemBlockPortal;
import terrails.netherutils.blocks.tank.BlockTank;
import terrails.netherutils.blocks.tank.ItemBlockTank;
import terrails.netherutils.blocks.wood.*;
import terrails.netherutils.init.ModBlocks;
import terrails.netherutils.init.ModItems;

import java.util.Objects;

import static terrails.netherutils.init.ModBlocks.*;

public class RegisterEvent {

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(ModBlocks.get());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        for (Block block : ModBlocks.get()) {
            if (TANK == block) event.getRegistry().register(new ItemBlockTank(block));
            else if (block == PLANKS) event.getRegistry().register(new ItemBlockPlanks(PLANKS).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
            else if (block == LOG) event.getRegistry().register(new ItemBlockLog(LOG).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
            else if (block == LEAVES) event.getRegistry().register(new ItemBlockLeaf(LEAVES).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
            else if (block == SAPLING) event.getRegistry().register(new ItemBlockSapling(SAPLING).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
            else if (block == PEDESTAL) event.getRegistry().register(new ItemBlockPedestal(PEDESTAL).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
            else if (block == PORTAL_NETHER_MASTER) event.getRegistry().register(new ItemBlockPortal(PORTAL_NETHER_MASTER));
            else if (block == PORTAL_END_MASTER) event.getRegistry().register(new terrails.netherutils.blocks.portal.end.ItemBlockPortal(PORTAL_END_MASTER));
            else if (!(block instanceof BlockPortalOverride)) event.getRegistry().register(new ItemBlock(block).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
        }
        event.getRegistry().registerAll(ModItems.get());
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        // Custom Models
        ((BlockTank) TANK).initModel();
        ((BlockObelisk) OBELISK).initModel();
        ((BlockPortal) PORTAL_NETHER_MASTER).initModel();
        ((BlockPortalSlave) PORTAL_NETHER_SLAVE).initModel();
        ((terrails.netherutils.blocks.portal.end.BlockPortal) PORTAL_END_MASTER).initModel();
        ((terrails.netherutils.blocks.portal.end.BlockPortalSlave) PORTAL_END_SLAVE).initModel();
        ((BlockPedestal) PEDESTAL).initModel();

        WoodType.Init.initModel();

        // Default Models
        for (Block block : ModBlocks.get()) {
            if (block != PLANKS && block != LOG && block != LEAVES && block != SAPLING && block != PEDESTAL && !(block instanceof BlockPortalOverride)) {
                ModelResourceLocation location = new ModelResourceLocation(Objects.requireNonNull(block.getRegistryName()), "inventory");
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, location);
            }
        }
        for (Item item : ModItems.get()) {
            ModelResourceLocation location = new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory");
            ModelLoader.setCustomModelResourceLocation(item, 0, location);
        }
    }

    @SubscribeEvent
    public void fixBlockMappings(RegistryEvent.MissingMappings<Block> event) {
        for (RegistryEvent.MissingMappings.Mapping<Block> mapping : event.getAllMappings()) {
            if (mapping.key.equals(new ResourceLocation("minecraft", "end_portal"))) {
                mapping.remap(ModBlocks.END_PORTAL_OVERRIDDEN);
            }
        }
    }

    @SubscribeEvent
    public void fixItemMappings(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getAllMappings()) {
            if (mapping.key.equals(new ResourceLocation("minecraft", "end_portal"))) {
                mapping.remap(Item.getItemFromBlock(ModBlocks.END_PORTAL_OVERRIDDEN));
            }
        }
    }
}
