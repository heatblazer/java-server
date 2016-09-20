package com.ilian.rtspserver;

import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;


/**
 * Created by ilian on 9/19/16.
 */
public class UDP {

    public static class Address
    {
        public InetAddress address;
        int port;
    }

    protected DatagramSocket m_socket = null;
    protected DatagramPacket m_packet = null;
    protected BufferedReader m_reader = null;
    protected  byte[] m_buffer = null;

    public UDP(int port, int buff_size)
    {
        try {
            m_socket = new DatagramSocket(port);
        } catch (Exception ex) {
            ex.printStackTrace(); /* failed to create dgram */
        }
        m_buffer = new byte[buff_size]; /* size of rtp packet */

    }

    public Address recievePacket() throws  IOException
    {
        byte[] data = new byte[160];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        m_socket.receive(packet);
        Address adr = null;
        adr.address = packet.getAddress();
        adr.port = packet.getPort();
        return adr;
    }

    public void sendPacket(String message, Address address) throws IOException
    {
        if (message.length() >= 160) {
            return ; /* rtp packet too big ... */
        }
        byte[] buff = new byte[160];
        buff = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buff, buff.length, address.address, address.port);
        m_socket.send(packet);
    }
}
