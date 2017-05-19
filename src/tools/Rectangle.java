package tools;

import java.awt.Point;
import java.util.ArrayList;

// Rectangle with 4 corners used for rotation calculations
public class Rectangle
{
	private ArrayList<Point> corners;
	private Point pivot;
	private int numberOfPoints = 4;
	
	Rectangle(ArrayList<Point> points, Point pivot)
	{
		corners = points;
		this.pivot = pivot;
	}
	
	Rectangle(Point leftTop, Point rightTop, Point rightBottom, Point leftBottom)
	{
		corners.add(leftTop);
		corners.add(rightTop);
		corners.add(rightBottom);
		corners.add(leftBottom);
		
		pivot.x = (leftTop.x+rightTop.x)/2;
		pivot.y = (leftTop.y+leftBottom.y)/2;
	}
	
	Rectangle(Point leftTop, Point rightTop, Point rightBottom, Point leftBottom, Point pivot)
	{
		corners.add(leftTop);
		corners.add(rightTop);
		corners.add(rightBottom);
		corners.add(leftBottom);
		
		this.pivot = pivot;
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
