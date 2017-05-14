package blitzEdit.application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import blitzEdit.core.Element;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class BlitzEdit implements javafx.fxml.Initializable
{
	public static Element[] elementsToCopy;
	
	@FXML
	private MenuItem New;
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
		addTab();
		addLibrary("New Library");
	}
	
	public Tab getCurrentTab()
	{
		return CircuitsTabPane.getSelectionModel().getSelectedItem();
	}
	
	public TitledPane getCurrentLibraryTitledPane()
	{
		return LibrariesAccordion.getExpandedPane();
	}
	
	public CircuitCanvas getCurrentCircuitCanvas()
	{
		ScrollPane sp = (ScrollPane) getCurrentTab().getContent();
		return (CircuitCanvas) sp.getContent();
	}
	
	public LibraryCanvas getCurrentLibraryCanvas()
	{
		ScrollPane sp = (ScrollPane) getCurrentLibraryTitledPane().getContent();
		return (LibraryCanvas) sp.getContent();
	}

	@FXML
	private void handleNewAction(Event event)
	{
		Debug_Text.setText("New");

		addTab();
	}

	@FXML
	private void handleOpenAction(Event event)
	{
		Debug_Text.setText("Open");

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open circuit diagram");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
		fileChooser.showOpenDialog(Main.mainStage);

	}

	@FXML
	private void handleSaveAction(Event event)
	{
		Debug_Text.setText("Save");
	}

	@FXML
	private void handleSaveAsAction(Event event)
	{
		Debug_Text.setText("Save As...");
	}

	@FXML
	private void handleImportLibraryAction(Event event)
	{
		Debug_Text.setText("Import Library");

		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(Main.mainStage);

		if (selectedDirectory != null)
		{
			addLibrary(selectedDirectory.getName());
			getCurrentLibraryCanvas().drawLibraryEntries();
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
	}

	@FXML
	private void handlePasteAction(Event event)
	{
		Debug_Text.setText("Paste");
		if(elementsToCopy != null)
		{
			getCurrentCircuitCanvas().pasteSelected(elementsToCopy);
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

	@FXML
	private void handleQuickSaveAction(Event event)
	{
		Debug_Text.setText("Save");
	}

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

	private void addTab()
	{
		// check if the TabPanel exists before creating tabs
		if (CircuitsTabPane != null)
		{
			Tab tab = new Tab("New Tab");
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
		}
	}

	private void addLibrary(String name)
	{
		// check if the TabPanel exists before creating tabs
		if (LibrariesAccordion != null)
		{
			AnchorPane root = new AnchorPane();
			LibraryCanvas canvas = new LibraryCanvas();
			ScrollPane sp = new ScrollPane();
			TitledPane library = new TitledPane(name, null);

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

			canvas.drawLibraryEntries();
		}
	}

	private void setAnchorForNode(Node n, double value)
	{
		AnchorPane.setTopAnchor(n, value);
		AnchorPane.setBottomAnchor(n, value);
		AnchorPane.setLeftAnchor(n, value);
		AnchorPane.setRightAnchor(n, value);
	}
}