package view.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import view.WindowManager;

public class LoginMenu {

    private WindowManager windowManager;

    @FXML
    private Button button;

    public LoginMenu(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    @FXML
    private void buttonPressed(ActionEvent event) {
        System.out.println("hello");
    }

}





