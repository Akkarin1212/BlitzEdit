package BlitzEdit.application;

	import java.net.URL;
	import java.util.ResourceBundle;
	import javafx.event.Event;
	import javafx.fxml.FXML;
	import javafx.scene.control.Button;
	import javafx.scene.control.TextArea;

	public class BlitzEdit implements javafx.fxml.Initializable {
		@FXML
		private Button Hello_World;
		
		@FXML
		private TextArea Hello_World_Text;

		@Override	
		public void initialize(URL location, ResourceBundle resources) {		
				
		}
		
		 @FXML
		 private void handleButtonAction(Event event) {
		     // Button was clicked, do something...
			 Hello_World_Text.setText("Hello World!");
		 }
		
	}