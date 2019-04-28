package clientApp.protocol;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class SendFiles {
    public SendFiles(Socket socket, String clientFolder, String fileName) {
        try {
            long start = System.currentTimeMillis();
            //* for sending to socket with BufferedOutputStream
            BufferedOutputStream buffOut = new BufferedOutputStream(socket.getOutputStream());
            //* reference to file
            File file = new File(clientFolder + fileName);
            //* read data from file
            FileInputStream inFile = new FileInputStream(file);

            /**/
            //sending command "send file"
            buffOut.write(15);
            buffOut.flush();
            //sending length of file
            buffOut.write((byte) file.length());
            buffOut.flush();
            //sending length of file name
//            buffOut.write(fileName.length());
//            buffOut.flush();
            //sending name of file
            buffOut.write(fileName.getBytes());
            buffOut.flush();
            /**/

            int bufLen;
            byte[] arr = new byte[8192];
            while ((bufLen = inFile.read(arr)) > 0){
                buffOut.write(arr, 0, bufLen);
            }

            System.out.println("Time " + (System.currentTimeMillis() - start));
            buffOut.close();
            inFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
