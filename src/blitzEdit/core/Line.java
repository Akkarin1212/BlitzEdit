package blitzEdit.core;

import java.awt.Point;
import javafx.scene.canvas.GraphicsContext;

import tools.GraphicDesignContainer;
import blitzEdit.core.Connector;

public class Line 
{
	private Connector _c1;
	private Connector _c2;
	
	public Connector getC1()
	{
		return _c1;
	}
	
	public Connector getC2()
	{
		return _c2;
	}
	
	public boolean equals(Line l)
	{
		Point p1 = _c1.getPosition();
		Point p2 = _c2.getPosition();
		
		if( ( p1.equals(l.getC1().getPosition()) && p2.equals(l.getC2().getPosition()) )
			|| (p2.equals(l.getC1().getPosition()) && p1.equals(l.getC2().getPosition())) )
		{
			return true;
		}
		else 
			return false;
	}
	
	public Line(Connector c1, Connector c2)
	{
		_c1 = c1;
		_c2 = c2;
	}
	
	public void draw(GraphicsContext gc)
	{	
		gc.save();
		gc.setStroke(GraphicDesignContainer.line_color);
		gc.setLineWidth(GraphicDesignContainer.line_width);
		drawLine(gc);
		gc.restore();
	}
	
	private void drawLine(GraphicsContext gc)
	{
		Point p1 = _c1.getPosition();
		Point p2 = _c2.getPosition();
		short alpha1 = _c1.getRotation();
		short alpha2 = _c2.getRotation();
		
		Point p3 = getCornerPoint(p1, alpha1, p2, alpha2);
		
		gc.strokeLine(p1.x, p1.y, p3.x, p3.y);
		gc.strokeLine(p2.x, p2.y, p3.x, p3.y);
	}
	
	private Point getCornerPoint(Point p1, short alpha1, Point p2, short alpha2)
	{
		Point p3 = new Point();
		//je nach winkel wird ein guenstiger eckpunkt gesucht
		//winkel zeigt in positive y-richtung -> groeßerer der beiden y-werte usw.
		if (alpha1 < 45)
		{
			p3.y = Math.max(p1.y, p2.y);
			// wenn y von p1 übernommen wurde, wird x von p2 übernommen
			// und umgekehrt
			p3.x = (p3.y == p1.y) ? p2.x : p1.x;
		}
		else if (alpha1 < 135)
		{
			p3.x = Math.max(p1.x, p2.x);
			p3.y = (p3.x == p1.x) ? p2.y : p1.y;
		}
		else if (alpha1 < 225)
		{
			p3.y = Math.min(p1.y, p2.y);
			p3.x = (p3.y == p1.y) ? p2.x : p1.x;
		}
		else
		{
			p3.x = Math.max(p1.x, p2.x);
			p3.y = (p3.x == p1.x) ? p2.y : p1.y;
		}
		return p3;
	}
}
