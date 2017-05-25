package blitzEdit.core;

import java.awt.Rectangle;

import tools.RotatableRectangle;

public abstract class RotatableElement extends Element
{
	public short getRotation()
	{
		return _rotation;
	}
	
	public void setRotation(short rotation)
	{
		rotation =  (short)Math.abs((int)rotation);
		_rotation = (short) (rotation % (short)360);
	}
	
	public void rotate(short rotation)
	{
		short r = (short)(((int)_rotation + rotation) % 360);
		_rotation = (short)((r < 0) ?  360 + r : r);
	}
	
	// Tests if the point (x, y) is contained by element
	// TODO: Test new implemented method
	@Override
	public boolean contains(int x, int y)
	{
		RotatableRectangle rect = new RotatableRectangle(this.getX()-_sizeX/2, this.getY()-_sizeY/2, _sizeX, _sizeY);
		rect.rotateRect(_rotation);
		return rect.contains(x,y);
	}
	
	@Override
	public boolean intersects(Rectangle rect)
	{
		RotatableRectangle rotatableRect = new RotatableRectangle(this.getX()-_sizeX/2, this.getY()-_sizeY/2, _sizeX, _sizeY);
		rotatableRect.rotateRect(_rotation);
		return rotatableRect.intersects(rect);
	}
	
	public RotatableElement(int x, int y, int sizeX, int sizeY, short rot)
	{
		super(x, y, sizeX, sizeY);
		_rotation = rot;
	}
	
	public RotatableElement(int x, int y, short rot)
	{
		super(x, y);
		_rotation = rot;
	}
	
	public RotatableElement()
	{
		super();
		_rotation = 0;
	}
	

	protected short _rotation;
}
