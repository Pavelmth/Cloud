package cloudNettyServer;

public class StartNettyServer {
    public static void main(String[] args) {
        try {
            new NettyServer().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
