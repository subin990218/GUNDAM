package com.mobilesuit.clientplugin.client;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.mobilesuit.clientplugin.oauth.service.ApiService;
import com.mobilesuit.clientplugin.singleton.DataContainer;
import com.mobilesuit.clientplugin.util.SecureUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;


public class OauthClientServer {
    private static final OauthClientServer instance = new OauthClientServer();
    private final DataContainer dataContainer = DataContainer.getInstance();
    private final ApiService apiService = ApiService.getInstance();
    private final SocketClient socketClient = SocketClient.getInstance();

    private OauthClientServer(){
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(23137), 0);
            server.createContext("/oauth", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    System.out.println("응답 도착");
                    String token = exchange.getRequestURI().toString().replace("/oauth?token=","");


                    // 기존의 서버에서 브라우저로 요청을 받아 브라우저에서 토큰을 보내주는 json 타입 반환 로컬웹서버 반응

                    /*InputStream inputStream = exchange.getRequestBody();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    StringBuilder body = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        body.append(line);
                    }
                    reader.close();

                    System.out.println(body);

                    String response = "Success!";
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();

                    Gson gson = new Gson();

                    GitHubOauthDto.Receive receive = gson.fromJson(body.toString(),GitHubOauthDto.Receive.class);*/

                    // 리다이렉션 URI 로 토큰 받아오기

                    String response = "<!DOCTYPE html>\n" +
                            "                            <html>\n" +
                            "                            <head>\n<meta charset=\"UTF-8\" />" +
                            "                                <link rel=\"icon\" href=\"http://k9e207.p.ssafy.io:7070/static/img192.ico\" />\n" +
                            "                                <title>GUNDAM</title>\n" +
                            "                                <style>\n" +
                            "                                    body {\n" +
                            "                                        display: flex;\n" +
                            "                                        justify-content: center;\n" +
                            "                                        align-items: center;\n" +
                            "                                        height: 100vh;\n" +
                            "                                        flex-direction: column;\n" +
                            "                                        margin: 0;\n" +
                            "                                    }\n" +
                            "                                    img {\n" +
                            "                                        width: 300px;\n" +
                            "                                    }\n" +
                            "                                </style>\n" +
                            "                            </head>\n" +
                            "                            <body>\n" +
                            "                                <img src=\"http://k9e207.p.ssafy.io:7070/static/img512.png\" alt=\"GUNDAM\">\n" +
                            "                                <p>This window will close automatically.</p>\n" +
                            "                                <script>\n" +
                            "                                    setTimeout(function() {\n" +
                            "                                        window.close();\n" +
                            "                                    }, 5000);\n" +
                            "                                </script>\n" +
                            "                            </body>\n" +
                            "                            </html>";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();

                    CredentialAttributes attributes = SecureUtil.createCredentialAttributes("GUNDAM_Plugin");
                    Credentials credentials = new Credentials("user", token);
                    PasswordSafe.getInstance().set(attributes, credentials);

                    System.out.println(SecureUtil.getToken());

                    dataContainer.setGitHubAccessToken(SecureUtil.getToken());

                    apiService.checkAuthority();
                    apiService.setUser();
                }
            });
            server.start();
            System.out.println("Server Started");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static OauthClientServer getInstance(){
        return instance;
    }
}
