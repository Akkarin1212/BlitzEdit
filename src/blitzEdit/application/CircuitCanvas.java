package blitzEdit.application;

import java.util.ArrayList;

import blitzEdit.core.Circuit;
import blitzEdit.core.Component;
import blitzEdit.core.Element;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public class CircuitCanvas extends ResizableCanvas
{
	GraphicsContext gc;
	Circuit circuit;
	String currentSvgPath;
	
	public CircuitCanvas()
	{
		gc = getGraphicsContext2D();
		circuit = new Circuit();
		
		currentSvgPath = "img/Widerstand.svg";
		
		addClickHandler();
	}
	
	private void addClickHandler()
	{
		EventHandler<MouseEvent> OnMouseClickedEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click)
			{
				refreshCanvas();
				int [][] relPos = {{0, 10},{0, -10}};
				Component comp = new Component((int)click.getX(), (int)click.getY(), (short)0, "dunno", relPos, currentSvgPath);
				circuit.addElement(comp);
				SvgRenderer.renderSvgString(comp.getSvgFileString(), gc, click.getX(), click.getY(), 1.0);
			}
		};
		this.setOnMouseClicked(OnMouseClickedEventHandler);
	}
	
	public void drawGrid()
	{
		System.out.println(getWidth() + " " + getHeight());
		
        gc.clearRect(0, 0, getWidth(), getHeight());

        gc.setLineWidth(0.1);
        
        // vertical lines
        for(int i = 0 ; i < getWidth() ; i+=30){
            gc.strokeLine(i, 0, i, getHeight());
        }        

        // horizontal lines
        for(int i = 30 ; i < getHeight() ; i+=30){
            gc.strokeLine(30, i, getWidth(), i);
        } 
	}
	
	public void drawAllCircuitElements()
	{
		ArrayList<Element> array = circuit.getElements();
		for(Element elem : array)
		{
			elem.draw(gc, 1.0);
		}
	}
	
	@Override
	public void resize(double width, double height)
	{
	    super.setWidth(width);
	    super.setHeight(height);
	    refreshCanvas();
	}
	
	public void refreshCanvas()
	{
		drawGrid();
	    drawAllCircuitElements();
	}
}
