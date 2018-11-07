package ro.lbi.sqliggybank.client.view.login;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ro.lbi.sqliggybank.client.util.Alert;

import java.io.IOException;

/**
 * This class is the login menu view. Its purpose is to connect the main application class and the login controller
 * class.
 *
 * @see ro.lbi.sqliggybank.client.view.App
 * @see ro.lbi.sqliggybank.client.view.login.LoginController
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-23-11 (v0.1)
 * @version 0.1
 *
 */
public class LoginView {

    /**
     * This is the default logger for the program view. The framework used is log4j.
     *
     * @see org.apache.log4j.Logger
     */
    private static final Logger LOGGER = Logger.getLogger(LoginView.class);

    /**
     * Load the FXML login file into the controller and get its view.
     *
     * @return the view of the FXML login file.
     */
    public Parent getView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ro/lbi/sqliggybank/client/view/login/login.fxml"));
            return loader.load();
        } catch (IOException exception) {
            /*
            This happens whenever the FXML loader can't load the specified file for whatever reason.
             */
            LOGGER.log(Level.ERROR, "The FXML loader couldn't load the FXML file." , exception);
            Alert.showAlert("FXML error", "The FXML loader couldn't load the FXML file.");
            Platform.exit();
        } catch (IllegalStateException exception) {
            /*
            This happens whenever the FXML file isn't found at the specified path or the file name is wrong.
             */
            LOGGER.log(Level.ERROR, "The FXML loader couldn't find the file at the specified path.", exception);
            Alert.showAlert("FXML error", "The FXML loader couldn't find the file at the specified path.");
            Platform.exit();
        }
        /*
        The application should never reach this point. Otherwise there's a bug.
         */
        return null;
    }

}
