package blitzEdit.core;

import java.awt.Rectangle;
import java.awt.Point;
import javafx.scene.canvas.*;


public abstract class Element 
{
	public int getX()
	{
		return (int)_position.getX();
	}
	
	public int getY()
	{
		return (int)_position.getY();
	}
	
	public int getSizeX()
	{
		return _sizeX;
	}
	
	public int getSizeY()
	{
		return _sizeY;
	}
	
	public Point getPosition()
	{
		return _position;
	}
	
	public boolean getIsSelected()
	{
		return _isSelected;
	}
	
	public Element setIsSelected(boolean isSelected)
	{
		_isSelected = isSelected;
		return this;
	}
	
	public Element setPosition(int x, int y)
	{
		_position.setLocation(x, y);
		return this;
	}
	
	public Element setPosition(double x, double y)
	{
		return setPosition((int)x, (int)y);
	}
	
	//Bewegt das Element
	public abstract Element move(int x, int y);
	
	public abstract Element move(double x, double y);
	
	public abstract void draw(GraphicsContext gc, double scale, boolean selected);
	
	public boolean contains(int x, int y)
	{
		Rectangle rect = new Rectangle((int)_position.getX()-_sizeX/2, (int)_position.getY()-_sizeY/2,
										_sizeX, _sizeY);
		return rect.contains(x, y);
	}
	
	public boolean intersects(Rectangle rect)
	{
		Rectangle r = new Rectangle((int)_position.getX()-_sizeX/2, 
										(int)_position.getY()-_sizeY/2,
										_sizeX, _sizeY);
		return r.intersects(rect);
	}
	
	public Element(int x, int y)
	{
		_position = new Point(x, y);
		_sizeX = 20;
		_sizeY = 20;
	}
	
	public Element(int x, int y, int sx, int sy)
	{
		_position = new Point(x, y);
		_sizeX = sx;
		_sizeY = sy;
	}
	
	public Element()
	{
		_position = new Point(0,0);
		_sizeX = 10;
		_sizeY = 10;
	}
	
	protected Point _position;
	protected boolean _isSelected;
	protected int _sizeX;
	protected int _sizeY;
	
}
