package chat.network;

import sun.rmi.transport.tcp.TCPConnection;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConection {

    private  final Socket socket;
    private  final Thread rxThread;
    private final TCPConnectionListener eventListner;
    private  final BufferedReader in;
    private final BufferedWriter out;

    public TCPConection(TCPConnectionListener eventListner, String ipAddress, int port) throws IOException{ //для клиента этот конструктор
        this(eventListner, new Socket(ipAddress, port));

    }

    public TCPConection(TCPConnectionListener eventListner, Socket socket) throws IOException { // кто-то создаст соединение снаружи
        this.eventListner = eventListner;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListner.onConnectionReady(TCPConection.this);
                    while(!rxThread.isInterrupted()){
                        String msg = in.readLine();
                        eventListner.onReceiveString(TCPConection.this, msg);
                    }

                } catch (IOException e) {
                    eventListner.onException(TCPConection.this, e);
                }finally {
                    eventListner.onDisconnect(TCPConection.this);
                }
            }
        });
        rxThread.start();
    }


    public synchronized void sendString(String value){
        try {
            out.write(value + "\r\n");
            out.flush(); //принудительно отправляем сообщения из буфера
        } catch (IOException e) {
            eventListner.onException(TCPConection.this, e);
            disconnect();
        }

    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListner.onException(TCPConection.this, e);
        }

    }


    @Override
    public String toString() {
        return "TCPConection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
