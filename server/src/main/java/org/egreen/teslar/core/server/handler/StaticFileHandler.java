package org.egreen.teslar.core.server.handler;

import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.StaticHttpHandlerBase;
import org.glassfish.grizzly.http.util.Header;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.grizzly.utils.ArraySet;

import java.io.File;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dewmal on 11/19/14.
 */
public class StaticFileHandler extends StaticHttpHandlerBase {
    private static final Logger LOGGER = Grizzly.logger(StaticFileHandler.class);

    protected final ArraySet<File> docRoots = new ArraySet<File>(File.class);

    /**
     * Create <tt>HttpHandler</tt>, which, by default, will handle requests
     * to the static resources located in the current directory.
     */
    public StaticFileHandler() {
        addDocRoot(".");
    }


    /**
     * Create a new instance which will look for static pages located
     * under the <tt>docRoot</tt>. If the <tt>docRoot</tt> is <tt>null</tt> -
     * static pages won't be served by this <tt>HttpHandler</tt>
     *
     * @param docRoots the folder(s) where the static resource are located.
     *                 If the <tt>docRoot</tt> is <tt>null</tt> - static pages won't be served
     *                 by this <tt>HttpHandler</tt>
     */
    public StaticFileHandler(String... docRoots) {
        if (docRoots != null) {
            for (String docRoot : docRoots) {
                addDocRoot(docRoot);
            }
        }
    }

    /**
     * Create a new instance which will look for static pages located
     * under the <tt>docRoot</tt>. If the <tt>docRoot</tt> is <tt>null</tt> -
     * static pages won't be served by this <tt>HttpHandler</tt>
     *
     * @param docRoots the folders where the static resource are located.
     *                 If the <tt>docRoot</tt> is empty - static pages won't be served
     *                 by this <tt>HttpHandler</tt>
     */
    @SuppressWarnings("UnusedDeclaration")
    public StaticFileHandler(Set<String> docRoots) {
        if (docRoots != null) {
            for (String docRoot : docRoots) {
                addDocRoot(docRoot);
            }
        }
    }

    /**
     * Return the default directory from where files will be serviced.
     *
     * @return the default directory from where file will be serviced.
     */
    @SuppressWarnings("UnusedDeclaration")
    public File getDefaultDocRoot() {
        final File[] array = docRoots.getArray();
        return (array != null && array.length > 0) ? array[0] : null;
    }

    /**
     * Return the list of directories where files will be serviced from.
     *
     * @return the list of directories where files will be serviced from.
     */
    public ArraySet<File> getDocRoots() {
        return docRoots;
    }

    /**
     * Add the directory to the list of directories where files will be serviced from.
     *
     * @param docRoot the directory to be added to the list of directories
     *                where files will be serviced from.
     * @return return the {@link File} representation of the passed <code>docRoot</code>.
     */
    public final File addDocRoot(String docRoot) {
        if (docRoot == null) {
            throw new NullPointerException("docRoot can't be null");
        }

        final File file = new File(docRoot);
        addDocRoot(file);

        return file;
    }

    /**
     * Add the directory to the list of directories where files will be serviced from.
     *
     * @param docRoot the directory to be added to the list of directories
     *                where files will be serviced from.
     */
    public final void addDocRoot(File docRoot) {
        docRoots.add(docRoot);
    }

    /**
     * Removes the directory from the list of directories where static files will be serviced from.
     *
     * @param docRoot the directory to remove.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void removeDocRoot(File docRoot) {
        docRoots.remove(docRoot);
    }


    // ------------------------------------------------------- Protected Methods


    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean handle(final String uri,
                             final Request request,
                             final Response response) throws Exception {

        boolean found = false;

        final File[] fileFolders = docRoots.getArray();
        if (fileFolders == null) {
            return false;
        }

        File resource = null;

        for (int i = 0; i < fileFolders.length; i++) {
            final File webDir = fileFolders[i];
            // local file
            resource = new File(webDir, uri);
            final boolean exists = resource.exists();
            final boolean isDirectory = resource.isDirectory();

            if (exists && isDirectory) {
                final File f = new File(resource, "/index.html");
                if (f.exists()) {
                    resource = f;
                    found = true;
                    break;
                }
            }

            if (isDirectory || !exists) {
                found = false;
            } else {
                found = true;
                break;
            }
        }

        if (!found) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "File not found {0}", resource);
            }
            return false;
        }

        assert resource != null;

        // If it's not HTTP GET - return method is not supported status
        if (!Method.GET.equals(request.getMethod())) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "File found {0}, but HTTP method {1} is not allowed",
                        new Object[]{resource, request.getMethod()});
            }
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED_405);
            response.setHeader(Header.Allow, "GET");
            return true;
        }

        pickupContentType(response, resource.getPath());

        addToFileCache(request, response, resource);
        sendFile(response, resource);

        return true;
    }
}
