package blitzEdit.application;

import java.util.Vector;

import blitzEdit.core.Component;
import blitzEdit.core.ComponentLibrary;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public class LibraryCanvas extends ResizableCanvas
{
	Vector<String> entries = new Vector<String>();
	ComponentLibrary componentLibrary;
	GraphicsContext gc;
	
	private double scale = 0.5;
	
	
	public LibraryCanvas()
	{
		// TODO Auto-generated constructor stub
		gc = getGraphicsContext2D();
		componentLibrary = new ComponentLibrary();
		
		entries.add("img/Widerstand.svg");
		entries.add("img/Kondensator.svg");
		entries.add("img/Spannungsquelle.svg");
		entries.add("img/Spule.svg");
		
	}
	
	public void drawLibraryEntries()
	{
		componentLibrary.draw(gc, scale);
	}
	
	public void OnMousePressedHandler()
	{
		this.setOnMousePressed(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent click)
			{
				if (click.isPrimaryButtonDown())
				{
					componentLibrary.getBlueprint(click.getX(), click.getY());
				}
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
