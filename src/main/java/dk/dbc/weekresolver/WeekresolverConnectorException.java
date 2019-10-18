/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.weekresolver;

public class WeekresolverConnectorException extends Exception {
    public WeekresolverConnectorException(String message) {
        super(message);
    }

    public WeekresolverConnectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
