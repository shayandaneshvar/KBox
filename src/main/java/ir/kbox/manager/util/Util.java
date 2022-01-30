package ir.kbox.manager.util;

import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletRequest;

@UtilityClass
public class Util {
    public String getRequestBaseAddress(HttpServletRequest req) {
        return req.getRequestURL()
                .toString()
                .replace(req.getRequestURI(), "");
    }
}
