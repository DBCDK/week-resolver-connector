/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */
package dk.dbc.weekresolver;

import com.github.tomakehurst.wiremock.WireMockServer;
import dk.dbc.httpclient.HttpClient;
import java.time.LocalDate;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WeekresolverConnectorTest {
    private static WireMockServer wireMockServer;
    private static String wireMockHost;

    final static Client CLIENT = HttpClient.newClient(new ClientConfig()
            .register(new JacksonFeature()));
    static WeekresolverConnector connector;

    @BeforeAll
    static void startWireMockServer() {

        wireMockServer = new WireMockServer(options().dynamicPort()
                .dynamicHttpsPort());
        wireMockServer.start();
        wireMockHost = "http://localhost:" + wireMockServer.port();
        configureFor("localhost", wireMockServer.port());
    }

    @BeforeAll
    static void setConnector() throws WeekresolverConnectorException {
        connector = new WeekresolverConnector(CLIENT, wireMockHost);
    }

    @AfterAll
    static void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    public void testGetWeekcode() throws WeekresolverConnectorException {
        WeekResolverResult weekResolverResult =
                connector.getWeekCode("dpf", LocalDate.parse("2019-10-10"));
        assertThat(weekResolverResult.getCatalogueCode(), is("DPF"));
        assertThat(weekResolverResult.getWeekCode(), is("DPF201943"));
        assertThat(weekResolverResult.getYear(), is(2019));

        WeekResolverResult weekResolverResult1 =
                connector.getWeekCode("dpf", LocalDate.parse("2019-12-31"));
        assertThat(weekResolverResult1.getCatalogueCode(), is("DPF"));
        assertThat(weekResolverResult1.getWeekCode(), is("DPF202003"));
        assertThat(weekResolverResult1.getYear(), is(2020));

        assertThrows(NullPointerException.class, () ->
                connector.getWeekCode(null, null));
    }
}