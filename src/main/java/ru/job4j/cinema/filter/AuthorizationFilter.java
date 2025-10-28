package ru.job4j.cinema.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
@Order(1)
public class AuthorizationFilter extends HttpFilter {

    /* Решил использовать встроенный в JDK логер чтобы не "заморачиваться" с зависимостями и настройками */
    private Logger logger = Logger.getLogger(AuthorizationFilter.class.getName());

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        var uri = request.getRequestURI();
        if (isAlwaysPermitted(uri)) {
            chain.doFilter(request, response);
            return;
        }
        var session = request.getSession();
        var userLoggedIn = session.getAttribute("user") != null;
        /* Запоминаем id кинопоказа на который хотел купить билет не залогиненный пользователь */
        if (!userLoggedIn && uri.startsWith("/tickets/")) {
            rememberFilmSessionId(uri, session);
        }
        /* Перенаправляем не залогиненного пользователя чтобы он залогинился */
        if (!userLoggedIn) {
            var loginPageUrl = request.getContextPath() + "/users/login";
            response.sendRedirect(loginPageUrl);
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isAlwaysPermitted(String uri) {
        return uri.startsWith("/users/register")
                || uri.startsWith("/users/login")
                || uri.startsWith("/movies")
                || uri.startsWith("/sessions")
                || uri.startsWith("/files")
                || uri.startsWith("/index")
                || uri.startsWith("/js")
                || uri.startsWith("/css")
                || uri.equals("/");
    }

    private void rememberFilmSessionId(String uri, HttpSession session) {
        String[] partsOfUri = uri.split("/");
        int filmSessionId = 0;
        try {
            filmSessionId = Integer.parseInt(partsOfUri[partsOfUri.length - 1]);
        } catch (NumberFormatException | NullPointerException e) {
            logger.info(e.getMessage());
        }
        if (filmSessionId > 0) {
            session.setAttribute("filmSessionId", filmSessionId);
        }
    }
}
