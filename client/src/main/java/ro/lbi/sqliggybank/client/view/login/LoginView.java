package ro.lbi.sqliggybank.client.view.login;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

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
     * Load the FXML login file into the controller and get its view.
     *
     * @return the view of the FXML login file.
     * @throws IOException in case the FXML loader can't load the file for whatever reason.
     */
    public Parent getView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ro/lbi/sqliggybank/client/view/login/login.fxml"));
        return loader.load();
    }

}
