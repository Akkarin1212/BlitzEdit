package blitzEdit.application;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ResizableCanvas extends Canvas
{
	GraphicsContext gc;
	
	public ResizableCanvas()
	{
		gc = this.getGraphicsContext2D();
		
		addClickHandler();
		
		
	}
	
	@Override
	public double minHeight(double width)
	{
	    return 200;
	}

	@Override
	public double maxHeight(double width)
	{
	    return 1000;
	}

	@Override
	public double prefHeight(double width)
	{
	    return minHeight(width);
	}

	@Override
	public double minWidth(double height)
	{
	    return 200;
	}

	@Override
	public double maxWidth(double height)
	{
	    return 1600;
	}
	
	@Override
	public double prefWidth(double height)
	{
	    return minHeight(height);
	}

	@Override
	public boolean isResizable()
	{
	    return true;
	}

	@Override
	public void resize(double width, double height)
	{
	    super.setWidth(width);
	    super.setHeight(height);
	    paint();
	}

	private void paint()
	{
		gc.setFill(Color.GREEN);
		//gc.fillRect(0,0,getWidth(), getHeight());
		System.out.println(getWidth() + " " + getHeight());
	}
	
	private void addClickHandler()
	{
		EventHandler<MouseEvent> OnMouseClickedEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click)
			{
				String foo = SvgRenderer.getSvgFileString("img/Widerstand.svg");
				System.out.println(foo);
				SvgRenderer.renderSvgString(foo, gc, click.getX(), click.getY());
			}
		};
		this.setOnMouseClicked(OnMouseClickedEventHandler);
	}
}
