package com.ilian.rtspserver;

import java.net.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;


import  com.ilian.rtspserver.UDP; /* udp for RTP */
import com.intellij.ide.ui.AppearanceOptionsTopHitProvider;

public class RtspServer extends Thread{

    public static final int MAX_USERS = 64;
	private static int clientsIds = 0;
	
	/* custom class for handling clients */
	protected  class ClientProc extends Thread {
		public int ID;
        public boolean isRunning = true;
		private Socket caller_sock = null;
		public ClientProc(Socket ref)
		{
			ID = clientsIds++; /* assign an unique id */
			caller_sock = ref; /* aggregation */ 
		}
		
		@Override
		public void run()
		{
			while(isRunning) {
                try {
                    if (_writeMessage("Client thread [" + ID + "]") == 0) {
                        try {
                            Thread.currentThread().sleep(100);
                        } catch (Exception ex) {/*eat it*/}
                    } else {
                        isRunning = false;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    isRunning = false;
                    return ;
                }
			}
		}

		private int _writeMessage(String msg) throws Exception {

            if (!caller_sock.isConnected()) {
                isRunning = false;
                return -1;
            }
            try {
                /* write the head to socket */
                DataOutputStream dataOUT =
                        new DataOutputStream(caller_sock.getOutputStream());
                dataOUT.writeUTF(msg);
            } catch (Exception ex) {
                ex.printStackTrace();
                isRunning = false;
                throw new Exception("Connection lost!");
            }
            return 0;
        }
	}



	/* access from anywhere */
	public   List<ClientProc> g_clients = new ArrayList<ClientProc>();
	
	private ServerSocket m_serv_sock = null;
	private  UDP    m_transport;

	
	public RtspServer(int port) throws IOException
	{
		m_serv_sock = new ServerSocket(port);
	}

	@Override
	public void run()
	{
		boolean isOpen = true;
        Socket server = null;
        while (isOpen) {
            try {
                System.out.println("Wait for client port "+
                m_serv_sock.getLocalPort()+ " ...");
                /* detach to another thread here */

                server = m_serv_sock.accept();

                if (server.isConnected()) {
                    System.out.println("Just connected to " +
                            server.getRemoteSocketAddress());
                    ClientProc cli = new ClientProc(server);
                    g_clients.add(cli);
                    cli.start();
                }

                for(int i=0; i < g_clients.size(); i++) {
                    if (!g_clients.get(i).isRunning) {
                        try {g_clients.get(i).join(); } catch (Exception ex){};
                        g_clients.remove(i);
                    }
                }

            } catch (SocketTimeoutException ex) {
                System.out.println("Socket timed out...");
                break;
            } catch (IOException ex2) {
                for(int i=0; i < g_clients.size(); i++) {
                    try {
                        g_clients.get(i).join();
                    } catch (Exception ex){}
                    g_clients.remove(i);
                }
                ex2.printStackTrace();
                isOpen = false;
                break;
            } finally {
                /* nothing */
            }
        }

        System.out.println("Connection lost!");

        try {
            server.close();
        } catch (Exception ex) {/* nothing */}

	}
	
	public static void main(String args[])
	{
		try {
			RtspServer rtsp = new RtspServer(10123);
			rtsp.start();
		} catch (Exception ex) {
			/* do nothing */
		}
	}
}
// end the server
