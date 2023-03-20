package general;

import java.util.List;

public class Intersection {
    public final boolean intersects;
    public final Side side;

    public Intersection(boolean intersects, Side side) {
        this.intersects = intersects;
        this.side = side;
    }
}
