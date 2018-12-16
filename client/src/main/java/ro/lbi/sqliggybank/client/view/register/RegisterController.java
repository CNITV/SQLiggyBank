package ro.lbi.sqliggybank.client.view.register;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ro.lbi.sqliggybank.client.backend.database.DatabaseHandler;
import ro.lbi.sqliggybank.client.util.Alert;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

import java.io.IOException;
import java.net.ConnectException;

import static ro.lbi.sqliggybank.client.view.App.win_height;
import static ro.lbi.sqliggybank.client.view.App.win_width;

/**
 * This class implements the register controller. It prompts the user to create a new account.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @version 0.1
 * @since 2018-15-11 (v0.1)
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
	 * The default constructor for the controller.
	 */
	RegisterController(WindowManager windowManager) {
		this.windowManager = windowManager;

		databaseHandler = new DatabaseHandler();
	}

	/**
	 * This method is the default initialize method for an FXML controller class.
	 * <p>
	 * It is called <u>right after</u> the constructor finished execution and the @FXML annotated fields
	 * are populated.
	 *
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


		try {
			String result;

			if (usernameTextField.getText().equals("") || passwordField.getText().equals("")) {
				throw new IllegalStateException("Username and password cannot be empty!");
			}

			result = databaseHandler.registerUser(
					usernameTextField.getText(),
					passwordField.getText(),
					firstNameTextField.getText(),
					lastNameTextField.getText(),
					emailTextField.getText()
			);

			if (result != null) {
				javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
				alert.setTitle("User registered");
				alert.setHeaderText(null);
				alert.setContentText("User registration complete! You successfully signed up!");
				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

				alert.showAndWait();

				windowManager.loginMenu();
			}
		} catch (ConnectException e) {
			Alert.errorAlert("Failed to connect to server", "Failed to connect to the database!" +
					" This might be due to the server not currently working! Please try again in a few moments!");
			LOGGER.log(Level.ERROR, "Server connection error", e);
		} catch (IOException e) {
			Alert.errorAlert("Error", e.getMessage());
			LOGGER.log(Level.ERROR, "Server error", e);
		} catch (IllegalStateException e) {
			Alert.errorAlert("Error", e.getMessage());
			LOGGER.log(Level.ERROR, e.getMessage(), e);
		}
	}

}
