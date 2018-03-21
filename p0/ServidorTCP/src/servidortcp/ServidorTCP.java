package servidortcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Implementación de un servidor de eco TCP multihilo
 *
 * @author Pablo
 */
public class ServidorTCP {

    private static final int TIMEOUT = 30000;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("uso: ServidorTCP <puerto>");
            return;
        }
        Socket s = null;
        int serverPort = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            serverSocket.setSoTimeout(TIMEOUT);
            while (true) {
                s = serverSocket.accept();
                System.out.println("SERVIDOR: conexión establecida con el cliente.");
                ThreadServidor thread = new ThreadServidor(s);
                thread.start();
            }
        } catch (SocketTimeoutException e) {
            System.err.println("TIMEOUT: excedidos " + TIMEOUT / 1000 + "s.");
        } catch (SocketException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Error de i/o.");
        } finally {
            try {
                if (s != null) {
                    s.close();
                }
            } catch (IOException e) {
                System.err.println("Error de i/o.");
            }
        }
    }
}
