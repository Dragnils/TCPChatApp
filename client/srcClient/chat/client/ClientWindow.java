package chat.client;

import chat.network.TCPConection;
import chat.network.TCPConnectionListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener { // от интерфейса ActionListener берем чтобы перехватывать нажатие клавиши Enter
    // TCPConnectionListener интерфесй чтобы слушать TCPConnection, в данном случае eventListener будет само ОКНО
    private  static final String IP_ADDR = "localhost";
    private  static final int PORT = 1234;
    private static  final int WDTH = 600;
    private static  final int HEIGHT = 400;

    private TCPConection connection;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() { //в графи-х интерфейсах нельзя работать с различных потоков, во Swing можно работать в потоке EDT
            @Override
            public void run() {
                new ClientWindow(); //выполняется в потоке EDT
            }
        });

    }

    private final JTextArea log = new JTextArea(); //поле куда выходят наши сообщения
    private  final JTextField fieldNickName = new JTextField("Anuar");
    private final JTextField fieldInput = new JTextField();//однострочное поле куда мы будем писать текст

    private ClientWindow(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//дефолтом закрываем окно, если нажали на крестик окна
        setSize(WDTH, HEIGHT); // размер окна
        setLocationRelativeTo(null); // чтобы окно было по середине
        setAlwaysOnTop(true); //чтобы окно всегда было сверху, и не убералось под другое окно


        log.setEditable(false); // запрещаем редатирование в данном окне
        log.setLineWrap(true); // автоматический перенос слов
        add(log, BorderLayout.CENTER); // добвляем наш log в наше окно по центру


        fieldInput.addActionListener(this); // добавляем себя в поле fieldInput чтобы перехватить
        add(fieldInput, BorderLayout.SOUTH); //добвляем наш fieldInput в наше окно вниз, т.е. на юг
        add(fieldNickName, BorderLayout.NORTH); //добвляем наш fieldNickName в наше окно вверх, т.е. на север

        setVisible(true);//чтобы мы увидели окно
        try {
            connection = new TCPConection(this, IP_ADDR, PORT); // в данном случае eventListener будет само ОКНО
        } catch (IOException e) {
            printMsg("Connection eception: " + e);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) { // по нажатию кнопки передать сообщение
        String msg = fieldInput.getText(); //
        if(msg.equals("")) return; // при случайном нажатии кнопки когда пустая строка чтобы ничего не передавалось
        fieldInput.setText(null); // если все таки есть строчка то мы должны стереть что есть в нашем поле инпут
        connection.sendString(fieldNickName.getText() + ": " + msg); // в наше соединение мы могли передать имя и само сообщение

    }


    @Override
    public void onConnectionReady(TCPConection tcpConection) {
        printMsg("Connection ready...");

    }

    @Override
    public void onReceiveString(TCPConection tcpConection, String value) {
        printMsg(value);

    }

    @Override
    public void onDisconnect(TCPConection tcpConection) {
        printMsg("Connection close");

    }

    @Override
    public void onException(TCPConection tcpConection, Exception e) {
        printMsg("Connection eception: " + e);

    }

    private synchronized void printMsg(String msg){ // чтобы выводить текст в наше текстовое поле, synchronized для того чтобы этот метод испл-ся в разных потоках
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength()); //чтобы автоскролл был автоматом
            }
        });

    }
}
