package general;

public class Trigger implements Cloneable {
    private Rectangle boundingBox;
    private OnTrigger onTrigger = null;

    public Trigger(Rectangle boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setBoundingBox(Rectangle boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setOnTrigger(OnTrigger onTrigger) {
        this.onTrigger = onTrigger;
    }

    public void trigger() {
        if (onTrigger != null) {
            onTrigger.trigger();
        }
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    @Override
    public Trigger clone() {
        try {
            Trigger clone = (Trigger) super.clone();
            clone.boundingBox = boundingBox.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
