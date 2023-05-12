package com.testing;

public class ScenarioException extends Exception{
    /**
     * Constructs an {@code ScenarioException} with {@code null}
     * as its error detail message.
     */
    public ScenarioException() {
        super();
    }

    /**
     * Constructs an {@code ScenarioException} with the specified detail message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     */
    public ScenarioException(String message) {
        super(message);
    }
}
