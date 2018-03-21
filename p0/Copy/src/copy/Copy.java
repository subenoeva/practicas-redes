package copy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Copy {

    private static FileInputStream fis = null;
    private static FileOutputStream fos = null;
    private static final int BUFF_SIZE = 1024;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("uso: Copy <origen> <destino>");
            return;
        }
        try {
            fis = new FileInputStream(args[0]);
            fos = new FileOutputStream(args[1]);
            byte[] buffer = new byte[BUFF_SIZE];
            int numBytes;

            System.out.println("Copiando...");
            while ((numBytes = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, numBytes);
            }
            
            System.out.println("Fichero: " + args[0] + 
                    " copiado con éxito en: " + args[1]);
            
        } catch (FileNotFoundException ex) {
            System.err.println("Fichero " + args[0] + " no encontrado.");
        } catch (IOException ex) {
            System.err.println("Error al copiar.");
        } finally {
            try {
                if (fis != null) fis.close();
                if (fos != null) fos.close();
            } catch (IOException ex) {
                System.err.println("Error al cerrar el stream: " + ex);
            }
        }
    }
}
