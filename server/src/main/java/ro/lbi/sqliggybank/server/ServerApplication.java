package ro.lbi.sqliggybank.server;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ro.lbi.sqliggybank.server.Core.User;
import ro.lbi.sqliggybank.server.Database.GroupDAO;
import ro.lbi.sqliggybank.server.Database.GroupListDAO;
import ro.lbi.sqliggybank.server.Database.UserDAO;
import ro.lbi.sqliggybank.server.Resources.GroupResource;
import ro.lbi.sqliggybank.server.Resources.UserResource;

/**
 * ServerApplication is the main entry point of the server. It builds the Hibernate bundle, reads the configuration,
 * initializes all the necessary environment variables and finally, configures the resources and registers them with
 * the Dropwizard framework. The vast majority of this application is example boilerplate from the Dropwizard website.
 *
 * @author StormFireFox1
 * @since 2018-11-25
 */
public class ServerApplication extends Application<ServerConfiguration> {
	/**
	 * The Hibernate bundle for the server. The database is configured here as well.
	 */
	private final HibernateBundle<ServerConfiguration> hibernateBundle =
			new HibernateBundle<ServerConfiguration>(User.class) {
				@Override
				public DataSourceFactory getDataSourceFactory(ServerConfiguration configuration) {
					return configuration.getDataSourceFactory();
				}
			};

	/**
	 * The main method for the server. This is the standard main method for any Dropwizard application.
	 *
	 * @param args The arguments passed from the command line.
	 * @throws Exception Any exception that might happen. All of them. Wow.
	 */
	public static void main(String[] args) throws Exception {
		new ServerApplication().run(args);
	}

	/**
	 * Gets the name of the Dropwizard application.
	 *
	 * @return The name of the application: "SQLiggyBank-Server"
	 */
	@Override
	public String getName() {
		return "SQLiggyBank-Server";
	}

	/**
	 * This method initializes the bootstrap for the application. Currently, it merely adds the Hibernate bundles and
	 * environment variables to the application.
	 *
	 * @param bootstrap The bootstrap to initialize.
	 */
	@Override
	public void initialize(Bootstrap<ServerConfiguration> bootstrap) {
		// Enable environment variables for all configs
		bootstrap.setConfigurationSourceProvider(
				new SubstitutingSourceProvider(
						bootstrap.getConfigurationSourceProvider(),
						new EnvironmentVariableSubstitutor(false)
				)
		);
		bootstrap.addBundle(new MigrationsBundle<ServerConfiguration>() {
			@Override
			public DataSourceFactory getDataSourceFactory(ServerConfiguration configuration) {
				return configuration.getDataSourceFactory();
			}
		});
		bootstrap.addBundle(hibernateBundle);
	}

	/**
	 * This method is the main logic for the application. This is where all the resources are collated and registered
	 * in the environment. The resources and DAO's are constructed and registered here.
	 *
	 * @param configuration The configuration to read information from.
	 * @param environment The environment to configure the application in.
	 */
	@Override
	public void run(ServerConfiguration configuration, Environment environment) {
		final UserDAO userDAO = new UserDAO(hibernateBundle.getSessionFactory());
		final GroupDAO groupDAO = new GroupDAO(hibernateBundle.getSessionFactory());
		final GroupListDAO groupListDAO = new GroupListDAO(hibernateBundle.getSessionFactory(), userDAO);
		final String JWTSecret = configuration.getJWTSecret();
		final byte[] secret = JWTSecret.getBytes();
		environment.jersey().register(new UserResource(userDAO, secret));
		environment.jersey().register(new GroupResource(groupDAO, groupListDAO, userDAO, secret));
	}
}
