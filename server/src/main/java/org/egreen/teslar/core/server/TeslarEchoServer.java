package org.egreen.teslar.core.server;

import org.egreen.teslar.core.server.handler.StaticFileHandler;
import org.glassfish.grizzly.http.server.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Created by dewmal on 11/19/14.
 */
public class TeslarEchoServer {
    private static final Logger logger = Logger.getLogger(TeslarEchoServer.class.getName());

    public static final String HOST = "0.0.0.0";
    public static final int PORT = 7777;

    public static void main(String[] args) throws IOException {


        HttpServer server = HttpServer.createSimpleServer();
        server.getServerConfiguration().addHttpHandler(
                new HttpHandler() {
                    public void service(Request request, Response response) throws Exception {
                        final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
                        final String date = format.format(new Date(System.currentTimeMillis()));
                        response.setContentType("text/plain");
                        response.setContentLength(date.length());
                        response.getWriter().write(date);
                    }
                },
                "/time");

        StaticHttpHandler staticFileHandler = new StaticHttpHandler();
        staticFileHandler.addDocRoot("web");
        staticFileHandler.setFileCacheEnabled(false);
        logger.info(staticFileHandler.getDefaultDocRoot().getAbsolutePath());
        server.getServerConfiguration().addHttpHandler(staticFileHandler, "/file/*");
        try {
            server.start();
            System.out.println("Press any key to stop the server...");
            System.in.read();
        } catch (Exception e) {
            System.err.println(e);
        }

    }
}
