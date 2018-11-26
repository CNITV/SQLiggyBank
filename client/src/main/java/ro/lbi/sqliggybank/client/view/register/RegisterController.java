package ro.lbi.sqliggybank.client.view.register;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

import static ro.lbi.sqliggybank.client.view.App.win_height;
import static ro.lbi.sqliggybank.client.view.App.win_width;

/**
 * This class implements the register controller. It prompts the user to create a new account.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-15-11 (v0.1)
 * @version 0.1
 *
 */
public class RegisterController {

    /**
     * This is the default logger for the program view. The framework used is log4j.
     *
     * @see org.apache.log4j.Logger
     */
    private static final Logger LOGGER = Logger.getLogger(RegisterController.class);

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
     * This is the dependency injection of the window manager.
     *
     * @see #windowManager
     */
    void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
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
     * This method fires whenever the user clicks on the login menu button.
     * <p>
     * It sends the user back to the login screen.
     *
     * @param event the action event received from the application.
     */
    @FXML
    private void loginMenuButtonPressed(ActionEvent event) {

        /*
        persist the current window settings
         */
        win_width = (int)((Node)event.getSource()).getScene().getWidth();
        win_height = (int)((Node)event.getSource()).getScene().getHeight();

        /*
        redirect user to login screen
         */
        windowManager.loginMenu();
    }

    /**sss
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
        persist the current window settings
         */
        win_width = (int)((Node)event.getSource()).getScene().getWidth();
        win_height = (int)((Node)event.getSource()).getScene().getHeight();

        /*
        if (input is valid) {
            send user back to login screen
        } else {
            prompt the user for new input
        }
         */
    }

}
