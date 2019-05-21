package clientApp.protocol;

import java.io.*;
import java.net.Socket;

public class DownloadFiles {
    private byte byteCod = -1;
    private long counter = 0;

    public byte downloadFiles(DataOutputStream out, DataInputStream in, String clientFolder, String fileName, long fileLength) throws IOException {
        long start = System.currentTimeMillis();
        counter = fileLength;

        //* create file
        File file = new File(clientFolder + fileName);

        BufferedOutputStream outFile = new BufferedOutputStream(new FileOutputStream(file));

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

        while (counter != 0) {
            outFile.write(in.readByte());
            counter--;
        }

        System.out.println("Time " + (System.currentTimeMillis() - start));

        outFile.close();

        byteCod = in.readByte();
        System.out.println("From the server has been received command: " + byteCod);
        return byteCod;
    }
}
