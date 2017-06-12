package tools;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Used for rotating a rectangle around his center and getting information about intersections and overlaps.
 * 
 * @author Chrisian Gärtner
 */
public class RotatableRectangle
{
	private ArrayList<Point> corners;
	private Point pivot = new Point();
	private int numberOfPoints = 4;
	
	/**
	 * Constructor
	 * 
	 * @param	x		X Position of the rect
	 * @param	y		Y Position of the rect
	 * @param	width	Width of the rect
	 * @param	height	Height of the rect
	 */
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
	
	/**
	 * Constructor
	 * 
	 * @param	x		X Position of the rect
	 * @param	y		Y Position of the rect
	 * @param	width	Width of the rect
	 * @param	height	Height of the rect
	 * @param	pivot	Center of the rect
	 */
	public RotatableRectangle(double x, double y, double width, double height, Point pivot)
	{
		corners = new ArrayList<Point>();
		corners.add(new Point((int) x, (int) y));
		corners.add(new Point((int) (x + width), (int) y));
		corners.add(new Point((int) (x + width), (int) (y + height)));
		corners.add(new Point((int) x, (int) (y + height)));
		
		this.pivot = pivot;
	}
	
	/**
	 * Constructor
	 * Size of the points array need to be exactly four.
	 * 
	 * @param	points	Corners of the rectangle
	 * @param	pivot	Center of the rect
	 */
	public RotatableRectangle(ArrayList<Point> points, Point pivot)
	{
		if (points.size() == 4)
		{
			corners = points;
			this.pivot = pivot;
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param	leftTop			Point located at the leftTop of the rect
	 * @param	rightTop		Point located at the rightTop of the rect
	 * @param	rightBottom		Point located at the rightBottom of the rect
	 * @param	leftBottom		Point located at the leftBottom of the rect
	 */
	public RotatableRectangle(Point leftTop, Point rightTop, Point rightBottom, Point leftBottom)
	{
		corners.add(leftTop);
		corners.add(rightTop);
		corners.add(rightBottom);
		corners.add(leftBottom);
		
		pivot.x = (leftTop.x+rightBottom.x)/2;
		pivot.y = (leftTop.y+rightBottom.y)/2;
	}
	
	/**
	 * Constructor
	 * 
	 * @param	leftTop			Point located at the leftTop of the rect
	 * @param	rightTop		Point located at the rightTop of the rect
	 * @param	rightBottom		Point located at the rightBottom of the rect
	 * @param	leftBottom		Point located at the leftBottom of the rect
	 * @param	pivot			Center of the rect
	 */
	public RotatableRectangle(Point leftTop, Point rightTop, Point rightBottom, Point leftBottom, Point pivot)
	{
		corners.add(leftTop);
		corners.add(rightTop);
		corners.add(rightBottom);
		corners.add(leftBottom);
		
		this.pivot = pivot;
	}
	
	/**
	 * Used to check if a point is in this (rotated) rectangle or not.
	 * 
	 * @param	point		Point to check
	 * @return	boolean		True if the point is located in the rect, false if not
	 */
	public boolean contains(Point point)
	{
		Polygon poly = new Polygon();
		for(Point p : corners)
		{
			poly.addPoint(p.x, p.y);
		}
		return poly.contains(point);
	}
	
	/**
	 * Used to check if a point is in this (rotated) rectangle or not.
	 * 
	 * @param	x			X position of the point
	 * @param	y			Y position of the point
	 * @return	boolean		True if the point is located in the rect, false if not
	 */
	public boolean contains(int x, int y)
	{
		return contains(new Point(x,y));
	}
	
	/**
	 * Used to check if a point is in this (rotated) rectangle or not.
	 * 
	 * @param	x			X position of the point
	 * @param	y			Y position of the point
	 * @return	boolean		True if the point is located in the rect, false if not
	 */
	public boolean contains(double x, double y)
	{
		return contains(new Point((int)x,(int)y));
	}
	
	/**
	 * Used to check if a rectangle intersects with this (rotated) rectangle
	 * 
	 * @param	rect		Rectangle to check
	 * @return	boolean		True if the rectangle intersects with this rect, false if not
	 */
	public boolean intersects(Rectangle rect)
	{
		Polygon poly = new Polygon();
		for(Point p : corners)
		{
			poly.addPoint(p.x, p.y);
		}
		return poly.intersects(rect);
	}
	
	/**
	 * Used to rotate this rectangle for angle degrees
	 * 
	 * @param	angle		Angle of the rotation
	 */
	public void rotateRect(double angle)
	{	
		for(Point p : corners)
		{
			rotatePoint(angle, p);
		}
	}
	
	/**
	 * Used to get the x positions of the corners after a rotation.
	 * 
	 * @return	double[]	Array with the x locations of the corners
	 */
	public double[] getXCoordinates()
	{
		double[] result = new double[numberOfPoints];
		for(int i = 0; i < numberOfPoints; i++)
		{
			result[i] = corners.get(i).getX();
		}
		return result;
	}
	
	/**
	 * Used to get the y positions of the corners after a rotation.
	 * 
	 * @return	double[]	Array with the y locations of the corners
	 */
	public double[] getYCoordinates()
	{
		double[] result = new double[numberOfPoints];
		for(int i = 0; i < numberOfPoints; i++)
		{
			result[i] = corners.get(i).getY();
		}
		return result;
	}
	
	
	/**
	 * Used when rotating on of the corners around the center of this rectangle.
	 * 
	 * @param	angle			Angle of the rotation
	 * @param	pointToRotate	Point that gets rotated
	 * @return	Point			Position of the rotated point
	 */
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
