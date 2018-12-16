package ro.lbi.sqliggybank.client.view.login;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

import java.io.IOException;

/**
 * This class is the login menu view. Its purpose is to connect the main application class and the login controller
 * class.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @version 0.1
 * @see ro.lbi.sqliggybank.client.view.App
 * @see ro.lbi.sqliggybank.client.view.login.LoginController
 * @since 2018-11-23 (v0.1)
 */
public class LoginView {

	/**
	 * Load the FXML login file into the controller and get its view.
	 *
	 * @param windowManager the window manager injected into the controller.
	 * @return the view of the FXML login file.
	 * @throws IOException           throws this exception whenever the loader can't load the file.
	 * @throws IllegalStateException throws this exception whenever the loader can't find the file.
	 */
	public Parent getView(WindowManager windowManager) throws IOException, IllegalStateException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/ro/lbi/sqliggybank/client/view/login/login.fxml"));
		loader.setControllerFactory(
				c -> new LoginController(windowManager)
		);

		return loader.load();
	}

}
