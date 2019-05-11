package clientApp.protocol;

import clientApp.fileWork.UserFile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DeleteServerFile {
    private byte byteCod = -1;

    public byte deleteServerFile(DataOutputStream out, DataInputStream in, String fileName) throws IOException {
        long start = System.currentTimeMillis();

        //sending command "reset" - '17'
        out.writeByte(18);
        out.flush();
        System.out.println("The command '17' has been sent");

        //sending file name length
        out.writeInt(fileName.length());
        out.flush();
        System.out.println("The file name length '" + fileName.length() + "' has been sent");

        //sending file name
        out.write(fileName.getBytes());
        out.flush();
        System.out.println("The file name '" + fileName + "' has been sent");

        byteCod = in.readByte();

        System.out.println("Time " + (System.currentTimeMillis() - start));

        return byteCod;
    }
}
