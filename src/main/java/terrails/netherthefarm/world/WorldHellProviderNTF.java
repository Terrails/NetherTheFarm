package terrails.netherthefarm.world;

import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldHellProviderNTF extends WorldProviderHell {

    public void init() {
        this.biomeProvider = new BiomeProvider(this.world.getWorldInfo());
        this.doesWaterVaporize = false;
        this.nether = true;
    }
}
