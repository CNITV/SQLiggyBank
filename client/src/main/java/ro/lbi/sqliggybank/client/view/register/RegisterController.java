package ro.lbi.sqliggybank.client.view.register;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ro.lbi.sqliggybank.client.backend.database.DatabaseHandler;
import ro.lbi.sqliggybank.client.backend.exceptions.ForbiddenException;
import ro.lbi.sqliggybank.client.util.Alert;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static ro.lbi.sqliggybank.client.view.App.win_height;
import static ro.lbi.sqliggybank.client.view.App.win_width;

/**
 * This class implements the register controller. It prompts the user to create a new account.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-11-15
 */
public class RegisterController {

	/**
	 * This is the default logger for the program view. The framework used is log4j.
	 */
	private static final Logger LOGGER = Logger.getLogger(RegisterController.class);

	/**
	 * This is the window manager. This way the controller can switch to other scenes.
	 *
	 * @see ro.lbi.sqliggybank.client.view.window_manager.WindowManager
	 */
	private WindowManager windowManager;

	/**
	 * This is the database handler. It handles API calls to the server.
	 *
	 * @see ro.lbi.sqliggybank.client.backend.database.DatabaseHandler
	 */
	private DatabaseHandler databaseHandler;

	/**
	 * The progress indicator. It tells the user to wait until the connection with the server is
	 * established.
	 */
	@FXML
	private ProgressIndicator progressIndicator;

	/**
	 * The username text field.
	 */
	@FXML
	private TextField usernameTextField;

	/**
	 * The password text field.
	 */
	@FXML
	private PasswordField passwordField;

	/**
	 * The first name text field.
	 */
	@FXML
	private TextField firstNameTextField;

	/**
	 * The last name text field.
	 */
	@FXML
	private TextField lastNameTextField;

	/**
	 * The e-mail text field.
	 */
	@FXML
	private TextField emailTextField;

	/**
	 * Redirects the user back to the login screen.
	 */
	@FXML
	private Button loginMenuButton;

	/**
	 * The sign-up button.
	 */
	@FXML
	private Button signUpButton;

	/**
	 * The default constructor for the controller.
	 */
	RegisterController(WindowManager windowManager) {
		this.windowManager = windowManager;

		databaseHandler = new DatabaseHandler();
	}

	/**
	 * This method creates a task for the register. It creates a new Thread in the background with the
	 * task specified and starts it while the application is running.
	 * <p>
	 * It displays a loading progress indicator while the background process takes the username and the
	 * password and checks for them in the database. If they already exist, an error prompt tells the user
	 * something went wrong.
	 *
	 * @return the task created.
	 */
	private Task<Boolean> createWorker() {
		return new Task<Boolean>() {
			@Override
			protected Boolean call() {
				try {
					String result;

					if (usernameTextField.getText().equals("") || passwordField.getText().equals("")) {
						throw new IllegalStateException("Username and password cannot be empty!");
					}

					result = databaseHandler.newUser(
							usernameTextField.getText(),
							passwordField.getText(),
							firstNameTextField.getText(),
							lastNameTextField.getText(),
							emailTextField.getText()
					);

					/*
					If everything went fine, announce the user and redirect him to the login screen.
					 */
					if (result != null) {
						Platform.runLater(() ->
								{
									Alert.infoAlert("User registered", "User registration complete! You successfully " +
											"signed up!");
									windowManager.loginMenu();
								}
						);

						return true;
					}
				} catch (IOException e) {
					setButtonsEnabled(true);
					showAlert("Failed to connect to server", "Failed to connect to the database!" +
							" This might be due to the server not currently working! Please try again in a few moments!");
					LOGGER.log(Level.ERROR, "Server connection error", e);
				} catch (IllegalStateException | ForbiddenException e) {
					setButtonsEnabled(true);
					showAlert("Error", e.getMessage());
					LOGGER.log(Level.ERROR, e.getMessage(), e);
				}
				return false;
			}
		};
	}

	/**
	 * This method enables or disables all the buttons in the scene.
	 */
	private void setButtonsEnabled(boolean value) {
		progressIndicator.setVisible(!value);
		progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		loginMenuButton.setDisable(!value);
		signUpButton.setDisable(!value);
		usernameTextField.setEditable(value);
		passwordField.setEditable(value);
		firstNameTextField.setEditable(value);
		lastNameTextField.setEditable(value);
		emailTextField.setEditable(value);
	}

	/**
	 * This method displays an alert pop-up in another thread.
	 *
	 * @param title the title of the error alert.
	 * @param message the message of the error alert.
	 */
	private void showAlert(String title, String message) {
		Platform.runLater(() ->
				Alert.errorAlert(title, message)
		);
	}

	/**
	 * This method is the default initialize method for an FXML controller class.
	 * <p>
	 * It is called <u>right after</u> the constructor finished execution and the @FXML annotated fields
	 * are populated.
	 * <p>
	 * This method then initializes any attributes needed in the GUI.
	 */
	@FXML
	private void initialize() {

	}

	/**
	 * This method fires whenever the user clicks on the login menu button.
	 * <p>
	 * It sends the user back to the login screen.
	 *
	 * @param event the action event received from the application.
	 */
	@FXML
	private void loginMenuButtonPressed(ActionEvent event) {

        /*
        Persist the current window settings throughout the application.
         */
		win_width = (int) ((Node) event.getSource()).getScene().getWidth();
		win_height = (int) ((Node) event.getSource()).getScene().getHeight();

        /*
        Redirect user to login screen.
         */
		windowManager.loginMenu();
	}

	/**
	 * This method fires whenever the user presses the sign-up button to register to the application.
	 * <p>
	 * It checks if the input is valid and either sends the user back to the login menu or prompts for new input if
	 * the one introduced is invalid.
	 *
	 * @param event the action event received from the application.
	 */
	@FXML
	private void signUpButtonPressed(ActionEvent event) {

        /*
        Persist the current window settings throughout the application.
         */
		win_width = (int) ((Node) event.getSource()).getScene().getWidth();
		win_height = (int) ((Node) event.getSource()).getScene().getHeight();

		/*
		Prepare to start the login thread.
		 */
		setButtonsEnabled(false);

		/*
		Create the login thread task and start it.
		 */
		Task<Boolean> registerTask = createWorker();
		Thread registerThread = new Thread(registerTask);
		registerThread.setDaemon(true);
		registerThread.start();

	}
}
