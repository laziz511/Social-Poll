package com.dev.socialPoll.exception;

import java.sql.SQLException;

public class ConnectionException extends SQLException {
    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException() {

    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionException(Throwable cause) {
        super(cause);
    }
}