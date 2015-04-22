package com.unigames.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorHandler extends HttpServlet {
    private static final long serialVersionUID = -4118197060919475063L;
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Throwable throwable = null;
        Object obj = req.getAttribute("javax.servlet.error.exception");
        if (obj != null) {
            if (obj instanceof ServletException) {
                throwable = ((ServletException) obj).getRootCause();
            } else if (obj instanceof Throwable) {
                throwable = (Throwable) obj;
            }
        }
        String msg;
        if (throwable != null) {
            msg = "[service] Throwable message:" + throwable.getMessage();
            logger.warn(msg);
            logger.debug(msg, throwable);
        } else {
            msg = "[service] Servlet error message:" + req.getAttribute("javax.servlet.error.message");
            logger.warn(msg);
        }

        resp.getOutputStream().print(msg);
    }
}
