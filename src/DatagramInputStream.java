import java.io.*;

public class DatagramInputStream extends InputStream implements Closeable {

    private DatagramSocketStreamer ds;

    public DatagramInputStream(DatagramSocketStreamer ds) {
        this.ds = ds;
    }

    public int read() throws IOException {
        return ds.read();
    }

    public int available() throws IOException {
        return ds.available();
    }

    public void close() throws IOException {
        ds.close();
    }

}
