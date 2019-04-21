package clientApp;

import java.io.File;
import java.util.ArrayList;

public class UserFiles {
    private final File USER_FOLDER = new File("folder");

    //Collection is used for representing files in TableView
    private ArrayList<UserFile> useFiles = new ArrayList<>();

    public void scanFiles() {
        for (int i = 0; i < USER_FOLDER.listFiles().length; i++) {
            useFiles.add(new UserFile(USER_FOLDER.listFiles()[i].getName(), USER_FOLDER.listFiles()[i].length()));
        }
    }

    public ArrayList<UserFile> getUseFiles() {
        return useFiles;
    }

    public void printFiles() {
        for (int i = 0; i < USER_FOLDER.listFiles().length; i++) {
            System.out.println(useFiles.get(i).getName() + " " + useFiles.get(i).getSize());
        }
    }
}

