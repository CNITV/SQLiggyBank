package ro.lbi.sqliggybank.client.view.login;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ro.lbi.sqliggybank.client.backend.account.Account;
import ro.lbi.sqliggybank.client.backend.database.DatabaseHandler;
import ro.lbi.sqliggybank.client.backend.user.User;
import ro.lbi.sqliggybank.client.util.Alert;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

import java.io.IOException;
import java.net.ConnectException;

import static ro.lbi.sqliggybank.client.view.App.win_height;
import static ro.lbi.sqliggybank.client.view.App.win_width;

/**
 * This class implements the login controller. It's a lightweight interface for the user, giving him an option to either
 * log in using an existing username/password combination or create a new one by registering to the server.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-10-23 (v0.1)
 * @version 0.1
 *
 */
public class LoginController {

    /**
     * This is the default logger for the program view. The framework used is log4j.
     */
    private static final Logger LOGGER = Logger.getLogger(LoginController.class);

    /**
     * This is the database handler. It handles API calls to the server.
     *
     * @see ro.lbi.sqliggybank.client.backend.database.DatabaseHandler
     */
    private DatabaseHandler databaseHandler;

    /**
     * This is the window manager. This way the controller can switch to other scenes.
     *
     * @see ro.lbi.sqliggybank.client.view.window_manager.WindowManager
     */
    private WindowManager windowManager;

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
     * The default constructor for the controller.
     */
    LoginController(WindowManager windowManager) {
        this.windowManager = windowManager;

        databaseHandler = new DatabaseHandler();
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
     * This method fires whenever the login button is pressed.
     *
     * <p>
     * It takes the username/password combination introduced and checks them through the
     * <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8" target="_top">API</a>.
     *
     * <p>
     * If the information is correct and the user exists in the database, then the program proceeds
     * to the dashboard.
     *
     * @param event the action event received from the application.
     */
    @FXML
    private void loginButtonPressed(ActionEvent event) {

        /*
        Persist the current window settings throughout the application.
         */
        win_width = (int)((Node)event.getSource()).getScene().getWidth();
        win_height = (int)((Node)event.getSource()).getScene().getHeight();

        /*
        Get the username and password and check through the api.
        Assuming everything is fine, redirect user to their dashboard.
        Otherwise, throw an error message to indicate something went wrong.
         */
        try {
            /*
            Create an account for the user with the username and password.
             */
            Account account;
            account = new Account(usernameTextField.getText(), passwordField.getText());
            try {
                /*
                Get the logged in user credentials using the account created earlier.
                 */
                String result;
                result = databaseHandler.loginUser(account);

                /*
                Extract the JWT for the user from the credentials received from the server.
                 */
                String JWT = null;
                if (result != null) {
                    JWT = new JsonParser()
                            .parse(result)
                            .getAsJsonObject()
                            .get("token")
                            .getAsString();
                }

                try {
                    /*
                    Get the user account from the server using the JWT.
                     */
                    result = databaseHandler.getUser(account, JWT);

                    Gson gson = new Gson();
                    User user = gson.fromJson(result, User.class);
                    user.setJWT(JWT);

                    /*
                    Redirect the user to their dashboard.
                     */
                    windowManager.dashboardMenu(user);

                } catch (IOException e) {
                    Alert.errorAlert("Error", e.getMessage());
                    LOGGER.log(Level.ERROR, "Server error", e);
                } catch (IllegalAccessException e) {
                    Alert.errorAlert("Wrong authorization schema", e.getMessage());
                    LOGGER.log(Level.ERROR, "Wrong authorization schema", e);
                }


            } catch (ConnectException e) {
                Alert.errorAlert("Failed to connect to server", "Failed to connect to the database!" +
                        " This might be due to the server not currently working! Please try again in a few moments!");
                LOGGER.log(Level.ERROR, "Server connection error", e);
            } catch (IOException e) {
                Alert.errorAlert("Error", e.getMessage());
                LOGGER.log(Level.ERROR, "Server error", e);
            } catch (IllegalAccessException e) {
                Alert.errorAlert("Failed to login", e.getMessage());
                LOGGER.log(Level.ERROR, "Failed to login", e);
            }
        } catch (IllegalArgumentException e) {
            Alert.errorAlert("Failed to login", e.getMessage());
            LOGGER.log(Level.ERROR, "Failed to login", e);
        }
    }

    /**
     * This method fires whenever the register button is pressed.
     * <p>
     * It redirects the user to the register screen.
     *
     * @param event the action event received from the application.
     */
    @FXML
    private void registerButtonPressed(ActionEvent event) {

        /*
        Persist the current window settings throughout the application.
         */
        win_width = (int)((Node)event.getSource()).getScene().getWidth();
        win_height = (int)((Node)event.getSource()).getScene().getHeight();

        /*
        Redirect user to register screen.
         */
        windowManager.registerMenu();
    }

    /**
     * This method fires whenever the user clicks on the Github link.
     *
     * <p>
     * Redirects to the project's official <a href="https://github.com/CNITV/SQLiggyBank" target="_top">Github</a>.
     *
     * @param event the action event received from the application.
     */
    @FXML
    private void githubURLPressed(ActionEvent event) {
        /*
        !!! This is a mess and really complicated to comprehend.

        Basically we get the stage through the event so we can access the stage properties.
        One of the stage properties is the host services that we need in order to reference a web page from a hyperlink.
         */
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ((HostServices)stage.getProperties().get("hostServices")).showDocument("https://github.com/CNITV/SQLiggyBank");
    }

    /**
     * This method fires whenever the user clicks on the API link.
     *
     * <p>
     * Redirects to the project's official
     * <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8" target="_top">API</a>.
     *
     * @param event the action event received from the application.
     */
    @FXML
    private void apiURLPressed(ActionEvent event) {
        /*
        !!! This is a mess and really complicated to comprehend.

        Basically we get the stage through the event so we can access the stage properties.
        One of the stage properties is the host services that we need in order to reference a web page from a hyperlink.
         */
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        ((HostServices)stage.getProperties().get("hostServices")).showDocument("https://documenter.getpostman.com/view/3806934/RWgwRFa8");
    }
}
