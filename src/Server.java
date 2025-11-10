import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    private final int port;
    private final ExecutorService pool;

    public Server(int port, int threads) {
        this.port = port;
        this.pool = Executors.newFixedThreadPool(threads);
    }

    public void start() throws IOException {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            while (true) {
                Socket client = server.accept();
                pool.submit(new ClientTask(client));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 1357;
        if (args.length >= 1) {
            try { port = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
        }
        new Server(port, 8).start();
    }

    private static class ClientTask implements Runnable {
        private final Socket socket;
        ClientTask(Socket s) { this.socket = s; }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)) {

                out.println("100 READY");
                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) { out.println("400 ERR EMPTY"); continue; }
                    if (line.equalsIgnoreCase("QUIT")) { out.println("201 BYE"); break; }

                    String[] tok = line.split("\\s+");
                    if (tok.length != 3) { out.println("400 ERR ARGERR"); continue; }

                    String op = tok[0].toUpperCase();
                    int a, b;
                    try {
                        a = Integer.parseInt(tok[1]);
                        b = Integer.parseInt(tok[2]);
                    } catch (NumberFormatException e) {
                        out.println("400 ERR NAN");
                        continue;
                    }

                    switch (op) {
                        case "ADD": out.println("200 OK " + (a + b)); break;
                        case "SUB": out.println("200 OK " + (a - b)); break;
                        case "MUL": out.println("200 OK " + (a * b)); break;
                        case "DIV":
                            if (b == 0) out.println("400 ERR DIVZERO");
                            else out.println("200 OK " + (a / b));
                            break;
                        default:
                            out.println("400 ERR UNKNOP");
                    }
                }
            } catch (IOException ignored) {
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
    }
}