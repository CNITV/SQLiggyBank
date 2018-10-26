package ro.lbi.sqliggybank.client.view;

/**
 * This is just an interface for the windows that need to be implemented in the application.
 * So far the application needs a {@link #loginMenu() login} menu.
 *
 * @author Alexandru GHERGHESCU (alexghergh)
 * @since 2018-23-11 (v0.1)
 * @version 0.1
 */
public interface WindowManager {

    /**
     * This is the login menu. It needs to get the user credentials and check through the
     * <a href="https://documenter.getpostman.com/view/3806934/RWgwRFa8" target="_top">API</a> to see if the user
     * introduced the right login/password combination. If that's not the case, an error pop-up should be displayed
     * to alert the user that the combination is erroneous.
     * <p>
     * If the user introduced the right login/password combination, then the programs proceeds to the dashboard menu.
     */
    void loginMenu();

}
