package terrails.netherthefarm.blocks;

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
import terrails.netherthefarm.items.block.ItemBlockTank;
import terrails.terracore.registry.BlockRegistry;

@Mod.EventBusSubscriber
public class ModBlocks extends BlockRegistry {

    public static BlockWater HYDROOFARM;
    public static BlockSpawnPoint SPAWN_POINT;
    public static BlockObelisk OBELISK;
    public static BlockTank TANK;

    public static void init() {
        SPAWN_POINT = (BlockSpawnPoint) addBlock("spawn_point", new BlockSpawnPoint("spawn_point"));
        TANK = (BlockTank) addBlock("tank", new BlockTank("tank"));
        HYDROOFARM = (BlockWater) addBlock("hydroofarm", new BlockWater("hydroofarm"));
        OBELISK = (BlockObelisk) addBlock("obelisk", new BlockObelisk("obelisk"));
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        OBELISK.initModel();
        TANK.initModel();
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(getBlocks());
    }

    @SubscribeEvent
    public static void registerItems(net.minecraftforge.event.RegistryEvent.Register<Item> event) {
        for (Block block : getBlocks()) {
            if (block != TANK)
                event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
            else
                event.getRegistry().register(new ItemBlockTank(TANK).setRegistryName(block.getRegistryName()));
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
