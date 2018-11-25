package ro.lbi.sqliggybank.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

class ServerConfiguration extends Configuration {

	@NotEmpty
	private String JWTSecret;

	@JsonProperty
	public String getJWTSecret() {
		return JWTSecret;
	}

	@JsonProperty
	public void setJWTSecret(String JWTSecret) {
		this.JWTSecret = JWTSecret;
	}

	@Valid
	@NotNull
	private DataSourceFactory database = new DataSourceFactory();

	@JsonProperty("database")
	public DataSourceFactory getDataSourceFactory() {
		return database;
	}

	@JsonProperty("database")
	public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
		this.database = dataSourceFactory;
	}

}
