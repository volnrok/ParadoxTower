package gameObjects;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import game.*;
import util.*;

public class SceneryObject extends GameObject {
 private int width;
 private int height;
 private BufferedImage image;
 private boolean collision;
 
 //note that x and y location are based on bottom right corner due to perspective shifts
 private static HashMap<String, BufferedImage> imagelist = new HashMap<String, BufferedImage>();
 
 boolean hasBeenDrawn = false;
 public SceneryObject(Level level, GridCoordinate location, int w, int h, String loc, boolean c)
 {
  super (level, location);
  width = w;
  height = h;
  collision = c;
  
  if(imagelist.containsKey(loc)) {
      image = imagelist.get(loc);
  } else {
      image = FileInput.loadImage(loc);
      imagelist.put(loc, image);
  }
 }
 
 @Override
 public void Draw(Graphics g) {
  if (!hasBeenDrawn)
  {
   g.drawImage(image, (location.X + 1) * level.TILE_SIZE - image.getWidth(),
               (location.Y + 1) * level.TILE_SIZE - image.getHeight(), null);
   hasBeenDrawn = true;
  }
 }

 @Override
 public void Update() {
  hasBeenDrawn = false;
 }

 @Override
 public boolean Collides() {
  // TODO Auto-generated method stub
  return collision;
 }

}
