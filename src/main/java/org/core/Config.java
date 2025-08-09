package org.core;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpHandler;

import java.util.Collection;
import java.util.Map;

public interface Config {
    Map<String, HttpHandler> getServerHandlers();
    Map<String, Collection<Filter>> getServerFilters();
}
