package chat.server;

import chat.network.TCPConection;
import chat.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {


     public static void main(String[] args) {
         new ChatServer();


    }

    private  final ArrayList<TCPConection> connections = new ArrayList<>(); //список всех соединений

    private ChatServer(){
        System.out.println("Server Running...");
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            while (true){
                try{
                    new TCPConection(this, serverSocket.accept());

                }catch(IOException e){
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConection tcpConection) {
        connections.add(tcpConection);
        sendAllConnection("Client connected: " + tcpConection); //когда складываем какой то объект со строкой, то неявно вызывается метод toString(), мы переопредили в классе TCPConection метод toString()

    }

    @Override
    public synchronized void onReceiveString(TCPConection tcpConection, String value) {
        sendAllConnection(value);

    }

    @Override
    public synchronized void onDisconnect(TCPConection tcpConection) {
        connections.remove(tcpConection);
        sendAllConnection("Client disconnected: " + tcpConection);

    }

    @Override
    public synchronized void onException(TCPConection tcpConection, Exception e) {
        System.out.println("TCPConnection exception: " + e);

    }

    private  void sendAllConnection(String value){ //если приняли строчку, нам надо разослать всем клиентам
        System.out.println(value);
        final int cnt = connections.size();
        for (int i = 0; i < cnt; i++) {
            connections.get(i).sendString(value); // пробежались по спику всех соединений и отправили всем клиентам
        }
    }
}
