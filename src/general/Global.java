package general;

public class Global {
    public static final String fileSeparator = System.getProperty("file.separator");
    public static final String projectDirectory = System.getProperty("user.dir");
    public static final String resourcesDirectory = projectDirectory + fileSeparator + "resources";

    public static final double SPRITE_SCALE = 2.5;
}
