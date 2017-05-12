package blitzEdit.application;

import java.util.ArrayList;
import java.util.Vector;

import blitzEdit.core.Circuit;
import blitzEdit.core.Component;
import blitzEdit.core.Element;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class CircuitCanvas extends ResizableCanvas
{
	GraphicsContext gc;
	Circuit circuit;
	ScrollPane sp;
	
	private MouseEvent currentMousePosition;
	
	private CircuitCanvas ref = this;
	private ContextMenu rightClickMenu;
	private String currentSvgPath;
	private Vector<Element> currentSelectedElements = new Vector<Element>();
	private boolean isSelectingMultipleElements;
	private boolean hasSelectedMultipleElements;
	
	private double zoomScale = 1;
	
	// used to save click begin
	private double clickX; 
	private double clickY;
	
	// used for calculation when moving multiple elements
	private double dragX; 
	private double dragY;

	public CircuitCanvas(ScrollPane sp)
	{
		gc = getGraphicsContext2D();
		circuit = new Circuit();
		currentSvgPath = "img/Widerstand.svg";

		this.sp = sp;
		// this.sp.setPannable(true);

		onClickHandler();
		onMouseDraggedHandler();
		onMouseReleasedHandler();
		onMouseMovedHandler();

		// initiateRightClickMenu();
	}

	private void onClickHandler()
	{
		this.setOnMousePressed(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent click)
			{
				if (click.isSecondaryButtonDown())
				{
					int[][] relPos = { { 0, 10 }, { 0, -10 } };
					short[] relRot = { 0, 0 };
					Component comp = new Component((int) click.getX(), (int) click.getY(), (short) 0, "dunno", relPos,
							relRot, currentSvgPath);
					circuit.addElement(comp);
					refreshCanvas();
					
					System.err.println("secondary click");
				}
				else if (click.isPrimaryButtonDown())
				{
					if (!hasSelectedMultipleElements)
					{
						selectElement(click.getX(), click.getY());
					}

					clickX = click.getX();
					clickY = click.getY();
					dragX = click.getX();
					dragY = click.getY();
					
					click.consume();
					refreshCanvas();
					
					System.err.println("primary click");
				}
			}
		});
	}

	private void onMouseDraggedHandler()
	{
		this.setOnMouseDragged(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent click)
			{
				if(hasSelectedMultipleElements)
				{
					for(Element e : currentSelectedElements)
					{
						if (e.getClass() == Component.class)
						{
							e.setPosition(e.getX() + click.getX() - dragX, e.getY() + click.getY() - dragY);		
						}
					}
					dragX = click.getX();
					dragY = click.getY();
					refreshCanvas();
					
					System.err.println("move multiple elements");
				}
				else if (!currentSelectedElements.isEmpty())
				{
					for(Element e : currentSelectedElements)
					{
						e.move((int) click.getX(), (int) click.getY());
					}
					refreshCanvas();
					
					System.err.println("move element");
				}
				else
				{
					drawSelectRect(click.getX(),click.getY());
					isSelectingMultipleElements = true;
					
					System.err.println("draw select rect");
				}
			}
		});
	}

	private void onMouseReleasedHandler()
	{
		this.setOnMouseReleased(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent click)
			{
				if(hasSelectedMultipleElements)
				{
					if(click.getX() == clickX && click.getY() == clickY)
					{
						deselectCurrentSelectedElements();
						hasSelectedMultipleElements = false;
						System.err.println("release muliple elements");
					}
				}
				else if (!currentSelectedElements.isEmpty())
				{
					System.err.println("release element");
					deselectCurrentSelectedElements(); // TODO dunno
				}
				else if(isSelectingMultipleElements)
				{
					selectElements(clickX, clickY, click.getX(), click.getY());
					if(!currentSelectedElements.isEmpty())
					{
						isSelectingMultipleElements = false;
						hasSelectedMultipleElements = true;
					}
				}
				refreshCanvas();
				clickX = 0;
				clickY = 0;
				dragX = 0;
				dragY = 0;
			}
		});
	}
	
	private void onMouseMovedHandler()
	{
		setOnMouseMoved(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				currentMousePosition = event;
			}
		});
	}

	public void drawGrid()
	{
		gc.clearRect(0, 0, getWidth(), getHeight());

		gc.setLineWidth(0.1);

		// vertical lines
		for (int i = 0; i < getWidth(); i += 30)
		{
			gc.strokeLine(i, 0, i, getHeight());
		}

		// horizontal lines
		for (int i = 30; i < getHeight(); i += 30)
		{
			gc.strokeLine(30, i, getWidth(), i);
		}
	}

	public void refreshCanvas()
	{
		drawGrid();
		drawAllCircuitElements();
	}

	private void drawAllCircuitElements()
	{
		ArrayList<Element> array = circuit.getElements();
		for (Element elem : array)
		{
			elem.draw(gc, 1.0, elem.getIsSelected());
		}
	}
	
	public Element[] copySelected()
	{
		Element[] elem = currentSelectedElements.toArray(new Element[currentSelectedElements.size()]);
		return elem;
	}
	
	public void pasteSelected(Element[] elem)
	{
		deselectCurrentSelectedElements();
		for(Element e : elem)
		{
			if(e.getClass() == Component.class)
			{
				Component orginal = (Component) e; //TODO copy function for components
			}
			
		}
	}
	
	public void deleteSelected()
	{
		for(Element e : currentSelectedElements)
		{
			circuit.removeElement(e);
		}
		isSelectingMultipleElements = false;
		hasSelectedMultipleElements = false;
		currentSelectedElements.clear();
		refreshCanvas();
		
	}
	
	public void selectAll()
	{
		deselectCurrentSelectedElements();
		
		ArrayList<Element> elements = circuit.getElements();
		for(Element e : elements)
		{
			if(e.getClass() == Component.class)
			{
				currentSelectedElements.add(e.setIsSelected(true));
			}
		}
		refreshCanvas();
	}
	
	public void deselectAll()
	{
		deselectCurrentSelectedElements();
		refreshCanvas();
	}
	
	public void zoomIn()
	{
		
	}

	public void zoomOut()
	{
		
	}
	
	private void initiateRightClickMenu()
	{
		final ContextMenu contextMenu = new ContextMenu();
		MenuItem cut = new MenuItem("Cut");
		MenuItem copy = new MenuItem("Copy");
		MenuItem paste = new MenuItem("Paste");

		EventHandler<ActionEvent> OnMouseClickedEventHandler = new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent click)
			{
				System.out.println("cut");
			}
		};
		cut.setOnAction(OnMouseClickedEventHandler);

		contextMenu.getItems().addAll(cut, copy, paste);

		CircuitCanvas ref = this;
		setOnMousePressed(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent event)
			{
				if (event.isSecondaryButtonDown())
				{
					contextMenu.show(ref, event.getScreenX(), event.getScreenY());
				}
			}
		});
	}
	
	private void drawSelectRect(double currX, double currY)
	{
		refreshCanvas();
		gc.setStroke(Color.BLACK);

		if (currX > clickX && currY > clickY)
		{
			gc.strokeRect(clickX, clickY, currX - clickX, currY - clickY);
		}
		else if (currX < clickX && currY > clickY)
		{
			gc.strokeRect(currX, clickY, clickX - currX, currY - clickY);
		}
		else if (currX > clickX && currY < clickY)
		{
			gc.strokeRect(clickX, currY, currX - clickX, clickY - currY);
		}
		else if (currX < clickX && currY < clickY)
		{
			gc.strokeRect(currX, currY, clickX - currX, clickY - currY);
		}
	}
	
	private void deselectCurrentSelectedElements()
	{
		if (!currentSelectedElements.isEmpty())
		{
			for (Element e : currentSelectedElements)
			{
				e.setIsSelected(false);
			}
			currentSelectedElements.clear();
		}
	}
	
	private void selectElement(double x, double y)
	{
		deselectCurrentSelectedElements();
		
		ArrayList<Element> elements = circuit.getElementsByPosition(x, y);
		if (elements != null && !currentSelectedElements.contains(elements.get(0)))
		{
			currentSelectedElements.add(elements.get(0).setIsSelected(true)); // take first element found
		}
	}
	
	private void selectElements(double x, double y, double sizeX, double sizeY)
	{
		ArrayList<Element> elements = new ArrayList<Element>();
		
		if (sizeX > x && sizeY > y)
		{
			elements = circuit.getElementsByPosition(x, y, sizeX - x, sizeY - y);
		}
		else if (sizeX < x && sizeY > y)
		{
			elements = circuit.getElementsByPosition(sizeX, y, x - sizeX, sizeY - y);
		}
		else if (sizeX > x && sizeY < y)
		{
			elements = circuit.getElementsByPosition(x, sizeY, sizeX - x, y - sizeY);
		}
		else if (sizeX < x && sizeY < y)
		{
			elements = circuit.getElementsByPosition(sizeX, sizeY, x - sizeX, y - sizeY);
		}
		
		if (elements != null)
		{
			for (Element e : elements)
			{
				currentSelectedElements.add(e.setIsSelected(true));
			}
		}
	}

	@Override
	public void resize(double width, double height)
	{
		super.setWidth(width);
		super.setHeight(height);
		refreshCanvas();
	}

	@Override
	public double minWidth(double height)
	{
		return 2000;
	}

	@Override
	public double maxWidth(double height)
	{
		return 4000;
	}

	@Override
	public double minHeight(double width)
	{
		return 2000;
	}

	@Override
	public double maxHeight(double width)
	{
		return 4000;
	}
}
