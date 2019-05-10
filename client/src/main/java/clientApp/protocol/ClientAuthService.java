package clientApp.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientAuthService {
    Byte byteCod = -1;

    public byte clientAuthService(DataOutputStream out, DataInputStream in, String login, String password) throws IOException {
        try {
            out.writeInt(login.length());
            out.write(login.getBytes());
            System.out.println("Login has been sent");

            int passwordCode = password.hashCode();
            out.writeInt(passwordCode);
            System.out.println("Password HashCode has been sent");
            byteCod = in.readByte();
        } finally {

        }
        return byteCod;
    }
}
