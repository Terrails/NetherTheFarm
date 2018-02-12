package terrails.netherutils.entity.capabilities.deathzone;

import terrails.netherutils.api.capabilities.IDeathZone;

public class DeathZone implements IDeathZone {

    private int deathZoneCounter = -2;
    private int tickCounter;

    @Override
    public int getDeathCounter() {
        return this.deathZoneCounter;
    }

    @Override
    public void setDeathCounter(int counter) {
        this.deathZoneCounter = counter;
    }

    @Override
    public int tickCounter() {
        return this.tickCounter;
    }

    @Override
    public void tickCounter(int tick) {
        this.tickCounter = tick;
    }
}
