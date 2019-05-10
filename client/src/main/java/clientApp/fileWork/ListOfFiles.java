package clientApp.fileWork;

import java.io.Serializable;
import java.util.ArrayList;

public class ListOfFiles implements Serializable {
    private static final long serialVersionUID = 5193392663743561680L;

    private ArrayList<UserFile> userFiles;

    public ArrayList<UserFile> getUserFiles() {
        return userFiles;
    }

    public ListOfFiles(ArrayList<UserFile> userFiles) {
        this.userFiles = userFiles;
    }
}
