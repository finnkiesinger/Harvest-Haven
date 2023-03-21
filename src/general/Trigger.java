package general;

public class Trigger {
    private Rectangle boundingBox;
    private OnTrigger onTrigger;

    public Trigger(Rectangle boundingBox, OnTrigger onTrigger) {
        this.boundingBox = boundingBox;
        this.onTrigger = onTrigger;
    }

    public void setBoundingBox(Rectangle boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setOnTrigger(OnTrigger onTrigger) {
        this.onTrigger = onTrigger;
    }

    public void trigger() {
        onTrigger.trigger();
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public OnTrigger getOnTrigger() {
        return onTrigger;
    }
}
