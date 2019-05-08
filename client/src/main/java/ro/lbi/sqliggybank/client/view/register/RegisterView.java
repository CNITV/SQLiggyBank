package ro.lbi.sqliggybank.client.view.register;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

import java.io.IOException;

/**
 * This class is the register menu view. Its purpose is to connect the main application class and the register
 * controller class.
 *
 * @see ro.lbi.sqliggybank.client.view.App
 * @see ro.lbi.sqliggybank.client.view.register.RegisterController
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-11-15
 *
 */
public class RegisterView {

    /**
     * Load the FXML register file into the controller and get its view.
     *
     * @param windowManager the window manager injected into the controller.
     * @return the view of the FXML register file.
     * @throws IOException throws this exception whenever the loader can't load the file.
     * @throws IllegalStateException throws this exception whenever the loader can't find the file.
     */
    public Parent getView(WindowManager windowManager) throws IOException, IllegalStateException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ro/lbi/sqliggybank/client/view/register/register.fxml"));
        loader.setControllerFactory(
                c -> new RegisterController(windowManager)
        );

        return loader.load();
    }
}
