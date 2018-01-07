package terrails.netherutils.event;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherutils.blocks.*;
import terrails.netherutils.blocks.item.*;
import terrails.netherutils.blocks.wood.WoodType;
import terrails.netherutils.init.ModBlocks;
import terrails.netherutils.init.ModItems;

import java.util.Objects;

import static terrails.netherutils.init.ModBlocks.*;

public class RegisterEvent {

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(ModBlocks.getBlocks());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        for (Block block : ModBlocks.getList()) {
            if (TANK == block) event.getRegistry().register(new ItemBlockTank(block));
            else if (block == PLANKS) event.getRegistry().register(new ItemBlockPlanks(PLANKS).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
            else if (block == LOG) event.getRegistry().register(new ItemBlockLog(LOG).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
            else if (block == LEAVES) event.getRegistry().register(new ItemBlockLeaf(LEAVES).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
            else if (block == SAPLING) event.getRegistry().register(new ItemBlockSapling(SAPLING).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
            else if (block == PORTAL_MASTER) event.getRegistry().register(new ItemBlockPortal(PORTAL_MASTER));
            else event.getRegistry().register(new ItemBlock(block).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
        }
        event.getRegistry().registerAll(ModItems.getItems());
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        // Custom Models
        ((BlockTank) TANK).initModel();
        ((BlockObelisk) OBELISK).initModel();
        ((BlockPortal) PORTAL_MASTER).initModel();
        ((BlockPortalSlave) PORTAL_SLAVE).initModel();
        ((BlockPedestal) PEDESTAL).initModel();

        WoodType.Init.initModel();

        // Default Models
        for (Block block : ModBlocks.getList()) {
            if (block != PLANKS && block != LOG && block != LEAVES && block != SAPLING) {
                ModelResourceLocation location = new ModelResourceLocation(Objects.requireNonNull(block.getRegistryName()), "inventory");
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, location);
            }
        }
        for (Item item : ModItems.getList()) {
            ModelResourceLocation location = new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory");
            ModelLoader.setCustomModelResourceLocation(item, 0, location);
        }
    }
}
