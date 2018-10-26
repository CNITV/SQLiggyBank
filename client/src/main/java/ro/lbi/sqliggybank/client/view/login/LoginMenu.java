package ro.lbi.sqliggybank.client.view.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import ro.lbi.sqliggybank.client.view.WindowManager;

/**
 * This class implements the login menu. It's a lightweight interface for the user, giving him an option to either
 * log in using an existing username/password combination or create a new one by registering to the server.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-23-11 (v0.1)
 * @version 0.1
 *
 */
public class LoginMenu {

    /**
     * This is the {@link WindowManager window manager} instance that helps the program switch between views.
     *
     * @see WindowManager
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
    private TextField passwordTextField;

    /**
     * The login button.
     */
    @FXML
    private Button loginButton;

    /**
     * The register button.
     */
    @FXML
    private Button registerButton;

    /**
     * This method is the default initialize method for an FXML controller class.
     * <p>
     * It is called <u>before</u> any constructor to set any properties that might be needed in the GUI.
     */
    @FXML
    private void initialize() {
        usernameTextField.setPromptText("username");
        passwordTextField.setPromptText("password");
    }

    /**
     * This is the default constructor. It takes a {@link WindowManager window manager} as a parameter.
     *
     * @param windowManager the window manager passed by the calling function.
     *
     * @see ro.lbi.sqliggybank.client.view.WindowManager
     */
    public LoginMenu(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    /**
     * This method fires whenever the login button is pressed.
     * <p>
     * It takes the username/password combination introduced and checks them through the API.
     * <p>
     * If the information is correct and the user exists in the database, then the program proceeds
     * to the dashboard.
     * TODO links to API and dashboard.
     *
     * @param event the action event received from the application.
     */
    @FXML
    private void loginButtonPressed(ActionEvent event) {
        System.out.println("Username is: " + usernameTextField.getText());
        System.out.println("Password is: " + passwordTextField.getText());
    }

    /**
     * This method fires whenever the register button is pressed.
     * <p>
     * It redirects the user to the register screen.
     * TODO link to register screen.
     *
     * @param event the action event received from the application.
     */
    @FXML
    private void registerButtonPressed(ActionEvent event) {
        System.out.println("New user: " + usernameTextField.getText() + "/" + passwordTextField.getText());
    }

}





