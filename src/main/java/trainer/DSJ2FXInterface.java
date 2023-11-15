package trainer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import trainer.core.DSJ2FXInterfaceHelper;

import java.io.IOException;
import java.util.logging.Level;

public class DSJ2FXInterface extends Application {

    public static Stage owner;

    public static void main(String[] args) {
        //possible logger values: INFO, FINE, SEVERE, WARNING
        if (args.length == 1) {
            setLoggersLevel(args[0]);
        }
        launch();
    }

    private static void setLoggersLevel(String arg) {
        try {
            String levelArg = arg.trim().toUpperCase();
            if (levelArg.contentEquals("INFO")) {
                DSJ2FXInterfaceHelper.logLevel = Level.INFO;
            }
            if (levelArg.contentEquals("FINE")) {
                DSJ2FXInterfaceHelper.logLevel = Level.FINE;
            }
            if (levelArg.contentEquals("SEVERE")) {
                DSJ2FXInterfaceHelper.logLevel = Level.SEVERE;
            }
            if (levelArg.contentEquals("WARNING")) {
                DSJ2FXInterfaceHelper.logLevel = Level.WARNING;
            }
        } catch (RuntimeException exp) {
            System.out.println("You tried to set weird LOGGER level, available values are: " +
                    "INFO, FINE, SEVERE, WARNING");
        }
    }

    private static Parent loadFxml() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DSJ2FXInterface.class.getResource("primary" + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene scene = new Scene(loadFxml());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("DSJ2 Trainer by Mtik333");
        owner = primaryStage;
        primaryStage.show();
    }

}
