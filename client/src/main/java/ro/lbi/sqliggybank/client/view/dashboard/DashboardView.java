package ro.lbi.sqliggybank.client.view.dashboard;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ro.lbi.sqliggybank.client.backend.User;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

import java.io.IOException;

/**
 * This class is the dashboard menu view. Its purpose is to connect the main application class and the
 * dashboard controller class.
 *
 * @see ro.lbi.sqliggybank.client.view.App
 * @see ro.lbi.sqliggybank.client.view.dashboard.DashboardController
 * 
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-11-26
 */
public class DashboardView {

	/**
	 * Load the FXML dashboard file into the controller and get its view.
	 *
	 * @param windowManager the window manager injected into the controller.
	 * @param user the user currently logged in.
	 * @return the view of the FXML dashboard file.
	 * @throws IOException throws this exception whenever the loader can't load the file.
	 * @throws IllegalStateException throws this exception whenever the loader can't find the file.
	 */
	public Parent getView(WindowManager windowManager, User user) throws IOException, IllegalStateException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/ro/lbi/sqliggybank/client/view/dashboard/dashboard.fxml"));
		loader.setControllerFactory(
				c -> new DashboardController(windowManager, user)
		);

		return loader.load();
	}

}
