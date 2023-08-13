package com.example;

import io.swagger.v3.jaxrs2.integration.OpenApiServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(1);
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "com.example.view");

        // Add Swagger UI servlet
        ServletHolder swaggerServlet = context.addServlet(OpenApiServlet.class, "/swagger-ui/*");
        swaggerServlet.setInitOrder(2);
        swaggerServlet.setInitParameter("openApi.configuration.resourcePackages",
                "com.example.view");

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}