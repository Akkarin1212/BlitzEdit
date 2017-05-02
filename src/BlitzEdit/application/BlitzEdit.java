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
			 
		 } 
		 
		 @FXML
		 private void handleSaveAsAction(Event event) {
			 
		 }

		 @FXML
		 private void handleImportLibraryAction(Event event) {
			 
		 }

		 @FXML
		 private void handleImportComponentAction(Event event) {
			 
		 }

		 @FXML
		 private void handleCloseAction(Event event) {
			 
		 }

		 @FXML
		 private void handleUndoAction(Event event) {
			 
		 }

		 @FXML
		 private void handleRedoAction(Event event) {
			 
		 }

		 @FXML
		 private void handleCopyAction(Event event) {
			 
		 }

		 @FXML
		 private void handlePasteAction(Event event) {
			 
		 }
		
		 @FXML
		 private void handleDuplicateAction(Event event) {
			 
		 }

		 @FXML
		 private void handleDeleteAction(Event event) {
			 
		 }

		 @FXML
		 private void handleSelectAllAction(Event event) {
			 
		 }

		 @FXML
		 private void handleSelectNoneAction(Event event) {
			 
		 }

		 @FXML
		 private void handleQuickSaveAction(Event event) {
			 
		 }
		 @FXML
		 private void handleViewZoomInAction(Event event) {
			 
		 }

		 @FXML
		 private void handleViewZoomOutAction(Event event) {
			 
		 }

		 @FXML
		 private void handleHelpAboutAction(Event event) {
			 
		 }
		
	}