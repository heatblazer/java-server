package com.ilian.rtspserver;

import java.net.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

/* customs */
import  com.ilian.rtspserver.UDP; /* udp for RTP */
import com.intellij.ide.ui.AppearanceOptionsTopHitProvider;
import com.ilian.rtspserver.RTSP;
import com.ilian.html.HTML;

public class RtspServer extends Thread {

    public static final int MAX_USERS = 64;
	private static int clientsIds = 0;
	
	/* custom class for handling clients */
	protected  class ClientProc extends Thread {
		protected int ID;
        protected boolean isRunning = true;
		protected Socket caller_sock = null;
        protected Object m_user_data;

		protected ClientProc(Socket ref, Object user_data)
		{
			ID = clientsIds++; /* assign an unique id */
			caller_sock = ref; /* aggregation */
			m_user_data = user_data; /* attach user data */
		}

		@Override
		public void run()
		{
            HTML html = new HTML();
			while(isRunning) {
                /* do something with user data */

                /* just print message */
                print_message("some message... ");
			}
		}

        private void print_message(String s)
        {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(s);
                if (_writeMessage(sb.toString()) == 0) {
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

		private int _writeMessage(String msg) throws Exception {

            DataOutputStream dataOUT = null;
            if (!caller_sock.isConnected()) {
                isRunning = false;
                return -1;
            }
            try {
                /* write the head to socket */
                dataOUT =  new DataOutputStream(caller_sock.getOutputStream());
                dataOUT.writeUTF(msg);
                dataOUT.flush();

            } catch (Exception ex) {
                dataOUT.flush();
                dataOUT.close();
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

	private void sleepCurrentThread(int mills) {
        try {
            Thread.currentThread().sleep(mills);
        } catch (Exception ex) {
        }
    }

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
                    /* pass user data - may be a UDP packet or whatever it may be */
                    ClientProc cli = new ClientProc(server, null);
                    g_clients.add(cli);
                    cli.start();
                }

                for(int i=0; i < g_clients.size(); i++) {
                    if (!g_clients.get(i).isRunning) {
                        try {g_clients.get(i).join(); } catch (Exception ex){};
                        g_clients.remove(i);
                    }
                }
                sleepCurrentThread(100); /* give a bit of time to breathe, so other threads are working good */

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
                /* do some finalizing here if needed */
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
