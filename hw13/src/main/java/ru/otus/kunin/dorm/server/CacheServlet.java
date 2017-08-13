package ru.otus.kunin.dorm.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ru.otus.kunin.dicache.base.DiCache;
import ru.otus.kunin.dorm.base.DormImpl;
import ru.otus.kunin.dorm.templates.TemplateProcessor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Predicates.not;


public class CacheServlet extends HttpServlet {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final LinkedList<String> logs = new LinkedList<>();

  @Autowired
  private DiCache<String, String> diCache;

  @Autowired
  private DormImpl dorm;

  @Override
  public void init() throws ServletException {
    SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    Preconditions.checkNotNull(diCache);
    // We don't actually use database here, but to satisfy
    // requirements of HW13 check that DB part is started up
    Preconditions.checkNotNull(dorm);
  }

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    final ImmutableMap.Builder<String, Object> dataModelBuilder = new ImmutableMap.Builder();

    if ("read".equals(req.getParameter("action"))) {
      final Optional<String> keyToRead = Optional.ofNullable(req.getParameter("read_key"));
      logs.add(0, "reading " + keyToRead);
      keyToRead.ifPresent(key -> {
        final Optional<String> value = Optional.ofNullable(diCache.get(key));
        dataModelBuilder.put("read_key", key);
        dataModelBuilder.put("result_read", value.orElse("<not in cache>"));
      });
    }

    Map<String, Object> stats = OBJECT_MAPPER.convertValue(diCache.getStats(), Map.class);
    dataModelBuilder.put("stats", stats.entrySet());
    dataModelBuilder.put("logs", logs);
    render(resp, dataModelBuilder.build());
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    final ImmutableMap.Builder<String, Object> dataModelBuilder = new ImmutableMap.Builder();

    if ("write".equals(req.getParameter("action"))) {
      final Optional<String> keyToWrite = Optional.ofNullable(req.getParameter("new_key"));
      final Optional<String> valueToWrite = Optional.ofNullable(req.getParameter("new_value"));
      logs.add(0, "writing '" + keyToWrite + "':'" + valueToWrite + "'");
      if (keyToWrite.filter(not(String::isEmpty)).isPresent() && valueToWrite.filter(not(String::isEmpty)).isPresent()) {
        diCache.put(keyToWrite.get(), valueToWrite.get());
      }
    }

    Map<String, Object> stats = OBJECT_MAPPER.convertValue(diCache.getStats(), Map.class);
    dataModelBuilder.put("stats", stats.entrySet());
    dataModelBuilder.put("logs", logs);
    render(resp, dataModelBuilder.build());
  }

  private void render(final HttpServletResponse resp, final Map<String, Object> dataModel) throws IOException {
    final String page = TemplateProcessor.instance().getPage("cache.ftl", dataModel);
    resp.getWriter().println(page);
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.setContentType("text/html;charset=utf-8");
  }
}
