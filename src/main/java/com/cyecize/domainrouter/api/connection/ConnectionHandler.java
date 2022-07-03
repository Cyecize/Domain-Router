package com.cyecize.domainrouter.api.connection;

import java.net.Socket;

public interface ConnectionHandler {
    void process(Socket socket);
}
