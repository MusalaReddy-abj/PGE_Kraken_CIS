package com.pge.kraken.cis.utils;

import java.io.IOException;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class RetryUtil {

    private RetryUtil() {
    }

    public static <T> T executeWithRetry(Callable<T> action,
                                         int maxAttempts,
                                         Duration delay,
                                         Predicate<Throwable> retryPredicate,
                                         Consumer<RetryContext> onRetry) throws Exception {
        Objects.requireNonNull(action, "action cannot be null");
        Objects.requireNonNull(delay, "delay cannot be null");
        Objects.requireNonNull(retryPredicate, "retryPredicate cannot be null");

        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be at least 1");
        }

        int attempt = 1;
        while (true) {
            try {
                return action.call();
            } catch (Exception e) {
                if (attempt >= maxAttempts || !retryPredicate.test(e)) {
                    throw e;
                }
                if (onRetry != null) {
                    onRetry.accept(new RetryContext(attempt, maxAttempts, e));
                }
                Thread.sleep(delay.toMillis());
                attempt++;
            }
        }
    }

    public static boolean isRetryableHttpStatusCode(int statusCode) {
        return statusCode == 429 || (statusCode >= 500 && statusCode <= 599);
    }

    public static boolean isRetryableException(Throwable throwable) {
        return findCause(throwable, cause -> cause instanceof IOException
                || cause instanceof HttpTimeoutException
                || cause instanceof RetryableHttpStatusCodeException) != null;
    }

    public static Throwable findCause(Throwable throwable, Predicate<Throwable> predicate) {
        while (throwable != null) {
            if (predicate.test(throwable)) {
                return throwable;
            }
            throwable = throwable.getCause();
        }
        return null;
    }

    public static RetryableHttpStatusCodeException findRetryableHttpStatusCodeException(Throwable throwable) {
        Throwable found = findCause(throwable, cause -> cause instanceof RetryableHttpStatusCodeException);
        return found instanceof RetryableHttpStatusCodeException ? (RetryableHttpStatusCodeException) found : null;
    }

    public static final class RetryableHttpStatusCodeException extends Exception {
        private final int statusCode;
        private final String responseBody;

        public RetryableHttpStatusCodeException(int statusCode, String responseBody) {
            super(String.format("Retryable HTTP response status %d", statusCode));
            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getResponseBody() {
            return responseBody;
        }
    }

    public static final class RetryContext {
        private final int attempt;
        private final int maxAttempts;
        private final Throwable exception;

        public RetryContext(int attempt, int maxAttempts, Throwable exception) {
            this.attempt = attempt;
            this.maxAttempts = maxAttempts;
            this.exception = exception;
        }

        public int getAttempt() {
            return attempt;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public Throwable getException() {
            return exception;
        }
    }
}
