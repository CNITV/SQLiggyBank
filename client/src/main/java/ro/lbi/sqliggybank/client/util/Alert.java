package ro.lbi.sqliggybank.client.util;

import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;

import java.util.Optional;

/**
 * This class is a default implementation of different alerts needed in the program.
 *
 * <p>
 * It uses JavaFX alert implementation to create all the different alerts and customize them as needed.
 *
 * @see javafx.scene.control.Alert
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-11-23 (v0.1)
 * @version 0.1
 *
 */
public class Alert {

    /**
     * The default constructor. It prevents the creation of any instance of this class.
     */
    private Alert() {

    }

    /**
     * This method fires an error message.
     *
     * @param title the title for the error pop-up.
     * @param message the message for the error pop-up.
     */
    public static void errorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.showAndWait();
    }

    /**
     * This method fires a confirmation message.
     *
     * @param title the title for the confirmation prompt.
     * @param message the message for the confirmation prompt.
     * @param buttons the buttons for the confirmation prompt(yes/no for example).
     * @return the button pressed by the user.
     */
    public static Optional<ButtonType> promptAlert(String title, String message, ButtonType... buttons) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.getButtonTypes().setAll(buttons);

        return alert.showAndWait();
    }

}