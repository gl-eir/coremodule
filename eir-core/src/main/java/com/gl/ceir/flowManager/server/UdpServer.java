package com.gl.ceir.flowManager.server;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

@Service
@Slf4j
public class UdpServer implements IUdpServer {

    private DatagramSocket socket;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("receivingQueue")
    private BlockingQueue<String> receivingQueue;

    @Override
    public void run() {
        logger.info("Start receiving now at port : {}", socket.getLocalPort());
        byte[] packet = new byte[4000];
        while (true) {
            try {
                DatagramPacket request = new DatagramPacket(packet, packet.length);
                socket.receive(request);
                String data = new String(request.getData(), 0, request.getLength());
                logger.info("Request received at UDP : " + data + " current Queue Size : " + receivingQueue.size());
                receivingQueue.add(data);

            } catch (Exception e) {
                logger.error("Error in receiving", e);
            }
        }

    }

    @Override
    public void startServer(int port) {
        try {
            socket = new DatagramSocket(port);
            logger.info("UDP Server socket bind at : {}", port);
        } catch (Exception e) {
            logger.error("Unable to start udp Server", e);
        }
    }
}