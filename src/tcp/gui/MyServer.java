package tcp.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GaryLee on 2018-08-05 14:33.
 * 用tcp结合gui做的简易聊天器
 * 先启动server类，再启动client类
 */
public class MyServer extends JFrame{
    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    public MyServer(){
        init();
        startServer();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(8888);//默认端口8888
            socket = serverSocket.accept();
            printWriter = new PrintWriter(socket.getOutputStream(),true);//true即自动flush
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new ServerThread().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class ServerThread extends Thread{
        @Override
        public void run() {
            jTextArea.append("客户端"+socket.getInetAddress()+"已连接...\r\n");
            String line = null;
            try {
                //判断是否有输入流(有则读取并append)
                while ((line = bufferedReader.readLine()) != null) {
                    jTextArea.append(line+"\r\n");
                }
            }catch (Exception e){

            }
        }
    }
    private JTextArea jTextArea;
    private void init() {
        this.setTitle("服务器");
        this.setSize(400,600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        JPanel jPanel = new JPanel();
        JTextField jTextField = new JTextField(28);
        JButton jButton = new JButton("发送");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取用户输入内容
                String str = jTextField.getText()+"\r\n";
                //格式化当前时间
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = simpleDateFormat.format(date);
                //最终的字符串拼接
                String msg = "服务器["+time+"]:\r\n"+str;
                printWriter.println(msg);

                jTextArea.append(msg+"\r\n");

                jTextField.setText(null);
            }
        });
        jPanel.add(jTextField);
        jPanel.add(jButton);
        this.add(jPanel,BorderLayout.SOUTH);

        this.setVisible(true);//放最后
    }
    public static void main(String[] args) {
        new MyServer();
    }
}
