package servidorudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Ejemplo de un programa que implementa un servidor UDP de eco.
 *
 * @author p.costa
 */
public class ServidorUDP {

    private static final int TIMEOUT = 30000;
    private static final int BUFF_SIZE = 1024;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("uso: ServidorUDP <puerto>");
            return;
        }
        try (DatagramSocket ds = new DatagramSocket(Integer.parseInt(args[0]))) {
            ds.setSoTimeout(TIMEOUT);
            while (true) {
                byte buffer[] = new byte[BUFF_SIZE];
                DatagramPacket dgRec = new DatagramPacket(buffer, buffer.length);
                // Recibimos el mensaje
                ds.receive(dgRec);
                System.out.println("SERVIDOR: Recibido " + new String(
                        dgRec.getData(), 0, dgRec.getLength())
                        + " de " + dgRec.getAddress().toString()
                        + ":" + dgRec.getPort());
                // Preparamos el datagrama que vamos a enviar y lo enviamos
                DatagramPacket dgEnv = new DatagramPacket(
                        dgRec.getData(), 0, dgRec.getLength(),
                        dgRec.getAddress(), dgRec.getPort());
                ds.send(dgEnv);
                System.out.println("SERVIDOR: Enviado " + new String(
                        dgEnv.getData(), 0, dgEnv.getLength())
                        + " a " + dgEnv.getAddress().toString()
                        + ":" + dgEnv.getPort());
            }
        } catch (SocketTimeoutException e) {
            System.err.println("TIMEOUT: excedidos " + TIMEOUT / 1000 + "s.");
        } catch (SocketException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Error de i/o.");
        }

    }
}
