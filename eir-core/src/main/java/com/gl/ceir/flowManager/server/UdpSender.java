package com.gl.ceir.flowManager.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

@Service
public class UdpSender implements IUdpSender {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    DatagramSocket socket =null;

    @PostConstruct
    public void inIt()
    {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            logger.error("Error in ups sender ",e);
        }
    }

    @Override
    public void send(String ip, int port, String data) {
        byte[] send_data = data.getBytes();
        try {
            InetAddress clientAddress = InetAddress.getByName(ip);
            DatagramPacket responsePacket = new DatagramPacket(send_data, send_data.length, clientAddress, port);
            socket.send(responsePacket);
            logger.info("Packet {}, Sent to IP : {} , PORT : {}",data,ip,port);
        }catch (Exception e){
            logger.error("Error sending packet {}, IP : {} , PORT : {}",data,ip,port,e);
        }
    }

}
