package io.mateu.refugisontrias.servlet;

import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import io.mateu.refugisontrias.model.googlecalendar.Receiver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.locks.Lock;

/**
 * Created by miguel on 26/5/17.
 */
@WebServlet(urlPatterns = {"/oauth2callback"})
public class OAuthCallbackServlet extends HttpServlet {


    public static Receiver receiver;


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("OAUTH2: received callback");
        System.out.println("uri:" + req.getRequestURI());
        System.out.println("url:" + req.getRequestURL());

        String error = req.getParameter("error");
        String code = req.getParameter("code");

        if (receiver != null) {
            System.out.println("OAUTH2: receiver is not null");

            receiver.error = req.getParameter("error");
            receiver.code = req.getParameter("code");


            receiver = null;

            response.setStatus(200);
            response.setContentType("text/html");
            PrintWriter doc = response.getWriter();
            doc.println("<html>");
            doc.println("<head><title>OAuth 2.0 Authentication Token Received</title></head>");
            doc.println("<body>");
            doc.println("Received verification code. You may now close this window...");
            doc.println("</body>");
            doc.println("</HTML>");
            doc.flush();

        } else {

            System.out.println("OAUTH2: receiver is null");

            response.setStatus(500);
            response.setContentType("text/html");
            PrintWriter doc = response.getWriter();
            doc.println("<html>");
            doc.println("<head><title>OAuth 2.0 Authentication Token Received but no one was expecting it!</title></head>");
            doc.println("<body>");
            doc.println("Received verification code but nothing to do. You may now close this window...");
            doc.println("</body>");
            doc.println("</HTML>");
            doc.flush();

        }

    }
}
