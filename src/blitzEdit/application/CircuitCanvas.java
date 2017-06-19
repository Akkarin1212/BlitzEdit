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


/**
 * Canvas used for displaying the circuit with its components and connectors.
 * 
 * @author Chrisian Gärtner
 * @author David Schick
 */
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
	private boolean grid = true;
	
	private double canvasScaleFactor = 1;
	
	// used to save click begin
	private double clickX; 
	private double clickY;
	
	// used for calculation when moving multiple elements
	private double dragX; 
	private double dragY;


	/**
	 * Sets the mouse event handler and adds a new {@link Circuit}.
	 * 
	 * @param	scrollPane	Reference to the scroll panel the canvas is located
	 */
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

	/**
	 * Adds the event handler for the mouse pressed event. 
	 * Primary button click used selecting elements and connecting {@link Connector},
	 * secondary button used for right click menu and 
	 * middle mouse button used for moving the viewport in the scroll panel.
	 */
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
					else if (click.isShiftDown())
					{
						currentSelectedConnector = null;
						selectAdditionalElement(click.getX(), click.getY());
					}
					else if(!hasSelectedMultipleElements)
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
					dragX = clickX;
					dragY = clickY;
					System.err.println("middle mouse click");
				}
				
			}
		});
	}

	/**
	 * Adds the event handler for the mouse drag event. 
	 * Primary button click used for moving one or multiple elements,
	 * middle mouse button used for moving the viewport in the scroll panel.
	 */
	private void onMouseDraggedHandler()
	{
		this.setOnMouseDragged(new EventHandler<MouseEvent>()
		{
			//moved wird mit true initialisiert, damit dragX und Y beim ersten aufruf
			//gesetzt sind
			boolean moved = true;
			double prevScaleFactor = 1;
			@Override
			public void handle(MouseEvent click)
			{
				if (click.isPrimaryButtonDown())
				{
					// moves multiple elements at same time
					if (hasSelectedMultipleElements)
					{
						//wurde der drag handler gerade erst aufgerufen, wird
						//initialDrag = false;
						moved = translateElements(currentSelectedElements, click.getX() - dragX, click.getY() - dragY);
            
						//der mauspunkt als ursprungspunkt f�r die translation uebernommen
						if (moved)
						{
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
					// checkt, ob sich die zoomstufe ge�ndert hat, um zu verhindern
					// dass sich durch den ver�nderten coordinatenursprung eine falsche
					// differenz zwischen vorherigem und neuem wert bildet.
					if (prevScaleFactor == canvasScaleFactor)
					{
						double diffx = (click.getX() - dragX)/(ref.getWidth() * Math.pow(canvasScaleFactor, -1));
						double diffy = (click.getY() - dragY)/(ref.getHeight() * Math.pow(canvasScaleFactor, -1));
					
						sp.setHvalue(sp.getHvalue() - diffx);
						sp.setVvalue(sp.getVvalue() - diffy);
					}
					prevScaleFactor = canvasScaleFactor;
					dragX = click.getX();
					dragY = click.getY();
					
					changeCursorStyle(GraphicDesignContainer.move_cursor);
				}
			}
		});
	}
	
	/**
	 * Used for moving an element by an offset. Checks that coordinates aren't below zero to prevent the element from disappearing.
	 * 
	 * @param e			Element to move.
	 * @param dx		X-axis offset the element gets moved
	 * @param dy		Y-axis offset the element gets moved
	 * @return boolean	True if succesfully moved the element, false if the position would be out of boundaries 
	 */
	private boolean translateElement(Element e, double dx, double dy)
	{
		//prevent elements from moving out of canvas
		if(e.getX() + dx <= 0 || e.getY() + dy <= 0)
			return false;
		
		if (GlobalSettings.SNAP_TO_GRID)
		{
			int [] snapped = snapToGrid(dx, dy);
			if (snapped[0] == 0 && snapped[1] == 0)
				return false;
			e.move(e.getX() + snapped[0], e.getY() + snapped[1]);
		}
		else
		{
			e.move(e.getX() + dx, e.getY() + dy);
		}
		return true;
	}
	
	/**
	 * Used for moving multiple elements by an offset. Uses the translateElement method.
	 * 
	 * @param elements	Collection of element to move.
	 * @param x			X-axis offset the element gets moved
	 * @param y			Y-axis offset the element gets moved
	 * @return boolean	True if succesfully moved the element, false if the position would be out of boundaries 
	 */
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
	
	/**
	 * Used for moving an element to a given position.
	 * 
	 * @param e	Element to move.
	 * @param x			X position the element get moved to
	 * @param y			Y position the element get moved to
	 * @return boolean	True if succesfully moved the element, false if the position would be out of boundaries 
	 */
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
	
	/**
	 * Used for aligning a point to the grid.
	 * 
	 * @param x			X position needs to be aligned to the grid
	 * @param y			Y position needs to be aligned to the grid
	 * @return int[]	Contains the aligned grid position
	 */
	private int [] snapToGrid(double x, double y)
	{
		int [] ret = new int[2];
		int gridWidth = (int)GraphicDesignContainer.grid_spacing;
		ret[0] = (int)((int)(x / gridWidth) * gridWidth);
		ret[1] = (int)((int)(y / gridWidth) * gridWidth);
		return ret;
	}
	
	/**
	 * Adds the event handler for the drag and drop between library canvas and circuit canvas.
	 * Adds a component when mouse enters circuit canvas, the reference for component is saved
	 * as dragAndDropElement in BlitzEdit controller class. Moving the mouse in the canvas
	 * moves the new component.
	 */
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
					if (!circuit.containsElement(newComp))
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

	/**
	 * Adds the event handler for mouse release.
	 * Used for deselecting one or multiple components and connectors.
	 */
	private void onMouseReleasedHandler()
	{
		this.setOnMouseReleased(new EventHandler<MouseEvent>()
		{
			@Override
			public void handle(MouseEvent click)
			{
				if(click.isShiftDown())
				{
					return;
				}
				
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
	
	/**
	 * Adds the event handler for mouse release.
	 * Used for deselecting one or multiple components and connectors.
	 */
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
	
	/**
	 * Adds the event handler when mouse is moving in the canvas.
	 * Used for saving the current mouse position in the canvas.
	 */
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
  
	public void gridOnOff()
	{
		if(grid)
		{
			grid = false;
		}
		else
		{
			grid = true;
		}
	}

	/**
	 * Draws a grid of lines on the graphical context of this canvas.
	 * Use the constants in the {@link GraphicDesignContainer} to change the design of the grid.
	 * Called everytime the refreshCanvas() is executed.
	 */
	public void drawGrid()
	{
		if(grid) 
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
		else 
		{
			gc.save();
			gc.clearRect(0, 0, getWidth(), getHeight());
			gc.restore();
		}
		
	}

	/**
	 * Used for redrawing the content of the canvas when changes to elements of the circuit occured.
	 */
	public void refreshCanvas()
	{
		drawGrid();
		drawAllCircuitElements();

	}

	/**
	 * Draws all elements in the circuit of this canvas according to selection mode.
	 * Called everytime the refreshCanvas() is executed.
	 */
	private synchronized void drawAllCircuitElements()
	{
		ArrayList<Line> lines = circuit.getLines();
		for(Line line : lines)
		{
			line.draw(gc);
		}
		for(Line line : circuit.getSelectedLines())
		{
			line.draw(gc);
		}
		
		ArrayList<Element> array = circuit.getElements();
		for (Element elem : array)
		{
			elem.draw(gc, 1.0, elem.getSelectionMode());
		}
		
		// prevent overlapping from lines etc. and draw current selected connector last
		if(currentSelectedConnector != null)
		{
			currentSelectedConnector.draw(gc, 1.0, currentSelectedConnector.getSelectionMode());
		}
		
		highlightConnectors();
	}
	
	/**
	 * Highlights the connectors in this circuit when the user wants to connect two.
	 * Needs a selected connector to prevent highlighting conenctor 
	 * with the same owner or already connected connectors.
	 */
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
	
	/**
	 * Checks if a connector is located at x,y and if yes, connects it with the current selected connector.
	 * Used when a connector is selected and the mouse button is pressed in the canvas.
	 * 
	 * @param	x			X position to check after a connector
	 * @param	y			Y position to check after a connector
	 * @return	boolean		true if connection worked, false of no connector found at this location or coudn't connect 
	 */
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
	
	/**
	 * Checks if a connector is located at x,y and if yes, disconnects it with the current selected connector.
	 * Used when a connector is selected and the mouse button is pressed in the canvas.
	 * 
	 * @param	x			X position to check after a connector
	 * @param	y			Y position to check after a connector
	 * @return	boolean		true if disconnect worked, false of no connector found at this location or disconnect didn't work
	 */
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
	
	/**
	 * Copies the current selected elements of this canvas.
	 * 
	 * @return	Element[]	Array containing the selected elements
	 */
	public Element[] copySelected()
	{
		Element[] elem = currentSelectedElements.toArray(new Element[currentSelectedElements.size()]);
		return elem;
	}
	
	/**
	 * Deselect all elements in this canvas and clones all elements in the elem array 
	 * at the respective position around the mousePos.
	 * 
	 * @param	elem		Elements to paste
	 * @param 	mousePos	Position in canvas to paste the elements
	 */
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
				short rot = ((Component)e).getRotation();
				
				((Component)e).setRotation((short)0);
				Element clone = orginal.clone();
				
				((Component)clone).rotate(rot);
				((Component)e).rotate(rot);
				circuit.addElement(clone.move(currentMousePosition.getX() + offsetX, currentMousePosition.getY() + offsetY));
				selectElement(clone);
				refreshCanvas();
			}
		}
		refreshCanvas();
	}
	
	/**
	 * Deletes the current selected elements.
	 */
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
	
	/**
	 * Selects all components in this circuit.
	 */
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
	
	/**
	 * Deselects all components in this circuit.
	 */
	public void deselectAll()
	{
		deselectCurrentSelectedElements();
		refreshCanvas();
	}
	
	/**
	 * Rotates the selected components in the canvas.
	 * 
	 * @param 	rotation	Angle of rotation
	 */
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
	
	/**
	 * Zooms into the canvas and reduces the scale of the canvas.
   * Scale is limited to a max of 1.
	 */
	public void zoomIn()
	{
		if(canvasScaleFactor < 1)
		{
			zoom(GraphicDesignContainer.zoom_factor);
		}
		else
		{
			canvasScaleFactor = 1;
		}
	}

	/**
	 * Zooms out of the canvas and increases the scale of the canvas. 
   * Scale is limited to a min of 0,5.
	 */
	public void zoomOut()
	{
		if(canvasScaleFactor > 0.5)
		{
			zoom(-GraphicDesignContainer.zoom_factor);
			
		}
		else
		{
			canvasScaleFactor = 0.55;
		}
	}
	
  	/**
	 * Used for zooming in and out of the canvas. 
   *
   * @param   step  Scale factor that gets added (can be negative)
	 */
	private void zoom(double step)
	{
		canvasScaleFactor += step;
		
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
  
	/**
	 * Returns the current mouse position in this canvas or the last position before it left the node.
	 * 
	 * @return	Point2D		Current mouse position
	 */
	public Point2D getMousePosition()
	{
		return new Point2D(currentMousePosition.getX(), currentMousePosition.getY());
	}
	
	/**
	 * Used to change the cursor style in the application.
	 * 
	 * @param 	value		New cursor style
	 */
	private void changeCursorStyle(Cursor value)
	{
		Main.mainStage.getScene().setCursor(value);
	}
	
	/**
	 * Used for creating the right click menu with all its menu entries.
	 */
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
	
	/**
	 * Draws an rectangle with clickX and clickY as origin.
	 * 
	 * @param 	currX		X position in canvas, width of rect
	 * @param	currY		Y position in canvas, height of rect
	 */
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
	
	/**
	 * Sets selection mode of selected elements to unselected.
	 */
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
	
	/**
	 * Checks if there's a component at x,y position and if yes set it's selection mode to selected.
	 * Deselects all other elements first.
	 * 
	 * @param	x		X position in the canvas
	 * @param	y		Y position in the canvas
	 */
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
	
	/**
	 * Checks if there's a component at x,y position and if yes set it's selection mode to selected.
	 * Deselects elemenent if already added. Sets hasSelectedMultipleElements to true or false if afterwards
	 * multiple or only one element is selected.
	 * 
	 * @param	x		X position in the canvas
	 * @param	y		Y position in the canvas
	 */
	private void selectAdditionalElement(double x, double y)
	{
		ArrayList<Element> elements = circuit.getElementsByPosition(x, y);
		if(elements == null)
		{
			return;
		}
		Element elemToAdd = elements.get(0);
		
		if(!currentSelectedElements.contains(elemToAdd)) // avoid selection duplicates
		{
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
		else if(currentSelectedElements.contains(elemToAdd)) // deselect duplicate
		{
			currentSelectedElements.remove(elemToAdd.setSelectionMode(SelectionMode.UNSELECTED));
		}
		
		// update indicator for multiple element selection
		hasSelectedMultipleElements = currentSelectedElements.size()>1 ? true : false; 
	}
	
	/**
	 * Sets the selection mode of the element to selected.
	 * 
	 * @param	element		Element to select
	 */
	private void selectElement(Element element)
	{
		if(element != null)
		{
			currentSelectedElements.add(element.setSelectionMode(SelectionMode.SELECTED));
		}
	}
	
	/**
	 * Sets the selection mode of all the elements in the rectangle to selected.
	 * 
	 * @param	x		X position in the canvas
	 * @param	y		Y position in the canvas
	 * @param	sizeX	Width of the rectangle	
	 * @param	sizeY	Height of the rectangle
	 */
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
	
	/**
	 * Sets the selection mode of all the elements in the elements array to selected.
	 * 
	 * @param	elements	Elements that get selected
	 */
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
