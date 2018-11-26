package ro.lbi.sqliggybank.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * ServerConfiguration contains all the information the application needs in order to run.
 * <p>
 * The Dropwizard Configuration class automatically reads the passed YAML configuration file when the application is
 * booted. In particular, any extender sets the application specific properties.
 * To see a good example of a configuration file for this project, read the config.example.yml file included in the
 * source code of SQLiggyBank.
 *
 * @author StormFireFox1
 * @since 2018-11-24
 */
class ServerConfiguration extends Configuration {

	/**
	 * The JWT secret for the signing HMAC256 algorithm.
	 */
	@NotEmpty
	private String JWTSecret;

	/**
	 * Gets the JWT secret.
	 *
	 * @return The JWT secret.
	 */
	@JsonProperty
	String getJWTSecret() {
		return JWTSecret;
	}

	/**
	 * Sets the JWT secret.
	 *
	 * @param JWTSecret The new JWT secret.
	 */
	@JsonProperty
	public void setJWTSecret(String JWTSecret) {
		this.JWTSecret = JWTSecret;
	}

	/**
	 * The data source factory for the configured database in the config file.
	 */
	@Valid
	@NotNull
	private DataSourceFactory database = new DataSourceFactory();

	/**
	 * Gets the data source factory.
	 *
	 * @return The data source factory.
	 */
	@JsonProperty("database")
	DataSourceFactory getDataSourceFactory() {
		return database;
	}

	/**
	 * Sets the data source factory.
	 *
	 * @param dataSourceFactory The new data source factory.
	 */
	@JsonProperty("database")
	public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
		this.database = dataSourceFactory;
	}

}
