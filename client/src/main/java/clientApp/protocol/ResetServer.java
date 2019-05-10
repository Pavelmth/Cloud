package clientApp.protocol;

import clientApp.fileWork.ListOfFiles;
import clientApp.fileWork.UserFile;

import java.io.*;
import java.util.ArrayList;

public class ResetServer {
    public ResetServer(ObjectInputStream inputObject, DataOutputStream outputStream) throws IOException, ClassNotFoundException {
        long start = System.currentTimeMillis();
        /**/
        //sending command "reset" - '18'
        outputStream.writeByte(18);
        outputStream.flush();
        System.out.println("The command '18' has been sent");

         ListOfFiles listOfFiles = (ListOfFiles) inputObject.readObject();

        ArrayList<UserFile> arrayList = listOfFiles.getUserFiles();
        System.out.println(arrayList.get(0).getName());

        System.out.println("Time " + (System.currentTimeMillis() - start));

    }
}

