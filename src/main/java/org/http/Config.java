package org.http;

import com.sun.net.httpserver.HttpHandler;

import java.util.Map;

public interface Config {
    Map<String, HttpHandler> getServerHandlers();
}
