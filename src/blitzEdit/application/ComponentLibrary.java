package blitzEdit.application;

import blitzEdit.core.BlueprintContainer;
import blitzEdit.core.Component;
import blitzEdit.core.ComponentBlueprint;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.TreeMap;

//Stellt Elemente in einer Bibliothek dar und gibt sie zurück wenn sie angeklickt werden
public class ComponentLibrary extends ResizableCanvas
{
	private static BlueprintContainer bc;
	private GraphicsContext gc;
	// Liste der Positionen
	private ArrayList<Rectangle> locations;
	// Ordnet die Blueprints ihren Positionen auf dem Canvas zu
	private TreeMap<Rectangle, ComponentBlueprint> posBlueprints;
	int _marginX, _marginY;
	
	
	//Konstruktor
	public ComponentLibrary(int marginX, int marginY)
	{
		bc 		 		= BlueprintContainer.get();
		gc 		 		= getGraphicsContext2D();
		locations 		= new ArrayList<Rectangle>();
		posBlueprints 	= new TreeMap<Rectangle, ComponentBlueprint>();
		_marginX 		= marginX;
		_marginY 		= marginY;
	}
	
	//Standardkonstruktor
	public ComponentLibrary()
	{
		bc 		 		= BlueprintContainer.get();
		gc 		 		= getGraphicsContext2D();
		locations 		= new ArrayList<Rectangle>();
		posBlueprints 	= new TreeMap<Rectangle, ComponentBlueprint>();
		_marginX 		= 10;
		_marginY 		= 10;
	}
	
	
	//Zeichnet die Elemente in der Library
	public void draw()
	{
		int posY = 50;
		int posX = 25;
		int maxY = 0;
		locations.clear();
		posBlueprints.clear();
		
		for (ComponentBlueprint cb : bc.getBlueprints())
		{
			String foo = SvgRenderer.getSvgFileString(cb.getSvg());
			SvgRenderer.renderSvgString(foo, gc, posX, posY, 0.5, false);
			
			//Mappen der Elemente
			Rectangle position = new Rectangle(posX, posY, cb.getSizeX(), cb.getSizeY());
			
			locations.add(position);
			posBlueprints.put(position, cb);
			
			posX += cb.getSizeX() + _marginX;
			
			//Setzt die Höhe auf den Wert des höchsten Elements in der Reihe
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
	
	//gibt das angeklickte Element in der Bibliothek zurück
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
	
	//Clickhandler für Linksclick auf Element in Library
	@SuppressWarnings("unused")
	private void onClickHandler()
	{
		this.setOnMousePressed(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent click)
			{
				if (click.isPrimaryButtonDown())
				{
					getBlueprint(click.getX(), click.getY());
				}
			}
		});
	}
	
	
	
	
	
	
}
