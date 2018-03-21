package webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase encargada de gestionar las disintas peticiones HTTP que lleguen al
 * servidor web.
 *
 * @author Pablo
 */
public class RequestHandler extends Thread {

    private final Socket s;

    private BufferedReader input; // Canal de entrada del socket
    private PrintWriter output;   // Canal de salida del socket

    private final static String HTTP_VERSION = "HTTP/1.1";
    private final static String SERVER_VERSION = "WebServer 0.1a";
    // Códigos de respuesta del servidor
    private final static String RESPONSE_OK = "200 OK";
    private final static String RESPONSE_BAD_REQUEST = "400 BAD REQUEST";
    private final static String RESPONSE_NOT_FOUND = "404 NOT FOUND";
    private final static String RESPONSE_FORBIDDEN = "403 ACCESS FORBIDDEN";
    //Expresión regular usada para detectar y extraer campos de las peticiones dinamicas
    private final static String DYN_REQUEST_REGEXP = "\\/([A-Za-z]*)Servlet\\.do\\?(.*)";
    private final Pattern dynPattern;
    // Directorio base del servidor, carpeta webcontent en el raíz del proyecto
    private final static String BASE_DIRECTORY = "webcontent";
    // Página por defecto
    private final static String INDEX = "/index.html";
    // Ubicación de los ficheros con los .html de las páginas de error
    private final static String ERROR_DIRECTORY = BASE_DIRECTORY + "/" + "error";
    private final static String ERROR_400_HTML = ERROR_DIRECTORY + "/" + "400.html";
    private final static String ERROR_403_HTML = ERROR_DIRECTORY + "/" + "403.html";
    private final static String ERROR_404_HTML = ERROR_DIRECTORY + "/" + "404.html";

    public RequestHandler(Socket s) {
        this.s = s;
        this.input = null;
        this.output = null;
        this.dynPattern = Pattern.compile(DYN_REQUEST_REGEXP);
    }

    @Override
    public void run() {

        try {
            // Abrimos los flujos de entrada y de salida.
            input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            output = new PrintWriter(s.getOutputStream(), true);
            // Parseamos la cabecera en tokens.
            String[] hdTokens = ServerUtils.parseHeader(input.readLine());
            // Validamos que esos tokens formen una cabecera válida
            if (!ServerUtils.validHeader(hdTokens)) {
                replyBadRequest(output);
            } else {
                Matcher matcher = dynPattern.matcher(hdTokens[1]);
                if (matcher.matches()) { // Si es una petición dinámica.
                    // Se envia el nombre del servlet y sus parámetros
                    replyGetDyn(output, matcher.group(1), matcher.group(2));
                } else {
                    if (hdTokens[1].equals("/")) {
                        hdTokens[1] = INDEX; // Se devuelve la página predeterminada
                    }
                    File file = new File(BASE_DIRECTORY + hdTokens[1]); // BD + URL
                    if (file.exists()) {
                        if (file.canRead()) {
                            switch (hdTokens[0]) { // Tipo de petición.
                                case "HEAD":
                                    replyHead(output, file);
                                    break;
                                case "GET":
                                    replyGet(output, file);
                                    break;
                            }
                        } else {
                            replyForbidden(output);
                        }
                    } else {
                        replyNotFound(output);
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        } catch (Exception ex) {
            System.err.println(ex);
        } finally {
            try {
                input.close();
                output.close();
                s.close();
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }

    /**
     * Imprime 3 lineas en output: - version del servidor y código de respuesta.
     * - fecha en la que se realizó la petición. - la versión del servidor.
     *
     * @param output
     * @param responseCode
     */
    private void printResponseHeader(PrintWriter output, String responseCode) {
        output.println(HTTP_VERSION + " " + responseCode);
        output.println("Date: " + ServerUtils.getServerTime());
        output.println("Server: " + SERVER_VERSION);
    }

    /**
     * Imprime en output los detalles (tamaño en bytes y tipo MIME) de file.
     *
     * @param output
     * @param file
     */
    private void printFileDetails(PrintWriter output, File file) {
        output.println("Content-Length: " + file.length());
        output.println("Content-Type: " + ServerUtils.getFileMimeType(file));
    }

    /**
     * Imprime en output los detalles (tamaño en bytes y tipo MIME) de file.
     *
     * @param output
     * @param file
     */
    private void printFileDetails(PrintWriter output, String file) {
        output.println("Content-Length: " + file.getBytes().length);
        output.println("Content-Type: text/html");
    }

    /**
     * Imprime 3 lineas en output: - la cabecera de una respuesta con código 200
     * (OK). - los detalles del fichero file. - una línea en blanco.
     *
     * @param output
     * @param file
     */
    private void replyHead(PrintWriter output, File file) {
        printResponseHeader(output, RESPONSE_OK);
        printFileDetails(output, file);
        output.println();
    }

    /**
     * Imprime 4 o más lineas en output: - la cabecera de una respuesta con
     * código 200 (OK). - los detalles del fichero file. - una línea en blanco.
     * - el contenido del fichero file.
     *
     * @param output
     * @param file
     */
    private void replyGet(PrintWriter output, File file) throws IOException {
        printResponseHeader(output, RESPONSE_OK); // 200
        printFileDetails(output, file);
        output.println();
        ServerUtils.printFile(s.getOutputStream(), file);
    }

    /**
     * Imprime 4 o más lineas en output: 
     * - la cabecera de una respuesta con código 200 (OK). 
     * - los detalles del String file
     * - una línea en blanco.
     * - el contenido del String file.
     *
     * @param output
     * @param dynResponse
     */
    private void replyGet(PrintWriter output, String dynResponse) throws IOException {
        printResponseHeader(output, RESPONSE_OK); // 200
        printFileDetails(output, dynResponse);
        output.println();
        output.println(dynResponse);
    }

    /**
     * Genera una respuesta dinámica usando el servlet y los parámetros
     * proporcionados. Si no se encuentra el servlet responde un 404 (NOT
     * FOUND).
     *
     * @param output
     * @param servlet
     * @param params
     * @throws IOException
     */
    private void replyGetDyn(PrintWriter output, String servlet, String params) throws IOException {
        try {
            String response = ServerUtils.processDynRequest(
                    "webserver.servlet." + servlet + "Servlet",
                    ServerUtils.parseParams(params));
            replyGet(output, response);
        } catch (Exception e) {
            replyBadRequest(output);
        }
    }

    private void replyBadRequest(PrintWriter output) throws IOException {
        File file = new File(ERROR_400_HTML);
        printResponseHeader(output, RESPONSE_BAD_REQUEST); //400
        printFileDetails(output, file);
        output.println();
        ServerUtils.printFile(s.getOutputStream(), file);
    }

    private void replyForbidden(PrintWriter output) throws IOException {
        File file = new File(ERROR_403_HTML);
        printResponseHeader(output, RESPONSE_FORBIDDEN); //403
        printFileDetails(output, file);
        output.println();
        ServerUtils.printFile(s.getOutputStream(), file);
    }

    private void replyNotFound(PrintWriter output) throws IOException {
        File file = new File(ERROR_404_HTML);
        printResponseHeader(output, RESPONSE_NOT_FOUND); //404
        printFileDetails(output, file);
        output.println();
        ServerUtils.printFile(s.getOutputStream(), file);
    }

}
