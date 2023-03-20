package exceptions;

public class MapLoadException extends Exception {
    public MapLoadException(String mapName) {
        super(mapName);
    }

    @Override
    public String getMessage() {
        return "Could not load map: " + super.getMessage();
    }
}
