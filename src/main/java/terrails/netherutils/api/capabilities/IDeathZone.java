package terrails.netherutils.api.capabilities;

public interface IDeathZone {

    int getDeathCounter();
    void setDeathCounter(int counter);

    int tickCounter();
    void tickCounter(int tick);
}
