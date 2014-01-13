import java.io.*;

public class DatagramOutputStream extends OutputStream implements Closeable,
        Flushable {

    private DatagramSocketStreamer ds;

    public DatagramOutputStream(DatagramSocketStreamer ds) {
        this.ds = ds;
    }

    public void write(int b) throws IOException {
        ds.write(b);
    }

    public void flush() throws IOException {
        ds.flush();
    }

    public void close() throws IOException {
        ds.close();
    }

}
