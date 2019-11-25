/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.weekresolver;

public class WeekResolverConnectorException extends Exception {
    public WeekResolverConnectorException(String message) {
        super(message);
    }

    public WeekResolverConnectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
