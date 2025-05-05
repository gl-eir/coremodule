package com.gl.ceir.flowManager.server;

import java.util.Queue;

public interface IUdpSender {

    void send(String ip, int port, String data);
}
