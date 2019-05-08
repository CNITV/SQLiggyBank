package ro.lbi.sqliggybank.client.view;

import javafx.application.Application;
import javafx.stage.Stage;

import org.apache.log4j.Logger;

import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManagerImpl;


/**
 * This class is the starting point for the program. The application uses JavaFX in order to build the GUI.
 *
 * @see javafx.application.Application
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-10-23
 *
 */
public class App extends Application {

    /**
     * This is the default logger for the program view. The framework used is log4j.
     */
    private static final Logger LOGGER = Logger.getLogger(App.class);

    /**
     * This is the implementation of the window manager.
     *
     * @see ro.lbi.sqliggybank.client.view.window_manager.WindowManagerImpl
     */
    private static WindowManagerImpl windowManager;

    /**
     * The preferred application window width.
     */
    public static int win_width = 1280;

    /**
     * The preferred application window height.
     */
    /*
    The added 33 is to compensate for the border.
     */
    public static int win_height = 720 + 33;

    /**
     * The application name.
     */
    public static String app_name = "SQLiggyBank";

    /**
     * The init method. It is called before the {@link #start(Stage) start} method to initialize any dependencies as
     * necessary.
     *
     * @see javafx.application.Application#init()
     */
    @Override
    public void init() {
        windowManager = new WindowManagerImpl();
    }

    /**
     * This is the starting point for the application. The {@link javafx.application.Application Application} class
     * first calls the init method {@link javafx.application.Application#init() init} to initialize the program and then
     * calls this method (start) to actually run the program.
     *
     * <p>
     * This method then calls {@link ro.lbi.sqliggybank.client.view.login.LoginView#getView(WindowManager) getView} to
     * load the login view.
     *
     * <p>
     * When the program finishes execution, the Application class calls the method
     * {@link javafx.application.Application#stop() stop} in order to close any connection with the server.
     *
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
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(win_width);
        primaryStage.setMinHeight(win_height);

        /*
        Set the host services property. Host services are needed to display a web page when a hyperlink is pressed.
         */
        primaryStage.getProperties().put("hostServices", getHostServices());

        /*
        Set the stage for the window manager.
         */
        windowManager.setStage(primaryStage);

        /*
        After the program is done building the dependencies, load the login menu.
         */
        windowManager.loginMenu();
    }

    /**
     * This method is called after the {@link #start(Stage) start} method to close any connection or delete any
     * dependency as necessary.
     *
     * @see javafx.application.Application#stop()
     */
    @Override
    public void stop() {

    }

    /**
     * Main function of the application.
     *
     * <p>
     * It is not actually used for anything. This method is only here because some IDEs don't fully support
     * JavaFX. Running the program from the command-line should correctly call the method
     * {@link javafx.application.Application#launch(String...) launch}.
     *
     * @param args the command line arguments for the program.
     */
    public static void main(String[] args) {
        launch(args);
    }

}
