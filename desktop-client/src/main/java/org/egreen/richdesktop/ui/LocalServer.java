package org.egreen.richdesktop.ui;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import java.io.File;

/**
 * Created by dewmal on 11/27/14.
 */
public class LocalServer {
    public static void mainRunServer() throws Exception {

        System.out.println(new File("./web-app").getAbsolutePath());

        String fileLocation="./web-app";
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[] { connector });
        ContextHandler context0 = new ContextHandler();
        context0.setContextPath("/");
        ResourceHandler rh0 = new ResourceHandler();
        rh0.setBaseResource(Resource.newResource(fileLocation));
        context0.setHandler(rh0);
        ContextHandler context1 = new ContextHandler();
        context1.setContextPath("/");
        ResourceHandler rh1 = new ResourceHandler();
        rh1.setBaseResource(Resource.newResource(fileLocation));
        context1.setHandler(rh1);
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { context0, context1 });
        server.setHandler(contexts);
        server.start();
        System.err.println(server.dump());
        server.join();
    }
}
