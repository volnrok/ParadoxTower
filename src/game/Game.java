package game;

import gameStates.*;
import util.*;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import java.util.*;

@SuppressWarnings("serial")
public class Game extends JFrame
{
    protected Stack<GameState> gameStates = new Stack<GameState>();
    private Insets insets;
    private BufferedImage backBuffer;
    private InputHandler input;
    public AudioPlayer audio;

    private boolean isRunning = true;

    public final int width = 800;
    public final int height = 600;
    private int scale = 1;

    public final int fps = 60;

    public static void main(String[] args)
    {
        new Game().Run();
    }

    protected boolean Init()
    {
        setVisible(true);
        setSize(width, height);
        setResizable(false);
        insets = getInsets();
        setSize(insets.left + width + insets.right, insets.top + height + insets.bottom);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //the +1's are to make sure the screen is covered by the buffer
        backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        input = new InputHandler(this);
        
        audio = new AudioPlayer();
        //audio.repeat = true;
        
        gameStates.push(new GameState_MainMenu(this));

        return true;
    }

    public void Stop()
    {
        isRunning = false;
    }

    private void ApplyBackBuffer()
    {
        getGraphics().drawImage(backBuffer, insets.left, insets.top, this);
    }

    public void Run()
    {
        if (Init())
        {
            while (isRunning)
            {
                long time = System.currentTimeMillis();

                gameStates.peek().Update();

                //Scale the graphics for increased "retroness"
                Graphics2D g = (Graphics2D) backBuffer.getGraphics();
                //g.scale(scale, scale);

                g.setColor(Color.BLACK);
                g.clearRect(0, 0, width, height);
                gameStates.peek().Draw(g);
                ApplyBackBuffer();

                if (!gameStates.peek().IsAlive())
                {
                    gameStates.pop();

                    if (gameStates.isEmpty())
                    {
                        isRunning = false;
                    }
                }

                long timeDifference = System.currentTimeMillis() - time;
                long delayTime = 1000 / fps - timeDifference;

                if (delayTime > 0)
                {
                    try
                    {
                        Thread.sleep(delayTime);
                    }
                    catch(Exception e) {e.printStackTrace();}
                }
            }
        }

        dispose();
    }
    
    public void dispose() {
     //audio.stop();
     
     super.dispose();
    }

    public void AddGameState(GameState gs)
    {
        gameStates.push(gs);
    }

    public InputHandler GetInput()
    {
        return input;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
