package ro.lbi.sqliggybank.client.util;

import javafx.scene.layout.Region;

/**
 * This class is a default implementation of an error alert. It uses a JavaFX alert implementation.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-23-11 (v0.1)
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
     * This method fires an alert message. It makes use of a JavaFX alert implementation.
     *
     * @see javafx.scene.control.Alert
     *
     * @param title the title for the error pop-up.
     * @param message the message for the error pop-up.
     */
    public static void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.showAndWait();
    }

}
