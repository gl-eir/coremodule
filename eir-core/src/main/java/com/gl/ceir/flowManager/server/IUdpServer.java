package com.gl.ceir.flowManager.server;

import java.util.Queue;

public interface IUdpServer extends Runnable{
    void startServer(int port);
}
