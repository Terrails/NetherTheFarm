package terrails.netherthefarm.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;

public class WorldTypeNetherSurvival extends WorldType {

    public WorldTypeNetherSurvival() {
        super("NetherSurvival");
    }

    @Override
    public int getMinimumSpawnHeight(World world) {
        return 1;
    }

    @Override
    public int getSpawnFuzz(WorldServer world, MinecraftServer server) {
        return 100;
    }
}
