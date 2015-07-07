package util;

import game.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Camera
{
	float scale = 1;
	Vector2 translation = new Vector2();
	Vector2 translationTarget = new Vector2();
	Vector2 translationTargetVariable = new Vector2();
	boolean aimAtStaticPoint = false;
	Vector2 staticTarget = new Vector2();
	
	private float[] fadeTarget = {0, 0, 0, 0};
	private float[] fade = {0, 0, 0, 0};
	private float[] fadeSpeed = new float[4];
	private boolean isFading = false;
	
	Game game;
	Level level;
	
	public Camera(Game newGame, Level newLevel)
	{
		game = newGame;
		level = newLevel;
	}
	
	public void AimAtPoint(Vector2 target)
	{
		staticTarget = target;
		aimAtStaticPoint = true;
	}
	
	public void AimAtNonStaticTarget()
	{
		aimAtStaticPoint = false;
	}
	
	public void SetTranslationTarget(Vector2 target)
	{
		translationTargetVariable = target;
	}
	
	public void SetTranslation(Vector2 newTranslation)
	{
		SetTranslationTarget(newTranslation);
		Update();
		translation = translationTarget.Copy();
	}
	
	//used by script command camera-fade a r g b t
	public void fade(int a, int r, int g, int b, int t) {
		fadeTarget[0] = a;
		fadeTarget[1] = r;
		fadeTarget[2] = g;
		fadeTarget[3] = b;
		for(int i = 0; i < 4; i++) {
			fadeSpeed[i] = (fadeTarget[i] - fade[i]) / (t * 1.0f / game.fps);
		}
		isFading = true;
	}

	//used by script command camera-fade-immediate a r g b
	public void fadeImmediate(int a, int r, int g, int b) {
		fadeTarget[0] = a;
		fadeTarget[1] = r;
		fadeTarget[2] = g;
		fadeTarget[3] = b;

		fade[0] = a;
		fade[1] = r;
		fade[2] = g;
		fade[3] = b;
		
		isFading = false;
	}

	//used by script command camera-unfade t
	public void unfade(int t) {
		fadeTarget[0] = 0;
		fadeTarget[1] = 0;
		fadeTarget[2] = 0;
		fadeTarget[3] = 0;
		for(int i = 0; i < 4; i++) {
			fadeSpeed[i] = (fadeTarget[i] - fade[i]) / (t * 1.0f / game.fps);
		}
		isFading = true;
	}
	
	public void SetScale(float newScale)
	{
		scale = newScale;
	}
	
	public void Update()
	{
		Vector2 target = translationTargetVariable;
		
		if (aimAtStaticPoint)
		{
			target = staticTarget;
		}
		
		Vector2 middleOfScreen = new Vector2(game.getWidth(), game.getHeight()).Times(0.5f);
		translationTarget = target.Plus(middleOfScreen.Times(-1 / scale));
		
		Vector2 translationDifference = translationTarget.Plus(translation.Times(-1));
		
		translation.PlusEquals(translationDifference.Times(0.1f));
		
		//update fading
		if(isFading) {
			boolean[] colourUp = new boolean[4];
			
			isFading = false; //set back to true if speed is not all zeroes
			for(int i = 0; i < 4; i++) {
				if(fadeSpeed[i] > 0) {colourUp[i] = true;}
				
				fade[i] += fadeSpeed[i];
				
				// if we're fading up and fade > target, or if we're fading down and fade < target
				if((fade[i] > fadeTarget[i] && colourUp[i]) || (fade[i] < fadeTarget[i] && !colourUp[i])) {
					fade[i] = fadeTarget[i];
					fadeSpeed[i] = 0;
					
					if(fade[i] < 0) {fade[i] = 0;}
					if(fade[i] > 255) {fade[i] = 255;}
				}
				
				if(fadeSpeed[i] != 0) {
					isFading = true;
				}
			}
		}
	}
	
	public void Apply(Graphics2D g)
	{
		g.scale(scale, scale);
		g.translate(-(int)translation.X, -(int)translation.Y);
	}
	
	public void Reset(Graphics2D g)
	{
		g.setTransform(new AffineTransform());
	}
	
	public void applyFade(Graphics g) {
		g.setColor(new Color((int) fade[1], (int) fade[2], (int) fade[3], (int) fade[0]));
		g.fillRect(0, 0, game.width, game.height);
	}
}
