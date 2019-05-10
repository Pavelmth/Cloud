package clientApp.protocol;

import java.io.File;

public class DeleteClientFile {
    private final File USER_FOLDER = new File("client/folder/");

    public DeleteClientFile(String fileName) {
        File detetedFile = new File("client/folder/" + fileName);
        if (detetedFile.exists() && detetedFile.isFile()) {
            detetedFile.delete();
        }
    }
}
