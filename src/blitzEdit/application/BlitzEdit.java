package blitzEdit.application;

	import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
	import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
	import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

	public class BlitzEdit implements javafx.fxml.Initializable {
		
		LibraryCanvas currentLibraryCanvas;
		CircuitCanvas currentCircuitCanvas;
		
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
		public void initialize(URL location, ResourceBundle resources) {		
			addTab();
			addLibrary();
		}
		 
		 @FXML
		 private void handleNewAction(Event event) {
			 Debug_Text.setText("New");
			 
			 addTab();
		 }
		 
		 @FXML
		 private void handleOpenAction(Event event) {
			 Debug_Text.setText("Open");
		 }
		 
		 @FXML
		 private void handleSaveAction(Event event) {
			 Debug_Text.setText("Save");
		 } 
		 
		 @FXML
		 private void handleSaveAsAction(Event event) {
			 Debug_Text.setText("Save As...");
		 }

		 @FXML
		 private void handleImportLibraryAction(Event event) {
			 Debug_Text.setText("Import Library");
			 
			 addLibrary();
			 currentLibraryCanvas.drawLibraryEntries();
		 }

		 @FXML
		 private void handleImportComponentAction(Event event) {
			 Debug_Text.setText("Import Component");
		 }

		 @FXML
		 private void handleCloseAction(Event event) {
			 Debug_Text.setText("Close");
		 }

		 @FXML
		 private void handleUndoAction(Event event) {
			 Debug_Text.setText("Undo");
		 }

		 @FXML
		 private void handleRedoAction(Event event) {
			 Debug_Text.setText("Redo");
		 }

		 @FXML
		 private void handleCopyAction(Event event) {
			 Debug_Text.setText("Copy");
		 }

		 @FXML
		 private void handlePasteAction(Event event) {
			 Debug_Text.setText("Paste");
		 }
		
		 @FXML
		 private void handleDuplicateAction(Event event) {
			 Debug_Text.setText("Duplicate");
		 }

		 @FXML
		 private void handleDeleteAction(Event event) {
			 Debug_Text.setText("Delete");
		 }

		 @FXML
		 private void handleSelectAllAction(Event event) {
			 Debug_Text.setText("Select All");
		 }

		 @FXML
		 private void handleSelectNoneAction(Event event) {
			 Debug_Text.setText("Select None");
		 }

		 @FXML
		 private void handleQuickSaveAction(Event event) {
			 Debug_Text.setText("Save");
		 }
		 @FXML
		 private void handleViewZoomInAction(Event event) {
			 Debug_Text.setText("Zoom In");
		 }

		 @FXML
		 private void handleViewZoomOutAction(Event event) {
			 Debug_Text.setText("Zoom Out");
		 }

		 @FXML
		 private void handleHelpAboutAction(Event event) {
			 Debug_Text.setText("About");
		 }
		 
		 private void addTab()
		 {
			// check if the TabPanel exists before creating tabs
			 if(CircuitsTabPane != null)
			 {	
				 Tab tab = new Tab("New Tab");
				 AnchorPane root = new AnchorPane();
				 CircuitCanvas canvas = new CircuitCanvas();
				 
				 AnchorPane.setTopAnchor(canvas, 0.0);
				 AnchorPane.setBottomAnchor(canvas, 0.0);
				 AnchorPane.setLeftAnchor(canvas, 0.0);
				 AnchorPane.setRightAnchor(canvas, 0.0);
				 
				 tab.setContent(root);
				 tab.setContent(canvas);
				 CircuitsTabPane.getTabs().add(tab);
				 CircuitsTabPane.getSelectionModel().select(tab);
				 currentCircuitCanvas = canvas;
			 }
		 }
		 
		 private void addLibrary()
		 {
			// check if the TabPanel exists before creating tabs
			 if(LibrariesAccordion != null)
			 {	
				 AnchorPane root = new AnchorPane();
				 LibraryCanvas canvas = new LibraryCanvas();
				 TitledPane library = new TitledPane("New Library", null);
				 
				 AnchorPane.setTopAnchor(canvas, 0.0);
				 AnchorPane.setBottomAnchor(canvas, 0.0);
				 AnchorPane.setLeftAnchor(canvas, 0.0);
				 AnchorPane.setRightAnchor(canvas, 0.0);
				 
				 library.setContent(root);
				 library.setContent(canvas);
				 LibrariesAccordion.getPanes().add(library);
				 LibrariesAccordion.setExpandedPane(library);
				 
				 canvas.drawLibraryEntries();
				 currentLibraryCanvas = canvas;
			 }
		 }
	}