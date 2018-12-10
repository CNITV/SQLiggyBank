package ro.lbi.sqliggybank.client.view.dashboard;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ro.lbi.sqliggybank.client.backend.user.User;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

import java.io.IOException;

/**
 * This class is the dashboard menu view. Its purpose is to connect the main application class and the
 * dashboard controller class.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @version 0.1
 * @see ro.lbi.sqliggybank.client.view.App
 * @see ro.lbi.sqliggybank.client.view.dashboard.DashboardController
 * @since 2018-26-11 (v0.1)
 */
public class DashboardView {

//	/**
//	 * Load the FXML dashboard file into the controller and get its view.
//	 *
//	 * @param windowManager the window manager injected into the controller.
//	 * @return the view of the FXML dashboard file.
//	 */
//	public Parent getView(WindowManager windowManager, User user) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource("/ro/lbi/sqliggybank/client/view/dashboard/dashboard.fxml"));
//			Parent root = loader.load();
//			((DashboardController)loader.getController()).setWindowManager(windowManager);
//			((DashboardController)loader.getController()).setUser(user);
//
//			return root;
//		} catch (IOException exception) {
//            /*
//            This happens whenever the FXML loader can't load the specified file for whatever reason.
//             */
//			LOGGER.log(Level.ERROR, "The FXML loader couldn't load the FXML file." , exception);
//			Alert.errorAlert("FXML error", "The FXML loader couldn't load the FXML file.");
//			Platform.exit();
//		} catch (IllegalStateException exception) {
//            /*
//            This happens whenever the FXML file isn't found at the specified path or the file name is wrong.
//             */
//			LOGGER.log(Level.ERROR, "The FXML loader couldn't find the file at the specified path.", exception);
//			Alert.errorAlert("FXML error", "The FXML loader couldn't find the file at the specified path.");
//			Platform.exit();
//		}
//        /*
//        The application should never reach this point. Otherwise there's a bug.
//         */
//		return null;
//	}

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
