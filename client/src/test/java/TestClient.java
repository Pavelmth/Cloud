import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import clientApp.Controller;
import clientApp.UserFiles;
import clientApp.UserFilesStatic;

public class TestClient {
    UserFiles userFiles;
    UserFilesStatic userFilesStatic;
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
    public void MyTest2() {
        userFilesStatic = new UserFilesStatic();
        userFilesStatic.getUseFiles();
    }

    @Test
    public void Controller() {
        controller = new Controller();
        controller.connect();
    }
}
