/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.weekresolver;

import dk.dbc.httpclient.FailSafeHttpClient;
import dk.dbc.httpclient.HttpGet;
import dk.dbc.invariant.InvariantUtil;
import java.time.LocalDate;
import net.jodah.failsafe.RetryPolicy;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import dk.dbc.util.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WeekresolverConnector - Weekresolver client
 * <p>
 * To use this class, you construct an instance, specifying a web resources client as well as
 * a base URL for the Weekresolver service endpoint you will be communicating with.
 * </p>
 * <p>
 * This class is thread safe, as long as the given web resources client remains thread safe.
 * </p>
 */
public class WeekresolverConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeekresolverConnector.class);
    public static final String WEEKRESOLVER_URI_AND_PARMS = "api/v1/date/%s/%s";
    private static final RetryPolicy RETRY_POLICY = new RetryPolicy()
            .retryOn(Collections.singletonList(ProcessingException.class))
            .retryIf((Response response) ->
                    response.getStatus() == 404
                            || response.getStatus() == 500
                            || response.getStatus() == 502)
            .withDelay(5, TimeUnit.SECONDS)
            .withMaxRetries(3);

    private final FailSafeHttpClient failSafeHttpClient;
    private final String baseUrl;

    public WeekresolverConnector(Client httpClient, String baseUrl)
            throws WeekresolverConnectorException {
        this(FailSafeHttpClient.create(httpClient, RETRY_POLICY), baseUrl);
    }

    /**
     * Returns new instance with custom retry policy
     *
     * @param failSafeHttpClient web resources client with custom retry policy
     * @param baseUrl            base URL for record service endpoint
     * @throws WeekresolverConnectorException on failure to create {@link WeekresolverConnector}
     */
    public WeekresolverConnector(FailSafeHttpClient failSafeHttpClient, String baseUrl)
            throws WeekresolverConnectorException {
        this.failSafeHttpClient = InvariantUtil.checkNotNullOrThrow(
                failSafeHttpClient, "failSafeHttpClient");
        this.baseUrl = InvariantUtil.checkNotNullNotEmptyOrThrow(
                baseUrl, "baseUrl");
    }

    public WeekResolverResult getWeekCode(String catalogueCode) throws WeekresolverConnectorException {
        final Stopwatch stopwatch = new Stopwatch();
        try {
            Params params = new Params()
                    .withCatalogueCode(catalogueCode)
                    .withDate(LocalDate.now());

            return sendRequest(params);
        }
        finally {
            LOGGER.info("getWeekCode took {} ms", stopwatch.getElapsedTime(TimeUnit.MILLISECONDS));

        }
    }

    public WeekResolverResult getWeekCode(String catalogueCode, LocalDate date) throws WeekresolverConnectorException {
        final Stopwatch stopwatch = new Stopwatch();
        try {
            Params params = new Params()
                    .withCatalogueCode(catalogueCode)
                    .withDate(date);

            return sendRequest(params);
        }
        finally {
            LOGGER.info("getWeekCode took {} ms", stopwatch.getElapsedTime(TimeUnit.MILLISECONDS));

        }
    }

    public void close() {
        failSafeHttpClient.getClient().close();
    }

    private WeekResolverResult sendRequest(Params params) throws WeekresolverConnectorException {
        final HttpGet httpGet = new HttpGet(failSafeHttpClient)
                .withBaseUrl(String.format("%s/%s", baseUrl, params.toString()));
        final Response response = httpGet.execute();
        assertResponseStatus(response, Response.Status.OK);
        return readResponseEntity(response);
    }

    public static class Params {
        private String catalogueCode;
        private LocalDate date;

        public Params withCatalogueCode(String catalogueCode) {
            this.catalogueCode = InvariantUtil.checkNotNullOrThrow(catalogueCode, "catalogueCode");;
            return this;
        }

        public Params withDate(LocalDate date) {
            this.date = InvariantUtil.checkNotNullOrThrow(date, "date");
            return this;
        }

        public String toString(){
            return String.format(WEEKRESOLVER_URI_AND_PARMS, catalogueCode, date);
        }
    }


    private WeekResolverResult readResponseEntity(Response response) throws WeekresolverConnectorException {
        final WeekResolverResult entity = response.readEntity(WeekResolverResult.class);
        if (entity == null) {
            throw new WeekresolverConnectorException("Weekresolver service returned with null-valued %s entity");
        }
        return entity;
    }

    private void assertResponseStatus(Response response, Response.Status expectedStatus)
            throws WeekresolverUnexpectedStatusCodeException {
        final Response.Status actualStatus =
                Response.Status.fromStatusCode(response.getStatus());
        if (actualStatus != expectedStatus) {
            throw new WeekresolverUnexpectedStatusCodeException(
                    String.format("Weekresolver service returned with unexpected status code: %s",
                            actualStatus), actualStatus.getStatusCode());
        }
    }
}