package BlitzEdit.application;

	import java.net.URL;
	import java.util.ResourceBundle;
	import javafx.event.Event;
	import javafx.fxml.FXML;
	import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

	public class BlitzEdit implements javafx.fxml.Initializable {
		@FXML 
		private Button Hello_World;
		@FXML
		private TextArea Hello_World_Text;
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
		private TitledPane Library1;
		@FXML 
		private TitledPane Library2;
		@FXML 
		private TitledPane Library3;
		@FXML
		private TitledPane Properties;
		@FXML
		private Label Debug_Text_Label;
		@FXML
		private TextField Debug_Text;
		
		@Override	
		public void initialize(URL location, ResourceBundle resources) {		
				
		}
		
		 @FXML
		 private void handleButtonAction(Event event) {
		     // Button was clicked, do something...
			 Hello_World_Text.setText("Hello World!");
		 }
		 
		 @FXML
		 private void handleNewAction(Event event) {
			 Debug_Text.setText("New");
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
		
	}