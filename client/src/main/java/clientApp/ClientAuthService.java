package clientApp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientAuthService {
    public ClientAuthService(DataOutputStream buffOut) throws IOException{
        try {
            String login = "Tom";
            buffOut.writeInt(login.length());
            buffOut.write(login.getBytes());
            System.out.println("login has been sent");
        } finally {

        }
    }
}
