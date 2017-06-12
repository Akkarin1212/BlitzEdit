package blitzEdit.application;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import blitzEdit.core.Element;
import blitzEdit.storage.XMLParser;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * Controller class for BlitzEdit JavaFX application.
 * 
 * @author Nico Pfaff
 * @author Chrisian Gärtner
 */
public class BlitzEdit implements javafx.fxml.Initializable
{
	public static Element[] elementsToCopy;
	public static Point2D copyMousePosition;
	public static Element dragAndDropElement;
	private static ArrayList<LibraryCanvas> libraries = new ArrayList<LibraryCanvas>();
	
	@FXML
	private MenuItem New;
	@FXML
	private MenuItem Reload;
	@FXML
	private MenuItem Open;
	@FXML
	private MenuItem Save;
	@FXML
	private MenuItem SaveAs;
	@FXML
	private MenuItem Import_Library;
	@FXML
	private MenuItem Import_Component;
	@FXML
	private MenuItem Close;
	@FXML
	private MenuItem Undo;
	@FXML
	private MenuItem Redo;
	@FXML
	private MenuItem Copy;
	@FXML
	private MenuItem Paste;
	@FXML
	private MenuItem Duplicate;
	@FXML
	private MenuItem Delete;
	@FXML
	private MenuItem SelectAll;
	@FXML
	private MenuItem SelectNone;
	@FXML
	private MenuItem QuickSave;
	@FXML
	private MenuItem ZoomIn;
	@FXML
	private MenuItem ZoomOut;
	@FXML
	private MenuItem About;

	@FXML
	private TabPane CircuitsTabPane;
	@FXML
	private Accordion LibrariesAccordion;

	@FXML
	private TitledPane Library1;
	@FXML
	private TitledPane Library2;
	@FXML
	private TitledPane Library3;
	@FXML
	private TitledPane Properties;
	@FXML
	private Label Debug_Text;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		addTab("New Circuit");
		addLibrary("Template Library");
		getCurrentLibraryCanvas().addLibraryEntries(new File("blueprints/"));
	}
	
	/**
	 * Uses the TabPane class to check which circuit tab is currently selected.
	 * 
	 * @return the selected tab, or null if there is none
	 */
	public Tab getCurrentTab()
	{
		return CircuitsTabPane.getSelectionModel().getSelectedItem();
	}
	
	/**
	 * Uses the Accordion class to check which library is currently selected.
	 * 
	 * @return the selected library pane, or null if there is none
	 */
	public TitledPane getCurrentLibraryTitledPane()
	{
		return LibrariesAccordion.getExpandedPane();
	}
	
	/**
	 * Uses the TabPane to check which circuit canvas is currently active.
	 * 
	 * @return the selected canvas, or null if there is none
	 */
	public CircuitCanvas getCurrentCircuitCanvas()
	{
		ScrollPane sp = (ScrollPane) getCurrentTab().getContent();
		return (CircuitCanvas) sp.getContent();
	}
	
	/**
	 * Uses the Accordion to check which library canvas is currently active.
	 * 
	 * @return the selected canvas, or null if there is none
	 */
	public LibraryCanvas getCurrentLibraryCanvas()
	{
		ScrollPane sp = (ScrollPane) getCurrentLibraryTitledPane().getContent();
		return (LibraryCanvas) sp.getContent();
	}
	
	@FXML
	private void handleNewAction(Event event)
	{
		Debug_Text.setText("New");

		addTab("New Circuit");
	}
	
	@FXML
	private void handleReloadAction(Event event)
	{
		Debug_Text.setText("Reload");

		File filepath = getCurrentCircuitCanvas().currentSaveDirection;
		if (filepath != null)
		{
			XMLParser parser = new XMLParser();
			parser.loadCircuit(getCurrentCircuitCanvas().circuit, filepath.getPath());
			getCurrentCircuitCanvas().refreshCanvas();
		}
		else
		{
			Debug_Text.setText("Need a valid document to reload.");
		}
	}

	@FXML
	private void handleOpenAction(Event event)
	{
		Debug_Text.setText("Open");

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open circuit diagram");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
		File filepath = fileChooser.showOpenDialog(Main.mainStage);
		
		if (filepath != null)
		{
			CircuitCanvas newCanvas = addTab(filepath.getName().replace(".xml", ""));
			XMLParser parser = new XMLParser();
			parser.loadCircuit(newCanvas.circuit, filepath.getPath());
			newCanvas.refreshCanvas();
			newCanvas.currentSaveDirection = filepath;
		}
	}

	@FXML
	private void handleSaveAction(Event event)
	{
		Debug_Text.setText("Save");
		
		File destination = getCurrentCircuitCanvas().currentSaveDirection;
		if(destination ==  null)
		{
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save circuit diagram");
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
			destination = fileChooser.showSaveDialog(Main.mainStage);
		}
		
		if (destination != null)
		{
			XMLParser parser = new XMLParser();
			parser.saveCircuit(getCurrentCircuitCanvas().circuit, destination.getPath(), true); // TODO: option to choose usage of hashes
			getCurrentCircuitCanvas().currentSaveDirection = destination;
			
			String filename = destination.getName().replace(".xml", "");
			getCurrentTab().setText(filename);
			
			Debug_Text.setText("Circuit saved under" + destination);
		}
		else
		{
			Debug_Text.setText("Failed to save Circuit under " + destination);
		}
	}

	@FXML
	private void handleSaveAsAction(Event event)
	{
		Debug_Text.setText("Save As...");
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save circuit diagram");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
		File destination = fileChooser.showSaveDialog(Main.mainStage);

		if (destination != null)
		{
			XMLParser parser = new XMLParser();
			parser.saveCircuit(getCurrentCircuitCanvas().circuit, destination.getPath(), true); // TODO: option to choose usage of hashes
			getCurrentCircuitCanvas().currentSaveDirection = destination;
			
			String filename = destination.getName().replace(".xml", "");
			getCurrentTab().setText(filename);
			
			JOptionPane.showConfirmDialog(null,
					"Circuit saved under" + destination, "Save as",
					JOptionPane.CLOSED_OPTION);
		}
		else
		{
			Debug_Text.setText("Failed to save Circuit under " + destination);
		}
	}

	@FXML
	private void handleImportLibraryAction(Event event)
	{
		Debug_Text.setText("Import Library");

		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(Main.mainStage);

		if (selectedDirectory != null)
		{
			if(checkForDuplicateLibrary(selectedDirectory))
			{
				Debug_Text.setText("Duplicate library directory selected");
				return;
			}
			
			LibraryCanvas newCanvas = addLibrary(selectedDirectory.getName());
			if(!newCanvas.addLibraryEntries(selectedDirectory)) //if no files got added
			{
				removeCurrentLibraryTitlesPane();
				Debug_Text.setText("Directory contains no valid xml files");
			}
			else
			{
				newCanvas.drawLibraryEntries();
			}
			
		}
		else
		{
			Debug_Text.setText("Invalid directory location: None selected");
		}
	}

	@FXML
	private void handleImportComponentAction(Event event)
	{
		Debug_Text.setText("Import Component");
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Add xml Component");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
		File selectedFile = fileChooser.showOpenDialog(Main.mainStage);

		if (selectedFile != null)
		{
			if(!getCurrentLibraryCanvas().addLibraryEntry(selectedFile)) //if no files got added
			{
				Debug_Text.setText("File already added or not a valid xml document");
			}
			else
			{
				getCurrentLibraryCanvas().drawLibraryEntries();
			}
		}
		else
		{
			Debug_Text.setText("Invalid file location: None selected");
		}
	}

	@FXML
	private void handleCloseAction(Event event)
	{
		Debug_Text.setText("Close");
	}

	@FXML
	private void handleUndoAction(Event event)
	{
		Debug_Text.setText("Undo");
	}

	@FXML
	private void handleRedoAction(Event event)
	{
		Debug_Text.setText("Redo");
	}

	@FXML
	private void handleCopyAction(Event event)
	{
		Debug_Text.setText("Copy");
		
		elementsToCopy = getCurrentCircuitCanvas().copySelected();
		copyMousePosition = getCurrentCircuitCanvas().getMousePosition();
	}

	@FXML
	private void handlePasteAction(Event event)
	{
		Debug_Text.setText("Paste");
		if(elementsToCopy != null && copyMousePosition != null)
		{
			getCurrentCircuitCanvas().pasteSelected(elementsToCopy, copyMousePosition);
		}
	}

	@FXML
	private void handleDuplicateAction(Event event)
	{
		Debug_Text.setText("Duplicate");
	}

	@FXML
	private void handleDeleteAction(Event event)
	{
		Debug_Text.setText("Delete");
		
		getCurrentCircuitCanvas().deleteSelected();
	}

	@FXML
	private void handleSelectAllAction(Event event)
	{
		Debug_Text.setText("Select All");
		
		getCurrentCircuitCanvas().selectAll();
	}

	@FXML
	private void handleSelectNoneAction(Event event)
	{
		Debug_Text.setText("Select None");
		
		getCurrentCircuitCanvas().deselectAll();
	}

//	@FXML
//	private void handleQuickSaveAction(Event event)
//	{
//		Debug_Text.setText("Quicksave");
//	}

	@FXML
	private void handleViewZoomInAction(Event event)
	{
		Debug_Text.setText("Zoom In");
		
		getCurrentCircuitCanvas().zoomIn();
	}

	@FXML
	private void handleViewZoomOutAction(Event event)
	{
		Debug_Text.setText("Zoom Out");
		
		getCurrentCircuitCanvas().zoomOut();
	}

	@FXML
	private void handleHelpAboutAction(Event event)
	{
		Debug_Text.setText("About");
	}

	/**
	 * Adds a new tab to the tabpane that contains a {@link CircuitCanvas} and selects the new one.
	 * 
	 * @param	name			Name for the tab
	 * @return	CircuitCanvas	Contains the created canvas
	 */
	private CircuitCanvas addTab(String name)
	{
		// check if the TabPanel exists before creating tabs
		if (CircuitsTabPane != null)
		{
			Tab tab = new Tab(name);
			AnchorPane root = new AnchorPane();
			ScrollPane sp = new ScrollPane();
			
			CircuitCanvas canvas = new CircuitCanvas(sp);

			setAnchorForNode(canvas, 0.0);
			setAnchorForNode(sp, 0.0);

			sp.setContent(canvas);

			tab.setContent(root);
			tab.setContent(sp);

			CircuitsTabPane.getTabs().add(tab);
			CircuitsTabPane.getSelectionModel().select(tab);
			
			return canvas;
		}
		return null;
	}

	/**
	 * Adds a new tab to the accordion that contains a {@link LibraryCanvas} and selects the new one.
	 * 
	 * @param	name			Name for the library tab
	 * @return	LibraryCanvas	Contains the created canvas
	 */
	private LibraryCanvas addLibrary(String name)
	{
		// check if the TabPanel exists before creating tabs
		if (LibrariesAccordion != null)
		{
			AnchorPane root = new AnchorPane();
			LibraryCanvas canvas = new LibraryCanvas();
			ScrollPane sp = new ScrollPane();
			TitledPane library = new TitledPane(name, null);

			library.setOnMouseClicked(event -> {
	            if (MouseButton.SECONDARY.equals(event.getButton()) && event.isControlDown()) {
	                LibrariesAccordion.getPanes().remove(library);
	            }
	        });
			
			setAnchorForNode(canvas, 0.0);
			setAnchorForNode(sp, 0.0);

			sp.setContent(canvas);
			sp.setHbarPolicy(ScrollBarPolicy.NEVER);
			sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);

			library.setContent(root);
			library.setContent(sp);
			System.err.println(LibrariesAccordion.getMaxHeight() + " " + LibrariesAccordion.getMaxWidth());
			LibrariesAccordion.getPanes().add(library);
			LibrariesAccordion.setExpandedPane(library);

			libraries.add(canvas);
			canvas.drawLibraryEntries();
			
			return canvas;
		}
		return null;
	}

	/**
	 * Sets the anchor top, bottom, left and right positons for the javafx node.
	 * 
	 * @param	node	Node anchor changes apply to
	 * @param	value	Anchor positions
	 */
	private void setAnchorForNode(Node node, double value)
	{
		AnchorPane.setTopAnchor(node, value);
		AnchorPane.setBottomAnchor(node, value);
		AnchorPane.setLeftAnchor(node, value);
		AnchorPane.setRightAnchor(node, value);
	}
	
	/**
	 * Removes the current selected TitledPane from the accordion and the library from libraries array.
	 * 
	 * @return boolean	True if succesfully removed
	 */
	private boolean removeCurrentLibraryTitlesPane()
	{
		TitledPane tp = getCurrentLibraryTitledPane();
		LibraryCanvas canvas = getCurrentLibraryCanvas();
		if(LibrariesAccordion.getPanes().remove(tp))
		{
			libraries.remove(canvas);
			return true;
		}
		return false;
	}
	
	/**
	 * Checks the LibraryCanvas directories of libraries for any duplication with the destination.
	 * 
	 * @param 	destination		Directory to check
	 * @return	boolean			True if duplicate was found
	 */
	private boolean checkForDuplicateLibrary(File destination)
	{
		if(libraries.isEmpty())
		{
			return false;
		}
		else
		{
			for(LibraryCanvas canvas : libraries)
			{
				if(canvas.directory.toString().equalsIgnoreCase(destination.toString()))
				{
					return true;
				}
			}
			return false;
		}
	}
}