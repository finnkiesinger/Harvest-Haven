package game;

import general.Global;
import graphics.SpriteData;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Assets {
    public static Assets instance = new Assets();
    private boolean loaded;

    private final Map<String, BufferedImage> images = new HashMap<>();
    private final Map<String, SpriteData> sprites = new HashMap<>();

    private Assets() {}

    public void loadAssets() {
        if (loaded) {
            return;
        }

        try {
            ClassLoader loader = this.getClass().getClassLoader();
            try (InputStream is = loader.getResourceAsStream("character.png")) {
                if (is == null) {
                    throw new IOException("character.png not found");
                }
                images.put("character", ImageIO.read(is));
            }
            try (InputStream is = loader.getResourceAsStream("basic.png")) {
                if (is == null) {
                    throw new IOException("basic.png not found");
                }
                images.put("basic", ImageIO.read(is));
            }
            try (InputStream is = loader.getResourceAsStream("pants.png")) {
                if (is == null) {
                    throw new IOException("pants.png not found");
                }
                images.put("pants", ImageIO.read(is));
            }
            try (InputStream is = loader.getResourceAsStream("shoes.png")) {
                if (is == null) {
                    throw new IOException("shoes.png not found");
                }
                images.put("shoes", ImageIO.read(is));
            }
            try (InputStream is = loader.getResourceAsStream("hair/curly.png")) {
                if (is == null) {
                    throw new IOException("hair/curly.png not found");
                }
                images.put("hair", ImageIO.read(is));
            }
            try (InputStream is = loader.getResourceAsStream("tilesets" + Global.fileSeparator + "global.png")) {
                if (is == null) {
                    throw new IOException("tilesets/global.png not found");
                }
                images.put("tilesets/global.png", ImageIO.read(is));
            }
            try (InputStream is = loader.getResourceAsStream("sprites.xml")) {
                if (is == null) {
                    throw new IOException("sprites.xml not found");
                }

                Document doc = new SAXBuilder().build(is);
                Element root = doc.getRootElement();
                List<Element> spriteElements = root.getChildren("sprite");
                for (Element spriteElement : spriteElements) {
                    String name = spriteElement.getAttributeValue("name");

                    InputStream spriteStream = loader.getResourceAsStream("sprites" + Global.fileSeparator + name + ".png");
                    if (spriteStream == null) {
                        throw new IOException("sprites/" + name + ".png not found");
                    }
                    images.put(name, ImageIO.read(spriteStream));

                    SpriteData spriteData = new SpriteData(name);
                    Element collision = spriteElement.getChild("collision");
                    if (collision != null) {
                        int x = collision.getAttribute("startX").getIntValue();
                        int y = collision.getAttribute("startY").getIntValue();
                        int width = collision.getAttribute("endX").getIntValue() - x;
                        int height = collision.getAttribute("endY").getIntValue() - y;
                        spriteData.addBoundingBox(x, y, width, height);
                    }

                    sprites.put(name, spriteData);
                }
            } catch (JDOMException e) {
                throw new IOException("sprites.xml is not a valid XML file");
            }
            loaded = true;
        } catch (IOException exception) {
            System.err.println("File not found: " + exception.getMessage());
            loaded = false;
        }
    }

    public BufferedImage getImage(String name) {
        return images.get(name);
    }

    public SpriteData getSprite(String name) {
        return sprites.get(name);
    }
}
