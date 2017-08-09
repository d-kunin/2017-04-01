package ru.otus.kunin.dorm.server;

import ru.otus.kunin.dicache.base.DiCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CacheServlet extends HttpServlet {

  private DiCache<?, ?> diCache;

  public CacheServlet(final DiCache<?,?> diCache) {
    this.diCache = diCache;
  }

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    resp.getWriter().println(diCache.getStats().toString());
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.setContentType("text/html;charset=utf-8");
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

  }
}
