package com.ukarim.httprouter.example;

import com.ukarim.httprouter.HttpRouter;
import com.ukarim.httprouter.Params;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.IOException;
import java.security.SecureRandom;

/*
    Run the main method and try to make http requests

    curl localhost:8080/users/ukarim/uppercase
    curl -X POST localhost:8080/random-num
 */

public class JettyExample {

    public static void main(String[] args) throws Exception {
        var server = new Server();
        var serverConnector = new ServerConnector(server);
        serverConnector.setPort(8080);
        server.setConnectors(new Connector[]{ serverConnector });

        server.setHandler(new RoutingHandler());

        server.start();
        server.join();
    }
}

class RoutingHandler extends AbstractHandler {

    private final HttpRouter<HttpHandler> httpRouter = new HttpRouter<>();

    public RoutingHandler() {
        httpRouter
                .GET("/users/:username/uppercase", new UppercaseUsernameHandler())
                .POST("/random-num", new RandomNumHandler());
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        var routerMatch = httpRouter.match(baseRequest.getMethod(), target);
        if (routerMatch != null) {
            HttpHandler handler = routerMatch.getAttachment();
            Params params = routerMatch.getParams();

            handler.handle(request, response, params);
            baseRequest.setHandled(true); // otherwise you will receive default 404 page
        }
    }
}

// ------------------------------- Request handlers --------------------------------

interface HttpHandler {
    // our version of http handler that receives Params arg

    void handle(HttpServletRequest request, HttpServletResponse response, Params params) throws IOException, ServletException;
}

class UppercaseUsernameHandler implements HttpHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Params params) throws IOException {
        String username = params.getParam("username");
        response.setStatus(200);
        response.setContentType("text/plain;charset=utf-8");
        response.getWriter().write("Uppercase username: " + username.toUpperCase() + "\n");
    }
}

class RandomNumHandler implements HttpHandler {

    private final SecureRandom random = new SecureRandom();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Params params) throws IOException {
        response.setStatus(200);
        response.setContentType("text/plain;charset=utf-8");
        String respText = String.format("Random num between 0 and 100: %s\n", random.nextInt(100));
        response.getWriter().write(respText);
    }
}
