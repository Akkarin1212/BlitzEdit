package BlitzEdit.core;

public abstract class RotatableElement extends Element
{
	void setRotation(short rotation)
	{
		if (rotation > 180 || rotation < -180)
			return;
		_rotation = rotation;
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
	

	public short _rotation;
}