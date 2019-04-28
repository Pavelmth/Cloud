import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import clientApp.Controller;
import clientApp.UserFiles;

public class TestClient {
    UserFiles userFiles;
    Controller controller;

    @Before
    public void MyTestBefore() {}

    @Ignore
    @Test
    public void MyTest() {
        userFiles = new UserFiles();
        userFiles.scanFiles();
        userFiles.printFiles();
    }

    @Ignore
    @Test
    public void Controller() {
        controller = new Controller();
    }
}
