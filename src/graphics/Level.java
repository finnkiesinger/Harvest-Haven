package graphics;

import exceptions.MapLoadException;
import game.Assets;
import general.Global;
import general.Vector2;
import general.Rectangle;
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
                sprite.draw(graphics);
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

    public String getName() {
        return name;
    }

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
                    List<Element> objectElements = objectGroup.getChildren("object");
                    for (Element objectElement : objectElements) {
                        String name = objectElement.getAttribute("name").getValue();
                        int x = (int) objectElement.getAttribute("x").getDoubleValue();
                        int y = (int) objectElement.getAttribute("y").getDoubleValue();
                        SpriteData data = Assets.instance.getSprite(name);
                        if (data.isAnimated()) {
                            AnimatedWorldObject animatedWorldObject = new AnimatedWorldObject(data.getNames(), x, y);
                            animatedWorldObject.addBoundingBox(data.getBoundingBox());
                            sprites.add(animatedWorldObject);
                        } else {
                            Sprite sprite = new Sprite(name, x, y);
                            sprite.addBoundingBox(data.getBoundingBox());
                            sprites.add(sprite);
                        }
                    }
                } else if (objectGroupName.equals("Collisions")) {
                    List<Element> collisionElements = objectGroup.getChildren("object");
                    for (Element collisionElement : collisionElements) {
                        int x = (int) (collisionElement.getAttribute("x").getDoubleValue() * Global.SPRITE_SCALE);
                        int y = (int) (collisionElement.getAttribute("y").getDoubleValue() * Global.SPRITE_SCALE);
                        int width = (int) (collisionElement.getAttribute("width").getDoubleValue() * Global.SPRITE_SCALE);
                        int height = (int) (collisionElement.getAttribute("height").getDoubleValue() * Global.SPRITE_SCALE);
                        collisionRectangles.add(new Rectangle(x, y, width, height));
                    }
                } else if (objectGroupName.equals("Spawn Points")) {
                    List<Element> spawnPointElements = objectGroup.getChildren("object");
                    for (Element spawnPointElement : spawnPointElements) {
                        String name = spawnPointElement.getAttribute("name").getValue();
                        if (name.equals("player_spawn")) {
                            int x = (int) (spawnPointElement.getAttribute("x").getDoubleValue() * Global.SPRITE_SCALE);
                            int y = (int) (spawnPointElement.getAttribute("y").getDoubleValue() * Global.SPRITE_SCALE);
                            playerSpawn = new Vector2(x, y);
                        }
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
}
