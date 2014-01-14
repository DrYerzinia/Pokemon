package com.dryerzinia.pokemon.net;
import java.io.*;

public class ByteInputStream extends InputStream implements Closeable {

    byte[] b;
    int location;

    public ByteInputStream(byte[] b) {
        this.b = b;
        location = 0;
    }

    public int read() throws IOException {
        if (b.length == location)
            throw new IOException("End of data reached...");
        location++;
        return b[location - 1];
    }

    public static void main(String args[]) {
        byte[] b = new byte[4];
        b[0] = 4;
        b[1] = 0;
        b[2] = 0;
        b[3] = 0;
        int value = 0;
        for (int i = 3; i >= 0; i--) {
            int offset = i * 8;
            value |= b[i] << offset;
        }

        System.out.println("Val:" + value);
    }

    public int readInt() throws IOException {
        return DatagramSocketStreamer.byteArrayToInt(new byte[] {
                (byte) read(), (byte) read(), (byte) read(), (byte) read() });
    }

    public int available() throws IOException {
        return b.length - location;
    }

    public void close() throws IOException {
        throw new IOException("This realy cant be closed...");
    }

}
