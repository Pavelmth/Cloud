package clientApp.protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DownloadFiles {
    public DownloadFiles(Socket socket, String clientFolder, String fileName) {
        try {
            long start = System.currentTimeMillis();
            //* create file
            FileOutputStream outFile = new FileOutputStream(clientFolder + fileName);
            //* for sending to socket with BufferedOutputStream
            BufferedOutputStream buffOut = new BufferedOutputStream(socket.getOutputStream());
            //* take stream for receiving data from client by BufferedInputStream
            BufferedInputStream buffIn = new BufferedInputStream(socket.getInputStream());

            /**/
            //sending command "download file"
            buffOut.write(16);
            buffOut.flush();
            //send length of file name
            buffOut.write(fileName.length());
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

            System.out.println("Time " + (System.currentTimeMillis() - start));

            buffIn.close();
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
