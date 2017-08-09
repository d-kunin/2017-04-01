package ru.otus.kunin.dorm.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import ru.otus.kunin.dicache.base.DiCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


public class CacheServlet extends HttpServlet {

  private DiCache<?, ?> diCache;

  public CacheServlet(final DiCache<?, ?> diCache) {
    this.diCache = diCache;
  }

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {


    ObjectMapper m = new ObjectMapper();
    Map<String, Object> stats = m.convertValue(diCache.getStats(), Map.class);

    final ImmutableMap<String, Object> data = ImmutableMap.of(
        "stats", stats.entrySet()
    );
    
    final String page = TemplateProcessor.instance().getPage("stats.ftl", data);
    resp.getWriter().println(page);
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.setContentType("text/html;charset=utf-8");
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

  }
}
