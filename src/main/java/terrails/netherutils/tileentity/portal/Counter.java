package terrails.netherutils.tileentity.portal;

public class Counter {
    private float i = 0;

    public float increment() {
        return increment(1);
    }
    public float increment(int amount) {
        i += amount;
        return i;
    }

    public float decrement() {
        return decrement(1);
    }

    public float decrement(int amount) {
        i -= amount;
        return i;
    }

    public void clear() {
        i = 0;
    }

    public float value() {
        return i;
    }

    public void set(int amount) {
        this.i = amount;
    }
}