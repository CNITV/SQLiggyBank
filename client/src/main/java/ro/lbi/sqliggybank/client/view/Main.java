package ro.lbi.sqliggybank.client.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ro.lbi.sqliggybank.client.util.Alert;
import ro.lbi.sqliggybank.client.view.login.LoginMenu;

import java.io.IOException;

/**
 * This class is the starting point for the program. The application uses JavaFX in order to build the GUI. It
 * implements the {@link ro.lbi.sqliggybank.client.view.WindowManager WindowManager} interface.
 *
 * @see javafx.application.Application
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-23-11 (v0.1)
 * @version 0.1
 *
 */
public class Main extends Application implements WindowManager {

    /**
     * This is the default logger for the program view. The framework used is log4j.
     *
     * @see org.apache.log4j.Logger
     */
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    /**
     * The preferred application window width.
     */
    private static int win_width = 1280;

    /**
     * The preferred application window height.
     */
    private static int win_height = 720;

    /**
     * The application name.
     */
    private static String app_name = "SQLiggyBank";

    /**
     * The primary stage for the application.
     */
    private Stage stage;

    /**
     * This is the starting point for the application. The {@link javafx.application.Application Application} class
     * first calls the init method {@link javafx.application.Application#init()} to initialize the program and then
     * calls this method (start) to actually run the program.
     * <p>
     * This method then calls {@link #loginMenu()}.
     * <p>
     * When the program finishes execution, the Application class calls the method
     * {@link javafx.application.Application#stop()} in order to close a connection.
     * <p>
     * The application window minimum width is set to {@link #win_width} and the minimum height is set
     * to {@link #win_height}.
     *
     * @see javafx.application.Application#start(Stage)
     *
     * @param primaryStage this is the primary stage used for the JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setScene(new Scene(new Group(), win_width, win_height));
        stage.setResizable(true);
        stage.setMinWidth(win_width);
        stage.setMinHeight(win_height);

        loginMenu();
    }

    /**
     * This method represents the login menu for the application. It implements the
     * {@link ro.lbi.sqliggybank.client.view.WindowManager#loginMenu() loginMenu} method.
     * <p>
     * It gives the user two options to choose from: either log in with an existing username/password combination or
     * create a new account.
     *
     * @see ro.lbi.sqliggybank.client.view.WindowManager#loginMenu()
     */
    @Override
    public void loginMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ro/lbi/sqliggybank/client/views/LoginMenu/LoginMenu.fxml"));
            loader.setControllerFactory(
                    c -> new LoginMenu(this, getHostServices())
            );
            Parent root = loader.load();

            stage.setTitle(app_name + " - Login");
            stage.getScene().setRoot(root);
            stage.show();
        } catch (IOException exception) {
            /*
            This happens whenever the FXML loader can't load for whatever reason the file specified.
             */
            LOGGER.log(Level.ERROR, "The FXML loader couldn't load the FXML file." , exception);
            Alert.showAlert("FXML error", "The FXML loader couldn't load the FXML file.");
            Platform.exit();
        } catch (IllegalStateException exception) {
            /*
            This happens whenever the FXML file isn't found at the specified path or the file name is wrong.
             */
            LOGGER.log(Level.ERROR, "The FXML loader couldn't find the file at the specified path.", exception);
            Alert.showAlert("FXML error", "The FXML loader couldn't find the file at the specified path.");
            Platform.exit();
        }
    }

    /**
     * Main function of the application.
     * <p>
     * It is not actually used for anything. This method is only here because some IDEs don't fully support
     * JavaFX. Running the program from the command-line should correctly call the method
     * {@link javafx.application.Application#launch(String...) launch}.
     * <p>
     * This method's only purpose is to call the {@link #start(Stage) start} method.
     *
     * @param args the command line arguments for the program.
     */
    public static void main(String[] args) {
        launch(args);
    }

}
