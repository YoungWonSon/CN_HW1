import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.Scanner;

public class Client {

    static class Config {
        final String host; final int port;
        Config(String host, int port) { this.host = host; this.port = port; }
    }

    static Config loadConfig(String[] args) {
        String host = null; Integer port = null;


        for (int i = 0; i < args.length - 1; i++) {
            if ("--host".equalsIgnoreCase(args[i])) host = args[i+1];
            if ("--port".equalsIgnoreCase(args[i])) {
                try { port = Integer.parseInt(args[i+1]); } catch (NumberFormatException ignored) {}
            }
        }

        if (host == null || port == null) {
            Properties p = new Properties();
            try (FileInputStream fis = new FileInputStream("server_info.dat")) {
                p.load(fis);
                if (host == null) {
                    String h = p.getProperty("host");
                    if (h != null && !h.trim().isEmpty()) host = h.trim();
                }
                if (port == null) {
                    String po = p.getProperty("port");
                    if (po != null && !po.trim().isEmpty()) port = Integer.parseInt(po.trim());
                }
            } catch (Exception ignored) {
            }
        }

        if (host == null) host = "localhost";
        if (port == null) port = 1357;

        return new Config(host, port);
    }

    public static void main(String[] args) {
        Config cfg = loadConfig(args);
        System.out.println("Connecting to " + cfg.host + ":" + cfg.port);

        try (Socket sock = new Socket(cfg.host, cfg.port);
             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
             PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
             Scanner sc = new Scanner(System.in)) {

            String greet = in.readLine();
            if (greet != null) System.out.println(greet);
            System.out.println("Enter: ADD 10 20 / SUB 7 3 / MUL 3 5 / DIV 10 2 / QUIT");

            while (true) {
                System.out.print("> ");
                String line = sc.nextLine();
                out.println(line);
                String resp = in.readLine();
                if (resp == null) { System.out.println("Server closed."); break; }
                System.out.println("< " + resp);
                if (resp.startsWith("201 BYE")) break;
            }
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }
}
