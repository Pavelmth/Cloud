package clientApp.protocol;

import java.io.*;
import java.net.Socket;

public class DownloadFiles {
    public DownloadFiles(DataOutputStream buffOut, DataInputStream buffIn, String clientFolder, String fileName) throws IOException {
        try {
            long start = System.currentTimeMillis();
            //* create file
            File file = new File(clientFolder + fileName);

            FileOutputStream outFile = new FileOutputStream(file);

            /**/
            //sending command "download file"
            buffOut.write(16);
            buffOut.flush();
            //send file name length
            buffOut.writeByte(fileName.length());
            System.out.println(fileName + " " + fileName.length());
            buffOut.flush();
            //sending name of file
            buffOut.write(fileName.getBytes());
            buffOut.flush();
            /**/

            byte[] arr = new byte[8192];
            int bufLen;
            while (( bufLen = buffIn.read(arr)) > 0){
                outFile.write(arr, 0, bufLen);
            }

            System.out.println("Time " + (System.currentTimeMillis() - start));

            outFile.close();
        } finally {

        }
    }
}
