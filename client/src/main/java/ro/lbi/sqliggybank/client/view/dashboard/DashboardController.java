package ro.lbi.sqliggybank.client.view.dashboard;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ro.lbi.sqliggybank.client.backend.account.Account;
import ro.lbi.sqliggybank.client.backend.database.DatabaseHandler;
import ro.lbi.sqliggybank.client.backend.exceptions.NotFoundException;
import ro.lbi.sqliggybank.client.backend.exceptions.UnauthorizedException;
import ro.lbi.sqliggybank.client.backend.user.User;
import ro.lbi.sqliggybank.client.util.Alert;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

import java.io.IOException;
import java.util.Optional;

import static ro.lbi.sqliggybank.client.view.App.win_height;
import static ro.lbi.sqliggybank.client.view.App.win_width;

/**
 * This class implements the dashboard controller. It is the main window that the user encounters while
 * using the application. Here a user can see, create, delete different banks, groups or goals.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-11-15
 */
public class DashboardController {

	/**
	 * This is the default logger for the program view. The framework used is log4j.
	 */
	private static final Logger LOGGER = Logger.getLogger(DashboardController.class);

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
	 * Search bar for another user.
	 */
	@FXML
	private TextField searchBar;

	/**
	 * The groups tree view structure.
	 */
	@FXML
	private TreeView<String> groupsTreeView;

	/**
	 * The group name.
	 */
	@FXML
	private Label groupNameLabel;

	/**
	 * The label which states who created the group.
	 */
	@FXML
	private Label createdByLabel;

	/**
	 * The group description tooltip.
	 */
	@FXML
	private Tooltip groupDescriptionTooltip;

	/**
	 * List of all the members of a group.
	 */
	@FXML
	private ListView<String> membersList;

	/**
	 * List of all the banks of a group.
	 */
	@FXML
	private ListView<String> banksList;

	/**
	 * List of goals for a specific bank of a group.
	 */
	@FXML
	private ListView<String> goalsList;

	DashboardController(WindowManager windowManager, User user) {
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
		*/

		///TODO get groups,banks and goals and list them
		//test groups
		ImageView groupIcon = new ImageView(
				new Image("/ro/lbi/sqliggybank/client/view/dashboard/image/folder.png",
						20, 20, true, true)
		);
		TreeItem<String> rootItem = new TreeItem<>("Groups");
		TreeItem<String> group1 = new TreeItem<>("Group 1", groupIcon);

		rootItem.getChildren().add(group1);

		for (int i = 1; i < 10; i++) {
			TreeItem<String> item = new TreeItem<>("Bank " + i);

			group1.getChildren().add(item);
		}

		groupsTreeView.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {
					groupNameLabel.setVisible(true);
					groupNameLabel.setText(newValue.getValue());

					createdByLabel.setVisible(true);
					createdByLabel.setText("Created by " + user.getUsername());

					groupDescriptionTooltip.setText("this is the group description ahahahaha this is the group description ahahahaha this is the group description ahahahaha this is the group description ahahahaha this is the group description ahahahaha this is the group description ahahahaha");

					for (int i = 0; i < 15; ++i) {
						membersList.getItems().add("Member " + i);
					}
				}
		);
		groupsTreeView.setRoot(rootItem);
		groupsTreeView.setShowRoot(false);
	}

	/**
	 * This method is used to open a dialog with the search of another user.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	private void searchBarPressed(ActionEvent event) {
		try {
			/*
			Search for user in the database.
		    */
			// TODO put this in a separate thread so it doesn't block the main application thread
			String result = databaseHandler.getUser(searchBar.getText(), user.getJWT());

			Gson gson = new Gson();
			User searchedUser = gson.fromJson(result, User.class);

			javafx.scene.control.Alert userInfo = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
			userInfo.setTitle("Information for the user " + searchBar.getText());
			userInfo.setHeaderText(null);
			userInfo.setContentText("Username: " + searchedUser.getUsername() + "\n" +
					"Last name: " + searchedUser.getLast_name() + "\n" +
					"First name: " + searchedUser.getFirst_name() + "\n" +
					"Email: " + searchedUser.getEmail());
			userInfo.showAndWait();

		} catch (IOException e) {
			Platform.runLater(() ->
					Alert.errorAlert("Error", "Failed to connect to the database!" +
							" This might be due to the server not currently working! Please try again in a few moments!")
			);
			LOGGER.log(Level.ERROR, "Server error", e);
		} catch (UnauthorizedException e) {
			Platform.runLater(() ->
					Alert.errorAlert("Wrong authorization schema", e.getMessage())
			);
			LOGGER.log(Level.ERROR, "Wrong authorization schema", e);
		} catch (NotFoundException e) {
			Platform.runLater(() ->
					Alert.errorAlert("User not found", e.getMessage())
			);
			LOGGER.log(Level.ERROR, "User not found", e);
		}
	}

	/**
	 * This method opens a settings dialog for the user.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	private void userSettingsButtonPressed(ActionEvent event) {
		/*
		Open settings pop-up window.
		 */
		try {
			Stage settings = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ro/lbi/sqliggybank/client/view/dashboard/userSettings.fxml"));
			loader.setControllerFactory(
					c -> new UserSettingsController(windowManager, user)
			);

			Parent root = loader.load();

			settings.setScene(new Scene(root, 600 , 400));
			settings.setTitle("User settings");
			settings.initModality(Modality.WINDOW_MODAL);
			settings.initOwner(((Node)event.getSource()).getScene().getWindow());
			settings.setResizable(false);
			settings.show();

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
	 * This method fires whenever the logout button is pressed.
	 *
	 * @param event the action event received from the application.
	 */
	@FXML
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private void logoutButtonPressed(ActionEvent event) {

		/*
        Persist the current window settings throughout the application.
         */
		win_width = (int) ((Node) event.getSource()).getScene().getWidth();
		win_height = (int) ((Node) event.getSource()).getScene().getHeight();

		/*
		Prompt the user if they really want to logout.
		 */
		ButtonType yesButton = new ButtonType("Yes");
		ButtonType noButton = new ButtonType("No");

		Optional<ButtonType> result = Alert.promptAlert("Logout", "Are you sure you want to logout?", yesButton, noButton);

		if (result.get() == yesButton) {
			/*
			Check that everything is ok and logout user, then go back to login menu.
			 */
			windowManager.loginMenu();
		}

	}

	/**
	 * This method fires whenever the group settings button is pressed.
	 *
	 * @param event the action event received from the application.
	 */
	@FXML
	private void groupSettingsButtonPressed(ActionEvent event) {

	}

	@FXML
	private void joinGroupButtonPressed(ActionEvent event) {

	}

	@FXML
	private void newGroupButtonPressed(ActionEvent event) {

	}

	@FXML
	private void transactionsButtonPressed(ActionEvent event) {

	}

	@FXML
	private void createGroupInviteButtonPressed(ActionEvent event) {

	}

	@FXML
	private void showGroupInvitesButtonPressed(ActionEvent event) {

	}

	@FXML
	private void addBankButtonPressed(ActionEvent event) {

	}

	@FXML
	private void removeBankButtonPressed(ActionEvent event) {

	}

	@FXML
	private void addGoalButtonPressed(ActionEvent event) {

	}

	@FXML
	private void removeGoalButtonPressed(ActionEvent event) {

	}
}
