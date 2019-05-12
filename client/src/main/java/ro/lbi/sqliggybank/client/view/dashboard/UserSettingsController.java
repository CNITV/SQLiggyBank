package ro.lbi.sqliggybank.client.view.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ro.lbi.sqliggybank.client.backend.database.DatabaseHandler;
import ro.lbi.sqliggybank.client.backend.exceptions.BadRequestException;
import ro.lbi.sqliggybank.client.backend.exceptions.ForbiddenException;
import ro.lbi.sqliggybank.client.backend.exceptions.UnauthorizedException;
import ro.lbi.sqliggybank.client.backend.user.User;
import ro.lbi.sqliggybank.client.util.Alert;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

import java.io.IOException;
import java.util.Optional;

/**
 * This class implements the user settings controller. It modifies user settings when needed.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2019-05-12
 */
public class UserSettingsController {

	/**
	 * This is the default logger for the program view. The framework used is log4j.
	 */
	private static final Logger LOGGER = Logger.getLogger(UserSettingsController.class);

	/**
	 * This is the database handler. It handles API calls to the server.
	 *
	 * @see ro.lbi.sqliggybank.client.backend.database.DatabaseHandler
	 */
	private DatabaseHandler databaseHandler;

	/**
	 * This is the window manager. This way the controller can switch to other scenes, like for example the
	 * login menu if the user logs out of the account.
	 *
	 * @see ro.lbi.sqliggybank.client.view.window_manager.WindowManager
	 */
	private WindowManager windowManager;

	/**
	 * The currently logged in user.
	 *
	 * @see ro.lbi.sqliggybank.client.backend.user.User
	 */
	private User user;

	/**
	 * New username.
	 */
	@FXML
	private TextField usernameBox;

	/**
	 * New password.
	 */
	@FXML
	private PasswordField passwordBox;

	/**
	 * New first name.
	 */
	@FXML
	private TextField firstNameBox;

	/**
	 * New last name.
	 */
	@FXML
	private TextField lastNameBox;

	/**
	 * New email.
	 */
	@FXML
	private TextField emailBox;

	UserSettingsController(WindowManager windowManager, User user) {
		this.windowManager = windowManager;
		this.user = user;
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
		usernameBox.setPromptText(user.getUsername());
		passwordBox.setPromptText("**********");
		firstNameBox.setPromptText(user.getFirst_name());
		lastNameBox.setPromptText(user.getLast_name());
		emailBox.setPromptText(user.getEmail());
	}

	/**
	 * This method fires whenever the user wants to change their data.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private void okButtonPressed(ActionEvent event) {
		ButtonType yesButton = new ButtonType("Yes");
		ButtonType noButton = new ButtonType("No");

		Optional<ButtonType> result = Alert.promptAlert("User settings", "Are you sure you want to modify your data?", yesButton, noButton);

		if (result.get() == yesButton) {
			/*
			Check that everything is ok and change user settings.
			 */
			try {
				databaseHandler.editUser(user.getUsername(),
						usernameBox.getText().isEmpty() ? user.getUsername() : usernameBox.getText(),
						passwordBox.getText(),
						firstNameBox.getText().isEmpty() ? (user.getFirst_name() == null ? "" : user.getFirst_name()) : firstNameBox.getText(),
						lastNameBox.getText().isEmpty() ? (user.getLast_name() == null ? "" :user.getLast_name()) : lastNameBox.getText(),
						emailBox.getText().isEmpty() ? (user.getEmail() == null ? "" : user.getEmail()) : emailBox.getText(),
						user.getJWT()
				);

				Alert.infoAlert("Success", "You successfully changed your data!!");
				Alert.infoAlert("New session", "You need to login again");

				windowManager.loginMenu();

				((Node)event.getSource()).getScene().getWindow().hide();

			} catch (IOException e) {
				Alert.errorAlert("Failed to connect to server", "Failed to connect to the database!" +
						" This might be due to the server not currently working! Please try again in a few moments!");
				LOGGER.log(Level.ERROR, "Server connection error", e);
			} catch (UnauthorizedException e) {
				Alert.errorAlert("Not authorized", "Wrong authorization schema!");
				LOGGER.log(Level.ERROR, "Authorization error", e);
			} catch (BadRequestException e) {
				LOGGER.log(Level.ERROR, "Bad request", e);
			} catch (IllegalStateException e) {
				Alert.errorAlert("Username taken", "Username already exists in the database!");
				LOGGER.log(Level.ERROR, "Username exists", e);
			} catch (ForbiddenException e) {
				Alert.errorAlert("Empty fields", "Username and password cannot be empty!");
				LOGGER.log(Level.ERROR, "Empty username or password", e);
			}
		}
	}

	/**
	 * This method fires if the user wants to delete their account.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private void deleteUserPressed(ActionEvent event) {
		ButtonType yesButton = new ButtonType("Yes");
		ButtonType noButton = new ButtonType("No");

		Optional<ButtonType> result = Alert.promptAlert("WARNING!!", "Are you sure you want to permanently " +
				"delete your account?\n This action cannot be reversed!!!!", yesButton, noButton);

		if (result.get() == yesButton) {
			/*
			Delete user account.
			 */
			try {
				databaseHandler.deleteUser(user);

				Alert.infoAlert("Success", "Your account was removed!!");

				windowManager.loginMenu();

				((Node)event.getSource()).getScene().getWindow().hide();

			} catch (IOException e) {
				Alert.errorAlert("Failed to connect to server", "Failed to connect to the database!" +
						" This might be due to the server not currently working! Please try again in a few moments!");
				LOGGER.log(Level.ERROR, "Server connection error", e);
			} catch (UnauthorizedException e) {
				Alert.errorAlert("Not authorized", "Wrong authorization schema!");
				LOGGER.log(Level.ERROR, "Authorization error", e);
			}
		}
	}
}
