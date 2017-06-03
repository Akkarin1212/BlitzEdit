package blitzEdit.application;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import blitzEdit.core.Circuit;
import blitzEdit.core.Component;
import blitzEdit.core.Connector;
import blitzEdit.core.Element;
import blitzEdit.core.Line;
import tools.GlobalSettings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import tools.GraphicDesignContainer;
import tools.SelectionMode;
import javafx.geometry.Point2D;

public class CircuitCanvas extends ResizableCanvas
{
	GraphicsContext gc;
	Circuit circuit;
	ScrollPane sp;
	File currentSaveDirection;
	
	private MouseEvent currentMousePosition;
	
	private CircuitCanvas ref = this;
	private ContextMenu rightClickMenu;
	private Vector<Element> currentSelectedElements = new Vector<Element>();
	private Connector currentSelectedConnector;
	
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
		
		sp = scrollPane;
		
		onMousePresseHandler();
		onMouseDraggedHandler();
		onMouseReleasedHandler();
		onMouseMovedHandler();
		onScrollEventHandler();
		DragAndDropElements();
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
					rightClickMenu.show(ref, click.getScreenX(), click.getScreenY());

					System.err.println("secondary click");
				}
				else if (click.isPrimaryButtonDown())
				{
					if(currentSelectedConnector != null)
					{
						if(connectConnector(click.getX(), click.getY())) // connect worked
						{
							
						}
						else if(disconnectConnector(click.getX(), click.getY())) // disconnect worked
						{
							
						}
						else
						{
							deselectAll();
							currentSelectedConnector = null;
							selectElement(click.getX(), click.getY());
						}
						
						refreshCanvas();
					}
					else if (!hasSelectedMultipleElements)
					{
						currentSelectedConnector = null;
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
				else if (click.isMiddleButtonDown())
				{
					clickX = click.getX();
					clickY = click.getY();
					dragX = sp.getVvalue();
					dragY = sp.getHvalue();
					System.err.println("middle mouse click");
				}
				
			}
		});
	}

	private void onMouseDraggedHandler()
	{
		this.setOnMouseDragged(new EventHandler<MouseEvent>()
		{
			//
			boolean initialDrag = true;
			@Override
			public void handle(MouseEvent click)
			{
				if (click.isPrimaryButtonDown())
				{
					// moves multiple elements at same time
					if (hasSelectedMultipleElements)
					{
						//wurde der drag handler gerade erst aufgerufen, wird
						//der mauspunkt als ursprungspunkt für die translation uebernommen
						if (initialDrag)
						{
							dragX = click.getX();
							dragY = click.getY();
						}
						initialDrag = false;
						boolean moved = translateElements(currentSelectedElements, click.getX() - dragX, click.getY() - dragY);
						
						// wurden objekte bewegt, wird der ursprungspunkt für die
						// nächste translation neu gesetzt
						if(moved){
							dragX = click.getX();
							dragY = click.getY();
						}
							
						changeCursorStyle(GraphicDesignContainer.move_cursor);
						refreshCanvas();

						System.err.println("move multiple elements");
					}
					// only move 1 element
					else if (!currentSelectedElements.isEmpty())
					{
						for (Element e : currentSelectedElements)
						{
							moveElement(e, click.getX(), click.getY());
							//e.move(click.getX(), click.getY());
						}
						changeCursorStyle(GraphicDesignContainer.move_cursor);
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
				else if(click.isMiddleButtonDown())
				{
					double vvalue = (clickX - click.getX())/ref.getWidth();
					double hvalue = (clickY - click.getY())/ref.getHeight();
					
					
					sp.setHvalue(dragX + vvalue);
					sp.setVvalue(dragY + hvalue);
					
					changeCursorStyle(GraphicDesignContainer.move_cursor);
				}
			}
		});
	}
	
	private boolean translateElement(Element e, double dx, double dy)
	{
		if (GlobalSettings.SNAP_TO_GRID)
		{
			int [] snapped = snapToGrid(dx, dy);
			if (snapped[0] == 0 && snapped[1] == 0)
				return false;
			//prevent elements from moving out of canvas
			if(e.getX() + snapped[0] <= 0 || e.getY() + snapped[1] <= 0)
				return false;
			e.move(e.getX() + snapped[0], e.getY() + snapped[1]);
		}
		else
		{
			//prevent elements from moving out of canvas
			if(e.getX() + dx <= 0 || e.getY() + dy <= 0)
				return false;
			e.move(e.getX() + dx, e.getY() + dy);
		}
		return true;
	}
	
	private boolean translateElements(Collection<Element> elements, double x, double y)
	{
		boolean moved = false;
		for (Element e : elements)
		{
			if (e instanceof Component)
			{
				if (GlobalSettings.SNAP_TO_GRID)
				{
					int [] newPos = snapToGrid(x, y);
					moved = translateElement(e, newPos[0], newPos[1]);
				}
				else
				{
					moved = translateElement(e, x, y);
				}
				if (!moved)
					return false;
			}
		}
		return moved;
	}
	
	private boolean moveElement(Element e, double x, double y)
	{
		//prevent elements from moving out of canvas
		if(x <= 0 || y <= 0)
			return false;
		if (GlobalSettings.SNAP_TO_GRID)
		{
			int [] newPos = snapToGrid(x, y);
			if (newPos[0] == 0 && newPos[1] == 0)
				return false;
			e.move(newPos[0], newPos[1]);
		}
		else{
			e.move(x, y);
		}
		return true;
	}
	
	private int [] snapToGrid(double x, double y)
	{
		int [] ret = new int[2];
		int gridWidth = (int)GraphicDesignContainer.grid_spacing;
		ret[0] = (int)((int)(x / gridWidth) * gridWidth);
		ret[1] = (int)((int)(y / gridWidth) * gridWidth);
		return ret;
	}
	
	private void DragAndDropElements()
	{
		// add element when entering the canvas
		this.setOnMouseDragEntered(new EventHandler<MouseDragEvent>()
		{
			@Override
			public void handle(MouseDragEvent click)
			{
				if (click.isPrimaryButtonDown() && BlitzEdit.dragAndDropElement != null)
				{
					deselectAll();
					Component newComp = (Component) BlitzEdit.dragAndDropElement;
					moveElement(newComp, click.getX(), click.getY());
					circuit.addElement(newComp);
					selectElement(newComp);
					refreshCanvas();

					System.err.println(click.getX() + " " + click.getY());
					BlitzEdit.dragAndDropElement = null;
				}
			}
		});
		
		// refresh element position when still dragging
		this.setOnMouseDragOver(new EventHandler<MouseDragEvent>()
		{
			@Override
			public void handle(MouseDragEvent click)
			{
				moveElement(currentSelectedElements.get(0), click.getX(), click.getY());
				refreshCanvas();
			}
		});
		
		/*
		// delete if exit while dragging
		this.setOnMouseDragExited(new EventHandler<MouseDragEvent>()
		{
			@Override
			public void handle(MouseDragEvent click)
			{
				deleteSelected();
				refreshCanvas();
			}
		});
		*/
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
				changeCursorStyle(GraphicDesignContainer.default_cursor);
				refreshCanvas();
				clickX = 0;
				clickY = 0;
				dragX = 0;
				dragY = 0;
				System.err.println("release");
			}
		});
	}
	
	private void onScrollEventHandler()
	{
		sp.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>(){
			@Override
			public void handle(ScrollEvent click)
			{
				if (currentSelectedElements.isEmpty())
				{
					if (click.getDeltaY() < 0)
					{
						zoomOut();
					}
					else
					{
						zoomIn();
					}
				}
				else
				{
					if (click.getDeltaY() < 0)
					{
						rotateSelectedElements(45);
					}
					else
					{
						rotateSelectedElements(-45);
					}
				}
				click.consume();
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
		gc.save();
		gc.clearRect(0, 0, getWidth(), getHeight());
		
		gc.setStroke(GraphicDesignContainer.grid_color);
		gc.setLineWidth(GraphicDesignContainer.grid_line_width);
		double lineSpace = GraphicDesignContainer.grid_spacing;

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
		gc.restore();
	}

	public void refreshCanvas()
	{
		drawGrid();
		drawAllCircuitElements();

	}

	private synchronized void drawAllCircuitElements()
	{
		ArrayList<Element> array = circuit.getElements();
		for (Element elem : array)
		{
			elem.draw(gc, 1.0, elem.getSelectionMode());
		}
		
		ArrayList<Line> lines = circuit.getLines();
		for(Line line : lines)
		{
			line.draw(gc);
		}
		
		// prevent overlapping from lines etc. and draw current selected connector last
		if(currentSelectedConnector != null)
		{
			currentSelectedConnector.draw(gc, 1.0, currentSelectedConnector.getSelectionMode());
		}
		
		highlightConnectors();
	}
	
	// needs a selected Connector
	private void highlightConnectors()
	{
		ArrayList<Element> array = circuit.getElements();
		if (currentSelectedConnector != null)
		{
			ArrayList<Connector> connectedConn = currentSelectedConnector.getConnections();
			// draw again to prevent overlap effects when highlighting
			for (Element elem : array)
			{
				if (elem.getClass() == Connector.class
						&& ((Connector)elem).getOwner() != currentSelectedConnector.getOwner() // don't highlight connectors with same owner
						&& !connectedConn.contains(elem)) // don't highlight already connected connectors
				{
					elem.draw(gc, 1.0, SelectionMode.HIGHLIGHTED);
				}
			}
		}
	}
	
	// connects or disconnects connector at x,y
	private boolean connectConnector(double x, double y)
	{
		ArrayList<Element> elements = circuit.getElementsByPosition(x, y);
		if (elements != null) // avoid selection duplicates
		{
			Connector connector = null; // selected connector
			for(Element e : elements)
			{
				if(e.getClass() == Connector.class)
				{
					connector = (Connector)e;
				}
			}
			
			if(connector != null
					&& currentSelectedConnector != null
					&& currentSelectedConnector.getOwner() != connector.getOwner()) // don't connect 2 connector with same owner
			{
				// try to connect
				return(connector.connect((Connector) currentSelectedElements.get(0)));
			}
		}
		return false;
	}
	
	private boolean disconnectConnector(double x, double y)
	{
		ArrayList<Element> elements = circuit.getElementsByPosition(x, y);
		if (elements != null)
		{
			Connector connector = null; // get clicked connector
			for(Element e : elements)
			{
				if(e.getClass() == Connector.class)
				{
					connector = (Connector)e;
				}
			}
			
			if(connector != null
					&& currentSelectedConnector != null
					&& currentSelectedConnector.getOwner() != connector.getOwner()) // don't connect 2 connector with same owner
			{
				ArrayList<Connector> connectedConnectors = currentSelectedConnector.getConnections();
				
				// check if connected and disconnect if true
				if (connectedConnectors != null)
				{
					for (Connector conn : connectedConnectors)
					{
						if (conn.equals(connector)) // is already connected
						{
							return(currentSelectedConnector.disconnect(connector));
						}
					}
				}
			}
		}
		return false;
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
				currentSelectedElements.add(e.setSelectionMode(SelectionMode.SELECTED));
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
	
	public void rotateSelectedElements(double rotation)
	{
		if (!currentSelectedElements.isEmpty())
		{
			for (Element e : currentSelectedElements)
			{
				if (e.getClass() == Component.class)
				{
					Component comp = (Component) e;
					comp.rotate((short) (rotation));
				}
			}
			refreshCanvas();
		}
	}
	
	public void zoomIn()
	{
		if(canvasScaleFactor < 1)
		{
			canvasScaleFactor += GraphicDesignContainer.zoom_factor;
			
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
			canvasScaleFactor -= GraphicDesignContainer.zoom_factor;
			
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
		MenuItem rotate = new MenuItem("Rotate");

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
		
		rotate.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent click)
			{
				if(!currentSelectedElements.isEmpty())
				{
					rotateSelectedElements(45);
				}
			}
		});

		rightClickMenu.getItems().addAll(copy, paste, delete, rotate);
	}
	
	private void drawSelectRect(double currX, double currY)
	{
		refreshCanvas();
		gc.save();
		gc.setStroke(GraphicDesignContainer.selection_rect_color);

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
		gc.restore();
	}
	
	private void deselectCurrentSelectedElements()
	{
		if (!currentSelectedElements.isEmpty())
		{
			for (Element e : currentSelectedElements)
			{
				e.setSelectionMode(SelectionMode.UNSELECTED);
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
			// prioritize connectors over components
			for(Element e : elements)
			{
				if(e.getClass() == Connector.class)
				{
					elemToAdd = e;
					currentSelectedConnector = (Connector) e;
				}
			}
			currentSelectedElements.add(elemToAdd.setSelectionMode(SelectionMode.SELECTED)); // take first element found
		}
	}
	
	private void selectElement(Element element)
	{
		if(element != null)
		{
			currentSelectedElements.add(element.setSelectionMode(SelectionMode.SELECTED));
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
				if (e.getClass() == Component.class)
				{
					currentSelectedElements.add(e.setSelectionMode(SelectionMode.SELECTED));
				}
			}
		}
	}
	
	private void selectElements(Element[] elements)
	{
		if(elements != null)
		{
			for(Element e : elements)
			{
				currentSelectedElements.add(e.setSelectionMode(SelectionMode.SELECTED));
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
