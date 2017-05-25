package blitzEdit.core;

import javafx.scene.canvas.GraphicsContext;
import tools.SelectionMode;
import tools.SvgRenderer;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

//Stellt Elemente in einer Bibliothek dar und gibt sie zur�ck wenn sie angeklickt werden
public class ComponentLibrary
{
	private static BlueprintContainer bc;
	// Ordnet die Blueprints ihren Positionen auf dem Canvas zu
	private ArrayList<LibraryEntry> blueprints;
	int _marginX, _marginY;
	double _scale;
	
	
	//Konstruktor
	public ComponentLibrary(int marginX, int marginY, double scale)
	{
		bc 		 		= BlueprintContainer.get();
		blueprints 		= new ArrayList<LibraryEntry>();
		_marginX 		= marginX;
		_marginY 		= marginY;
		_scale			= scale;
	}
	
	//Standardkonstruktor
	public ComponentLibrary()
	{
		bc 		 		= BlueprintContainer.get();
		blueprints 		= new ArrayList<LibraryEntry>();
		_marginX 		= 10;
		_marginY 		= 10;
		_scale			= 0.5;
	}
	
	public void initiate(GraphicsContext gc)
	{
		int posY = 50;
		int posX = 25;
		int maxY = 0;
		blueprints.clear();
		
		for (ComponentBlueprint cb : bc.getBlueprints())
		{
			String svgString = SvgRenderer.getSvgFileString(cb.getSvgFilePath());
			SvgRenderer.renderSvgString(svgString, gc, posX, posY, _scale, SelectionMode.UNSELECTED);
			
			double compSizeX = cb.getSizeX() * _scale;
			double compSizeY = cb.getSizeY() * _scale;
			
			LibraryEntry entry = new LibraryEntry(posX, posY, compSizeX, compSizeY, cb);
			
			blueprints.add(entry);
			
			posX += compSizeX + _marginX;
			
			//Setzt die H�he auf den Wert des h�chsten Elements in der Reihe
			if (maxY < compSizeY)
			{
				maxY = (int) compSizeY;
			}
					
					
			if (posX > 150)
			{
				posX  = 25;
				posY += maxY + _marginY;
				maxY  = 0;
			}
		}
	}
	

	public void draw(GraphicsContext gc)
	{
		gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
		for (LibraryEntry entry : blueprints)
		{
			int posX = entry.rect.x + (int)(entry.rect.width * 0.5);
			int posY = entry.rect.y + (int)(entry.rect.height * 0.5);
			
			String svgString = SvgRenderer.getSvgFileString(entry.bp.getSvgFilePath());
			SvgRenderer.renderSvgString(svgString, gc, posX, posY, _scale, SelectionMode.UNSELECTED);
		}
	}
	
	public void setScale(double scale)
	{
		_scale = scale;
	}
	
	public double getScale()
	{
		return _scale;
	}
	
	public Element createComponent(ComponentBlueprint bp, double posX, double posY)
	{
		return new Component((int)posX, (int)posY, (short)0, bp.getType(), bp.getRelPos(), bp.getConRelRot(), bp.getSvgFilePath());
	}
	
	public void addBlueprint(File filepath)
	{
		bc.addBlueprint(filepath);
	}
	
	//gibt das angeklickte Element in der Bibliothek zur�ck
	public ComponentBlueprint getBlueprint(int x, int y)
	{
		for (LibraryEntry entry : blueprints)
		{
			if (entry.rect.contains(x, y))
				return entry.bp;
		}
		return null;		
	}
	
	public ComponentBlueprint getBlueprint(double x, double y)
	{
		return getBlueprint((int)x, (int)y);
	}
	
	private class LibraryEntry
	{
		public Rectangle rect = null;
		public ComponentBlueprint bp = null;
		
		public LibraryEntry(double posX, double posY, double sizeX, double sizeY, ComponentBlueprint bp)
		{
			rect = new Rectangle((int)(posX - 0.5*sizeX), (int)(posY - 0.5*sizeY), (int)sizeX, (int)sizeY);
			this.bp = bp;
		}
	}
}
