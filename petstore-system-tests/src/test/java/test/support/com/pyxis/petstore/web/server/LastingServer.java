package test.support.com.pyxis.petstore.web.server;

public class LastingServer implements ServerLifeCycle {

    private final SingleWebApp shared;

    public LastingServer(ServerProperties properties) {
        this.shared = new SingleWebApp(properties);
    }

    public ServerDriver start() {
        shared.start();
        return shared;
    }

    public void stop(ServerDriver server)  {
        shared.stopOnShutdown();
    }
}
