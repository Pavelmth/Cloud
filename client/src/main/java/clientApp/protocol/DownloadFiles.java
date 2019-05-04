package clientApp.protocol;

import java.io.*;
import java.net.Socket;

public class DownloadFiles {
    public DownloadFiles(DataOutputStream buffOut, DataInputStream buffIn, String clientFolder, String fileName) throws IOException {
        try {
            long start = System.currentTimeMillis();
            //* create file
            FileOutputStream outFile = new FileOutputStream(clientFolder + fileName);

            /**/
            //sending command "download file"
            buffOut.write(16);
            buffOut.flush();
            //send file name length
            buffOut.writeInt(fileName.length());
            System.out.println(fileName.length() + " " + fileName);
            buffOut.flush();
            //sending name of file
            buffOut.write(fileName.getBytes());
            buffOut.flush();
            /**/

//            byte[] arr = new byte[8192];
//            int bufLen;
//            while (( bufLen = buffIn.read(arr)) > 0){
//                outFile.write(arr, 0, bufLen);
//            }
            System.out.println(buffIn.readUTF());

            System.out.println("Time " + (System.currentTimeMillis() - start));

            outFile.close();
        } finally {

        }
    }
}
