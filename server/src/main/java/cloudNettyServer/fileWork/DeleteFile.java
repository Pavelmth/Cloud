package cloudNettyServer.fileWork;

import java.io.File;

public class DeleteFile {
    private final String SERVER_FOLDERS = "server/folder/";

    public boolean deleteFile(String clientFolder, String fileName) {
        System.out.println(SERVER_FOLDERS + clientFolder + "/" + fileName);
        File path = new File(SERVER_FOLDERS + clientFolder + "/" + fileName);
        boolean isDeleted = path.delete();
        return isDeleted;
    }
}
