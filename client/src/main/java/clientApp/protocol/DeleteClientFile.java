package clientApp.protocol;

import java.io.File;

public class DeleteClientFile {
    public DeleteClientFile(String fileName) {
        File deletedFile = new File("client/folder/" + fileName);
        if (deletedFile.exists() && deletedFile.isFile()) {
            boolean isDel = deletedFile.delete();
            if (isDel) {
                System.out.println("File: " + fileName + " has been deleted");
            }
        }
    }
}
