package ro.lbi.sqliggybank.client.view.window_manager;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ro.lbi.sqliggybank.client.backend.user.User;
import ro.lbi.sqliggybank.client.util.Alert;
import ro.lbi.sqliggybank.client.view.dashboard.DashboardView;
import ro.lbi.sqliggybank.client.view.login.LoginView;
import ro.lbi.sqliggybank.client.view.register.RegisterView;

import java.io.IOException;

import static ro.lbi.sqliggybank.client.view.App.app_name;
import static ro.lbi.sqliggybank.client.view.App.win_height;
import static ro.lbi.sqliggybank.client.view.App.win_width;

/**
 * This is the implementation of the {@link ro.lbi.sqliggybank.client.view.window_manager.WindowManager WindowManager}
 * interface.
 *
 * @see ro.lbi.sqliggybank.client.view.window_manager.WindowManager
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-23-11 (v0.1)
 * @version 0.1
 */
public class WindowManagerImpl implements WindowManager {

    /**
     * This is the default logger for the program view. The framework used is log4j.
     */
    private static final Logger LOGGER = Logger.getLogger(WindowManagerImpl.class);

    /**
     * This is the stage of the JavaFX Application. It is injected as a dependency into the class.
     *
     * @see #setStage(Stage)
     */
    private Stage stage;

    /**
     * This is the dependency injection of the stage.
     *
     * @see #stage
     *
     * @param stage the stage of the application.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * The overwritten login menu method.
     *
     * It loads the login menu view into the application.
     *
     * @see ro.lbi.sqliggybank.client.view.window_manager.WindowManager#loginMenu()
     * @see ro.lbi.sqliggybank.client.view.login.LoginView
     */
    @Override
    public void loginMenu() {
		try {
			LoginView loginView = new LoginView();
			Scene scene = new Scene(loginView.getView(this), win_width, win_height);

			stage.setTitle(app_name + " - Login");

			stage.setScene(scene);

			stage.show();
		} catch (IOException exception) {
            /*
            This happens whenever the FXML loader can't load the specified file for whatever reason.
             */
			LOGGER.log(Level.ERROR, "The FXML loader couldn't load the FXML file." , exception);
			Alert.errorAlert("FXML error", "The FXML loader couldn't load the FXML file.");
			Platform.exit();
		} catch (IllegalStateException exception) {
            /*
            This happens whenever the FXML file isn't found at the specified path or the file name is wrong.
             */
			LOGGER.log(Level.ERROR, "The FXML loader couldn't find the file at the specified path.", exception);
			Alert.errorAlert("FXML error", "The FXML loader couldn't find the file at the specified path.");
			Platform.exit();
		}
    }

	/**
	 * The overwritten register menu method.
	 *
	 * It loads the register menu view into the application.
	 *
	 * @see ro.lbi.sqliggybank.client.view.window_manager.WindowManager#registerMenu()
	 * @see ro.lbi.sqliggybank.client.view.register.RegisterView
	 */
	@Override
	public void registerMenu() {
		try {
			RegisterView registerView = new RegisterView();
			Scene scene = new Scene(registerView.getView(this), win_width, win_height);

			stage.setTitle(app_name + " - Register");

			stage.setScene(scene);

			stage.show();
		} catch (IOException exception) {
            /*
            This happens whenever the FXML loader can't load the specified file for whatever reason.
             */
			LOGGER.log(Level.ERROR, "The FXML loader couldn't load the FXML file." , exception);
			Alert.errorAlert("FXML error", "The FXML loader couldn't load the FXML file.");
			Platform.exit();
		} catch (IllegalStateException exception) {
            /*
            This happens whenever the FXML file isn't found at the specified path or the file name is wrong.
             */
			LOGGER.log(Level.ERROR, "The FXML loader couldn't find the file at the specified path.", exception);
			Alert.errorAlert("FXML error", "The FXML loader couldn't find the file at the specified path.");
			Platform.exit();
		}
	}

    /**
     * The overwritten dashboard menu method.
     *
     * It loads the dashboard menu view into the application.
     *
     * @param user the user currently logged in.
     *
     * @see ro.lbi.sqliggybank.client.view.window_manager.WindowManager#dashboardMenu(User)
     * @see ro.lbi.sqliggybank.client.view.dashboard.DashboardView
     * @see ro.lbi.sqliggybank.client.backend.user.User
     */
    @Override
    public void dashboardMenu(User user) {
        try {
            DashboardView dashboardView = new DashboardView();
            Scene scene = new Scene(dashboardView.getView(this, user), win_width, win_height);

            stage.setTitle(app_name + " - Dashboard");

            stage.setScene(scene);

            stage.show();
		} catch (IOException exception) {
            /*
            This happens whenever the FXML loader can't load the specified file for whatever reason.
             */
			LOGGER.log(Level.ERROR, "The FXML loader couldn't load the FXML file." , exception);
			Alert.errorAlert("FXML error", "The FXML loader couldn't load the FXML file.");
			Platform.exit();
		} catch (IllegalStateException exception) {
            /*
            This happens whenever the FXML file isn't found at the specified path or the file name is wrong.
             */
			LOGGER.log(Level.ERROR, "The FXML loader couldn't find the file at the specified path.", exception);
			Alert.errorAlert("FXML error", "The FXML loader couldn't find the file at the specified path.");
			Platform.exit();
		}
    }

}
