package BlitzEdit.application;
	
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root;
			try {
				root = FXMLLoader.load(Main.class.getResource("BlitzEdit.fxml"));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			primaryStage.setMinHeight(scene.getHeight());
			primaryStage.setMinWidth(scene.getWidth());
			
			System.out.println(scene.getHeight() + "-" + scene.getWidth());
			
			primaryStage.setTitle("BlitzEdit");
			primaryStage.getIcons().add(new Image( "file:img/Logo.png" ));
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
