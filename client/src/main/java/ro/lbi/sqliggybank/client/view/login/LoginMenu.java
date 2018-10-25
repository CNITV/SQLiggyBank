package ro.lbi.sqliggybank.client.view.login;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ro.lbi.sqliggybank.client.view.WindowManager;

public class LoginMenu {

    private WindowManager windowManager;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private void initialize() {
        usernameTextField.setPromptText("username");
        passwordTextField.setPromptText("password");
    }

    public LoginMenu(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

}





