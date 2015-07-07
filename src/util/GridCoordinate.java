package util;

public class GridCoordinate
{
	public int X;
	public int Y;
	
	public GridCoordinate()
	{
		X = 0;
		Y = 0;
	}
	
	public GridCoordinate(int newX, int newY)
	{
		X = newX;
		Y = newY;
	}
	
	public boolean Equals(GridCoordinate other)
	{
		return other != null && X == other.X && Y == other.Y;
	}
	
	public GridCoordinate Copy(){
		return new GridCoordinate(X, Y);
	}
	
	public GridCoordinate Plus(GridCoordinate other)
	{
		return new GridCoordinate(X + other.X, Y + other.Y);
	}
	public GridCoordinate Subtract(GridCoordinate other)
	{
		return new GridCoordinate(X - other.X, Y - other.Y);
	}
	
	public void PlusEquals(GridCoordinate other)
	{
		X += other.X;
		Y += other.Y;
	}
	public void SubtractEquals(GridCoordinate other)
	{
		X -= other.X;
		Y -= other.Y;
	}
}
