package blitzEdit.core;

import java.awt.Point;


public class Line 
{
	private Point _p1;
	private Point _p2;
	
	public Point getP1()
	{
		return _p1;
	}
	
	public Point getP2()
	{
		return _p2;
	}
	
	public boolean equals(Line l)
	{
		if( ( _p1.equals(l.getP1()) && _p2.equals(l.getP2()) )
			|| ( _p2.equals(l.getP1()) && _p1.equals(l.getP2())) )
		{
			return true;
		}
		else 
			return false;
	}
	
	public Line(Point p1, Point p2)
	{
		_p1 = (Point)p1.clone();
		_p2 = (Point)p2.clone();
	}
}
