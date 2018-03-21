package clientetcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Clase que implementa un cliente de eco mediante TCP
 *
 * @author Pablo
 */
public class ClienteTCP {

    private static Socket s = null;
    private static final int TIMEOUT = 30000;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("uso: ClienteTCP <maquina> <puerto> <mensaje>");
            return;
        }
        try {
            //Preparamos los parámetros que recibimos por linea de comandos
            InetAddress serverAddress = InetAddress.getByName(args[0]);
            int serverPort = Integer.parseInt(args[1]);
            String message = args[2];
            //Creamos el socket usando estos parámetros
            s = new Socket(serverAddress, serverPort);
            s.setSoTimeout(TIMEOUT); //Timeout de 30s

            System.out.println("CLIENTE: Conexión establecida con "
                    + serverAddress + ":" + serverPort);
            //Creamos el canal de entrada
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(s.getInputStream()));
            //Creamos el canal de salida
            PrintWriter output = new PrintWriter(s.getOutputStream(), true);

            System.out.println("CLIENTE: Enviando mensaje: " + message);
            output.println(message); //Enviamos el mensaje

            String response = input.readLine(); //Recibimos la respuesta del servidor
            System.out.println("CLIENTE: Recibido mensaje: " + response);
            //Cerramos los canales
            input.close();
            output.close();
        } catch (SocketTimeoutException e) {
            System.err.println("300 segs sin recibir nada");
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                if (s != null) {
                    s.close();
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
