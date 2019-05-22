package clientApp.protocol;

import clientApp.fileWork.UserFile;

import java.io.*;
import java.util.ArrayList;

public class ResetServer {
    ArrayList<UserFile> userFiles = new ArrayList<>();

    public ResetServer(DataOutputStream out, DataInputStream in) throws IOException {
        long start = System.currentTimeMillis();

        //sending command "reset" - '18'
        out.writeByte(18);
        out.flush();
        System.out.println("The command '18' has been sent");

        int msgLength = in.readInt();
        System.out.println(msgLength);

        byte[] msg = new byte[msgLength];
        in.read(msg);
        String str = new String(msg);

        String[] list = str.split(":");
        for (int i = 0; i < list.length; i = i + 2) {
            userFiles.add(new UserFile(list[i], Long.parseLong(list[(1 + i)])));
        }
        System.out.println("Time " + (System.currentTimeMillis() - start));
    }

    public ArrayList<UserFile> getUserFiles() {
        return userFiles;
    }
}
