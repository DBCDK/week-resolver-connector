/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */
package dk.dbc.weekresolver;

import com.github.tomakehurst.wiremock.WireMockServer;
import dk.dbc.httpclient.HttpClient;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

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

public class WeekResolverConnectorTest {
    private static WireMockServer wireMockServer;
    private static String wireMockHost;

    final static Client CLIENT = HttpClient.newClient(new ClientConfig()
            .register(new JacksonFeature()));
    static WeekResolverConnector connector;

    @BeforeAll
    static void startWireMockServer() {
        wireMockServer = new WireMockServer(options().dynamicPort()
                .dynamicHttpsPort());
        wireMockServer.start();
        wireMockHost = "http://localhost:" + wireMockServer.port();
        configureFor("localhost", wireMockServer.port());
    }

    @BeforeAll
    static void setConnector() {
        connector = new WeekResolverConnector(CLIENT, wireMockHost);
    }

    @AfterAll
    static void stopWireMockServer() {
        wireMockServer.stop();
    }

    @Test
    public void testGetWeekcode() throws WeekResolverConnectorException {

        WeekResolverResult weekResolverResult =
                connector.getWeekCode("DPF", LocalDate.parse("2019-10-10"));
        assertThat(weekResolverResult.getWeekNumber(), is(44));
        assertThat(weekResolverResult.getCatalogueCode(), is("DPF"));
        assertThat(weekResolverResult.getWeekCode(), is("DPF201944"));
        assertThat(weekResolverResult.getYear(), is(2019));
        assertThat(weekResolverResult.getDate(), is(Date.from(Instant.ofEpochMilli(1572476400000L))));

        WeekResolverResult weekResolverResult1 =
                connector.getWeekCode("DPF", LocalDate.parse("2019-12-31"));
        assertThat(weekResolverResult.getWeekNumber(), is(44));
        assertThat(weekResolverResult1.getCatalogueCode(), is("DPF"));
        assertThat(weekResolverResult1.getWeekCode(), is("DPF202004"));
        assertThat(weekResolverResult1.getYear(), is(2020));
        assertThat(weekResolverResult1.getDate(), is(is(Date.from(Instant.ofEpochMilli(1579561200000L)))));

        assertThrows(NullPointerException.class, () ->
                connector.getWeekCode(null, null));
    }

    @Test
    public void testGetCurrentWeekcode() throws WeekResolverConnectorException {

        WeekResolverResult weekResolverResult =
                connector.getCurrentWeekCode("BKM", LocalDate.parse("2023-06-16"));
        assertThat(weekResolverResult.getWeekNumber(), is(25));
        assertThat(weekResolverResult.getCatalogueCode(), is("BKM"));
        assertThat(weekResolverResult.getWeekCode(), is("BKM202325"));
        assertThat(weekResolverResult.getYear(), is(2023));
        assertThat(weekResolverResult.getDate(), is(Date.from(Instant.ofEpochMilli(1687471200000L))));

        assertThrows(NullPointerException.class, () ->
                connector.getCurrentWeekCode(null, null));
    }
}
