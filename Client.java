import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
/**
 * Write a description of class Server here.
 * This is a stand-alone program that connects to a serversocket.
 * This program can be used to chat with a server.
 * @author Nikhil Gupta ©2021-22
 * @version 1.0.2021
 */
public class Client extends Frame implements ActionListener {

    TextField textFieldIP, textFieldPort, msgBox;
    Label connectionStatus,ip, port;
    Button connect, send;
    TextArea chatBox;

    Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    static String incomingMessage = "", outgoingMessage = "", chatUpdate="";
    public Client() {
        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\client_icon.png");
        setIconImage(icon);
        setTitle("CLIENT - by Nikhil Gupta");//also set input hints
        textFieldIP = new TextField();
        textFieldPort = new TextField();
        ip = new Label(": IP");
        port = new Label(": Port");
        connectionStatus = new Label("Press Connect!");
        connect = new Button("Connect");
        chatBox = new TextArea();
        msgBox = new TextField("Type your message");
        send = new Button("Send");
        chatBox.setEditable(false);
        chatBox.setFocusable(false);
        connect.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chatBox.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        send.setCursor(new Cursor(Cursor.HAND_CURSOR));
        textFieldIP.setBounds(50,50,150,20);
        textFieldPort.setBounds(50,75,150,20);
        ip.setBounds(200,50,100,20);
        port.setBounds(200,75,100,20);
        connectionStatus.setBounds(50,100,500,20);
        connect.setBounds(50,130,80,30);
        chatBox.setBounds(50,170,300,250);
        msgBox.setBounds(50,420,240,25);
        send.setBounds(290,420,60,25);
        add(textFieldIP);add(ip);
        add(textFieldPort);add(port);
        add(connectionStatus);
        add(connect);
        add(chatBox);
        add(msgBox);
        add(send);
        send.addActionListener(this);
        connect.addActionListener(this);
        setSize(400,550);
        setLayout(null);
        setVisible(true);
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Input input = new Input();
        if (e.getSource()==send){
            try {
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                outgoingMessage = msgBox.getText();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            try {
                dataOutputStream.writeUTF(outgoingMessage);
                dataOutputStream.flush();
                msgBox.setText("");
                chatUpdate = chatUpdate+outgoingMessage+"\n";
                chatBox.setText(chatUpdate);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                connectionStatus.setText("Server Lost");
            }
        }else if(e.getSource()==connect){
            if (connect.getLabel().equals("Connect")){
                try {
                    socket = new Socket(textFieldIP.getText(),Integer.parseInt(textFieldPort.getText()));
                    connect.setLabel("Disconnect");
                    connectionStatus.setText("Connected!");
//                dataOutputStream = new DataOutputStream(socket.getOutputStream());
//                outgoingMessage = "This chat is\nend to end connected\n";
//                dataOutputStream.writeUTF(outgoingMessage);
//                dataOutputStream.flush();
//                chatUpdate = chatUpdate + outgoingMessage+"\n";
//                chatBox.setText(chatUpdate);
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    input.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }else if (connect.getLabel().equals("Disconnect")){
                try {
                    input.interrupt();
                    dataInputStream.close();
                    socket.close();
                    connect.setLabel("Connect");
                    connectionStatus.setText("Disconnected!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    class Input extends Thread{
        @Override
        public void run() {
            try {
                while (!incomingMessage.equals(".exit")){
                    incomingMessage = dataInputStream.readUTF();
                    chatUpdate = chatBox.getText()+"Server Says: "+incomingMessage+"\n";
                    chatBox.setText(chatUpdate);
                }
                System.out.println(".exit executed, outside loop and in thread");
                interrupt();
                dataInputStream.close();
                socket.close();
                connect.setLabel("Connect");
                connectionStatus.setText("Server Lost");
            } catch (IOException e) {
                e.printStackTrace();
                interrupt();
                try {
                    dataInputStream.close();
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                connect.setLabel("Connect");
                connectionStatus.setText("Server Lost!");
            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        new Client();
    }
}