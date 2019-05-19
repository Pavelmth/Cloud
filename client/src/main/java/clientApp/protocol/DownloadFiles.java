package clientApp.protocol;

import java.io.*;
import java.net.Socket;

public class DownloadFiles {
    private byte byteCod = -1;
    private long counter = 0;
    private long remain;
    private final int BUF_CAPACITY = 8192;

    public byte downloadFiles(DataOutputStream out, DataInputStream in, String clientFolder, String fileName, long fileLength) throws IOException {
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

        if (fileLength <= BUF_CAPACITY) {
            byte[] arr = new byte[(int) fileLength];
            in.read(arr, 0, (int) fileLength);
            outFile.write(arr);
        } else {
            counter = fileLength / BUF_CAPACITY;
            System.out.println(counter);
            remain = fileLength % BUF_CAPACITY;

            byte[] arr = new byte[BUF_CAPACITY];
            while (in.read(arr, 0, BUF_CAPACITY) == BUF_CAPACITY && counter != 0) {
                outFile.write(arr);
                counter--;
            }
            byte[] arrRemain = new byte[(int) remain];
            in.read(arrRemain, 0, (int) remain);
            outFile.write(arrRemain);
        }

        System.out.println("Time " + (System.currentTimeMillis() - start));

        outFile.close();

        byteCod = in.readByte();
        System.out.println("From the server has been received command: " + byteCod);
        return byteCod;
    }
}
