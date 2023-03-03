package dk.dbc.weekresolver;

import dk.dbc.httpclient.FailSafeHttpClient;
import dk.dbc.httpclient.HttpGet;
import dk.dbc.invariant.InvariantUtil;

import java.time.Duration;
import java.time.LocalDate;

import net.jodah.failsafe.RetryPolicy;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;
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
public class WeekResolverConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeekResolverConnector.class);
    public static final String WEEKRESOLVER_URI_AND_PARMS = "api/v1/date/%s/%s";
    private static final RetryPolicy<Response> RETRY_POLICY = new RetryPolicy<Response>()
            .handle(ProcessingException.class)
            .handleResultIf(response -> response.getStatus() == 404
                    || response.getStatus() == 500
                    || response.getStatus() == 502)
            .withDelay(Duration.ofSeconds(5))
            .withMaxRetries(3);

    private final FailSafeHttpClient failSafeHttpClient;
    private final String baseUrl;

    public WeekResolverConnector(Client httpClient, String baseUrl) {
        this(FailSafeHttpClient.create(httpClient, RETRY_POLICY), baseUrl);
    }

    /**
     * Returns new instance with custom retry policy
     *
     * @param failSafeHttpClient web resources client with custom retry policy
     * @param baseUrl            base URL for record service endpoint
     */
    public WeekResolverConnector(FailSafeHttpClient failSafeHttpClient, String baseUrl) {
        this.failSafeHttpClient = InvariantUtil.checkNotNullOrThrow(
                failSafeHttpClient, "failSafeHttpClient");
        this.baseUrl = InvariantUtil.checkNotNullNotEmptyOrThrow(
                baseUrl, "baseUrl");
    }

    @SuppressWarnings("unused")
    public WeekResolverResult getWeekCode(String catalogueCode) throws WeekResolverConnectorException {
        final Stopwatch stopwatch = new Stopwatch();
        try {
            Params params = new Params()
                    .withCatalogueCode(catalogueCode)
                    .withDate(LocalDate.now());

            return sendRequest(params);
        } finally {
            LOGGER.info("getWeekCode took {} ms", stopwatch.getElapsedTime(TimeUnit.MILLISECONDS));

        }
    }

    public WeekResolverResult getWeekCode(String catalogueCode, LocalDate date) throws WeekResolverConnectorException {
        final Stopwatch stopwatch = new Stopwatch();
        try {
            Params params = new Params()
                    .withCatalogueCode(catalogueCode)
                    .withDate(date);

            return sendRequest(params);
        } finally {
            LOGGER.info("getWeekCode took {} ms", stopwatch.getElapsedTime(TimeUnit.MILLISECONDS));

        }
    }

    public void close() {
        failSafeHttpClient.getClient().close();
    }

    private WeekResolverResult sendRequest(Params params) throws WeekResolverConnectorException {
        final HttpGet httpGet = new HttpGet(failSafeHttpClient)
                .withBaseUrl(String.format("%s/%s", baseUrl, params.toString()));
        final Response response = httpGet.execute();
        assertResponseStatus(response);
        return readResponseEntity(response);
    }

    public static class Params {
        private String catalogueCode;
        private LocalDate date;

        public Params withCatalogueCode(String catalogueCode) {
            this.catalogueCode = InvariantUtil.checkNotNullOrThrow(catalogueCode, "catalogueCode");
            return this;
        }

        public Params withDate(LocalDate date) {
            this.date = InvariantUtil.checkNotNullOrThrow(date, "date");
            return this;
        }

        public String toString() {
            return String.format(WEEKRESOLVER_URI_AND_PARMS, catalogueCode, date);
        }
    }


    private WeekResolverResult readResponseEntity(Response response) throws WeekResolverConnectorException {
        final WeekResolverResult entity = response.readEntity(WeekResolverResult.class);
        if (entity == null) {
            throw new WeekResolverConnectorException("Weekresolver service returned with null-valued %s entity");
        }
        return entity;
    }

    private void assertResponseStatus(Response response)
            throws WeekResolverUnexpectedStatusCodeException {
        final Response.Status actualStatus =
                Response.Status.fromStatusCode(response.getStatus());
        if (actualStatus != Response.Status.OK) {
            throw new WeekResolverUnexpectedStatusCodeException(
                    String.format("Weekresolver service returned with unexpected status code: %s",
                            actualStatus), actualStatus.getStatusCode());
        }
    }
}
