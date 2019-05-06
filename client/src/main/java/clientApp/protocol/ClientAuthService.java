package clientApp.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientAuthService {
    private Byte command;

    public ClientAuthService(DataOutputStream out, DataInputStream in, String login, String password) throws IOException {
        try {
            out.writeInt(login.length());
            out.write(login.getBytes());
            System.out.println("login has been sent");

            int passwordCode = password.hashCode();
            out.writeInt(passwordCode);
            System.out.println("Password HashCode has been sent");

            //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

            byte[] com = new byte[1];
            in.read(com, 0, 1);

            command = in.readByte();
            System.out.println("Receiving code: " + com[0]);
        } finally {

        }
    }

    public byte getCommand() {
        return command;
    }
}
