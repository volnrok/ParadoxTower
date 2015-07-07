package gameObjects;

import game.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ListIterator;

import java.util.*;

import javax.imageio.*;
import util.*;

public class Player extends GameObject
{
    public static final int X_OFFSET = 12; //the coordinates of the player relative to the image
    public static final int Y_OFFSET = 22; //basically where the player's feet are relative to image location

    public boolean getInput = true;
    //public boolean isAlive = true;

    public Vector2 moveTowards = new Vector2();
    private boolean heroMode = false; //is the player drawn as scientist?
    public void setMode(boolean b) {
        heroMode = b;
    }

    LinkedList<Color> keys = new LinkedList<Color>();
    LinkedList<Color> savedKeys = new LinkedList<Color>();
    boolean hasStunRay = false;
    boolean shooting = false;

    //subframes for animation
    public static final int WALK1 = 0;
    public static final int WALK2 = 1;
    public static final int WALK3 = 2;
    public static final int IDLE1 = 3;
    public static final int NUM_FRAMES = 4; //the number of frames

    private static BufferedImage[] sciPics;
    private static BufferedImage[] pics;

    //information about player's state
    private int frame;
    private int dir;
    private int frameCounter; //how long the player's been doing this action, used to calculate frame
    private boolean isWalking; //whether or not the player walked this frame
    public void faceDir(int d) {
    	if(0 <= d && d <= 3) {dir = d;}
    }

    private boolean playerWon = false;
    public boolean HasPlayerWon() {return playerWon;}
    public void WinAcknowledged() {playerWon = false;}
    
    private boolean playerCaptured = false;
    public boolean IsPlayerCaptured() {return playerCaptured;}
    public void CaptureAcknowledged() {playerCaptured = false;}
    
    private boolean robotCollisions = true;
    public void setRobotCollisions(boolean val) {robotCollisions = val;}


    Vector2 position = new Vector2();
    Game game;

    static {
        pics = new BufferedImage[NUM_FRAMES * 4]; //one for every direction
        sciPics = new BufferedImage[NUM_FRAMES * 4]; //one for every direction

        for(int d = 0; d < 4; d++) {
            for(int f = 0; f < NUM_FRAMES; f++) {
                pics[getFrame(d, f)] = FileInput.loadImage(imgFileName(d, f));
                sciPics[getFrame(d, f)] = FileInput.loadImage(imgFileName2(d, f));
            }
        }
    }

    public Player(Level level, Game newGame)
    {
        super(level, new GridCoordinate(0, 0));
        game = newGame;
        frame = 0;
        dir = 0;
        frameCounter = 0;
        position.X = (location.X + 0.5f) * Level.TILE_SIZE;
        position.Y = (location.Y + 0.5f) * Level.TILE_SIZE;
    }
    public Player(Level l, Game newGame, GridCoordinate location)
    {
        super(l, location);
        game = newGame;
        frame = 0;
        dir = 0;
        frameCounter = 0;
        position.X = (location.X + 0.5f) * Level.TILE_SIZE;
        position.Y = (location.Y + 0.5f) * Level.TILE_SIZE;
    }

    protected void Position(Vector2 position){
        this.position = position.Copy();
        this.location = level.ScreenToGrid(this.position);
    }
    public Vector2 Position()
    {
        return position;
    }
    public void Location(GridCoordinate location){
        this.location = location.Copy();
        this.position = level.CentreGridToScreen(this.location);
    }

    public void AddKey(Color c)
    {
        keys.add(c);
    }

    public LinkedList<Color> GetKeys()
    {
        return keys;
    }
    
    public void saveKeys() {
    	savedKeys = (LinkedList<Color>) keys.clone();
    }
    
    public void loadKeys() {
    	keys = (LinkedList<Color>) savedKeys.clone();
    }
    
    public void noKeys() {
    	keys = new LinkedList<Color>();
    }
    
    public void stopMoving() {
    	moveTowards = position.Copy();
    }

    public void Update()
    {
        InputHandler input = game.GetInput();

        Vector2 direction = new Vector2();

        boolean wasWalking = isWalking;
        isWalking = false;

        if (getInput)
        {
            if (input.IsKeyDown(KeyEvent.VK_W)) {
                direction.Y = -1;
                dir = DIR_UP;
                isWalking = true;
            }
            if (input.IsKeyDown(KeyEvent.VK_S)) {
                direction.Y = 1;
                dir = DIR_DOWN;
                isWalking = true;
            }
            if (input.IsKeyDown(KeyEvent.VK_A)) {
                direction.X = -1;
                dir = DIR_LEFT;
                isWalking = true;
            }
            if (input.IsKeyDown(KeyEvent.VK_D)) {
                direction.X = 1;
                dir = DIR_RIGHT;
                isWalking = true;
            }
            if (input.IsKeyDown(KeyEvent.VK_E)){
                shooting = true;
            }

            if (input.IsKeyDown(KeyEvent.VK_B))
            {
                direction.X = direction.X;
            }
        }
        else
        {
            direction = moveTowards.Plus(position.Times(-1));
            if (direction.Length() < 1)
            {
                direction = new Vector2();
                position = moveTowards.Copy();
            }


            if (direction.X > 0)
            {
                dir = DIR_RIGHT;
                isWalking = true;
            }
            else if (direction.X < 0)
            {
                dir = DIR_LEFT;
                isWalking = true;
            }
            else if (direction.Y > 0)
            {
                dir = DIR_DOWN;
                isWalking = true;
            }
            else if (direction.Y < 0)
            {
                dir = DIR_UP;
                isWalking = true;
            }
        }

        direction.Normalize();

        //do frame calculations
        if(isWalking != wasWalking) {frameCounter = 0;} //reset if changed from moving to not moving, or vice versa

        if(isWalking) {
            frameCounter %= 32;
            frame = frameCounter / 8 + 1;

            //eight frames neutral, eight frames one foot, eight frames neutral, eight frames other foot
            if(frame == 2) {frame = 0;}
            if(frame == 3) {frame = 2;}
            if(frame == 4) {frame = 0;}
        } else {
            frame = IDLE1;
        }

        frameCounter++;

        if (shooting){
            shooting = false;
            //TODO add projectile
        }

        GridCoordinate oldLocation = location;

        position.PlusEquals(direction);

        location = level.ScreenToGrid(position);

        if (!location.Equals(oldLocation))
        {
            boolean canGoThere = level.spaceAvailable(location);

            if (!canGoThere)
            {
                position.PlusEquals(direction.Times(-1));
                location = oldLocation;

                position.X += direction.X;
                location = level.ScreenToGrid(position);

                if (!location.Equals(oldLocation))
                {
                    canGoThere = level.spaceAvailable(location);

                    if (!canGoThere)
                    {
                        position.X -= direction.X;
                        location = oldLocation;

                        position.Y += direction.Y;
                        location = level.ScreenToGrid(position);

                        if (!location.Equals(oldLocation))
                        {
                            canGoThere = level.spaceAvailable(location);

                            if (!canGoThere)
                            {
                                position.Y -= direction.Y;
                                location = oldLocation;
                            }
                        }
                    }
                }
            }

            {
                ListIterator<GridCoordinate> i = level.endingLocations.listIterator();

                while (i.hasNext())
                {
                    if (location.Equals(i.next()))
                    {
                        playerWon = true;
                    }
                }
            }
        }

        if (input.IsKeyHit(KeyEvent.VK_SPACE) || input.IsKeyHit(KeyEvent.VK_ENTER))
        {         
            int dx = 0;
            int dy = 0;

            switch (dir)
            {
                case DIR_RIGHT:
                    dx = 1;
                    break;
                case DIR_LEFT:
                    dx = -1;
                    break;
                case DIR_UP:
                    dy = -1;
                    break;
                case DIR_DOWN:
                    dy = 1;
                    break;
            }

            LinkedList<GameObject> objectsAhead = level.objectsAt(location.Plus(new GridCoordinate(dx, dy)));

            {
                ListIterator<GameObject> i = objectsAhead.listIterator();

                while (i.hasNext())
                {
                    GameObject current = i.next();

                    if (current instanceof Door)
                    {
                        boolean hadAKey = false;

                        Door theDoor = (Door)current;

                        Color doorColor = theDoor.GetColor();

                        ListIterator j = keys.listIterator();

                        while (j.hasNext())
                        {
                            if (j.next().equals(doorColor))
                            {
                                j.remove();
                                theDoor.Open();
                                hadAKey = true;
                                break;
                            }
                        }

                        if (!hadAKey)
                        {
                            level.scriptHandler.AddCommand("disable-player");
                            level.scriptHandler.AddCommand("say You do not have the required key.");
                            level.scriptHandler.AddCommand("enable-player");
                        }
                    }
                }
            }
        }

        LinkedList<GameObject> objectsAhead = level.objectsAt(location);

        {
            ListIterator<GameObject> i = objectsAhead.listIterator();

            while (i.hasNext())
            {
                GameObject current = i.next();

                if (current instanceof CollectibleItem)
                {
                    ((CollectibleItem)current).RunScript();
                }
            }
        }
        
        LinkedList<ScriptTrigger> triggers = level.triggers;

        {
            ListIterator<ScriptTrigger> i = triggers.listIterator();

            while (i.hasNext())
            {
            	ScriptTrigger current = i.next();

                if (location.X >= current.lower.X && location.X <= current.upper.X
                        && location.Y >= current.lower.Y && location.Y <= current.upper.Y)
                {
                    current.RunScript();
                    i.remove();
                }
            }
        }
        
        if(robotCollisions) {
	        LinkedList<Robot> nearRobots = new LinkedList<Robot>();
	
	        for (int x = -1; x <= 1; x++)
	        {
	            for (int y = -1; y <= 1; y++)
	            {
	                LinkedList<GameObject> currentObjects = level.objectsAt(new GridCoordinate(location.X + x, location.Y + y));
	
	                ListIterator<GameObject> i = currentObjects.listIterator();
	
	                while (i.hasNext())
	                {
	                    GameObject current = i.next();
	
	                    if (current instanceof Robot)
	                    {
	                        nearRobots.add((Robot)current);
	                    }
	                }
	            }
	        }
	
	        {
	            ListIterator<Robot> i = nearRobots.listIterator();
	
	            while (i.hasNext())
	            {
	                Robot current = i.next();
	
	                Vector2 difference = current.position.Plus(position.Times(-1));
	
	                if (difference.Length() < 13)
	                {
	                    playerCaptured = true;
	                    break;
	                }
	            }
	        }
        }

    }

    public void Draw(Graphics g)
    {
        g.setColor(Color.WHITE);
        //g.fillRect((int)position.X, (int)position.Y, 50, 50);

        if(heroMode) {
            g.drawImage(sciPics[getFrame(dir, frame)], (int)position.X - X_OFFSET, (int)position.Y - Y_OFFSET,null);
        } else {
            g.drawImage(pics[getFrame(dir, frame)], (int)position.X - X_OFFSET, (int)position.Y - Y_OFFSET, null);
        }
    }

    //combines direction and frame into a single index
    private static int getFrame(int d, int f) {
        return d * NUM_FRAMES + f;
    }

    //uses direction and frame to figure out what image to use for this frame
    private static String imgFileName(int d, int f) {
        String fpath = null;
        switch(f) {
            //if the frame is walk1 up to walk 3, return "walk(dir)(f).png"
            case WALK1: case WALK2: case WALK3:
                fpath = "img\\player\\walk" + d + f + ".png"; break;
            case IDLE1:
                fpath = "img/player/idle" + d + "0.png"; break;
        }

        //if the frame is unrecognized, return nothing
        return fpath;
    }

    //uses direction and frame to figure out what image to use for this frame
    private static String imgFileName2(int d, int f) {
        String fpath = null;
        switch(f) {
            //if the frame is walk1 up to walk 3, return "walk(dir)(f).png"
            case WALK1: case WALK2: case WALK3:
                fpath = "img\\scientist\\walk" + d + f + ".png"; break;
            case IDLE1:
                fpath = "img/scientist/idle" + d + "0.png"; //System.out.println(fpath); break;
        }

        //if the frame is unrecognized, return nothing
        return fpath;
    }


    public boolean Collides() {
        return true;
    }
}
