package clientApp.fileWork;

import java.io.File;
import java.util.ArrayList;

public class UserFiles {
    private final File USER_FOLDER_PATH = new File("client/folder/");

    //Collection is used for representing files in TableView
    private ArrayList<UserFile> useFiles = new ArrayList<>();

    private int fileQuantity = USER_FOLDER_PATH.listFiles().length;

    private void scanFiles() {
        for (int i = 0; i < fileQuantity; i++) {
            useFiles.add(new UserFile(USER_FOLDER_PATH.listFiles()[i].getName(), USER_FOLDER_PATH.listFiles()[i].length()));
        }
    }

    public ArrayList<UserFile> getUseFiles() {
        scanFiles();
        return useFiles;
    }
}

