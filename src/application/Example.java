package application;

	import java.net.URL;
	import java.util.ResourceBundle;
	import javafx.event.ActionEvent;
	import javafx.event.Event;
	import javafx.event.EventHandler;
	import javafx.fxml.FXML;
	import javafx.scene.control.Button;

	public class Example implements javafx.fxml.Initializable {
		@FXML
		private Button Hello_World;

		@Override	
		public void initialize(URL location, ResourceBundle resources) {		
				
		}
		
		 @FXML
		 private void handleButtonAction(Event event) {
		     // Button was clicked, do something...
			 System.out.println("Hello World!");
		 }
		
	}