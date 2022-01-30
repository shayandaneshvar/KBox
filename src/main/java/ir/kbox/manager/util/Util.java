package ir.kbox.manager.util;

import javax.servlet.http.HttpServletRequest;

public final class Util {
    private Util() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String getRequestBaseAddress(HttpServletRequest req) {
        return req.getRequestURL()
                .toString()
                .replace(req.getRequestURI(), "");
    }

    public static Runnable throwException(RuntimeException exception) {
        throw exception;
    }
}
