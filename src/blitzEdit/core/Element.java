package blitzEdit.core;

import java.awt.Rectangle;
import java.awt.Point;
import javafx.scene.canvas.*;
import tools.SelectionMode;

/**
 * Superclass for Circuit Elements
 *  
 * @author David Schick
 * @author Christian Gärtner
 */
public abstract class Element 
{
	/**
	 * Returns x-coordinate of this Elements location
	 * 
	 * @return x-coordinate of location
	 */
	public int getX()
	{
		return (int)_position.getX();
	}
	
	/**
	 * Returns y-coordinate of this Elements location
	 * 
	 * @return y-coordinate of location
	 */
	public int getY()
	{
		return (int)_position.getY();
	}
	
	/**
	 * Returns x-size of this Element
	 * 
	 * @return x-size of Element
	 */
	public int getSizeX()
	{
		return _sizeX;
	}
	
	/**
	 * Returns y-size of this Element
	 * 
	 * @return y-size of Element
	 */
	public int getSizeY()
	{
		return _sizeY;
	}
	
	/**
	 * Resizes an Element
	 * 
	 * @param sizeX new x-size
	 * @param sizeY new y-size
	 * @return reference to this
	 */
	public Element setSize(int sizeX, int sizeY)
	{
		_sizeX = sizeX;
		_sizeY = sizeY;
		return this;
	}
  
	/**
	 * Resizes an Element
	 * 
	 * @param sizeX new x-size
	 * @param sizeY new y-size
	 * @return reference to this
	 */
	public Element setSize(double sizeX, double sizeY)
	{
		return setSize((int)sizeX, (int)sizeY);
	}
  
	/**
	 * Returns the location of this Element
	 * @return point containing location
	 */
	public Point getPosition()
	{
		return new Point(_position);
	}
	
	/**
	 * Returns the Assigned {@link SelectionMode}
	 * of this Element
	 * @return {@link SelectionMode} of Element
	 */
	public SelectionMode getSelectionMode()
	{
		return _selectionMode;
	}
	
	/**
	 * sets the {@link SelectionMode} for this Element
	 * @param mode
	 * @return reference to this
	 */
	public Element setSelectionMode(SelectionMode mode)
	{
		_selectionMode = mode;
		return this;
	}
	
	/**
	 * sets the location of this Element
	 * @param x x-coordinate of new location
	 * @param y y-coordinate of new location
	 * @return reference to this
	 */
	public Element setPosition(int x, int y)
	{
		_position.setLocation(x, y);
		return this;
	}
	
	/**
	 * sets the location of this Element
	 * @param x x-coordinate of new location
	 * @param y y-coordinate of new location
	 * @return reference to this
	 */
	public Element setPosition(double x, double y)
	{
		return setPosition((int)x, (int)y);
	}
	
	/**
	 * moves element to specified location
	 * @param x x-coordinate of new location
	 * @param y y-coordinate of new location
	 * @return reference to this
	 */
	public abstract Element move(int x, int y);
	
	/**
	 * moves element to specified location
	 * @param x x-coordinate of new location
	 * @param y y-coordinate of new location
	 * @return reference to this
	 */
	public abstract Element move(double x, double y);
	
	/**
	 * draws element on a {@link GraphicsContext}
	 * @param gc {@link GraphicsContext} to draw on
	 * @param scale factor specifying the scale
	 * @param mode {@link SelectionMode}, giving further information on how to draw
	 */
	public abstract void draw(GraphicsContext gc, double scale, SelectionMode mode);
	
	/**
	 * Returns deep copy of the Element
	 */
	public abstract Element clone();
	
	/**
	 * checks, if point (x, y) is contained in Element
	 * 
	 * @param x x-coordinate of point to check
	 * @param y y-coordinate of point to check
	 * @return true if point is contained, else false
	 */
	public boolean contains(int x, int y)
	{
		Rectangle rect = new Rectangle((int)_position.getX(), (int)_position.getY(),
										_sizeX, _sizeY);
		return rect.contains(x, y);
	}
	
	/**
	 * Checks, if Element intersects with specified Rectangle
	 * @param rect Rectangle to be checked for intersection
	 * @return true if Rectangle intersects, else false
	 */
	public boolean intersects(Rectangle rect)
	{
		Rectangle r = new Rectangle((int)_position.getX()-_sizeX/2, 
										(int)_position.getY()-_sizeY/2,
										_sizeX, _sizeY);
		return r.intersects(rect);
	}

	/**
	 * Constructs new Element at designated location.
	 * Sets sizeX and sizeY to a default value of 20.
	 * @param x x-coordinate of location
	 * @param y y-coordinate of location
	 */
	public Element(int x, int y)
	{
		_position = new Point(x, y);
		_sizeX = 20;
		_sizeY = 20;
	}
	
	/**
	 * Constructs new Element
	 * 
	 * @param x x-coordinate of location
	 * @param y y-coordinate of location
	 * @param sx x-size of Element
	 * @param sy y-size of Element
	 */
	public Element(int x, int y, int sx, int sy)
	{
		_position = new Point(x, y);
		_sizeX = sx;
		_sizeY = sy;
	}
	
	/**
	 * Defaultconstructor
	 */
	public Element()
	{
		_position = new Point(0,0);
		_sizeX = 10;
		_sizeY = 10;
	}
	
	protected Point _position;
	protected SelectionMode _selectionMode = SelectionMode.UNSELECTED;
	protected int _sizeX;
	protected int _sizeY;
	
}
