package ro.lbi.sqliggybank.client.view.window_manager;

import ro.lbi.sqliggybank.client.backend.User;

/**
 * This is just an interface for the windows that need to be implemented in the application.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-10-23
 */
public interface WindowManager {

    /**
     * This is the login menu method that needs to be overwritten by the class implementing this interface.
     */
    void loginMenu();

    /**
     * This is the register menu method that needs to be overwritten by the class implementing this interface.
     */
    void registerMenu();

    /**
     * This is the dashboard menu method that needs to be overwritten by the class implementing this interface
     *
     * @param user the user currently logged in.
     *
     * @see User
     */
    void dashboardMenu(User user);

}