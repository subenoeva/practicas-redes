package webserver.servlet;

import java.math.BigInteger;
import java.util.Map;

public class FibonacciServlet implements Servlet {
    
    public FibonacciServlet() {

    }

    @Override
    public String doGet(Map<String, String> parameters) {
        int numero = Integer.parseInt(parameters.get("numero"));
        return printHeader() + printBody(numero) + printEnd();
    }

    private String printHeader() {
        return "<html><head> <title>Fibonacci</title> </head> ";
    }

    private String printBody(int numero) {
        return "<body> <p> Fib(" + numero + ") = " 
                + getFibonacci(numero) + "</p></body>";
    }

    private String printEnd() {
        return "</html>";
    }

    private static BigInteger getFibonacci(int n){
        BigInteger a = BigInteger.valueOf(0);
        BigInteger b = BigInteger.valueOf(1);
        BigInteger c = BigInteger.valueOf(1);
        for (int j=2 ; j<=n ; j++){
            c =  a.add(b);
            a = b;
            b = c;
        }
        return (a);
    }
 

}
