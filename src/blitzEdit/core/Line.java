package blitzEdit.core;

import java.awt.geom.Point2D;


public class Line 
{
	private Point2D _p1;
	private Point2D _p2;
	
	public Point2D getP1()
	{
		return _p1;
	}
	
	public Point2D getP2()
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
	
	public Line(Point2D p1, Point2D p2)
	{
		_p1 = (Point2D)p1.clone();
		_p2 = (Point2D)p2.clone();
	}
}
