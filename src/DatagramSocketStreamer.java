import java.io.*;
import java.net.*;
import java.util.*;

public class DatagramSocketStreamer implements Streamer {

    private DatagramSocket ds;

    private DatagramOutputStream dos;
    private DatagramInputStream dis;

    private ArrayList<Byte> bytes;
    private ArrayList<Byte> bytesout;

    private InetSocketAddress to;

    private int id;

    public DatagramSocketStreamer(DatagramSocket ds, InetSocketAddress to,
            int id) throws IOException {

        this.ds = ds;
        this.id = id;
        this.to = to;

        bytes = new ArrayList<Byte>();
        bytesout = new ArrayList<Byte>();
        dos = new DatagramOutputStream(this);
        dis = new DatagramInputStream(this);

        addToByteoutArray(intToByteArray(id));
        addToByteoutArray(intToByteArray(0));

    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
                (byte) (value >>> 8), (byte) value };
    }

    public static final int byteArrayToInt(byte[] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
    }

    public int getID() {
        return id;
    }

    public synchronized void setID(int id) {
        this.id = id;
        byte[] ida = intToByteArray(id);
        for (int i = 0; i < 4; i++)
            bytesout.set(i, new Byte(ida[i]));
    }

    public InputStream getInputStream() throws IOException {
        return dis;
    }

    public OutputStream getOutputStream() throws IOException {
        return dos;
    }

    public synchronized int read() throws IOException {
        if (ds.isClosed())
            return -1;
        if (bytes.size() == 0) {
            try {
                wait();
            } catch (Exception x) {
            }
        }
        int ret = bytes.remove(0).intValue();
        if (ret < 0)
            ret += 256;
        return ret;
    }

    public synchronized void addToByteArray(Byte b) {
        bytes.add(b);
        notify();
    }

    public synchronized void addToByteArray(byte b) {
        bytes.add(new Byte(b));
        notify();
    }

    public synchronized void addToByteArray(int b) {
        bytes.add(new Byte((byte) b));
        notify();
    }

    public synchronized void addToByteArray(byte b[]) {
        for (int i = 0; i < b.length; i++)
            bytes.add(new Byte((byte) b[i]));
        notify();
    }

    public synchronized void addToByteoutArray(int b) {
        bytesout.add(new Byte((byte) b));
    }

    public synchronized void addToByteoutArray(byte b[]) {
        for (int i = 0; i < b.length; i++)
            bytesout.add(new Byte((byte) b[i]));
    }

    public synchronized void write(int b) {
        bytesout.add(new Byte((byte) b));
    }

    public synchronized void flush() throws IOException {
        if (ds.isClosed())
            throw new IOException("Socket is closed");

        byte[] len = intToByteArray(bytesout.size() - 8);
        for (int i = 0; i < 4; i++)
            bytesout.set(i + 4, new Byte(len[i]));

        ds.send(new DatagramPacket(toByteArray(bytesout), bytesout.size(), to));
        bytesout.clear();
        addToByteoutArray(intToByteArray(id));
        addToByteoutArray(intToByteArray(0));
    }

    public static byte[] toByteArray(ArrayList<Byte> b) {
        byte b2[] = new byte[b.size()];
        for (int i = 0; i < b.size(); i++)
            b2[i] = b.get(i);
        return b2;
    }

    public boolean isClosed() {
        return ds.isClosed();
    }

    public void close() throws IOException {
        if (ds.isClosed())
            throw new IOException("Socket is closed");
        ds.close();
    }

    public int available() throws IOException {
        if (ds.isClosed())
            throw new IOException("Socket is closed");
        return bytes.size();
    }

}
