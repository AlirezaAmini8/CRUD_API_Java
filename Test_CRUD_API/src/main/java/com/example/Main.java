package com.example;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.v3.jaxrs2.integration.OpenApiServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try {
            Resource.setDefaultUseCaches(false);

            buildSwagger();

            final HandlerList handlers = new HandlerList();

            handlers.addHandler(buildSwaggerUI());

            handlers.addHandler(buildContext());

            Server server = new Server(SERVER_PORT);
            server.setHandler(handlers);
            server.start();
            server.join();
        } catch (Exception e) {
            LOG.error("There was an error starting the server", e);
        }
    }

    private static void buildSwagger() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.0");
        beanConfig.setResourcePackage("com.example.view");
        beanConfig.setScan(true);
        beanConfig.setBasePath("/");
        beanConfig.setDescription("Swagger API documentation");
        beanConfig.setTitle("Swagger Example");
    }

    private static ContextHandler buildContext() {
        ServletContextHandler  context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(1);
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "com.example.view");

        // Add Swagger UI servlet
        ServletHolder swaggerServlet = context.addServlet(OpenApiServlet.class, "/swagger-ui/*");
        swaggerServlet.setInitOrder(2);
        swaggerServlet.setInitParameter("openApi.configuration.resourcePackages",
                "com.example.view");
        return context;
    }

    private static ContextHandler buildSwaggerUI() throws Exception {
        final ResourceHandler swaggerUIResourceHandler = new ResourceHandler();
        swaggerUIResourceHandler.setResourceBase(Main.class.getClassLoader()
                .getResource("META-INF/resources/webjars/swagger-ui/3.50.0")
                .toURI().toString());
        final ContextHandler swaggerUIContext = new ContextHandler();
        swaggerUIContext.setContextPath("/swagger-ui");
        swaggerUIContext.setHandler(swaggerUIResourceHandler);

        return swaggerUIContext;
    }
}
