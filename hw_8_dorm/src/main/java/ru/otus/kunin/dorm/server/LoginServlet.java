package ru.otus.kunin.dorm.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class LoginServlet extends HttpServlet {

  @Override
  protected void doGet(final HttpServletRequest req,
                       final HttpServletResponse resp) throws ServletException, IOException {

    final PrintWriter writer = resp.getWriter();
    final String failed = req.getParameter("failed");
    writer.write("<!DOCTYPE html>\n" +
                     "<html>\n" +
                     "<head>\n" +
                     "    <title></title>\n" +
                     "</head>\n" +
                     "<body>\n" +
                     (null == failed ? "" : "<p>try 'tully'</p><br/>") +
                     "<form method='POST' action='/j_security_check'>\n" +
                     "    <input type='text' name='j_username' value='tully'/><br/>\n" +
                     "    <input type='password' name='j_password' value='tully'/><br/>\n" +
                     "    <input type='submit' value='Login'/>\n" +
                     "</form>\n" +
                     "</body>\n" +
                     "</html>");
    resp.setContentType("text/html;charset=utf-8");
    resp.setStatus(HttpServletResponse.SC_OK);
  }

}
