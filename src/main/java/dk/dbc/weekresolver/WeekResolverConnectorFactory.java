/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.weekresolver;

import dk.dbc.httpclient.HttpClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.client.Client;

/**
 * WeekresolverConnector factory
 * <p>
 * Synopsis:
 * </p>
 * <pre>
 *    // New instance
 *    WeekresolverConnector connector = WeekresolverConnectorFactory.create("http://weekresolver-service");
 *
 *    // Singleton instance in CDI enabled environment
 *    {@literal @}Inject
 *    WeekresolverConnectorFactory factory;
 *    ...
 *    WeekresolverConnector connector = factory.getInstance();
 *
 *    // or simply
 *    {@literal @}Inject
 *    WeekresolverConnector connector;
 * </pre>
 * <p>
 * The CDI case depends on the Weekresolver service base-url being defined as
 * the value of either a system property or environment variable
 * named WEEKRESOLVER_SERVICE_URL.
 * </p>
 */
@ApplicationScoped
public class WeekResolverConnectorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeekResolverConnectorFactory.class);

    public static WeekResolverConnector create(String baseUrl) throws WeekResolverConnectorException {
        final Client client = HttpClient.newClient(new ClientConfig()
                .register(new JacksonFeature()));
        LOGGER.info("Creating WeekresolverConnector for: {}", baseUrl);
        return new WeekResolverConnector(client, baseUrl);
    }

    @Inject
    @ConfigProperty(name = "WEEKRESOLVER_SERVICE_URL")
    private String baseUrl;

    WeekResolverConnector weekresolverconnector;

    @PostConstruct
    public void initializeConnector() {
        try {
            weekresolverconnector = WeekResolverConnectorFactory.create(baseUrl);
        } catch (WeekResolverConnectorException e) {
            throw new IllegalStateException(e);
        }
    }

    @Produces
    public WeekResolverConnector getInstance() {
        return weekresolverconnector;
    }

    @PreDestroy
    public void tearDownConnector() {
        weekresolverconnector.close();
    }
}
