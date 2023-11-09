package io.karma.bts.client.shader.prepro;

public final class PreProcessorException extends RuntimeException {
    public PreProcessorException() {
        super();
    }

    public PreProcessorException(final String message) {
        super(message);
    }

    public PreProcessorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PreProcessorException(final Throwable cause) {
        super(cause);
    }

    public PreProcessorException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PreProcessorException(final String fmt, final Object... params) {
        this(String.format(fmt, params));
    }
}
