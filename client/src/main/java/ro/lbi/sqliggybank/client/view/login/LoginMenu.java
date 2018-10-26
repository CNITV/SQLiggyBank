package ro.lbi.sqliggybank.client.view.login;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
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
     * This is the {@link WindowManager window manager} property that helps the program switch between views.
     *
     * @see ro.lbi.sqliggybank.client.view.WindowManager
     */
    private WindowManager windowManager;

    /**
     * This is the host service needed for the application to access a web page.
     *
     * @see javafx.application.HostServices
     */
    private HostServices hostServices;

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
     * @param hostServices the host services passed by the calling function.
     *
     * @see #windowManager
     * @see #hostServices
     */
    public LoginMenu(WindowManager windowManager, HostServices hostServices) {
        this.windowManager = windowManager;
        this.hostServices = hostServices;
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
        System.out.println("Username is: " + usernameTextField.getText());
        System.out.println("Password is: " + passwordTextField.getText());
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
        System.out.println("New user: " + usernameTextField.getText() + "/" + passwordTextField.getText());
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
        hostServices.showDocument("https://github.com/CNITV/SQLiggyBank");
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
        hostServices.showDocument("https://documenter.getpostman.com/view/3806934/RWgwRFa8");
    }

}





