package terrails.netherthefarm.world.biome.biomes;

import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeHell;
import net.minecraftforge.common.BiomeManager;

public class BiomeHellSwamp extends BiomeHell {

    public BiomeHellSwamp(BiomeProperties properties) {
        super(properties);
        this.topBlock = Blocks.BONE_BLOCK.getDefaultState();
        this.fillerBlock = Blocks.COAL_BLOCK.getDefaultState();
        setRegistryName("Hell_Swamp");
        BiomeManager.addBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(this, 10));
    }
}
