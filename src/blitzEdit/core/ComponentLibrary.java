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

/**
 * Container used for organizing entries in a libary.
 * 
 * @author Chrisian Gärtner
 */
public class ComponentLibrary
{
	private ArrayList<ComponentBlueprint> blueprints;
	private ArrayList<LibraryEntry> entries;
	int _marginX, _marginY;
	double _scale;
	
	
	/**
	 * Constructor
	 * 
	 * @param marginX	Space between elements and left border
	 * @param marginY	Space between elements and top border
	 * @param scale		Scale of library entry images
	 */
	public ComponentLibrary(int marginX, int marginY, double scale)
	{
		blueprints 		= new ArrayList<ComponentBlueprint>();
		entries 		= new ArrayList<LibraryEntry>();
		_marginX 		= marginX;
		_marginY 		= marginY;
		_scale			= scale;
	}
	
	/**
	 * Constructor
	 * Uses default values for margin(20) and scale(0.5).
	 */
	public ComponentLibrary()
	{
		blueprints 		= new ArrayList<ComponentBlueprint>();
		entries 		= new ArrayList<LibraryEntry>();
		_marginX 		= 20;
		_marginY 		= 20;
		_scale			= 0.5;
	}
	
	/**
	 * Creates LibraryEntry for each component blueprint the library contains and sets the positions.
	 * Additionally draws each entry.
	 * 
	 * @param gc	GraphicsContext used for drawing
	 */
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

			// Setzt die Hï¿½he auf den Wert des hï¿½chsten Elements in der
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
	
	/**
	 * Calls draw() of each LibraryEntry in this library.
	 * 
	 * @param gc	GraphicsContext used for drawing
	 */
	public void draw(GraphicsContext gc)
	{
		gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
		for (LibraryEntry entry : entries)
		{
			entry.draw(gc);
		}
	}
	
	/**
	 * Setter
	 * @param scale	New scale
	 */
	public void setScale(double scale)
	{
		_scale = scale;
	}
	
	/**
	 * Getter
	 * @return double	Scale
	 */
	public double getScale()
	{
		return _scale;
	}
	
	/**
	 * Adds a blieprint to the BlueprintContainer instance and to the local blueprints array.
	 * 
	 * @param filepath Filepath containing the blueprint
	 */
	public void addBlueprint(File filepath)
	{
		ComponentBlueprint bp = BlueprintContainer.get().addBlueprint(filepath);
		if(bp != null)
		{
			blueprints.add(bp);
		}
		
	}
	
	/**
	 * Getter
	 * 
	 * @return	ComponentBlueprint[]	Contains the blueprints in this library
	 */
	public ComponentBlueprint[] getLibraryBlueprints()
	{
		return blueprints.toArray(new ComponentBlueprint[]{});
	}
	
	/**
	 * Uses contains() of LibraryEntry rect to check if (x,y) 
	 * is in the entry boundaries and returns the entry if true.
	 * @param 	x					Contains x position
	 * @param 	y					Contains y position
	 * @return	ComponentBlueprint	Blueprint at the position (x,y)
	 */
	public ComponentBlueprint getBlueprint(int x, int y)
	{
		for (LibraryEntry entry : entries)
		{
			if (entry.rect.contains(x, y))
				return entry.bp;
		}
		return null;		
	}
	
	/**
	 * Uses getBlueprint(int x, int y).
	 * @param 	x					Contains x position
	 * @param 	y					Contains y position
	 * @return	ComponentBlueprint	Blueprint at the position (x,y)
	 */
	public ComponentBlueprint getBlueprint(double x, double y)
	{
		return getBlueprint((int)x, (int)y);
	}
	
	/**
	 * Container used for drawing the entries.
	 * 
	 * @author Chrisian Gärtner
	 */
	private class LibraryEntry
	{
		public Rectangle rect = null;
		public ComponentBlueprint bp = null;
		
		/**
		 * Draws the blueprint and adds its type as text underneath it.
		 * 
		 * @param gc	GraphicsContext used for drawing
		 */
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
		
		/**
		 * Constructor
		 * 
		 * @param posX	Contains x position
		 * @param posY	Contains y position
		 * @param sizeX	Contains width
		 * @param sizeY	Contains height
		 * @param bp	Blueprint saved in this entry
		 */
		public LibraryEntry(double posX, double posY, double sizeX, double sizeY, ComponentBlueprint bp)
		{
			rect = new Rectangle((int)(posX - 0.5*sizeX), (int)(posY - 0.5*sizeY), (int)sizeX, (int)sizeY);
			this.bp = bp;
		}
	}
}
