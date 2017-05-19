package blitzEdit.core;

import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.TreeMap;

import blitzEdit.application.ResizableCanvas;
import blitzEdit.application.SvgRenderer;

//Stellt Elemente in einer Bibliothek dar und gibt sie zur�ck wenn sie angeklickt werden
public class ComponentLibrary
{
	private static BlueprintContainer bc;
	// Liste der Positionen
	private ArrayList<Rectangle> locations;
	// Ordnet die Blueprints ihren Positionen auf dem Canvas zu
	private TreeMap<Rectangle, ComponentBlueprint> posBlueprints;
	int _marginX, _marginY;
	
	
	//Konstruktor
	public ComponentLibrary(int marginX, int marginY)
	{
		bc 		 		= BlueprintContainer.get();
		locations 		= new ArrayList<Rectangle>();
		posBlueprints 	= new TreeMap<Rectangle, ComponentBlueprint>();
		_marginX 		= marginX;
		_marginY 		= marginY;
	}
	
	//Standardkonstruktor
	public ComponentLibrary()
	{
		bc 		 		= BlueprintContainer.get();
		locations 		= new ArrayList<Rectangle>();
		posBlueprints 	= new TreeMap<Rectangle, ComponentBlueprint>();
		_marginX 		= 10;
		_marginY 		= 10;
	}
	
	
	//Zeichnet die Elemente in der Library
	public void draw(GraphicsContext gc, double scale)
	{
		int posY = 50;
		int posX = 25;
		int maxY = 0;
		locations.clear();
		posBlueprints.clear();
		
		for (ComponentBlueprint cb : bc.getBlueprints())
		{
			String foo = SvgRenderer.getSvgFileString(cb.getSvg());
			SvgRenderer.renderSvgString(foo, gc, posX, posY, scale, false);
			
			//Mappen der Elemente
			Rectangle position = new Rectangle(posX, posY, cb.getSizeX(), cb.getSizeY());
			
			locations.add(position);
			posBlueprints.put(position, cb);
			
			posX += cb.getSizeX() + _marginX;
			
			//Setzt die H�he auf den Wert des h�chsten Elements in der Reihe
			if (maxY < cb.getSizeY())
					maxY = cb.getSizeY();
			
			if (posX > 75)
			{
				posX  = 25;
				posY += maxY + _marginY;
				maxY  = 0;
			}			
		}
	}
	
	//gibt das angeklickte Element in der Bibliothek zur�ck
	public ComponentBlueprint getBlueprint(int x, int y)
	{
		for (Rectangle rect : locations)
		{
			if (rect.contains(x, y))
				return posBlueprints.get(rect);
		}
		return null;		
	}
	
	public ComponentBlueprint getBlueprint(double x, double y)
	{
		return getBlueprint((int)x, (int)y);
	}
}
