package graphics;

import exceptions.MapLoadException;
import game.Assets;
import general.Global;
import general.Rectangle;
import general.Trigger;
import general.Vector2;
import lighting.Lighting;
import lighting.PointLight;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Level class.
 * Used to load and draw levels.
 *
 * @author Finn Kiesinger
 */
public class Level {
    private final String name;

    private final List<Layer> layers = new ArrayList<>();
    private final List<Sprite> sprites = new ArrayList<>();
    private final List<Sprite> actors = new ArrayList<>();
    private final List<Rectangle> collisionRectangles = new ArrayList<>();

    private Vector2 playerSpawn;

    public Level(String mapName) throws MapLoadException {
        this.name = mapName;
        loadMap(mapName);
    }

    /**
     * Draws the level.
     *
     * @param graphics The graphics object to draw on.
     */
    public void draw(Graphics2D graphics) {
        Vector2 position = Camera.main.apply(new Vector2(0, 0));
        for (Layer layer : layers) {
            BufferedImage layerImage = layer.getLayerImage();
            if (layerImage != null) {
                graphics.drawImage(layerImage, position.x, position.y, null);
            }
        }

        for (Sprite sprite : Stream.concat(sprites.stream(), actors.stream()).sorted().toList()) {
            if (Camera.main.isVisible(sprite)) {
                sprite.draw(graphics, Sprite.SPRITE);
            }
        }
    }

    public void update(long deltaTime) {
        for (Sprite sprite : sprites) {
            sprite.update(deltaTime);
        }
        for (Sprite actor : actors) {
            actor.update(deltaTime);
        }
    }

    public Vector2 getSize() {
        return layers.get(0).getSize();
    }

    public String getName() {
        return name;
    }

    /**
     * Loads a map from a .tmx file.
     *
     * @param mapName The name of the map.
     * @throws MapLoadException If the map could not be loaded.
     */
    private void loadMap(String mapName) throws MapLoadException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("maps" + Global.fileSeparator + mapName + ".tmx")) {
            if (is == null) {
                throw new IOException(mapName);
            }

            Document doc = new SAXBuilder().build(is);
            Element root = doc.getRootElement();

            // Tileset properties
            Element tileset = root.getChild("tileset");
            int tileWidth = tileset.getAttribute("tilewidth").getIntValue();
            int tileHeight = tileset.getAttribute("tileheight").getIntValue();
            int columns = tileset.getAttribute("columns").getIntValue();

            //Load tileset image
            String src = tileset.getChild("image").getAttribute("source").getValue().replace("../", "");
            BufferedImage sourceImage = Assets.instance.getImage(src);

            List<Element> layerElements = root.getChildren("layer");

            for (Element layerElement : layerElements) {
                // Layer index and name
                int index = layerElement.getAttribute("id").getIntValue();
                String name = layerElement.getAttribute("name").getValue();
                int width = layerElement.getAttribute("width").getIntValue();
                int height = layerElement.getAttribute("height").getIntValue();
                Layer layer = new Layer(index, name, width, height, (int) (tileWidth * Global.SPRITE_SCALE), (int) (tileHeight * Global.SPRITE_SCALE), src);

                // Layer data, i.e. all tiles
                Element data = layerElement.getChild("data");
                List<Element> tileElements = data.getChildren("tile");

                for (Element tileElement : tileElements) {
                    Attribute gidAttr = tileElement.getAttribute("gid");
                    if (gidAttr != null) {
                        int gid = gidAttr.getIntValue();
                        Tiles.addTile(gid, tileWidth, tileHeight, columns, sourceImage, src);
                        layer.addTile(gid);
                    } else {
                        layer.addTile(0);
                    }
                }
                layers.add(layer);
            }

            for (Layer layer : layers) {
                layer.createLayerImage();
            }

            // Load objects
            List<Element> objectGroups = root.getChildren("objectgroup");
            for (Element objectGroup : objectGroups) {
                String objectGroupName = objectGroup.getAttribute("name").getValue();

                if (objectGroupName.equals("Objects")) {
                    // Load all objects, i.e. sprites (static and animated)

                    List<Element> objectElements = objectGroup.getChildren("object");
                    for (Element objectElement : objectElements) {
                        String name = objectElement.getAttribute("name").getValue();
                        int x = (int) objectElement.getAttribute("x").getDoubleValue();
                        int y = (int) objectElement.getAttribute("y").getDoubleValue();
                        SpriteData data = Assets.instance.getSprite(name);
                        if (data.isAnimated()) {
                            AnimatedWorldObject animatedWorldObject = new AnimatedWorldObject(data.getNames(), x, y);
                            animatedWorldObject.addBoundingBox(data.getBoundingBox());
                            // Set trigger
                            if (data.getTrigger() != null) {
                                Trigger trigger = data.getTrigger().clone();
                                trigger.setOnTrigger(() -> animatedWorldObject.playAnimation("Interaction"));
                                animatedWorldObject.setTrigger(trigger);
                            }
                            sprites.add(animatedWorldObject);
                        } else {
                            Sprite sprite = new Sprite(name, x, y);
                            sprite.addBoundingBox(data.getBoundingBox());
                            if (data.getTrigger() != null) {
                                sprite.setTrigger(data.getTrigger().clone());
                            }
                            sprites.add(sprite);
                        }
                    }
                } else if (objectGroupName.equals("Collisions")) {
                    // Load all collision rectangles
                    List<Element> collisionElements = objectGroup.getChildren("object");
                    for (Element collisionElement : collisionElements) {
                        int x = (int) (collisionElement.getAttribute("x").getDoubleValue() * Global.SPRITE_SCALE);
                        int y = (int) (collisionElement.getAttribute("y").getDoubleValue() * Global.SPRITE_SCALE);
                        int width = (int) (collisionElement.getAttribute("width").getDoubleValue() * Global.SPRITE_SCALE);
                        int height = (int) (collisionElement.getAttribute("height").getDoubleValue() * Global.SPRITE_SCALE);
                        collisionRectangles.add(new Rectangle(x, y, width, height));
                    }
                } else if (objectGroupName.equals("Spawn Points")) {
                    // Load all spawn points
                    List<Element> spawnPointElements = objectGroup.getChildren("object");
                    for (Element spawnPointElement : spawnPointElements) {
                        String name = spawnPointElement.getAttribute("name").getValue();
                        if (name.equals("player_spawn")) {
                            int x = (int) (spawnPointElement.getAttribute("x").getDoubleValue() * Global.SPRITE_SCALE);
                            int y = (int) (spawnPointElement.getAttribute("y").getDoubleValue() * Global.SPRITE_SCALE);
                            playerSpawn = new Vector2(x, y);
                        }
                    }
                } else if (objectGroupName.equals("Lighting")) {
                    // Load all fixed light sources
                    List<Element> lightElements = objectGroup.getChildren("object");
                    for (Element lightElement : lightElements) {
                        int x = (int) (lightElement.getAttribute("x").getDoubleValue() * Global.SPRITE_SCALE);
                        int y = (int) (lightElement.getAttribute("y").getDoubleValue() * Global.SPRITE_SCALE);
                        Element propertiesElement = lightElement.getChild("properties");
                        List<Element> propertyElements = propertiesElement.getChildren("property");
                        int radius = 0;
                        double strength = 0;
                        Color color = null;
                        for (Element propertyElement : propertyElements) {
                            String propertyName = propertyElement.getAttribute("name").getValue();
                            if (propertyName.equals("radius")) {
                                radius = propertyElement.getAttribute("value").getIntValue();
                                radius *= Global.SPRITE_SCALE;
                            } else if (propertyName.equals("color")) {
                                String colorString = propertyElement.getAttribute("value").getValue();
                                colorString = "#" + colorString.substring(3);
                                color = Color.decode(colorString);
                            } else if (propertyName.equals("strength")) {
                                strength = propertyElement.getAttribute("value").getDoubleValue();
                            }
                        }
                        PointLight light = new PointLight(color, strength, radius, x, y);
                        Lighting.instance.addLight(light);
                    }
                }
            }
        } catch (IOException | JDOMException e) {
            throw new MapLoadException(mapName);
        }
    }

    public Vector2 getPlayerSpawn() {
        return playerSpawn;
    }

    public void addActor(Sprite actor) {
        actors.add(actor);
    }

    public List<Rectangle> getCollisionRects(Sprite exclude) {
        return Stream.concat(Stream.concat(sprites.stream(), actors.stream())
                .filter(sprite -> sprite.getBoundingBox() != null && sprite != exclude)
                .map(sprite -> new Rectangle(
                        sprite.getPosition().x + sprite.getBoundingBox().x,
                        sprite.getPosition().y + sprite.getBoundingBox().y,
                        sprite.getBoundingBox().width,
                        sprite.getBoundingBox().height
                )), collisionRectangles.stream()).toList();
    }

    public List<Sprite> getSpriteTriggers(Sprite exclude) {
        return Stream.concat(sprites.stream(), actors.stream())
                .filter(sprite -> sprite.getTrigger() != null && sprite != exclude)
                .toList();
    }
}
