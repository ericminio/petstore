package test.support.com.pyxis.petstore.web.server;

import org.testinfected.hamcrest.ExceptionImposter;
import test.support.com.pyxis.petstore.Properties;

import java.net.MalformedURLException;
import java.net.URL;

public class ServerProperties {

    public static final String LIFECYCLE = "server.lifecycle";
    public static final String SCHEME = "server.scheme";
    public static final String HOST = "server.host";
    public static final String PORT = "server.port";
    public static final String CONTEXT_PATH = "server.context.path";
    public static final String WEBAPP_PATH = "server.webapp.path";

    private final Properties properties;

    public ServerProperties(Properties properties) {
        this.properties = properties;
    }

    public String scheme() {
        return properties.getValue(SCHEME);
    }

    public String host() {
        return properties.getValue(HOST);
    }

    public int port() {
        return properties.getValueAsInt(PORT);
    }

    public String contextPath() {
        return properties.getValue(CONTEXT_PATH);
    }

    public String webAppPath() {
        return properties.getValue(WEBAPP_PATH);
    }

    public String lifeCycle() {
        return properties.getValue(LIFECYCLE);
    }

    public URL urlFor(String path) {
        try {
            return new URL(String.format("%s://%s:%s%s%s", scheme(), host(), port(), contextPath(), path));
        } catch (MalformedURLException e) {
            throw ExceptionImposter.imposterize(e);
        }
    }
}
