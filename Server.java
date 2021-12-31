import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Write a description of class S here.
 * This is a stand-alone program that creates a single client server.
 * @author Nikhil Gupta ©2021-22
 * @version 1.0.2021
 */
public class Server extends Frame implements ActionListener{

    TextField textFieldPort, msgBox;
    Label connectionStatus, port;
    Button startStop, send;
    TextArea chatBox;

    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    static String incomingMessage = "", outgoingMessage = "",chatUpdate="";

    public Server() {
        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\home_server_icon.png");
        setIconImage(icon);
        setTitle("SERVER Host - Nikhil Gupta ©2021-22");
        //set hint here for port
        textFieldPort = new TextField();
        port = new Label(": Port");
        connectionStatus = new Label("Enter port before starting");
        startStop = new Button("Start");
        chatBox = new TextArea("");
        msgBox = new TextField("Type your message");
        send = new Button("Send");
        chatBox.setEditable(false);
        chatBox.setFocusable(false);
        startStop.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chatBox.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        send.setCursor(new Cursor(Cursor.HAND_CURSOR));
        textFieldPort.setBounds(50,50,150,20);
        port.setBounds(200,50,100,20);
        connectionStatus.setBounds(50,80,500,20);
        startStop.setBounds(50,110,60,30);
        chatBox.setBounds(50,150,300,250);
        msgBox.setBounds(50,400,240,25);
        send.setBounds(290,400,60,25);
        add(textFieldPort);add(port);
        add(connectionStatus);
        add(startStop);
        add(chatBox);
        add(msgBox);
        add(send);
        send.addActionListener(this);
        startStop.addActionListener(this);
        setSize(400,550);
        setLayout(null);
        setVisible(true);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                new JFrame("Exit");
                if (startStop.getLabel().equals("Stop")){
                    if (JOptionPane.showConfirmDialog( Server.this,"Server still running, sure exit?","Server",
                            JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
                        System.exit(0);
                }else {
                    System.exit(0);
                }
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
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                dataOutputStream.writeUTF(outgoingMessage);
                dataOutputStream.flush();
                msgBox.setText("");
                chatUpdate = chatUpdate+outgoingMessage+"\n";
                chatBox.setText(chatUpdate);
            } catch (IOException ex) {
                ex.printStackTrace();
                connectionStatus.setText("Server restart required!");
//                startStop.setLabel("Restart");
            }
        }else if (e.getSource()==startStop){
            if(startStop.getLabel().equals("Start")){
//            if(!textFieldPort.equals(null)){//check emptiness and number validation
//
//                }
                try {
                    serverSocket = new ServerSocket(Integer.parseInt(textFieldPort.getText()));
                    startStop.setLabel("Stop");
                    connectionStatus.setText("Waiting for connection...");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                try {
                    socket = serverSocket.accept();
                } catch (IOException ex) {
                    ex.printStackTrace();
//                connectionStatus.setText("Connection Terminated!");
                }
                connectionStatus.setText("Connected to a client");
                try {
                    dataInputStream = new DataInputStream(socket.getInputStream());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                input.start();
            }else if (startStop.getLabel().equals("Stop")){
                try {
                    input.interrupt();
                    dataInputStream.close();
                    socket.close();
                    serverSocket.close();
                    startStop.setLabel("Start");
                    connectionStatus.setText("Stopped!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }else if (send.getLabel().equals("Restart")){

            }
        }
    }
    class Input extends Thread{
        @Override
        public void run() {
            try {
                while (!incomingMessage.equals(".exit")){
                    incomingMessage = dataInputStream.readUTF();
                    chatUpdate = chatBox.getText()+"Client Says: "+incomingMessage+"\n";
                    chatBox.setText(chatUpdate);
                }
                System.out.println(".exit executed, now outside loop but in thread");
                interrupt();
                dataInputStream.close();
                socket.close();
                serverSocket.close();
                startStop.setLabel("Start");
                connectionStatus.setText("Stopped!");
            } catch (IOException e) {
                e.printStackTrace();
                connectionStatus.setText("Connection terminated!");
            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args){
        new Server();
    }
}