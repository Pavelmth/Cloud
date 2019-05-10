package cloudNettyServer.fileWork;

import java.io.File;
import java.util.ArrayList;

public class UserFiles {
    private final String SERVER_FOLDERS = "server/folder/";
    private String clientFolder;

    public UserFiles(String clientFolder) {
        this.clientFolder = clientFolder;
    }

    //Collection is used for representing files in TableView
    private ArrayList<UserFile> useFiles = new ArrayList<>();

    private void scanFiles() {
        File path = new File(SERVER_FOLDERS + clientFolder);
        int fileQuantity = path.listFiles().length;
        for (int i = 0; i < fileQuantity; i++) {
            useFiles.add(new UserFile(path.listFiles()[i].getName(), path.listFiles()[i].length()));
        }
    }

    public ArrayList<UserFile> getUseFiles() {
        scanFiles();
        return useFiles;
    }
}

