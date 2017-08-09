package ru.otus.kunin.dorm.server;

import com.google.common.io.Closer;
import com.zaxxer.hikari.HikariDataSource;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import ru.otus.kunin.dorm.base.CachingDorm;
import ru.otus.kunin.dorm.base.DormImpl;
import ru.otus.kunin.dorm.base.FieldMapperImpl;
import ru.otus.kunin.dorm.base.ResultSetMapperImpl;
import ru.otus.kunin.dorm.base.SqlGeneratorImpl;
import ru.otus.kunin.dorm.base.TypeMapperImpl;
import ru.otus.kunin.dorm.main.Connector;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

public class DormApplication implements Closeable {

  private final CachingDorm cachingDorm;
  private final Closer closer = Closer.create();


  public static void main(String[] args) throws Exception {
    try (DormApplication dormApplication = new DormApplication()) {
      dormApplication.start();
    }
  }

  public DormApplication() {
    try {
      final HikariDataSource hikariDataSource = closer.register(
          Connector.createHikariDataSource());
      cachingDorm = closer.register(new CachingDorm(
          new DormImpl(
              hikariDataSource.getConnection(),
              new TypeMapperImpl(new FieldMapperImpl()),
              new SqlGeneratorImpl(),
              new ResultSetMapperImpl())));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  void start() throws Exception {
    final ServletContextHandler context =
        new ServletContextHandler(ServletContextHandler.SESSIONS | ServletContextHandler.SECURITY);

    context.setContextPath("/");
    context.addServlet(WelcomeServlet.class, "");
    context.addServlet(LoginServlet.class, "/login");
    context.addServlet(new ServletHolder(new CacheServlet(cachingDorm.getCache())), "/stats");
    context.setSecurityHandler(createSecurityHandler());

    final Server server = new Server(8081);
    server.setHandler(context);
    server.start();
    server.join();
  }

  @Override
  public void close() throws IOException {
    closer.close();
  }

  private static ConstraintSecurityHandler createSecurityHandler() {
    final Constraint constraint = new Constraint(Constraint.__FORM_AUTH, "user");
    constraint.setAuthenticate(true);

    final ConstraintMapping constraintMapping = new ConstraintMapping();
    constraintMapping.setConstraint(constraint);
    constraintMapping.setPathSpec("/*");

    final ConstraintSecurityHandler constraintSecurityHandler = new ConstraintSecurityHandler();
    constraintSecurityHandler.addConstraintMapping(constraintMapping);
    constraintSecurityHandler.setLoginService(
        new HashLoginService("LOGIN", "src/main/resources/realm.properties"));
    constraintSecurityHandler.setAuthenticator(
        new FormAuthenticator("/login", "/login?failed", false)
    );
    return constraintSecurityHandler;
  }
}
