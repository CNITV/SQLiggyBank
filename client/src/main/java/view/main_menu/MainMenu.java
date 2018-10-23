package view.main_menu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import view.WindowManager;

public class MainMenu {

    private WindowManager windowManager;

    @FXML
    private Button button;

    public MainMenu(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    @FXML
    private void buttonPressed(ActionEvent event) {
        System.out.println("hello");
    }

}





