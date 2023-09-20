import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(9001), 0);
        server.createContext("/user/register", new RegisterHandler());
        server.createContext("/user/login", new LoginHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try{
                System.out.println("Request received");
                Headers headers = t.getResponseHeaders();
                headers.add("Access-Control-Allow-Origin", "*");
                headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
                headers.add("Access-Control-Allow-Headers", "Content-Type");
                if ("OPTIONS".equals(t.getRequestMethod())) {
                    t.sendResponseHeaders(200, -1);
                    return;
                }
                System.out.println("Request method: " + t.getRequestMethod());
                if ("POST".equals(t.getRequestMethod())) {
                    System.out.println("Processing POST request");
                    InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    String jsonString = br.lines().collect(Collectors.joining());

                    // 使用org.json库解析JSON字符串
                    JSONObject json = new JSONObject(jsonString);

                    String name = json.optString("name");
                    String email = json.optString("email");
                    String password = json.optString("password");
                    System.out.println("Received request data: Name - " + name + ", Email - " + email + ", Password - " + password);
                    boolean isAlreadyRegistered = false;
                    try (BufferedReader reader = Files.newBufferedReader(Paths.get("users.txt"))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split(",");
                            if (parts.length < 3) {
                                System.out.println("Skipping invalid line: " + line);
                                continue;
                            }
                            if (parts[0].equals(email)) {
                                isAlreadyRegistered = true;
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String response = null;
                    if (!isAlreadyRegistered) {
                        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("users.txt"), StandardOpenOption.APPEND)) {
                            writer.write(name + "," + email + "," + password);
                            writer.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        response = "User registered successfully!";
                        t.sendResponseHeaders(200, response.length());
                    }

                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                    System.out.println("Request processing finished");
                }else {
                    System.out.println("Request is not a POST");
                }
            } catch (Exception e) {
                e.printStackTrace();  // 打印异常到控制台
                String response = "Internal Server Error";
                t.sendResponseHeaders(500, response.length());  // 返回500状态码
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }



    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("Received login request.");
            Headers headers = t.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            headers.add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(t.getRequestMethod())) {
                t.sendResponseHeaders(200, -1);
                return;
            }

            if ("POST".equals(t.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(t.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String jsonString = br.lines().collect(Collectors.joining());

                // 使用org.json库解析JSON字符串
                JSONObject json = new JSONObject(jsonString);

                String email = json.optString("email");
                String password = json.optString("password");

                boolean loggedIn = false;
                try (BufferedReader reader = Files.newBufferedReader(Paths.get("users.txt"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length < 3) {
                            System.out.println("Skipping invalid line: " + line);
                            continue;
                        }
                        if (parts[1].equals(email) && parts[2].equals(password)) {
                            loggedIn = true;
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String response = loggedIn ? "Logged in successfully!" : "Invalid email or password";
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }


    private static Map<String, String> parseFormData(String formData) {
        Map<String, String> map = new HashMap<>();
        if (formData == null) return map;

        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            map.put(pair.substring(0, idx), pair.substring(idx + 1));
        }
        return map;
    }
}
