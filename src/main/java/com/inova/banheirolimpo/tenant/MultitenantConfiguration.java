/**
 * 
 */
package com.inova.banheirolimpo.tenant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Markus Souza on 08/11/2017
 *
 */

@Configuration
public class MultitenantConfiguration {

	@Autowired
	private DataSourceProperties properties;

	/**
	 * Define a fonte de dados para o aplicativo
	 * 
	 * @return
	 */
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		File[] files = Paths.get("tenants").toFile().listFiles();
		Map<Object, Object> resolvedDataSources = new HashMap<>();

		for (File propertyFile : files) {
			Properties tenantProperties = new Properties();
			DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(this.getClass().getClassLoader());

			try {
				tenantProperties.load(new FileInputStream(propertyFile));

				String tenantId = tenantProperties.getProperty("name");

				dataSourceBuilder.driverClassName(properties.getDriverClassName())
						.url(tenantProperties.getProperty("datasource.url"))
						.username(tenantProperties.getProperty("datasource.username"))
						.password(tenantProperties.getProperty("datasource.password"));

				if (properties.getType() != null) {
					dataSourceBuilder.type(properties.getType());
				}

				resolvedDataSources.put(tenantId, dataSourceBuilder.build());
			} catch (IOException e) {
				e.printStackTrace();

				return null;
			}
		}

		// Crie a fonte final multi-tenant.
		// Precisa de um banco de dados padrão para se conectar.
		// Certifique-se de que o banco de dados padrão é realmente um banco de dados de
		// inquilino vazio.
		// Não use isso para um inquilino regular se quiser que as coisas estejam
		// seguras!
		MultitenantDataSource dataSource = new MultitenantDataSource();
		dataSource.setDefaultTargetDataSource(defaultDataSource());
		dataSource.setTargetDataSources(resolvedDataSources);

		// Chame isso para finalizar a inicialização da fonte de dados.
		dataSource.afterPropertiesSet();

		return dataSource;
	}

	/**
	 * Cria a fonte de dados padrão para o aplicativo
	 * 
	 * @return
	 */
	private DataSource defaultDataSource() {
		DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(this.getClass().getClassLoader())
				.driverClassName(properties.getDriverClassName()).url(properties.getUrl())
				.username(properties.getUsername()).password(properties.getPassword());

		if (properties.getType() != null) {
			dataSourceBuilder.type(properties.getType());
		}

		return dataSourceBuilder.build();
	}
}
