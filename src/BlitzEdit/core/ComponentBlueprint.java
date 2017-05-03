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
		return _relPos.clone();
	}
	
	public int getSizeX()
	{
		return _sizeX;
	}
	
	public int getSizeY()
	{
		return _sizeY;
	}
	
	public ComponentBlueprint(String type, String svg, int [][] relPos, int sizeX, int sizeY)
	{
		_type = new String(type);
		_svg = new String(svg);
		_relPos = relPos.clone();
		_sizeX = sizeX;
		_sizeY = sizeY;
	}
	
	private String _type;
	private String _svg;
	private int [][] _relPos;
	private int _sizeX;
	private int _sizeY;
}
