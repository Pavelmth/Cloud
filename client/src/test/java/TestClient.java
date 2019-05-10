import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import clientApp.Controller;
import clientApp.UserFiles;

public class TestClient {
    Controller controller;

    @Before
    public void MyTestBefore() {}

    @Ignore
    @Test
    public void Controller() {
        controller = new Controller();
    }
}
