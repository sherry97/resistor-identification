import javax.imageio.*;
import java.io.*;
import java.lang.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.FlowLayout;
import java.lang.Math;
import java.util.*;
import java.awt.*;

public class image
{
	private static BufferedImage loadimage(File imageName)
	{
		BufferedImage img = null;
		try {
			img = ImageIO.read(imageName);
		} catch (IOException e) {
			System.out.println("Error: Image failed to load.");
			System.exit(0);
		}
		return img;
	}
//--------------------//	
//	performs canny transform of color image
//	returns BufferedImage of edges, white on black
	private static BufferedImage canny(BufferedImage img)
	{
		CannyEdgeDetector det = new CannyEdgeDetector();
		det.setSourceImage(img);
		det.process();
		BufferedImage edges = det.getEdgesImage();
		return edges;
	}
//--------------------//	
//	displays image
	private static void displayImage(BufferedImage img)
	{
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(new JLabel(new ImageIcon(img)));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}
//--------------------//	
//	determine if point is within bounds of image
	private static boolean onScreen(int x, int y, BufferedImage i)
	{
		return (x < i.getWidth() && x > 0 && y < i.getHeight() && y > 0);
	}
//--------------------//	
//	draw LinePoint l in BufferedImage img
	public static BufferedImage drawLine(LinePoint l, BufferedImage img)
	{
		int r = l.getR();
		double theta = l.getTheta();
		double dx = Math.sin(theta);
		double dy = Math.cos(theta);
		double x = (double)r*dy;
		double y = (double)r*dx;
		img = recurVectorLine(x, y, dx, -dy, img, -65536);
		img = recurVectorLine(x, y, -dx, dy, img, -65536);
		return img;
	}
//--------------------//	
//	recur vector addition until end of image reached
	private static BufferedImage recurVectorLine
		(double x, double y, double dx, double dy, BufferedImage img, int color)
	{
		double newX = x+dx;
		double newY = y+dy;
		if (Math.round(newX) >= img.getWidth() && dx>0 || Math.round(newX) < 0 && dx<0||
		    Math.round(newY) >= img.getHeight() && dy>0 || Math.round(newY) < 0 && dy<0) 
			return img;
		if (onScreen((int)Math.round(newX), (int)Math.round(newY), img))
		{
			img.setRGB((int)Math.round(newX), (int)Math.round(newY), color);
		}
		return recurVectorLine(newX, newY, dx, dy, img, color);
	}
//--------------------//	
//	find all possible lines in image + their frequencies
	private static ArrayList<LinePoint> findAllLines(BufferedImage img)
	{
		ArrayList<LinePoint> arr = new ArrayList<LinePoint>();
		int maxLen = (int)Math.round(Math.sqrt(Math.pow(img.getWidth(), 2)+Math.pow(img.getHeight(), 2)));
		for (int r = 1; r < maxLen; r++)
		{
			for (double theta = -Math.PI/2; theta < Math.PI; theta+=.05)
			{
				LinePoint l = new LinePoint(r, theta, img);
				arr.add(l);
			}
		}
		return arr;
	}
//--------------------//	    
//	find/return LinePoint of max frequency in freqMap
	private static LinePoint maxFreq(HashMap<Integer, LinePoint> freqMap)
	{
		int max = 0;
		for (Integer i : freqMap.keySet())
		{
			max = Math.max(max, i);
		}
		return freqMap.get(max);
	}
//--------------------//	
//	put LinePoints in ArrayList a into Hashmap w/ <frequency, LinePoint>
	private static HashMap<Integer, LinePoint> findFrequencyOfLines(ArrayList<LinePoint> a, BufferedImage img)
	{
		HashMap<Integer, LinePoint> freqMap = new HashMap<Integer, LinePoint>();
		for (LinePoint l : a)
		{
			freqMap.put(l.getFrequency(), l);
		}
		return freqMap;
	}
//--------------------//	
//	print time since start w/ description
	private static void time(String s)
	{
		String p = "   @ "+(System.currentTimeMillis()-STARTTIME)/1000.0+" sec..."+s;
		System.out.println(p);
	}
//--------------------//	
//	find lines perpendicular to LinePoint l
//	return LinePoints
	private static ArrayList<LinePoint> findBars(BufferedImage i, LinePoint l)
	{
		ArrayList<LinePoint> bars = new ArrayList<LinePoint>();
		double oldTheta = l.getTheta();
		double phi = Math.PI/2+oldTheta;
		for (int r = 1; r < Math.sqrt(i.getHeight()*i.getHeight()+i.getWidth()*i.getWidth()); r++)
		{
			LinePoint posLine = new LinePoint(r, phi, i);
			bars.add(posLine);
			LinePoint negLine = new LinePoint(r, phi-Math.PI, i);
			bars.add(negLine);
		}
		return bars;
	}
//--------------------//
//	draw perpendicular line of next max freqnecy to maxFreq line
//	return true if line drawn, false if not
	private static boolean drawBarLine(HashMap<Integer, LinePoint> barLines, ArrayList<Integer> pastradii, BufferedImage i)
	{
		LinePoint maxBar = maxFreq(barLines);
		System.out.print("---"+maxBar.toString());
		for (int r : pastradii)
		{
			if (Math.abs(r-maxBar.getR()) < RADIUSRANGE)
			{
				pastradii.add(barLines.remove(maxBar.getFrequency()).getR());
				System.out.println();
				return false;
			}
		}
		System.out.println("*");
		drawLine(maxBar, i);
		pastradii.add(barLines.remove(maxBar.getFrequency()).getR());
		return true;
	}
//--------------------//
//	find distances between perpendicular lines to find pattern
	private static ArrayList<Integer> findDistances(ArrayList<Integer> pastRadii)
	{
		ArrayList<Integer> distances = new ArrayList<Integer>();
		System.out.println();
		for (int i = 0; i < pastRadii.size()-1; i++)
		{
			int r1 = pastRadii.get(i);
			int r2 = pastRadii.get(i+1);
			int distance = Math.abs(r1-r2);
			if (distance <= RADIUSRANGE)
			{
				continue;
			}
			System.out.println("---("+r1+", "+r2+") = "+distance);
			distances.add(distance);
		}
		return distances;
	}

	private static double standarddev(ArrayList<Integer> arr)
	{
		int avg = 0;
		for (int a : arr)
		{
			avg += a;
		}
		avg /= arr.size();
		double dev = 0;
		for (int a : arr)
		{
			dev += (a-avg)*(a-avg);
		}
		dev /= arr.size();
		dev = Math.sqrt(dev);
		return dev;
	}
//--------------------//
	private static boolean findBarDistances(ArrayList<Integer> distances)
	{
		ArrayList<Integer> r1 = new ArrayList<Integer>();
		ArrayList<Integer> r2 = new ArrayList<Integer>();
		int avg1 = 0;
		int avg2 = 0;
		for (int j = 0; j < distances.size(); j++)
		{
			if (j%2 == 0)
			{
				r1.add(distances.get(j));
				avg1 += distances.get(j);
			}
			else
			{
				r2.add(distances.get(j));
				avg2 += distances.get(j);
			}
		}
		double dev1 = standarddev(r1);
		double dev2 = standarddev(r2);
		if (dev1 <= dev2)
		{
			return true;
		}
		return false;
	}
//--------------------//
	private static int NUM_BANDS = 4;
	private static int RADIUSRANGE = 5;
	private static long STARTTIME = 0;
	private static int WHITE = -1;
	private static int RED = -65536;
	public static void main(String[] args)
	{
		// start
		System.out.println("--------------------------START-----------------------------");
		STARTTIME = System.currentTimeMillis();
		BufferedImage img = loadimage(new File("res4.png"));
		BufferedImage i = canny(img);
		time("image loaded");
		ArrayList<LinePoint> a = findAllLines(i);
		time("all lines found");
		HashMap<Integer, LinePoint> freqMap = findFrequencyOfLines(a, i);
		time("frequency of lines cataloged");
		LinePoint maxLine = maxFreq(freqMap);
		drawLine(maxLine, i);

		// find perpendicular lines, organize by frequency
		ArrayList<LinePoint> perpLines = findBars(i, maxLine);
		time("perpendicular lines found");
		HashMap<Integer, LinePoint> barLines = findFrequencyOfLines(perpLines, i);
		time("perpendicular line frequencies found");
		System.out.println();

		// draw perpendicular lines of max frequency
		ArrayList<Integer> pastradii = new ArrayList<Integer>();
		for (int j = 0; j < 2*NUM_BANDS; j++)
		{
			if (!drawBarLine(barLines, pastradii, img)) j--;
		}
		System.out.println();
		time("perpendicular lines drawn");

		//find pattern of distances between perpendicular lines
		Collections.sort(pastradii);
		ArrayList<Integer> distances = findDistances(pastradii);
		System.out.println("distances.size(): "+distances.size()+"\n");
		time("distances between perpendicular lines found");

		boolean bool = findBarDistances(distances);
		time("bar set found");

		double perptheta = maxLine.getTheta()-Math.PI/2;
		for (int q = 0; q < pastradii.size()-1; q++)
		{
			if ((pastradii.get(q+1)-pastradii.get(q)) < RADIUSRANGE)
			{
				continue;
			}
			if (bool)
			{
				for (int rad = pastradii.get(q); rad < pastradii.get(q+1); rad++)
				{
					LinePoint newLine = new LinePoint(rad, perptheta, img);
					drawLine(newLine, img);
				}
			}
			bool = !bool;
		}
		time("bar set colored in");
		displayImage(img);
		System.out.println("---------------------------END------------------------------");

		System.out.println("\n--------------------------NOTES-----------------------------");
		System.out.println(" analyze color >> spit numbers");
		System.out.println(" check res5.png");
		System.out.println("--------------------------NOTES-----------------------------");
	}
}
