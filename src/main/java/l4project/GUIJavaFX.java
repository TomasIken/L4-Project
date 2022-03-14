package l4project;

import javafx.application.Application;
import javafx.stage.Stage;

public class GUIJavaFX extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Statement Graph");

        primaryStage.show();
	}
	public GUIJavaFX(String[] args) {
		Application.launch(args);
	}
}
