package ro.agitman.atalk.server;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by d-uu31cq on 22.03.2016.
 */

@WebServlet("/users")
public class InfoResource extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");

        // Actual logic goes here.
        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        for (String name : MemCash.getInstance().getSessions().values()) {
            sb.append(name).append("\n\r");
        }

        out.println(sb.toString());
    }

}
