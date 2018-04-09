package terrails.netherutils.event;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
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

import java.util.Arrays;
import java.util.Objects;

import static terrails.netherutils.init.ModBlocks.*;

public class RegisterEvent {

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ModBlocks.END_PORTAL_OVERRIDDEN);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        OreDictionary.registerOre("treeLeaves", new ItemStack(ModBlocks.LEAVES, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("treeWood", new ItemStack(ModBlocks.LOG, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("treeSapling", new ItemStack(ModBlocks.SAPLING, 1, OreDictionary.WILDCARD_VALUE));
        OreDictionary.registerOre("plankWood", new ItemStack(ModBlocks.PLANKS, 1, OreDictionary.WILDCARD_VALUE));
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
