package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author Pablo
 */
public class WebServer {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("uso: ServidorWeb <puerto>");
            return;
        }
        Socket s = null;
        int serverPort = Integer.parseInt(args[0]);
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            while (true) {
                s = serverSocket.accept();      
                RequestHandler thread = new RequestHandler(s);
                thread.start();
            }
        }  catch (SocketException ex) {
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
