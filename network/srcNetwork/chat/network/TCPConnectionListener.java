package chat.network;

public interface TCPConnectionListener {              // прописываем событи дя класса TCPConection, слой абстракции

    void onConnectionReady(TCPConection tcpConection);
    void onReceiveString(TCPConection tcpConection, String value);
    void onDisconnect(TCPConection tcpConection);
    void onException(TCPConection tcpConection, Exception e);
}
