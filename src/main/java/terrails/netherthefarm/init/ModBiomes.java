package terrails.netherthefarm.init;

import net.minecraft.init.Biomes;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import terrails.netherthefarm.Constants;
import terrails.netherthefarm.world.WorldHellProviderNTF;
import terrails.netherthefarm.world.biome.biomes.BiomeHellSwamp;

@Mod.EventBusSubscriber
public class ModBiomes {

    public static Biome HELL_SWAMP;

    public static void init() {
        BiomeManager.removeBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(Biomes.BEACH, 50));
        BiomeManager.removeBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(Biomes.BIRCH_FOREST, 50));
        BiomeManager.removeBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(Biomes.OCEAN, 50));
        BiomeManager.removeBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(Biomes.DEEP_OCEAN, 50));
        BiomeManager.removeBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(Biomes.PLAINS, 50));
        BiomeManager.removeBiome(BiomeManager.BiomeType.DESERT, new BiomeManager.BiomeEntry(Biomes.DESERT, 50));
        BiomeManager.removeBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(Biomes.FOREST, 50));

        HELL_SWAMP = new BiomeHellSwamp(new Biome.BiomeProperties("Hell_Swamp").setTemperature(2.0F).setRainfall(0.0F).setRainDisabled());
    }

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event) {
        event.getRegistry().register(HELL_SWAMP);
    }

    @SubscribeEvent
    public static void worldLoad(WorldEvent.Load event) {
        if (event.getWorld().getWorldType().getName().equals(Constants.NETHER_SURVIVAL.getName())) {

            if (event.getWorld().provider.getDimension() == -1) {
                DimensionManager.unregisterDimension(-1);
                DimensionType.register("hell", "", -1, WorldHellProviderNTF.class, false);
                DimensionManager.registerDimension(-1, DimensionType.NETHER);
            }
        }
    }
}
