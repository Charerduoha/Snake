package com.langsin.snake;

import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Log extends JFrame {
    //设置一个含有背景图片的面板
    private MyPanel panel;
    private JLabel lab_accont = new JLabel("账号:");
    private JTextField jta_text = new JTextField();
    Font lab = new Font("楷体",1,30);
    private JLabel lat_password = new JLabel("密码:");
    Font lat  = new Font("楷体",1,30);
    private JPasswordField jtb_text = new JPasswordField();
    private JButton btn_register = new JButton("注册");
    private JButton btn_land = new JButton("登陆");
    Font btn = new Font("黑体",2,30);
    public static  int pd = 0;
    public static String ak1, ak2;
    private Container contentPane = this.getContentPane();
 
    public Log () {
        this.setSize(400,267);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setTitle("账号管理");
        this.setResizable(false);
        btn_register.addActionListener(new ActionListener() {
            @SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
                pd = 1;
                ak1 = jta_text .getText();
                ak2 = jtb_text .getText();
                String fileName = "users.txt";
                try {
                    FileWriter writer = new FileWriter(fileName,true);
                    BufferedWriter outWriter=new BufferedWriter(writer);
                    outWriter.write("ID:"+ak1+"&&"+"Password:"+ak2);
                    outWriter.newLine();
                    outWriter.close();
                    writer.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                jta_text.setText("");
                jtb_text.setText("");
                JOptionPane.showMessageDialog(null,"注册成功！ 您的用户名是："+ak1+"密码是："+ ak2);
            }
        });
        btn_land.addActionListener(new ActionListener() {
            @SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
            	
                if(pd==1||pd==0) {
                	File fileName=new File("users.dat");
                	try{
                	FileReader inOne=new FileReader(fileName);
                	BufferedReader inTwo=new BufferedReader(inOne);
                	String s=null;
                	int judge=1;
                	while((s=inTwo.readLine())!=null)
                	{
                	  StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("ID:");
						stringBuilder.append(jta_text.getText());
						stringBuilder.append("&&");
						stringBuilder.append("Password:");
						stringBuilder.append(jtb_text.getText());
					String ak=stringBuilder.toString();
                	  
                	  
                      if(s.equals(ak)) {
                        JOptionPane.showMessageDialog(null, "登录成功！");
                        jta_text .setText("");
                        jtb_text .setText("");
                        judge=0;
                        
                        break;
                      }
                	}  
                	inTwo.close();
                    inOne.close();
                	if(judge==1) {
                         JOptionPane.showMessageDialog(null,"登陆失败！检查账号密码是否正确或请注册后登录",null,JOptionPane.ERROR_MESSAGE, null);
                         jta_text .setText("");
                         jtb_text .setText("");
                      }
//                	 if(judge==0){
//                	new JMFMediaPlayer("JMF媒体播放器");  //在此处new出新的窗口
//                		 dispose(); 
//                		 return; 
//                	 }
                	}catch(IOException event){
                		JOptionPane.showMessageDialog(null,"系统错误"+event,null,JOptionPane.ERROR_MESSAGE, null);
                	}
                }
            }
 
        });
         
        init();
         
        panel = new MyPanel();
        //将所有的组件添加到panel面板中
        panel.add(lab_accont);
        panel.add(lat_password);
        panel.add(jta_text);
        panel.add(jtb_text);
        panel.add(btn_register);
        panel.add(btn_land);
        panel.setLayout(null);
         
        getContentPane().add(panel);
    }
    private void init() {
        lab_accont.setSize(200,100);
        lab_accont. setLocation(50,20);
        lab_accont.setFont(lab);
        lat_password.setSize(200,100);
        lat_password.setLocation(50,80);
        lat_password.setFont(lat);
        jta_text.setSize(190,30);
        jta_text.setLocation(160,55);
        jtb_text.setSize(190,30);
        jtb_text.setLocation(160,115);
        btn_register.setSize(120, 40);
        btn_register.setLocation(60, 180);
        btn_land.setSize(120, 40);
        btn_land.setLocation(230, 180);
        btn_register.setFont(btn);
        btn_land.setFont(btn);
    }
     
    public static void main(String[] args)
    throws ClassNotFoundException,
        InstantiationException, IllegalAccessException,
        UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        new Log().setVisible(true);
    }
}
 

 
//自定义一个面板类
class MyPanel extends JPanel{
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        //绘制一张背景图片  2.jpg是图片的路径  自己设定为自己想要添加的图片
        Image image = new ImageIcon("background.jpg").getImage();
        g.drawImage(image, 0, 0, this);
    }
}
