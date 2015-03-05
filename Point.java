public class Point
{
	int x;
	int y;
	int color;
	public Point()
	{
		x = 0;
		y = 0;
		color = 0;
	}
	public Point(int a, int b)
	{
		x = a;
		y = b;
		color = 0;
	}
	public Point(int x1, int y1, int c)
	{
		x = x1;
		y = y1;
		color = c;
	}
	public int getColor()
	{
		return color;
	}
	public int setColor(int c)
	{
		color = c;
		return color;
	}
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
	public int setX(int i)
	{
		x = i;
		return x;
	}
	public int setY(int i)
	{
		y = i;
		return y;
	}
	public String toString()
	{
		return "("+Integer.toString(x)+", "+Integer.toString(y)+")";
	}
}
