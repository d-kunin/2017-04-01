package ru.otus.kunin.dorm.server;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.security.Constraint;

public class DormServerRunner {

  public static void main(String[] args) throws Exception {

    final ServletContextHandler context =
        new ServletContextHandler(ServletContextHandler.SESSIONS | ServletContextHandler.SECURITY);

    context.setContextPath("/");
    context.addServlet(WelcomeServlet.class, "");
    context.addServlet(LoginServlet.class, "/login");
    context.addServlet(CacheStatsServlet.class, "/stats");
    context.setSecurityHandler(createSecurityHandler());

    final Server server = new Server(8081);
    server.setHandler(context);
    server.start();
    server.join();
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
