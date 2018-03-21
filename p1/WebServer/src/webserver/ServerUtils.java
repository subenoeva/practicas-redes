package webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import webserver.servlet.Servlet;

public class ServerUtils {
    
    // Expresiones regulares usadas para validar las peticiones
    private final static String HTTP_VERSION_REGEXP = "HTTP\\/1\\.0|HTTP\\/1\\.1";
    private final static String REQUEST_TYPE_REGEXP = "GET|HEAD";

    public static String processDynRequest(String nombreclase,
            Map<String, String> parameters) throws Exception {

        Servlet servlet;
        Class<?> instancia;

        instancia = Class.forName(nombreclase);
        servlet = (Servlet) instancia.newInstance();

        return servlet.doGet(parameters);
    }

    /**
     * Devuelve la hora del servidor en formato: EEE, dd MMM yyyy HH:mm:ss z
     *
     * @return String
     */
    public static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Devuelve el tipo MIME de un fichero file.
     *
     * @param file
     * @return mimeType
     */
    public static String getFileMimeType(File file) {
        String result = null;
        try {
            result = Files.probeContentType(file.toPath());
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return result;
    }

    /**
     * Toma la cabecera header como parámetro y devuelve un array de Strings con
     * cada uno de los tokens de la cabecera: 
     * - hdTokens[0] = HEAD|GET 
     * - hdTokens[1] = URL 
     * - hdTokens[2] = HTTP/1.0|HTTP/1.1
     *
     * @param header
     * @return tokens
     */
    public static String[] parseHeader(String header) {
        StringTokenizer st = new StringTokenizer(header);
        String[] tokens = new String[3];
        for (int i = 0; i < 3; i++) {
            tokens[i] = st.nextToken();
        }
        return tokens;
    }

    /**
     * Nos dice si un array de Strings tienen un formato válido como cabecera de
     * una petición HTTP: 
     * - que contenga 3 Strings no nulos 
     * - que el primero sea un tipo de petición admitido: GET|HEAD 
     * - que el segundo empiece por el carácter '/' 
     * - que el tercero sea una versión HTTP válida: 1.0|1.1
     *
     * @param hdTokens
     * @return true|false
     */
    public static boolean validHeader(String[] hdTokens) {
        for (String hdToken : hdTokens) {
            if (hdToken == null) {
                return false;
            }
        }
        return ((hdTokens.length == 3)
                && hdTokens[0].matches(REQUEST_TYPE_REGEXP)
                && hdTokens[1].startsWith("/")
                && hdTokens[2].matches(HTTP_VERSION_REGEXP));
    }

    public static Map<String, String> parseParams(String params) {
        String pairs[] = params.split("&");
        HashMap<String, String> parameters = new HashMap<>(params.length());
        for (String pair : pairs) {
            String pairValue[] = pair.split("=");
            parameters.put(pairValue[0], pairValue[1]);
        }
        return parameters;
    }

    /**
     * Imprime file en output como una secuencia de bytes.
     *
     * @param output
     * @param file
     */
    public static void printFile(OutputStream output, File file) {
        byte[] buffer = new byte[1024];
        int numBytes;
        try (FileInputStream fileInput = new FileInputStream(file)) {
            while ((numBytes = fileInput.read(buffer)) != -1) {
                output.write(buffer, 0, numBytes);
            }
            fileInput.close();
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
