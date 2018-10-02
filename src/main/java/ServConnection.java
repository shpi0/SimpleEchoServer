import java.io.*;
import java.net.Socket;

public class ServConnection implements Closeable {

    private Socket socket;
    private PrintWriter outputStream;
    private BufferedReader inputStream;

    public ServConnection(Socket socket) {
        try {
            this.socket = socket;
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
        inputStream.close();
        outputStream.close();
    }

    public String receive() throws IOException {
        synchronized (inputStream) {
            return inputStream.readLine();
        }
    }

    public void send(String message) {
        synchronized (outputStream) {
            outputStream.println(message);
        }
    }

}
