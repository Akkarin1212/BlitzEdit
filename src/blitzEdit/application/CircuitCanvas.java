package blitzEdit.application;

import java.util.ArrayList;
import java.util.Vector;

import blitzEdit.core.Circuit;
import blitzEdit.core.Component;
import blitzEdit.core.Connector;
import blitzEdit.core.Element;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;

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
	private boolean canSelectMultipleElements; 
	
	private double canvasScaleFactor = 1;
	
	// used to save click begin
	private double clickX; 
	private double clickY;
	
	// used for calculation when moving multiple elements
	private double dragX; 
	private double dragY;

	public CircuitCanvas(ScrollPane scrollPane)
	{
		gc = getGraphicsContext2D();
		circuit = new Circuit();
		currentSvgPath = "img/Widerstand.svg";

		sp = scrollPane;
		
		onMousePresseHandler();
		onMouseDraggedHandler();
		onMouseReleasedHandler();
		onMouseMovedHandler();
		onScrollEventHandler();
		initiateRightClickMenu();
	}

	private void onMousePresseHandler()
	{
		this.setOnMousePressed(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent click)
			{
				rightClickMenu.hide();
				
				if (click.isSecondaryButtonDown()) //TODO
				{
					if (click.isControlDown())
					{
						int[][] relPos = { { 0, 10 }, { 0, -10 } };
						short[] relRot = { 0, 0 };
						Component comp = new Component((int) click.getX(), (int) click.getY(), (short) 0, "dunno",
								relPos, relRot, currentSvgPath);
						circuit.addElement(comp);
						refreshCanvas();
					}
					else
					{
						rightClickMenu.show(ref, click.getScreenX(), click.getScreenY());
					}

					System.err.println("secondary click");
				}
				else if (click.isPrimaryButtonDown())
				{
					if (!hasSelectedMultipleElements)
					{
						selectElement(click.getX(), click.getY());
					}
					
					canSelectMultipleElements = true;

					clickX = click.getX();
					clickY = click.getY();
					dragX = click.getX();
					dragY = click.getY();
					
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
				if (click.isPrimaryButtonDown())
				{

					// moves multiple elements at same time
					if (hasSelectedMultipleElements)
					{
						for (Element e : currentSelectedElements)
						{
							if (e.getClass() == Component.class)
							{
								e.setPosition(e.getX() + click.getX() - dragX, e.getY() + click.getY() - dragY);
							}
						}
						dragX = click.getX();
						dragY = click.getY();

						changeCursorStyle(Cursor.MOVE);
						refreshCanvas();

						System.err.println("move multiple elements");
					}
					// only move 1 element
					else if (!currentSelectedElements.isEmpty())
					{
						for (Element e : currentSelectedElements)
						{
							e.move((int) click.getX(), (int) click.getY());
						}
						changeCursorStyle(Cursor.MOVE);
						refreshCanvas();

						System.err.println("move element");
					}
					// draw the selection rect
					else if (canSelectMultipleElements)
					{
						drawSelectRect(click.getX(), click.getY());
						isSelectingMultipleElements = true;

						System.err.println("draw select rect");
					}
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

				// deselect multiple elements when pressing and releasing
				// mouse at the same position
				if (hasSelectedMultipleElements && (click.getX() == clickX && click.getY() == clickY))
				{

					deselectCurrentSelectedElements();
					hasSelectedMultipleElements = false;
					System.err.println("release muliple elements");
				}
				// deselect single element when releasing
				else if (!currentSelectedElements.isEmpty())
				{

				}
				// when selection is in progress calculate all the objects
				// in the selection rect
				else if (isSelectingMultipleElements)
				{
					selectElements(clickX, clickY, click.getX(), click.getY());
					if (!currentSelectedElements.isEmpty())
					{
						isSelectingMultipleElements = false;
						hasSelectedMultipleElements = true;
					}
				}

				canSelectMultipleElements = false;
				changeCursorStyle(Cursor.DEFAULT);
				refreshCanvas();
				clickX = 0;
				clickY = 0;
				dragX = 0;
				dragY = 0;

			}
		});
	}
	
	private void onScrollEventHandler()
	{
		this.setOnScroll(new EventHandler<ScrollEvent>(){
			@Override
			public void handle(ScrollEvent click)
			{
				if(click.getDeltaY() < 0)
				{
					zoomOut();
				}
				else
				{
					zoomIn();
				}
			}
		});
	}
	
	private void onMouseMovedHandler()
	{
		this.setOnMouseMoved(new EventHandler<MouseEvent>()
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
		double lineSpace = 25;// * canvasScaleFactor;

		// vertical lines
		for (int i = 0; i < getWidth(); i += lineSpace)
		{
			gc.strokeLine(i, 0, i, getHeight());
		}

		// horizontal lines
		for (int i = (int) lineSpace; i < getHeight(); i += lineSpace)
		{
			gc.strokeLine(0, i, getWidth(), i);
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
	
	public void pasteSelected(Element[] elem, Point2D mousePos)
	{
		deselectCurrentSelectedElements();
		for(Element e : elem)
		{
			if(e.getClass() == Component.class)
			{
				Component orginal = (Component) e; //TODO copy function for components
				
				double offsetX = e.getX() - mousePos.getX();
				double offsetY = e.getY() - mousePos.getY();
				
				Element clone = orginal.clone();
				
				circuit.addElement(clone.move(currentMousePosition.getX() + offsetX, currentMousePosition.getY() + offsetY));
				selectElement(clone);
				refreshCanvas();
			}
		}
		refreshCanvas();
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
		
		isSelectingMultipleElements = false;
		hasSelectedMultipleElements = true;
		refreshCanvas();
	}
	
	public void deselectAll()
	{
		deselectCurrentSelectedElements();
		refreshCanvas();
	}
	
	public void zoomIn()
	{
		if(canvasScaleFactor < 1)
		{
			canvasScaleFactor += 0.125;
			
			double posX = sp.getVvalue();
			double posY = sp.getHvalue();
			
			setScaleX(canvasScaleFactor);
			setScaleY(canvasScaleFactor);
			
			sp.setHvalue(canvasScaleFactor * 0.5);
			sp.setVvalue(canvasScaleFactor * 0.5);
			
			sp.setHmin((1-canvasScaleFactor)/2);
			sp.setVmin((1-canvasScaleFactor)/2);
			
			sp.setVvalue(posX);
			sp.setHvalue(posY);
			
			refreshCanvas();
		}
		else
		{
			canvasScaleFactor = 1;
		}
	}

	public void zoomOut()
	{
		if(canvasScaleFactor > 0.5)
		{
			canvasScaleFactor -= 0.125;
			
			double posX = sp.getVvalue();
			double posY = sp.getHvalue();
			
			setScaleX(canvasScaleFactor);
			setScaleY(canvasScaleFactor);
			
			sp.setHvalue(canvasScaleFactor * 0.5);
			sp.setVvalue(canvasScaleFactor * 0.5);
			
			sp.setHmin((1-canvasScaleFactor)/2);
			sp.setVmin((1-canvasScaleFactor)/2);
			
			sp.setVvalue(posX);
			sp.setHvalue(posY);
			
			refreshCanvas();
		}
		else
		{
			canvasScaleFactor = 0.55;
		}
	}
	
	public Point2D getMousePosition()
	{
		return new Point2D(currentMousePosition.getX(), currentMousePosition.getY());
	}
	
	private void changeCursorStyle(Cursor value)
	{
		Main.mainStage.getScene().setCursor(value);
	}
	
	private void initiateRightClickMenu()
	{
		rightClickMenu = new ContextMenu();
		
		MenuItem copy = new MenuItem("Copy");
		MenuItem paste = new MenuItem("Paste");
		MenuItem delete = new MenuItem("Delete");

		copy.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent click)
			{
				BlitzEdit.elementsToCopy = copySelected();
				BlitzEdit.copyMousePosition = getMousePosition();
				
				System.out.println("copy");
			}
		});
		
		paste.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent click)
			{
				if(BlitzEdit.elementsToCopy !=  null && BlitzEdit.copyMousePosition != null)
				{
					pasteSelected(BlitzEdit.elementsToCopy, BlitzEdit.copyMousePosition);
				}
				
				System.out.println("paste");
			}
		});
		
		delete.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent click)
			{
				deleteSelected();
				System.out.println("delete");
			}
		});

		rightClickMenu.getItems().addAll(copy, paste, delete);
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
			
			hasSelectedMultipleElements = false;
		}
	}
	
	private void selectElement(double x, double y)
	{
		deselectCurrentSelectedElements();
		
		ArrayList<Element> elements = circuit.getElementsByPosition(x, y);
		if (elements != null && !currentSelectedElements.contains(elements.get(0))) // avoid selection duplicates
		{
			Element elemToAdd = elements.get(0);
			for(Element e : elements)
			{
				if(e.getClass() == Connector.class)
				{
					elemToAdd = e;
				}
			}
			currentSelectedElements.add(elemToAdd.setIsSelected(true)); // take first element found
		}
	}
	
	private void selectElement(Element element)
	{
		if(element != null)
		{
			currentSelectedElements.add(element.setIsSelected(true));
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
	
	private void selectElements(Element[] elements)
	{
		if(elements != null)
		{
			for(Element e : elements)
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
