package webserver.servlet;

import java.util.Map;

public interface Servlet {

    public String doGet(Map<String, String> parameters) throws Exception;

}
