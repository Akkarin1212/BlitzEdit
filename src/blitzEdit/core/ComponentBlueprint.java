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
	
	public String getSvg()
	{
		return new String(_svg);
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
	
	public ComponentBlueprint(String type, String svg, int [][] relPos, short [] conRelRot, int sizeX, int sizeY)
	{
		_type = new String(type);
		_svg = new String(svg);
		_conRelPos = relPos.clone();
		_conRelRot = conRelRot.clone();
		_sizeX = sizeX;
		_sizeY = sizeY;
	}
	
	private String _type;
	private String _svg;
	private int [][] _conRelPos;
	private short [] _conRelRot;
	private int _sizeX;
	private int _sizeY;
}
