package ro.lbi.sqliggybank.client.view.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import org.apache.log4j.Logger;
import ro.lbi.sqliggybank.client.backend.user.User;
import ro.lbi.sqliggybank.client.util.Alert;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

import java.util.Optional;

import static ro.lbi.sqliggybank.client.view.App.win_height;
import static ro.lbi.sqliggybank.client.view.App.win_width;

/**
 * This class implements the dashboard controller. It is the main window that the user encounters while
 * using the application. Here a user can see, create, delete different banks, groups or goals.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-11-15 (v0.1)
 * @version 0.1
 *
 */
public class DashboardController {

	/**
	 * This is the default logger for the program view. The framework used is log4j.
	 */
	private static final Logger LOGGER = Logger.getLogger(DashboardController.class);

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
	 * The label that contains the username.
	 */
	@FXML
	private Label usernameLabel;

	/**
	 * The label that contains the first name and the last name of the user.
	 */
	@FXML
	private Label nameLabel;

	/**
	 * The profile picture of the user.
	 */
	@FXML
	private Circle profilePicture;

	/**
	 * The search bar.
	 */
	@FXML
	private TextField searchTextField;

	/**
	 * The application main panel.
	 */
	@FXML
	private VBox panelBox;

	/**
	 * The groups tree view structure.
	 */
	@FXML
	private TreeView<String> groupsTreeView;

	DashboardController(WindowManager windowManager, User user) {
		this.windowManager = windowManager;
		this.user = user;
		initController();
	}

	/**
	 * This method is called to initialize the variables needed by the application, mainly the user related
	 * stuff, like name, groups, banks and goals.
	 */
	private void initController() {
		/*
		Initialize the username and first name/last name fields.
		 */
		usernameLabel.setText(user.getUsername());
		nameLabel.setText(
				(user.getFirst_name() != null ? user.getFirst_name() : "") + " " +
				(user.getLast_name() != null ? user.getLast_name() : "")
		);

		/*
		Initialize the profile picture(if any).
		 */
		ImagePattern pattern = new ImagePattern(new Image("/ro/lbi/sqliggybank/client/view/dashboard/image/blankprofile.png"));
		profilePicture.setFill(pattern);

		/*
		Initialize the groups, banks and goals.

		ImageView rootIcon = new ImageView(
				new Image("/ro/lbi/sqliggybank/client/view/dashboard/image/folder.png",
						20, 20, true, true)
		);

		TreeItem<String> rootItem = new TreeItem<String> ("Group 1", rootIcon);
		rootItem.setExpanded(true);
		for (int i = 1; i < 6; i++) {
			TreeItem<String> item = new TreeItem<String> ("Bank " + i);
			rootItem.getChildren().add(item);
		}
		groupsTreeView.setRoot(rootItem);
		rootItem.setExpanded(false);
		 */
	}

	/**
	 * This method is the default initialize method for an FXML controller class.
	 * <p>
	 * It is called <u>before</u> any constructor to set any properties that might be needed in the GUI.
	 */
	@FXML
	private void initialize() {

	}

	/**
	 * This method fires whenever the logout button is pressed by the user.
	 *
	 * @param event the action event received from the application.
	 */
	@FXML
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private void logoutButtonPressed(ActionEvent event) {

		/*
        Persist the current window settings throughout the application.
         */
		win_width = (int)((Node)event.getSource()).getScene().getWidth();
		win_height = (int)((Node)event.getSource()).getScene().getHeight();

		/*
		Prompt the user if they really want to logout.
		 */
		ButtonType yesButton = new ButtonType("Yes");
		ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

		Optional<ButtonType> result = Alert.promptAlert("Logout", "Are you sure you want to logout?", yesButton, noButton);

		if (result.get() == yesButton) {
			/*
			Check that everything is ok and logout user, then go back to login menu.
			 */
			windowManager.loginMenu();
		}

	}

	@FXML
	private void settingsButtonPressed(ActionEvent event) {
		/*
		Open settings pop-up window.
		 */
	}

	@FXML
	private void panelRequestFocus(MouseEvent event) {
		panelBox.requestFocus();
	}
}
