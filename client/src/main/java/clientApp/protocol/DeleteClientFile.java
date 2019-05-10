package clientApp.protocol;

import java.io.File;

public class DeleteClientFile {
    public DeleteClientFile(String fileName) {
        File deletedFile = new File("client/folder/" + fileName);
        if (deletedFile.exists() && deletedFile.isFile()) {
            deletedFile.delete();
        }
    }
}
