package util;

public class Vector2
{
	public float X;
	public float Y;
	
	public Vector2()
	{
		X = 0;
		Y = 0;
	}
	
	public Vector2(float newX, float newY)
	{
		X = newX;
		Y = newY;
	}
	
	public Vector2 Plus(Vector2 other)
	{
		return new Vector2(X + other.X, Y + other.Y);
	}
	public Vector2 Subtract(Vector2 other)
	{
		return new Vector2(X - other.X, Y - other.Y);
	}
	
	public Vector2 Negate(){
		X = -X;
		Y = -Y;
		return this;
	}
	
	public void PlusEquals(Vector2 other)
	{
		X += other.X;
		Y += other.Y;
	}
	public void SubtractEquals(Vector2 other)
	{
		X -= other.X;
		Y -= other.Y;
	}
	
	public Vector2 Copy()
	{
		return new Vector2(X, Y);
	}
	
	public float Length()
	{
		return (float)Math.sqrt(X*X + Y*Y);
	}
	
	public Vector2 Normalize() //Normalize this vector and return it
	{
		float len = Length();
		
		if (len > 0)
		{
			X /= len;
			Y /= len;
		}
		return this;
	}
	
	public Vector2 GetNormalized() //Return a normalized copy of this vector
	{
		Vector2 c = Copy();
		c.Normalize();
		return c;
	}
	
	public Vector2 Times(float f)
	{
		return new Vector2(f*X, f*Y);
	}
	
	public Vector2 SetMagnitude(float m){ //Sets the magnitude of this vector and returns it
		float len = Length();
		if (len > 0){
			Normalize();
			Times(m);
		}
		return this;
	}
}
