package ru.otus.kunin.dorm.server;

import com.google.common.collect.ImmutableMap;
import org.eclipse.jetty.servlet.DefaultServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WelcomeServlet extends DefaultServlet {

  @Override
  protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    final ImmutableMap<String, Object> dataModel =
        ImmutableMap.of("user", request.getUserPrincipal().getName());
    response.getWriter()
        .println(TemplateProcessor.instance().getPage("welcome.html", dataModel));
    response.setContentType("text/html;charset=utf-8");
    response.setStatus(HttpServletResponse.SC_OK);
  }
}
