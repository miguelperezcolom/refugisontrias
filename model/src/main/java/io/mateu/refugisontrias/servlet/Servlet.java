package io.mateu.refugisontrias.servlet;

import io.mateu.refugisontrias.model.util.Helper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by miguel on 1/3/17.
 */
@WebServlet(urlPatterns = {"/servidor/*"})
public class Servlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("uri:" + req.getRequestURI());
        System.out.println("url:" + req.getRequestURL());

        String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        System.out.println(body);

        Map<String, Object> m = Helper.fromJson(body);

        String uri = req.getRequestURI();
        try {

            if (uri.endsWith("combopax")) {
                resp.getWriter().print(Logic.comboPax(m));
            } else if (uri.endsWith("checkdispo")) {
                resp.getWriter().print(Logic.checkDisponibilidad(m));
            } else if (uri.endsWith("entradas")) {
                resp.getWriter().print(Logic.getEntradasAlternativas(m));
            } else if (uri.endsWith("confirmar")) {
                resp.getWriter().print(Logic.confirmar(m));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

}
