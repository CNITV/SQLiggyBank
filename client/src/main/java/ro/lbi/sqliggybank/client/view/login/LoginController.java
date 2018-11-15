package ro.lbi.sqliggybank.client.view.login;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 * This class implements the login controller. It's a lightweight interface for the user, giving him an option to either
 * log in using an existing username/password combination or create a new one by registering to the server.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-23-11 (v0.1)
 * @version 0.1
 *
 */
public class LoginController {

    /**
     * This is the default logger for the program view. The framework used is log4j.
     *
     * @see org.apache.log4j.Logger
     */
    private static final Logger LOGGER = Logger.getLogger(LoginController.class);

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
     * This method is the default initialize method for an FXML controller class.
     * <p>
     * It is called <u>before</u> any constructor to set any properties that might be needed in the GUI.
     */
    @FXML
    private void initialize() {
        usernameTextField.setPromptText("username");
        passwordField.setPromptText("password");
    }

    /**
     * This method fires whenever the login button is pressed.
     * <p>
     * It takes the username/password combination introduced and checks them through the
     * <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8" target="_top">API</a>.
     * <p>
     * If the information is correct and the user exists in the database, then the program proceeds
     * to the dashboard.
     *
     * @param event the action event received from the application.
     */
    @FXML
    private void loginButtonPressed(ActionEvent event) {
        /*
        get the username and password and check through the api.
         */
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
        redirect user to register screen.
         */
    }

    /**
     * This method fires whenever the user clicks on the Github link.
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
