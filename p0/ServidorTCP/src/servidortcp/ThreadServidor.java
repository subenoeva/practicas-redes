package servidortcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Un hilo del servidor de eco que almacena el socket.
 *
 * @author Pablo
 */
public class ThreadServidor extends Thread {

    private final Socket s;
    private final SimpleDateFormat sdf;
    private final static String DATE_FORMAT = "HH:mm:ss.SS";
    

    public ThreadServidor(Socket s) {
        this.s = s;
        this.sdf = new SimpleDateFormat(DATE_FORMAT);
    }

    @Override
    public void run() {
        BufferedReader input; // Canal de entrada
        PrintWriter output; // Canal de salida
        try {
            input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            output = new PrintWriter(s.getOutputStream(), true);
            // Procesamos el mensaje
            String message = input.readLine();
            String timestamp = sdf.format(Calendar.getInstance().getTime());
            System.out.println("SERVIDOR(" + timestamp + "): Recibido " + message);
            
            
            
            
            
            
            // Reenviamos el mensaje
            output.println(message);
            timestamp = sdf.format(Calendar.getInstance().getTime());
            System.out.println("SERVIDOR(" + timestamp + "): Enviado " + message);
            // Cerramos los canales
            input.close();
            output.close();
        } catch (IOException ex) {
            System.err.println("i/o exception: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                s.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
