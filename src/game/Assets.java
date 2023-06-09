package game;

import general.Global;
import general.Rectangle;
import general.Trigger;
import graphics.SpriteData;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
                    boolean isAnimated = false;
                    if (spriteElement.getAttribute("animated") != null) {
                        isAnimated = spriteElement.getAttribute("animated").getBooleanValue();
                    }
                    SpriteData spriteData;
                    if (!isAnimated) {
                        InputStream spriteStream = loader.getResourceAsStream("sprites" + Global.fileSeparator + name + ".png");
                        if (spriteStream == null) {
                            throw new IOException("sprites/" + name + ".png not found");
                        }

                        images.put(name, ImageIO.read(spriteStream));

                        spriteData = new SpriteData(name);



                    } else {
                        List<String> frames = new ArrayList<>();
                        int i = 0;
                        while (true) {
                            InputStream spriteStream = loader.getResourceAsStream("sprites" + Global.fileSeparator + name + Global.fileSeparator + i + ".png");
                            if (spriteStream == null) {
                                break;
                            }
                            String frameName = name + Global.fileSeparator + i;
                            images.put(frameName, ImageIO.read(spriteStream));
                            frames.add(frameName);
                            i++;
                        }
                        if (frames.isEmpty()) {
                            throw new IOException("sprites/" + name + " not found");
                        }

                        spriteData = new SpriteData(frames, true);
                    }
                    
                    Element collision = spriteElement.getChild("collision");
                    if (collision != null) {
                        int x = collision.getAttribute("startX").getIntValue();
                        int y = collision.getAttribute("startY").getIntValue();
                        int width = collision.getAttribute("endX").getIntValue() - x;
                        int height = collision.getAttribute("endY").getIntValue() - y;
                        spriteData.addBoundingBox(x, y, width, height);
                    }
                    Element triggerElement = spriteElement.getChild("trigger");
                    if (triggerElement != null) {
                        int x = triggerElement.getAttribute("startX").getIntValue();
                        int y = triggerElement.getAttribute("startY").getIntValue();
                        int width = triggerElement.getAttribute("endX").getIntValue() - x;
                        int height = triggerElement.getAttribute("endY").getIntValue() - y;
                        Rectangle rectangle = new Rectangle(x, y, width, height);
                        Trigger trigger = new Trigger(rectangle);
                        spriteData.addTrigger(trigger);
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
