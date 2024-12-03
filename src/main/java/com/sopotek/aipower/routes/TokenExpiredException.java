package com.sopotek.aipower.routes;

/**
 * Custom exception to handle scenarios where a token has expired.
 */
public class TokenExpiredException extends Exception {

    /**
     * Constructor with a custom error message.
     *
     * @param message The error message describing the exception.
     */
    public TokenExpiredException(String message) {
        super(message);
    }

    /**
     * Constructor with a custom error message and a cause.
     *
     * @param message The error message describing the exception.
     * @param cause   The cause of the exception (another Throwable).
     */
    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Default constructor with a predefined error message.
     */
    public TokenExpiredException() {
        super("The token has expired.");
    }
}
