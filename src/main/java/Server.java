import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int SERVER_PORT = 7;
    private static final int MAX_CONNECTIONS = 32;

    public Server() {
        serverMainLoop();
    }

    private void serverMainLoop() {
        ExecutorService executor;
        executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server started at localhost.");
            while (true) {
                executor.execute(new Handler(serverSocket.accept()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Client connection problem.");
            executor.shutdown();
        }
    }

    private class Handler implements Runnable {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            System.out.println("New connection received from " + socket.getRemoteSocketAddress());
            try (ServConnection connection = new ServConnection(socket)) {
                connection.send("SimpleEchoServer welcome message! Send any string to me and I will send you an answer.");
                echoProcess(connection);
            }
            catch (IOException e) {
                System.out.println("Connection problem.");
            }
        }

        private void echoProcess(ServConnection connection) {
            while (true) {
                try {
                    String incoming = connection.receive();
                    if (incoming == null) {
                        throw new IOException();
                    }
                    if (!incoming.isEmpty()) {
                        System.out.println("Incoming query from " + socket.getRemoteSocketAddress() + ": '" + incoming + "'");
                        Date currentDate = new Date();
                        DateFormat df = new SimpleDateFormat("(HH:mm)");
                        connection.send(df.format(currentDate) + " [ECHO] " + incoming);
                    }
                } catch (IOException e) {
                    System.out.println("Reading from socket problem.");
                }
            }
        }
    }

}
