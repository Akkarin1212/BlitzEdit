package BlitzEdit.core;

public abstract class Element 
{
	public int getX()
	{
		return _posX;
	}
	
	public int getY()
	{
		return _posY;
	}
	
	public int getSizeX()
	{
		return _sizeX;
	}
	
	public int getSizeY()
	{
		return _sizeY;
	}
	
	public void setPosition(int x, int y)
	{
		_posX = x;
		_posY = y;
	}
	
	abstract void move(int x, int y);
	
	public Element(int x, int y)
	{
		_posX = x;
		_posY = y;
		_sizeX = 10;
		_sizeY = 10;
	}
	
	public Element(int x, int y, int sx, int sy)
	{
		_posX = x;
		_posY = y;
		_sizeX = sx;
		_sizeY = sy;
	}
	
	public Element()
	{
		_posX = 0;
		_posY = 0;
		_sizeX = 10;
		_sizeY = 10;
	}
	
	protected int _posX;
	protected int _posY;
	protected int _sizeX;
	protected int _sizeY;
	
}