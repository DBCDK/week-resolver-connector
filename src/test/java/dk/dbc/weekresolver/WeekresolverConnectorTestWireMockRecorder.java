/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.weekresolver;

public class WeekresolverConnectorTestWireMockRecorder {
        /*
        Steps to reproduce wiremock recording:

        * Start standalone runner
            java -jar wiremock-standalone-{WIRE_MOCK_VERSION}.jar --proxy-all="{WEEKRESOLVER_SERVICE_HOST}" --record-mappings --verbose

        * Run the main method of this class

        * Replace content of src/test/resources/{__files|mappings} with that produced by the standalone runner
     */

    public static void main(String[] args) throws Exception {
        WeekResolverConnectorTest.connector = new WeekResolverConnector(
                WeekResolverConnectorTest.CLIENT, "http://localhost:8080");
        final WeekResolverConnectorTest weekResolverConnectorTest = new WeekResolverConnectorTest();
        recordGetApplicantRequests(weekResolverConnectorTest);
    }

    private static void recordGetApplicantRequests(WeekResolverConnectorTest weekResolverConnectorTest)
            throws WeekResolverConnectorException {
        weekResolverConnectorTest.testGetWeekcode();
    }

}
