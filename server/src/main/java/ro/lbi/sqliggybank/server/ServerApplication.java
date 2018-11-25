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
import ro.lbi.sqliggybank.server.Database.UserDAO;
import ro.lbi.sqliggybank.server.Resources.UserResource;

public class ServerApplication extends Application<ServerConfiguration> {
	private final HibernateBundle<ServerConfiguration> hibernateBundle =
			new HibernateBundle<ServerConfiguration>(User.class) {
				@Override
				public DataSourceFactory getDataSourceFactory(ServerConfiguration configuration) {
					return configuration.getDataSourceFactory();
				}
			};

	public static void main(String[] args) throws Exception {
		new ServerApplication().run(args);
	}

	@Override
	public String getName() {
		return "SQLiggyBank-Server";
	}

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

	@Override
	public void run(ServerConfiguration configuration, Environment environment) {
		final UserDAO userDAO = new UserDAO(hibernateBundle.getSessionFactory());
		final String JWTSecret = configuration.getJWTSecret();
		final byte[] secret = JWTSecret.getBytes();
		environment.jersey().register(new UserResource(userDAO, secret));
	}
}
