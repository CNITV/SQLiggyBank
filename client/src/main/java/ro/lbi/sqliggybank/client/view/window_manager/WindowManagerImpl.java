package ro.lbi.sqliggybank.client.view.window_manager;

import javafx.scene.Scene;
import javafx.stage.Stage;
import ro.lbi.sqliggybank.client.view.login.LoginView;

import static ro.lbi.sqliggybank.client.view.App.app_name;
import static ro.lbi.sqliggybank.client.view.App.win_height;
import static ro.lbi.sqliggybank.client.view.App.win_width;

/**
 * This is the implementation of the {@link ro.lbi.sqliggybank.client.view.window_manager.WindowManager WindowManager}
 * interface.
 *
 * @see ro.lbi.sqliggybank.client.view.window_manager.WindowManager
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-23-11 (v0.1)
 * @version 0.1
 */
public class WindowManagerImpl implements WindowManager {

    /**
     * This is the stage of the JavaFX Application. It is injected as a dependency into the class.
     *
     * @see #setStage(Stage)
     */
    private Stage stage;

    /**
     * This is the injection of the stage.
     *
     * @see #stage
     *
     * @param stage the stage of the application.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * The overwritten login menu method.
     *
     * It loads the login menu view into the application.
     *
     * @see ro.lbi.sqliggybank.client.view.window_manager.WindowManager#loginMenu()
     */
    @Override
    public void loginMenu() {
        LoginView loginView = new LoginView();
        Scene scene = new Scene(loginView.getView(), win_width, win_height);

        stage.setTitle(app_name + " - Login");

        stage.setScene(scene);

        stage.show();
    }

}
