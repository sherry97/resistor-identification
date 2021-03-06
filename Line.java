import java.awt.image.BufferedImage;

public class Line
{
	int r;
	double theta;
	int frequency;
	public Line()
	{
		r = 0;
		theta = 0;
		frequency = 0;
	}
	public Line(int R, double T)
	{
		r = R;
		theta = T;
		frequency = 0;
	}
	public void add()
	{
		frequency++;
	}
	public int getFrequency()
	{
		return frequency;
	}
	public int getR()
	{
		return r;
	}
	public double getTheta()
	{
		return theta;
	}
	public boolean inLine(int x, int y)
	{
		int centerx = (int)Math.round((double)r*Math.cos(theta));
		int centery = (int)Math.round((double)r*Math.sin(theta));
		int dx = x - centerx;
		int dy = y - centery;
		if (dx == 0 || dy == 0) return dx == 0 && dy == 0;
		return Math.abs(dy/dx - Math.tan(theta)) < 0.1;
	}
	public String toString()
	{
		return Integer.toString(r)+", "+Double.toString(theta);
	}
}
