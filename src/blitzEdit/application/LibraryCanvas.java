package blitzEdit.application;

import java.io.File;
import java.util.Vector;

import blitzEdit.core.ComponentBlueprint;
import blitzEdit.core.ComponentLibrary;
import blitzEdit.core.Element;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import tools.SelectionMode;

/**
 * Canvas used for displaying the library and its entries.
 * 
 * @author Chrisian Gärtner
 */
public class LibraryCanvas extends ResizableCanvas
{
	Vector<File> entries = new Vector<File>();
	ComponentLibrary componentLibrary;
	GraphicsContext gc;
	Element currentDraggedElement = null;
	
	private double scale = 0.5;
	
	
	public LibraryCanvas()
	{
		// TODO Auto-generated constructor stub
		gc = getGraphicsContext2D();
		componentLibrary = new ComponentLibrary();
		
		entries.add(new File("blueprints/Widerstand.xml"));
		entries.add(new File("blueprints/Kondensator.xml"));
		entries.add(new File("blueprints/Spannungsquelle.xml"));
		entries.add(new File("blueprints/Spule.xml"));
		
		for(File f : entries)
		{
			componentLibrary.addBlueprint(f);
		}
		
		componentLibrary.initiate(gc);
		
		onMousePressedHandler();
		onMouseDragDetectedHandler();
		onMouseDraggedHandler();
		onMouseReleasedHandler();
	}
	
	/**
	 * Uses the {@link ComponentLibrary} to draw all entries of this library.
	 */
	public void drawLibraryEntries()
	{
		componentLibrary.draw(gc);
	}
	
	/**
	 * Adds the event handler for mouse press.
	 * When clicked on the library canvas, saves the blueprint clicked on and and creates component.
	 */
	private void onMousePressedHandler()
	{
		this.setOnMousePressed(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent click)
			{
				if (click.isPrimaryButtonDown() && currentDraggedElement == null)
				{
					ComponentBlueprint bp = componentLibrary.getBlueprint(click.getX(), click.getY());
					if(bp != null)
					{
						currentDraggedElement = componentLibrary.createComponent(bp, click.getX(), click.getY());
						currentDraggedElement.draw(gc, scale, SelectionMode.UNSELECTED);
					}
				}
			}
		});
	}
	
	/**
	 * Adds the event handler for drag detection.
	 * Calls startFullDrag().
	 */
	private void onMouseDragDetectedHandler()
	{
		this.setOnDragDetected(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent click)
			{
				if (click.isPrimaryButtonDown() && currentDraggedElement != null)
				{
					startFullDrag();
					System.err.println("blub");
					
				}
			}
		});
	}
	
	/**
	 * Adds the event handler for mouse drag.
	 * Moves the currentDraggedElement according to mouse position. 
	 * Sets dragAndDropElement in BlitzEdit to currentDraggedElement.
	 */
	private void onMouseDraggedHandler()
	{
		this.setOnMouseDragged(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent click)
			{
				if (click.isPrimaryButtonDown() && currentDraggedElement != null)
				{
					currentDraggedElement.move((int) click.getX(), (int) click.getY());
					componentLibrary.draw(gc);
					currentDraggedElement.draw(gc, scale, SelectionMode.UNSELECTED);
					
					// create connection between circuitcanvas and librarycanvas
					BlitzEdit.dragAndDropElement = currentDraggedElement; 
				}
			}
		});
	}
	
	/**
	 * Adds the event handler for mouse release.
	 * Resets references to the current dragged element.
	 */
	private void onMouseReleasedHandler()
	{
		this.setOnMouseReleased(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent click)
			{
				currentDraggedElement = null;
				BlitzEdit.dragAndDropElement = null; 
				componentLibrary.draw(gc);
			}
		});
	}
	
	
	@Override
	public void resize(double width, double height)
	{
		super.setHeight(height);
		super.setWidth(width);
	    drawLibraryEntries();
	}
	
	@Override
	public double minWidth(double height)
	{
	    return 200;
	}

	@Override
	public double maxWidth(double height)
	{
	    return 200;
	}
	
	@Override
	public double minHeight(double width)
	{
	    return 680;
	}

	@Override
	public double maxHeight(double width)
	{
	    return 2000;
	}
	
	
}
