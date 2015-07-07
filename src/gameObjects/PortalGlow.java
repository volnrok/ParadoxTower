package gameObjects;

import game.*;
import java.awt.*;
import java.awt.image.*;
import util.*;

public class PortalGlow extends GameObject {
    
    public static final int X_OFFSET = 12;
    public static final int Y_OFFSET = 22;
    
    private static final int NUM_FRAMES = 6;
    private static final BufferedImage[] pics;
    
    private int frame = 0;
    private int frameCounter = 0;
    private int bob = 0;
    
    static {
        pics = new BufferedImage[NUM_FRAMES];
        for(int i = 0; i < NUM_FRAMES; i++) {
            pics[i] = FileInput.loadImage("img\\tiles\\glow" + i + ".png");
        }
    }
    
    public PortalGlow(GridCoordinate loc) {
        location = loc;
    }
    
    public void Draw(Graphics g) {
        g.drawImage(pics[frame], location.X*Level.TILE_SIZE + 6 - X_OFFSET, location.Y*Level.TILE_SIZE + 6 - Y_OFFSET + bob, null);
    }
    
    public void Update() {
        frameCounter %= 40;
        bob = 0;
        bob = frameCounter / 10;
        if(bob == 2) {bob = 0;}
        if(bob == 3) {bob = -1;}
        
        if(frameCounter % 2 == 0) {
            frame += (int) (Math.random() * 3);
            frame %= NUM_FRAMES;
        }
        
        frameCounter++;
    }
    
    public boolean Collides() {return false;}
}