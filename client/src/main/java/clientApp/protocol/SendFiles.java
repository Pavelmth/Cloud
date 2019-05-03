package clientApp.protocol;

import java.io.*;
import java.net.Socket;

public class SendFiles {
    public SendFiles(Socket socket, String clientFolder, String fileName) {
        try {
            long start = System.currentTimeMillis();
            //* for sending to socket with BufferedOutputStream
            DataOutputStream buffOut = new DataOutputStream(socket.getOutputStream());
            //* reference to file
            File file = new File(clientFolder + fileName);
            //* open connection to file
            FileInputStream inFile = new FileInputStream(file);

            /**/
            //sending command "send file"
            buffOut.writeByte(15);
            buffOut.flush();
            System.out.println("The command '15' has been sent");

            //sending file length
            buffOut.writeLong(file.length());
            buffOut.flush();
            System.out.println("The file length '" + file.length() + "' has been sent");

            //sending file name length
            buffOut.writeInt(fileName.length());
            buffOut.flush();
            System.out.println("The file name length '" + fileName.length() + "' has been sent");

            //sending file name
            buffOut.write(fileName.getBytes());
            buffOut.flush();
            System.out.println("The file name '" + fileName + "' has been sent");
            /**/

            int bufLen;
            byte[] arr = new byte[8192];
            while ((bufLen = inFile.read(arr)) > 0){
                buffOut.write(arr, 0, bufLen);
            }
            System.out.println("The file data '" + fileName + "' has been sent");

            System.out.println("Time " + (System.currentTimeMillis() - start));
            buffOut.close();
            inFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
