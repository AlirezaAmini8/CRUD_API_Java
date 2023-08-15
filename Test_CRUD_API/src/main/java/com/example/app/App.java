package com.example.app;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App
{
    private static final Logger LOG = LoggerFactory.getLogger( App.class );

    private static final int SERVER_PORT = 8080;


    public static void main( String[] args )
    {
        try {
            Resource.setDefaultUseCaches( false );

            buildSwagger();

            final HandlerList handlers = new HandlerList();

            handlers.addHandler( buildSwaggerUI() );

            handlers.addHandler( buildContext() );

            Server server = new Server( SERVER_PORT );
            server.setHandler( handlers );
            server.start();
            server.join();
        } catch ( Exception e ) {
            LOG.error( "There was an error starting up the Entity Browser", e );
        }
    }


    private static void buildSwagger()
    {
        // This configures Swagger
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion( "1.0.0" );
        beanConfig.setResourcePackage("com.example.resources");
        beanConfig.setScan( true );
        beanConfig.setBasePath( "/" );
        beanConfig.setDescription( "Entity Browser API to demonstrate Swagger with Jersey2 in an "
                + "embedded Jetty instance, with no web.xml or Spring MVC." );
        beanConfig.setTitle( "Entity Browser" );
    }


    private static ContextHandler buildContext()
    {
        ResourceConfig resourceConfig = new ResourceConfig();

        resourceConfig.packages( "com.example.resources", ApiListingResource.class.getPackage().getName() );
        ServletContainer servletContainer = new ServletContainer( resourceConfig );
        ServletHolder entityBrowser = new ServletHolder( servletContainer );
        ServletContextHandler entityBrowserContext = new ServletContextHandler( ServletContextHandler.SESSIONS );
        entityBrowserContext.setContextPath( "/" );
        entityBrowserContext.addServlet( entityBrowser, "/*" );

        return entityBrowserContext;
    }


    // This starts the Swagger UI at http://localhost:8080/swagger-ui
    private static ContextHandler buildSwaggerUI() throws Exception
    {
        final ResourceHandler swaggerUIResourceHandler = new ResourceHandler();
        swaggerUIResourceHandler.setResourceBase( App.class.getClassLoader().getResource( "swaggerui" ).toURI().toString() );
        final ContextHandler swaggerUIContext = new ContextHandler();
        swaggerUIContext.setContextPath( "/swagger-ui/" );
        swaggerUIContext.setHandler( swaggerUIResourceHandler );

        return swaggerUIContext;
    }
}