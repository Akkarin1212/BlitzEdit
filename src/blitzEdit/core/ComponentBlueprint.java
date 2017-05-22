package blitzEdit.core;

// Class ComponentBlueprint:
// holds constructor parameters for a
// specific component.
public class ComponentBlueprint 
{
	public String getType()
	{
		return new String(_type);
	}
	
	public String getSvgFilePath()
	{
		return new String(_svgFilePath);
	}
	public int [][] getRelPos() 
	{
		return _conRelPos.clone();
	}
	
	public short [] getConRelRot()
	{
		return _conRelRot.clone();
	}
	
	public int getSizeX()
	{
		return _sizeX;
	}
	
	public int getSizeY()
	{
		return _sizeY;
	}
	
	public ComponentBlueprint(String type, String svgFilePath, int [][] relPos, short [] conRelRot, int sizeX, int sizeY)
	{
		_type = new String(type);
		_svgFilePath = new String(svgFilePath);
		_conRelPos = relPos.clone();
		_conRelRot = conRelRot.clone();
		_sizeX = sizeX;
		_sizeY = sizeY;
	}
	
	private String _type;
	private String _svgFilePath;
	private int [][] _conRelPos;
	private short [] _conRelRot;
	private int _sizeX;
	private int _sizeY;
}
