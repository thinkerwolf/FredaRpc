package com.freda.remoting.web;

public interface WebServer {

    void start() throws WebServerException;

    void stop() throws WebServerException;

}
