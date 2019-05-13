package ro.lbi.sqliggybank.client.view.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;
import ro.lbi.sqliggybank.client.backend.database.DatabaseHandler;
import ro.lbi.sqliggybank.client.backend.User;
import ro.lbi.sqliggybank.client.util.Alert;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

import java.util.Optional;

/**
 * This class implements the group settings controller. It modifies group settings like name or description.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2019-05-12
 */
public class GroupSettingsController {

	/**
	 * This is the default logger for the program view. The framework used is log4j.
	 */
	private static final Logger LOGGER = Logger.getLogger(GroupSettingsController.class);

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
	 * @see User
	 */
	private User user;

	/**
	 * Group name.
	 */
	@FXML
	private TextField groupNameBox;

	/**
	 * Group description.
	 */
	@FXML
	private TextArea descriptionArea;

	GroupSettingsController(WindowManager windowManager, User user) {
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

	}

	/**
	 * This method fires whenever the user wants to change the group data.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private void okButtonPressed(ActionEvent event) {
		ButtonType yesButton = new ButtonType("Yes");
		ButtonType noButton = new ButtonType("No");

		Optional<ButtonType> result = Alert.promptAlert("Group settings", "Are you sure you want to modify the group's data?", yesButton, noButton);

		if (result.get() == yesButton) {
			/*
			Check that everything is ok and edit group information.
			 */
			//databaseHandler.editGroup();
		}
	}

	/**
	 * This method fires whenever the user wants to delete a group.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private void deleteGroupPressed(ActionEvent event) {
		ButtonType yesButton = new ButtonType("Yes");
		ButtonType noButton = new ButtonType("No");

		Optional<ButtonType> result = Alert.promptAlert("WARNING!!", "Are you sure you want to permanently " +
				"delete this group and all the banks/goals in it?\n This action cannot be reversed!!!!", yesButton, noButton);

		if (result.get() == yesButton) {
			/*
			Delete group.
			 */
			//databaseHandler.deleteGroup();
		}
	}

}
