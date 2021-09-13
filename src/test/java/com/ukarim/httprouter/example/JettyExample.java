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
        httpRouter.addRoute("GET", "/users/:username/uppercase", new UppercaseUsernameHandler());
        httpRouter.addRoute("GET", "/hello", new HelloHandler());
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        var routerMatch = httpRouter.match(baseRequest.getMethod(), target);
        if (routerMatch != null) {
            HttpHandler handler = routerMatch.getAttachment();
            Params params = routerMatch.getParams();

            handler.handle(request, response, params);
            baseRequest.setHandled(true);
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
        response.getWriter().write("Uppercase username: " + username.toUpperCase());
    }
}

class HelloHandler implements HttpHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Params params) throws IOException {
        response.setStatus(200);
        response.setContentType("text/plain;charset=utf-8");
        response.getWriter().write("Hello!");
    }
}
