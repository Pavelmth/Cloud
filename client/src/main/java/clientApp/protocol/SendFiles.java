package clientApp.protocol;

import java.io.*;

public class SendFiles {
    private byte byteCod = -1;

    public byte sendFiles(DataOutputStream out, DataInputStream in, String clientFolder, String fileName) throws IOException {
        long start = System.currentTimeMillis();
        //* reference to file
        File file = new File(clientFolder + fileName);
        //* open connection to file
        FileInputStream inFile = new FileInputStream(file);

        /**/
        //sending command "send file" - '15'
        out.writeByte(15);
        out.flush();
        System.out.println("The command '15' has been sent");

        //sending file length
        out.writeLong(file.length());
        out.flush();
        System.out.println("The file length '" + file.length() + "' has been sent");

        //sending file name length
        out.writeInt(fileName.length());
        out.flush();
        System.out.println("The file name length '" + fileName.length() + "' has been sent");

        //sending file name
        out.write(fileName.getBytes());
        out.flush();
        System.out.println("The file name '" + fileName + "' has been sent");
        /**/

        int bufLen;
        byte[] arr = new byte[8192];
        while ((bufLen = inFile.read(arr)) > 0) {
            out.write(arr, 0, bufLen);
        }
        System.out.println("The file data '" + fileName + "' has been sent");

        byteCod = in.readByte();

        System.out.println("Time " + (System.currentTimeMillis() - start));
        inFile.close();

        return byteCod;
    }
}
