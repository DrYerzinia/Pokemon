import java.io.*;
import java.net.*;

public class TCPStreamer implements Streamer {

    Socket s;

    public TCPStreamer(Socket s) {
        this.s = s;
    }

    public InputStream getInputStream() throws IOException {
        return s.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return s.getOutputStream();
    }

    public void close() throws IOException {
        if (s.isClosed())
            throw new IOException("Socket is closed");
        s.close();
    }

}
