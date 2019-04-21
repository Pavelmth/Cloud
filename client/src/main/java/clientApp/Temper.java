package clientApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;

public class Temper {
    FileInputStream inputStream = null;

    public static void main(String[] args) {
        Integer a = 123;
        int index = a * Integer.BYTES;
        byte buf = a.byteValue();

        System.out.println(index);
    }
}
