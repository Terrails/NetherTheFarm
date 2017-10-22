package terrails.netherthefarm.init;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import terrails.netherthefarm.blocks.*;
import terrails.netherthefarm.items.block.ItemBlockTank;
import terrails.terracore.registry.BlockRegistry;

@Mod.EventBusSubscriber
public class ModBlocks extends BlockRegistry {

    public static Block HYDROOFARM;
    public static Block SPAWN_POINT;
    public static Block OBELISK;
    public static Block TANK;
    public static Block SOUL_SOIL;

    public static void init() {
        SPAWN_POINT = addBlock(new BlockSpawnPoint("spawn_point"));
        TANK = addBlock(new BlockTank("tank"));
        HYDROOFARM = addBlock(new BlockHydrooFarm("hydroofarm"));
        OBELISK = addBlock(new BlockObelisk("obelisk"));
        SOUL_SOIL = addBlock(new BlockSoulSoil("soul_soil"));
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        ((BlockObelisk) OBELISK).initModel();
        ((BlockTank) TANK).initModel();
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(getBlocks());
    }
    @SubscribeEvent
    public static void registerItemBlocks(net.minecraftforge.event.RegistryEvent.Register<Item> event) {
        for (Block block : getBlocks()) {
            if (block.getRegistryName() != null) {
                if (block != TANK)
                    event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
                else
                    event.getRegistry().register(new ItemBlockTank(TANK).setRegistryName(block.getRegistryName()));
            }
        }
    }
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModBlocks.initModels();
        for (Block block : getBlocks()) {
            if (block.getRegistryName() != null)
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
        }
    }
}
