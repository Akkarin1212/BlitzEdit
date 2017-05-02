package BlitzEdit.core;

import java.awt.Rectangle;

public abstract class RotatableElement extends Element
{
	public short getRotation()
	{
		return _rotation;
	}
	
	public void setRotation(short rotation)
	{
		if (rotation > 180 || rotation < -180)
			return;
		_rotation = rotation;
	}
	
	public boolean contains(int x, int y)
	{
		Rectangle rect = new Rectangle(_posX, _posY, _sizeX, _sizeY);
		//TODO: check containment of (x, y) with considering rotation
		return rect.contains(x, y);
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
	

	private short _rotation;
}
