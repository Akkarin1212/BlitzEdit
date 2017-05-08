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
		int posY = 100;
		int posX = 50;
		for(String entry : entries)
		{
			String foo = SvgRenderer.getSvgFileString(entry);
			SvgRenderer.renderSvgString(foo, gc, posX, posY, 0.5);
			posX += 50;
			if(posX > 100)
			{
				posX = 50;
				posY += 120;
			}			
		}
	}
	@Override
	public void resize(double width, double height)
	{
	    super.setWidth(width);
	    super.setHeight(height);
	    drawLibraryEntries();
	}
	
}
