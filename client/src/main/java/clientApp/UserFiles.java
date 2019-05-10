package clientApp;

import java.io.File;
import java.util.ArrayList;

public class UserFiles {
    private final File USER_FOLDER = new File("client/folder/");

    //Collection is used for representing files in TableView
    private ArrayList<UserFile> useFiles = new ArrayList<>();

    private int fileQuantity = USER_FOLDER.listFiles().length;

    public void scanFiles() {
        for (int i = 0; i < fileQuantity; i++) {
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

    public static void main(String[] args) {
        UserFiles userFiles = new UserFiles();
        userFiles.scanFiles();
        userFiles.printFiles();
    }
}

