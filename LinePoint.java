import java.awt.image.BufferedImage;
import java.util.*;
import java.io.*;

public class LinePoint
{
	HashSet<Point> points;
	int r;
	double theta;
	int imgWidth;
	int imgHeight;
	int frequency;
	
	public LinePoint()
	{
		r = 0;	
		points = new HashSet<Point>();
		imgWidth = 0;
		imgHeight = 0;
		frequency = 0;
	}

	public LinePoint(int radius, double t, BufferedImage img)
	{
		r = radius;
		theta = t;
		points = new HashSet<Point>();
		imgWidth = img.getWidth();
		imgHeight = img.getHeight();
		frequency = 0;
		fillLine(img);
	}

	public void fillLine(BufferedImage img)
	{
		int xorig = (int)Math.round(r*Math.cos(theta));
		int yorig = (int)Math.round(r*Math.sin(theta));
		double dx = Math.sin(theta);
		double dy = -Math.cos(theta);
		recurfill(xorig, yorig, dx, dy, img);
		recurfill(xorig, yorig, -dx, -dy, img);
//		System.out.println("points: "+points);
//		System.out.println("----recur");
	}
	private void recurfill(double x, double y, double dx, double dy, BufferedImage img)
	{
		if ((x<0 && dx<0) || (x>imgWidth && dx>0) || (y<0 && dy<0) || (y>imgHeight && dy>0))
		{
			return;
		}
		int rx = (int)Math.round(x);
		int ry = (int)Math.round(y);
		if (rx>0 && rx<imgWidth && ry>0 && ry<imgHeight)
		{
			int c = img.getRGB(rx, ry);
			if (c == -1)
			{
				frequency++;
			}
			Point p = new Point(rx, ry, c);
			points.add(p);
		}
		recurfill(x+dx, y+dy, dx, dy, img);
	}

	public boolean inLine(Point p)
	{
		return points.contains(p);
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
	public String toString()
	{
		return "("+r+", "+theta+")";
	}
}
