package clientApp.protocol;

import java.io.*;
import java.net.Socket;

public class DownloadFiles {
    private byte byteCod = -1;

    public byte downloadFiles(DataOutputStream out, DataInputStream in, String clientFolder, String fileName) throws IOException {
        long start = System.currentTimeMillis();
        //* create file
        File file = new File(clientFolder + fileName);

        FileOutputStream outFile = new FileOutputStream(file);

        /**/
        //sending command "download file"
        out.write(16);
        out.flush();
        //send file name length
        out.writeByte(fileName.length());
        System.out.println(fileName + " " + fileName.length());
        out.flush();
        //sending name of file
        out.write(fileName.getBytes());
        out.flush();
        /**/

        byte[] arr = new byte[8192];
        int bufLen;
        while ((bufLen = in.read(arr)) > 0) {
            outFile.write(arr, 0, bufLen);
        }

        System.out.println("Time " + (System.currentTimeMillis() - start));

        outFile.close();

        byteCod = in.readByte();

        return byteCod;
    }
}
