package ro.lbi.sqliggybank.client.view.register;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.apache.log4j.Logger;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

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
        redirect user to login screen
         */
        windowManager.loginMenu();
    }

}
