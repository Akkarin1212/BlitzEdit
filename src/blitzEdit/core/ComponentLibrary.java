package blitzEdit.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import tools.SelectionMode;
import tools.SvgRenderer;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

//Stellt Elemente in einer Bibliothek dar und gibt sie zur�ck wenn sie angeklickt werden
public class ComponentLibrary
{
	private ArrayList<ComponentBlueprint> blueprints;
	// Ordnet die Blueprints ihren Positionen auf dem Canvas zu
	private ArrayList<LibraryEntry> entries;
	int _marginX, _marginY;
	double _scale;
	
	
	//Konstruktor
	public ComponentLibrary(int marginX, int marginY, double scale)
	{
		blueprints 		= new ArrayList<ComponentBlueprint>();
		entries 		= new ArrayList<LibraryEntry>();
		_marginX 		= marginX;
		_marginY 		= marginY;
		_scale			= scale;
	}
	
	//Standardkonstruktor
	public ComponentLibrary()
	{
		blueprints 		= new ArrayList<ComponentBlueprint>();
		entries 		= new ArrayList<LibraryEntry>();
		_marginX 		= 20;
		_marginY 		= 20;
		_scale			= 0.5;
	}
	
	public void initiate(GraphicsContext gc)
	{
		int posY = 60;
		int posX = 40;
		int maxY = 0;
		entries.clear();
		gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
		
		for (ComponentBlueprint cb : blueprints)
		{
			String svgString = SvgRenderer.getSvgFileString(cb.getSvgFilePath());
			SvgRenderer.renderSvgString(svgString, gc, posX, posY, _scale, SelectionMode.UNSELECTED);

			double compSizeX = cb.getSizeX() * _scale;
			double compSizeY = cb.getSizeY() * _scale;

			LibraryEntry entry = new LibraryEntry(posX, posY, compSizeX, compSizeY, cb);

			entries.add(entry);

			posX += compSizeX + _marginX;

			// Setzt die H�he auf den Wert des h�chsten Elements in der
			// Reihe
			if (maxY < compSizeY)
			{
				maxY = (int) compSizeY;
			}

			if (posX > 150)
			{
				posX = 40;
				posY += maxY + _marginY;
				maxY = 0;
			}
		}
	}
	

	public void draw(GraphicsContext gc)
	{
		gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
		for (LibraryEntry entry : entries)
		{
			entry.draw(gc);
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
	
	public void addBlueprint(File filepath)
	{
		ComponentBlueprint bp = BlueprintContainer.get().addBlueprint(filepath);
		if(bp != null)
		{
			blueprints.add(bp);
		}
		
	}
	
	//gibt das angeklickte Element in der Bibliothek zur�ck
	public ComponentBlueprint getBlueprint(int x, int y)
	{
		for (LibraryEntry entry : entries)
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
		
		public void draw(GraphicsContext gc)
		{
			int posX = rect.x + (int)(rect.width * 0.5);
			int posY = rect.y + (int)(rect.height * 0.5);
			
			gc.setFont(new Font(9));
			gc.setTextAlign(TextAlignment.CENTER);
			gc.strokeText(bp.getType(), rect.x + rect.width * 0.5, rect.y + 10 + rect.height);
			String svgString = SvgRenderer.getSvgFileString(bp.getSvgFilePath());
			SvgRenderer.renderSvgString(svgString, gc, posX, posY, _scale, SelectionMode.UNSELECTED);
		}
		
		public LibraryEntry(double posX, double posY, double sizeX, double sizeY, ComponentBlueprint bp)
		{
			rect = new Rectangle((int)(posX - 0.5*sizeX), (int)(posY - 0.5*sizeY), (int)sizeX, (int)sizeY);
			this.bp = bp;
		}
	}
}
