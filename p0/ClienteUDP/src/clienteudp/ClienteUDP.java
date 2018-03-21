package clienteudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Clase que implementa un cliente de eco mediante UDP.
 */
public class ClienteUDP {

    public static void main(String argv[]) {
        if (argv.length != 3) {
            System.err.println("Formato: ClienteUDP <maquina> <puerto> <mensaje>");
            System.exit(-1);
        }
        try (DatagramSocket sDatagram = new DatagramSocket()) { //Creamos el socket en cualquier puerto libre
            sDatagram.setSoTimeout(300000); // Establecemos un timeout de 300 segs
            InetAddress dirServidor = InetAddress.getByName(argv[0]); // Obtenemos la dirección IP del servidor
            int puertoServidor = Integer.parseInt(argv[1]); // Obtenemos el puerto del servidor
            String mensaje = argv[2]; // Obtenemos el mensaje
            // Preparamos el datagrama que vamos a enviar y lo enviamos
            DatagramPacket dgramEnv
                    = new DatagramPacket(mensaje.getBytes(),
                            mensaje.getBytes().length, dirServidor, puertoServidor);
            System.out.println(dgramEnv.getPort());
            sDatagram.send(dgramEnv); // Enviamos el datagrama
            System.out.println("CLIENTE: Enviando " + new String(
                    dgramEnv.getData())
                    + " a " + dgramEnv.getAddress().toString()
                    + ":" + dgramEnv.getPort());
            // Preparamos el datagrama de recepción
            byte array[] = new byte[1024];
            DatagramPacket dgramRec = new DatagramPacket(array, array.length);// Recibimos el mensaje
            sDatagram.receive(dgramRec);
            System.out.println("CLIENTE: Recibido " + new String(dgramRec.getData(), 0, dgramRec.getLength())
                    + " de " + dgramRec.getAddress().toString()
                    + ":" + dgramRec.getPort());
        } catch (SocketTimeoutException e) {
            System.err.println("300 segs sin recibir nada");
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
