package BlitzEdit.core;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

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
	
	// Tests if the point (x, y) is contained by element
	// TODO: Test new implemented method
	public boolean contains(int x, int y)
	{
		Rectangle rect = new Rectangle(_posX, _posY, _sizeX, _sizeY);
		
		AffineTransform at = AffineTransform.getRotateInstance((Math.PI * 2)*((double)_rotation / 360.0), _posX, _posY);
		
		return at.createTransformedShape(rect).contains(x, y);
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
