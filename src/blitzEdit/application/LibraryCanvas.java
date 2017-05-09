package blitzEdit.application;

import java.util.Vector;
import javafx.scene.canvas.GraphicsContext;

public class LibraryCanvas extends ResizableCanvas
{
	Vector<String> entries = new Vector<String>();
	GraphicsContext gc;
	
	
	public LibraryCanvas()
	{
		// TODO Auto-generated constructor stub
		gc = getGraphicsContext2D();
		
		entries.add("img/Widerstand.svg");
		entries.add("img/Kondensator.svg");
		entries.add("img/Spannungsquelle.svg");
		entries.add("img/Spule.svg");
		
	}
	
	public void drawLibraryEntries()
	{
		int posY = 50;
		int posX = 25;
		for(String entry : entries)
		{
			String foo = SvgRenderer.getSvgFileString(entry);
			SvgRenderer.renderSvgString(foo, gc, posX, posY, 0.5,false);
			posX += 50;
			if(posX > 75)
			{
				posX = 25;
				posY += 120;
			}			
		}
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
