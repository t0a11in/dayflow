package cl.dayflow.api.shared.exception;

import cl.dayflow.api.shared.config.TraceIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Centralizes error logging without exposing implementation details or sensitive data to API clients.
 */
@Component
public class RequestErrorLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestErrorLogger.class);

    public void logExpected(
            String code, Exception exception, HttpServletRequest request, int status) {
        LOGGER.warn(
                "API request failed: status={} code={} traceId={} method={} path={} query={}",
                status,
                code,
                traceId(request),
                request.getMethod(),
                request.getRequestURI(),
                queryParameters(request));
    }

    public void logUnexpected(Exception exception, HttpServletRequest request) {
        LOGGER.error(
                "Unexpected API error: traceId={} method={} path={} query={}",
                traceId(request),
                request.getMethod(),
                request.getRequestURI(),
                queryParameters(request),
                exception);
    }

    private String traceId(HttpServletRequest request) {
        return String.valueOf(request.getAttribute(TraceIdFilter.TRACE_ID_ATTRIBUTE));
    }

    private String queryParameters(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters.isEmpty()) {
            return "{}";
        }
        return parameters.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
