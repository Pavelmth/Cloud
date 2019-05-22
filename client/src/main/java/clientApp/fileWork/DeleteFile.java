package clientApp.fileWork;

import java.io.File;

public class DeleteFile {
    public boolean deleteFile(String clientFolder, String fileName) {
        File deletedFile = new File(clientFolder + "/" + fileName);

        if (deletedFile.exists() && deletedFile.isFile()) {
            boolean isDel = deletedFile.delete();
            return isDel;
        }

        return false;
    }
}
