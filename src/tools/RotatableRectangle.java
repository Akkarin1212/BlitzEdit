package tools;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

// Rectangle with 4 corners used for rotation calculations
public class RotatableRectangle
{
	private ArrayList<Point> corners;
	private Point pivot = new Point();
	private int numberOfPoints = 4;
	
	public RotatableRectangle(double x, double y, double width, double height)
	{
		corners = new ArrayList<Point>();
		corners.add(new Point((int) x, (int) y));
		corners.add(new Point((int) (x + width), (int) y));
		corners.add(new Point((int) (x + width), (int) (y + height)));
		corners.add(new Point((int) x, (int) (y + height)));
		
		pivot.x = (corners.get(0).x+corners.get(2).x)/2; // middle of left corner and right bottom corner
		pivot.y = (corners.get(0).y+corners.get(2).y)/2;
	}
	
	public RotatableRectangle(double x, double y, double width, double height, Point pivot)
	{
		corners = new ArrayList<Point>();
		corners.add(new Point((int) x, (int) y));
		corners.add(new Point((int) (x + width), (int) y));
		corners.add(new Point((int) (x + width), (int) (y + height)));
		corners.add(new Point((int) x, (int) (y + height)));
		
		this.pivot = pivot;
	}
	
	public RotatableRectangle(ArrayList<Point> points, Point pivot)
	{
		corners = points;
		this.pivot = pivot;
	}
	
	public RotatableRectangle(Point leftTop, Point rightTop, Point rightBottom, Point leftBottom)
	{
		corners.add(leftTop);
		corners.add(rightTop);
		corners.add(rightBottom);
		corners.add(leftBottom);
		
		pivot.x = (leftTop.x+rightBottom.x)/2;
		pivot.y = (leftTop.y+rightBottom.y)/2;
	}
	
	public RotatableRectangle(Point leftTop, Point rightTop, Point rightBottom, Point leftBottom, Point pivot)
	{
		corners.add(leftTop);
		corners.add(rightTop);
		corners.add(rightBottom);
		corners.add(leftBottom);
		
		this.pivot = pivot;
	}
	
	public boolean contains(Point point)
	{
		Polygon poly = new Polygon();
		for(Point p : corners)
		{
			poly.addPoint(p.x, p.y);
		}
		return poly.contains(point);
	}
	
	public boolean contains(int x, int y)
	{
		return contains(new Point(x,y));
	}
	
	public boolean contains(double x, double y)
	{
		return contains(new Point((int)x,(int)y));
	}
	
	public boolean intersects(Rectangle rect)
	{
		Polygon poly = new Polygon();
		for(Point p : corners)
		{
			poly.addPoint(p.x, p.y);
		}
		return poly.intersects(rect);
	}
	
	public void rotateRect(double angle)
	{	
		for(Point p : corners)
		{
			rotatePoint(angle, p);
		}
	}
	
	public double[] getXCoordinates()
	{
		double[] result = new double[numberOfPoints];
		for(int i = 0; i < numberOfPoints; i++)
		{
			result[i] = corners.get(i).getX();
		}
		return result;
	}
	
	public double[] getYCoordinates()
	{
		double[] result = new double[numberOfPoints];
		for(int i = 0; i < numberOfPoints; i++)
		{
			result[i] = corners.get(i).getY();
		}
		return result;
	}
	
	private Point rotatePoint(double angle, Point pointToRotate)
	{
		angle = Math.toRadians(angle);
		
		double s = Math.sin(angle);
		double c = Math.cos(angle);

		// translate point back to origin:
		pointToRotate.x -= pivot.x;
		pointToRotate.y -= pivot.y;

		// rotate point
		double xnew = pointToRotate.x * c - pointToRotate.y * s;
		double ynew = pointToRotate.x * s + pointToRotate.y * c;

		// translate point back:
		pointToRotate.x = (int) (xnew + pivot.x);
		pointToRotate.y = (int) (ynew + pivot.y);

		return pointToRotate;
	}
	
}
