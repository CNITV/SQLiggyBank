package ro.lbi.sqliggybank.client.view.dashboard;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import ro.lbi.sqliggybank.client.backend.*;
import ro.lbi.sqliggybank.client.backend.database.DatabaseHandler;
import ro.lbi.sqliggybank.client.backend.exceptions.ForbiddenException;
import ro.lbi.sqliggybank.client.backend.exceptions.NotFoundException;
import ro.lbi.sqliggybank.client.backend.exceptions.UnauthorizedException;
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
	 * @see User
	 */
	private User user;

	/**
	 * The current members of the highlighted group.
	 */
	private User[] members;

	/**
	 * The current highlighted group.
	 */
	private Group group;

	/**
	 * All the groups of a user.
	 */
	private Group[] groups;

	/**
	 * The current highlighted bank.
	 */
	private Bank bank;

	/**
	 * All the banks of a group.
	 */
	private Bank[] banks;

	/**
	 * The current highlighted goal.
	 */
	private Goal goal;

	/**
	 * All goals of a bank.
	 */
	private Goal[] goals;

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
		this.group = null;
		this.bank = null;
		this.goal = null;
		this.databaseHandler = new DatabaseHandler();
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

		banksList.setOnMouseClicked((event) ->
				{
					//databaseHandler.getBank();
				}
		);

		ImageView groupIcon = new ImageView(
				new Image("/ro/lbi/sqliggybank/client/view/dashboard/image/folder.png",
						20, 20, true, true)
		);
		TreeItem<String> rootItem = new TreeItem<>("Groups");
		try {
			String result = databaseHandler.getGroupsOfUser(user.getUsername(), user.getJWT());

			Gson gson = new Gson();
			groups = gson.fromJson(result, Group[].class);

			for (Group group : groups) {
				rootItem.getChildren().add(new TreeItem<>(group.getName(), new ImageView(
						new Image("/ro/lbi/sqliggybank/client/view/dashboard/image/folder.png",
								20, 20, true, true)
				)));
			}

		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "Connection error", e);
			Alert.errorAlert("Connection error", "Database is not available at the moment, try again" +
					" in a few moments.");
			Platform.exit();
		}

		groupsTreeView.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {
					try {
						String result;
						result = databaseHandler.groupInfo(newValue.getValue(), user.getJWT());

						Gson gson = new Gson();
						group = gson.fromJson(result, Group.class);

						groupNameLabel.setText(group.getName());
						groupNameLabel.setVisible(true);

						createdByLabel.setText("created by " + group.getOwner().getUsername());
						createdByLabel.setVisible(true);

						groupDescriptionTooltip.setText(group.getDescription());

						membersList.getItems().clear();
						try {
							result = databaseHandler.getMembersOfGroup(group.getName(), user.getJWT());

							gson = new Gson();
							members = gson.fromJson(result, User[].class);

							for (User member : members) {
								membersList.getItems().add(member.getUsername());
							}

						} catch (IOException e) {
							LOGGER.log(Level.ERROR, "Connection error", e);
							Alert.errorAlert("Connection error", "Database is not available at the moment, try again" +
									" in a few moments.");
						}

						banksList.getItems().clear();
						try {
							result = databaseHandler.getBanksOfGroup(group.getName(), user.getJWT());

							gson = new Gson();
							banks = gson.fromJson(result, Bank[].class);

							for (Bank bank : banks) {
								banksList.getItems().add(bank.getName());
							}
							banksList.setOnMouseClicked((event) ->
									{
										try {
											String res = databaseHandler.bankInfo(group.getName(),
													user.getJWT(), banksList.getSelectionModel().getSelectedItem());

											Gson gson1 = new Gson();
											bank = gson1.fromJson(res, Bank.class);

											Alert.infoAlert("Bank information", bank.toString());
										} catch (IOException e) {
											LOGGER.log(Level.ERROR, "Connection error", e);
											Alert.errorAlert("Connection error", "Database is not available at the moment, try again" +
													" in a few moments.");
										}

									}
							);
						} catch (IOException e) {
							LOGGER.log(Level.ERROR, "Connection error", e);
							Alert.errorAlert("Connection error", "Database is not available at the moment, try again" +
									" in a few moments.");
						}

						goalsList.getItems().clear();
						try {
							result = databaseHandler.getGoalsOfGroup(group.getName(), bank.getName(),
									user.getJWT());

							gson = new Gson();
							goals = gson.fromJson(result, Goal[].class);

							for (Goal goal : goals) {
								goalsList.getItems().add(goal.getName());
							}
							goalsList.setOnMouseClicked((event) ->
									{
										try {
											String res = databaseHandler.goalInfo(group.getName(), bank.getName(),
													user.getJWT(), goalsList.getSelectionModel().getSelectedItem());

											Gson gson1 = new Gson();
											goal = gson1.fromJson(res, Goal.class);

											Alert.infoAlert("Goal information", goal.toString());
										} catch (IOException e) {
											LOGGER.log(Level.ERROR, "Connection error", e);
											Alert.errorAlert("Connection error", "Database is not available at the moment, try again" +
													" in a few moments.");
										}
									}
							);
						} catch (IOException e) {
							LOGGER.log(Level.ERROR, "Connection error", e);
							Alert.errorAlert("Connection error", "Database is not available at the moment, try again" +
									" in a few moments.");
						}

					} catch (IOException e) {
						LOGGER.log(Level.ERROR, "Connection error", e);
						Alert.errorAlert("Connection error", "Database is not available at the moment, try again" +
								" in a few moments.");
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
			String result = databaseHandler.userProfile(searchBar.getText(), user.getJWT());

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
		} catch (NotFoundException e) {
			Platform.runLater(() ->
					Alert.errorAlert(e.getTitle(), e.getMessage())
			);
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

			settings.setScene(new Scene(root, 600, 400));
			settings.setTitle("User settings");
			settings.initModality(Modality.WINDOW_MODAL);
			settings.initOwner(((Node) event.getSource()).getScene().getWindow());
			settings.setResizable(false);
			settings.show();

		} catch (IOException exception) {
            /*
            This happens whenever the FXML loader can't load the specified file for whatever reason.
             */
			LOGGER.log(Level.ERROR, "The FXML loader couldn't load the FXML file.", exception);
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
		/*
		Open group settings pop-up window.
		 */
		try {
			Stage settings = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ro/lbi/sqliggybank/client/view/dashboard/groupSettings.fxml"));
			loader.setControllerFactory(
					c -> new GroupSettingsController(windowManager, user, group)
			);

			Parent root = loader.load();

			settings.setScene(new Scene(root, 600, 400));
			settings.setTitle("Group settings");
			settings.initModality(Modality.WINDOW_MODAL);
			settings.initOwner(((Node) event.getSource()).getScene().getWindow());
			settings.setResizable(false);
			settings.show();

		} catch (IOException exception) {
            /*
            This happens whenever the FXML loader can't load the specified file for whatever reason.
             */
			LOGGER.log(Level.ERROR, "The FXML loader couldn't load the FXML file.", exception);
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
	 * This method fires when the user wants to join a group.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	private void joinGroupButtonPressed(ActionEvent event) {
		String groupName = null;

		TextInputDialog dialog1 = new TextInputDialog("Group Name");
		dialog1.setTitle("Group Invite");
		dialog1.setHeaderText(null);
		dialog1.setContentText("Please enter the group name you want to join:");

		Optional<String> result1 = dialog1.showAndWait();
		if (result1.isPresent()) {
			groupName = result1.get();
		} else {
			Alert.errorAlert("Error", "Group name cannot be empty!");
			return;
		}

		TextInputDialog dialog = new TextInputDialog("Invite ID");
		dialog.setTitle("Group Invite");
		dialog.setHeaderText(null);
		dialog.setContentText("Please enter the group invite ID:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			String invite = result.get();
			try {
				databaseHandler.joinGroup(groupName, user.getJWT(), invite);
			} catch (NullPointerException e) {
				Alert.errorAlert("Error", "You didn't select a group!");
			} catch (IOException e) {
				LOGGER.log(Level.ERROR, "Connection error", e);
				Alert.errorAlert("Connection error", "The database can't be accessed at the moment.");
			} catch (ForbiddenException e) {
				Alert.errorAlert(e.getTitle(), e.getMessage());
			}
		} else {
			Alert.errorAlert("Error", "Group invite ID cannot be empty!");
		}
	}

	/**
	 * This method fires when the user wants to create a group.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	private void newGroupButtonPressed(ActionEvent event) {
		String name = null;
		String description = null;

		TextInputDialog dialog1 = new TextInputDialog("Group name");
		dialog1.setTitle("Create Group");
		dialog1.setHeaderText(null);
		dialog1.setContentText("Please enter the name of the group:");

		Optional<String> result1 = dialog1.showAndWait();
		if (result1.isPresent()) {
			name = result1.get();
		} else {
			Alert.errorAlert("Error", "Group name cannot be empty!");
			return;
		}

		TextInputDialog dialog2 = new TextInputDialog("Group Description");
		dialog2.setTitle("Create Group");
		dialog2.setHeaderText(null);
		dialog2.setContentText("Please enter the description of the group:");

		Optional<String> result2 = dialog2.showAndWait();
		description = (result2.isPresent() ? result2.get() : "");

		try {
			databaseHandler.newGroup(name, description, user.getJWT());
			Alert.infoAlert("Success", "Successfully created a group!");
			Alert.infoAlert("Login", "Please login again so your changes take place.");
			windowManager.loginMenu();
		} catch (NullPointerException e) {
			Alert.errorAlert("Error", "You didn't select a group!");
		} catch (ForbiddenException e) {
			Alert.errorAlert(e.getTitle(), e.getMessage());
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "Connection error", e);
			Alert.errorAlert("Connection error", "The database can't be accessed at the moment.");
		}
	}

	/**
	 * This method opens a dialog for the transactions.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	private void transactionsButtonPressed(ActionEvent event) {
		/*
		Go to transactions pop-up window.
		 */
		try {
			Stage settings = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ro/lbi/sqliggybank/client/view/dashboard/transactions.fxml"));
			if (group == null || bank == null) {
				throw new NullPointerException();
			}
			loader.setControllerFactory(
					c -> new TransactionsController(windowManager, user, group, bank)
			);

			Parent root = loader.load();

			settings.setScene(new Scene(root, 600, 400));
			settings.setTitle("Transactions");
			settings.initModality(Modality.WINDOW_MODAL);
			settings.initOwner(((Node) event.getSource()).getScene().getWindow());
			settings.setResizable(false);
			settings.show();

		} catch (IOException exception) {
            /*
            This happens whenever the FXML loader can't load the specified file for whatever reason.
             */
			LOGGER.log(Level.ERROR, "The FXML loader couldn't load the FXML file.", exception);
			Alert.errorAlert("FXML error", "The FXML loader couldn't load the FXML file.");
			Platform.exit();
		} catch (IllegalStateException exception) {
            /*
            This happens whenever the FXML file isn't found at the specified path or the file name is wrong.
             */
			LOGGER.log(Level.ERROR, "The FXML loader couldn't find the file at the specified path.", exception);
			Alert.errorAlert("FXML error", "The FXML loader couldn't find the file at the specified path.");
			Platform.exit();
		} catch (NullPointerException e) {
			LOGGER.log(Level.ERROR, "The FXML loader couldn't find the file at the specified path.", e);
			Alert.errorAlert("Error", "You didn't select a group!!");
		}
	}

	/**
	 * This method fires when the user wants to generate a 24-hour group invite link.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	private void createGroupInviteButtonPressed(ActionEvent event) {
		try {
			databaseHandler.generateGroupInvite(group.getName(), user.getJWT());
			Alert.infoAlert("Success", "You successfully generated a group invite!!\n" +
					"Go to group invites to see it.");
		} catch (NullPointerException e) {
			Alert.errorAlert("Error", "You didn't select a group!");
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "Connection error", e);
			Alert.errorAlert("Connection error", "The database can't be accessed at the moment.");
		} catch (ForbiddenException e) {
			Alert.errorAlert(e.getTitle(), e.getMessage());
		}
	}

	/**
	 * This method fires when the user wants to see the currently active group invite links.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	private void showGroupInvitesButtonPressed(ActionEvent event) {
		try {
			String result = databaseHandler.getInvitesOfGroup(group.getName(), user.getJWT());

			Gson gson = new Gson();
			Invite[] invites = gson.fromJson(result, Invite[].class);

			String inviteString = "";
			for (Invite invite : invites) {
				inviteString = inviteString.concat(invite.getUuid().toString()).concat("\n");
			}

			Alert.infoAlert("Invites", "These are the available invites:\n" +
					inviteString);

		} catch (NullPointerException e) {
			Alert.errorAlert("Error", "You didn't select a group!");
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "Connection error", e);
			Alert.errorAlert("Connection error", "The database can't be accessed at the moment.");
		} catch (ForbiddenException e) {
			Alert.errorAlert(e.getTitle(), e.getMessage());
		}
	}

	/**
	 * This method fires when the user wants to create a bank for the group.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	private void addBankButtonPressed(ActionEvent event) {
		String bankName = null;
		String bankDescription = null;

		TextInputDialog dialog1 = new TextInputDialog("Bank name");
		dialog1.setTitle("Create Bank");
		dialog1.setHeaderText(null);
		dialog1.setContentText("Please enter the name of the bank:");

		Optional<String> result1 = dialog1.showAndWait();
		if (result1.isPresent()) {
			bankName = result1.get();
		} else {
			Alert.errorAlert("Error", "Bank name can't be empty!");
			return;
		}

		TextInputDialog dialog2 = new TextInputDialog("Bank Description");
		dialog2.setTitle("Create Bank");
		dialog2.setHeaderText(null);
		dialog2.setContentText("Please enter the description of the bank:");

		Optional<String> result2 = dialog2.showAndWait();
		bankDescription = (result2.isPresent() ? result2.get() : "");
		try {
			databaseHandler.newBank(group.getName(), user.getJWT(), bankName, bankDescription);
			Alert.infoAlert("Success", "Successfully created a bank!");
			Alert.infoAlert("Login", "Please login again so your changes take place.");
			windowManager.loginMenu();
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "Connection error", e);
			Alert.errorAlert("Connection error", "The database can't be accessed at the moment.");
		} catch (ForbiddenException e) {
			Alert.errorAlert(e.getTitle(), e.getMessage());
		}
	}

	/**
	 * This method fires when the user wants to remove a bank from the group.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	private void removeBankButtonPressed(ActionEvent event) {
		try {
			databaseHandler.deleteBank(group.getName(), user.getJWT(), bank.getName());
			Alert.infoAlert("Success", "Successfully created a bank!");
			Alert.infoAlert("Login", "Please login again so your changes take place.");
			windowManager.loginMenu();
		} catch (IOException e) {
			LOGGER.log(Level.ERROR, "Connection error", e);
			Alert.errorAlert("Connection error", "The database can't be accessed at the moment.");
		} catch (ForbiddenException e) {
			Alert.errorAlert(e.getTitle(), e.getMessage());
		}
	}

	/**
	 * This method fires when the user wants to add a goal for a bank.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	private void addGoalButtonPressed(ActionEvent event) {
		//databaseHandler.addGoal();
	}

	/**
	 * This method fires when the user wants to delete a goal from a bank.
	 *
	 * @param event the event received from the application.
	 */
	@FXML
	private void removeGoalButtonPressed(ActionEvent event) {
		//databaseHandler.removeGoal();
	}
}
