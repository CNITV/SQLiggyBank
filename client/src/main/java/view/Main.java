package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import view.main_menu.MainMenu;

import java.io.IOException;

/**
 * This class is the starting point for the program. The application uses JavaFX in order to build the GUI. It
 * implements the {@link WindowManager WindowManager} interface.
 *
 * @see javafx.application.Application
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-23-11 (v0.1)
 * @version 0.1
 *
 */
public class Main extends Application implements WindowManager {

    private static int win_width = 800;
    private static int win_height = 600;

    /*
     *  TODO change the name of the application, this is just a placeholder
     */
    private static String app_name = "SQLiggyBank";

    private Stage stage;

    /**
     * This is the starting point for the application. The {@link javafx.application.Application Application} class
     * first calls the init method {@link Application#init()} to initialize the program and then calls this method
     * (start) to actually run the program.
     * <p>
     * This method then calls {@link #mainMenu()}.
     * <p>
     * When the program finishes execution, the Application class calls the  method {@link Application#stop()} in
     * order to close a connection.
     *
     * @see javafx.application.Application#start(Stage)
     *
     * @param primaryStage this is the primary stage used for the JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setScene(new Scene(new Group(), win_width, win_height));
        stage.setResizable(false);

        mainMenu();
    }

    /**
     * This method represents the main menu for the application. It implements the {@link WindowManager#mainMenu()
     * mainMenu} method.
     */
    @Override
    public void mainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainMenu.fxml"));
            loader.setControllerFactory(
                    c -> new MainMenu(this)
            );
            Parent root = loader.load();

            stage.setTitle(app_name + " - Main menu");
            stage.getScene().setRoot(root);
            stage.show();
        } catch (IOException exception) {
            /*
                log the exception
                exception.printStackTrace();
                Platform.exit();
            */
        }
    }

    /**
     * Main function that the JVM calls. It is not actually used for anything. It's main purpose is just calling
     * the {@link #start(Stage) start} method.
     *
     * @param args the command line arguments for the program.
     */
    public static void main(String[] args) {
        launch(args);
    }

}
