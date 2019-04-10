package com.langsin.snake;

/*
File:Game.java
Author: 胡鑫
Description:游戏主体
*/
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class Game extends JFrame implements ActionListener,KeyListener{
	enum Direction{up,down,left,right};	//蛇移动的四个方向
	private int ID;	//登录用户的id
	private Food food;
	private Snake snake;
	private JPanel gamePanel;	//绘制游戏的面板
	private int score=0;	//实时分数统计
	private Timer timer;	//Timer类的实例，用以循环绘制游戏，会循环触发actionPerformed
	private final int speedSlow=200;	//蛇慢速移动的速度
	private final int speedFast=50;	//按下SHIFT键蛇快速移动的速度，因为是Timer的delay时间，所以该值越小，速度越快
	private final int gamePanelWidth=875;	//游戏界面的宽
	private final int gamePanelHeight=700;	//游戏界面的高（875*700的宽高，格子数是35*28、食物25*25）
	private boolean isGameStart=false;	//游戏状态的记录
	private boolean isGameRunning=false;
	
	private boolean isGameFailed=false;
	private ImageIcon fail;	//游戏失败的图片
	private ImageIcon pause;	//游戏暂停的图片
	private JLabel label_maxscore;	//显示最高分
	private JLabel label_time;	//显示局数
	private JLabel label_score;	//显示分数
	private JLabel label_len;	//显示蛇长度
	private JLabel label_account;	//显示当前账号
	private Users users;
	
	//各个组件的绘制
	Game(JFrame mainFrame,int id){
		ID=id;
		this.setSize(gamePanelWidth+180,gamePanelHeight+50);
		this.setLayout(null);
		this.setResizable(false);
		this.setLocationRelativeTo(null);//固定窗口的位置  Null：在屏幕中央出现
		this.setTitle("贪吃蛇");
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//调用任意已注册 WindowListener 的对象后自动隐藏并释放该窗体。 

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				timer.stop();
				mainFrame.setVisible(true);
			}
		});
		
		gamePanel=new GamePanel();
		gamePanel.setBounds(0,0,gamePanelWidth,gamePanelHeight);
		gamePanel.setBackground(new Color(230,230,230));//灰阶色调
		this.add(gamePanel);
		
		//显示用户信息
		JPanel panel_account=new JPanel();
		panel_account.setBounds(gamePanelWidth+10,0,150,100);
		panel_account.setBorder(BorderFactory.createTitledBorder("Account"));//边框上显示题目
		panel_account.setLayout(new BoxLayout(panel_account,BoxLayout.Y_AXIS));//边框垂直布局
		this.add(panel_account);
		
		JLabel label_id=new JLabel("ID:"+ID);
		panel_account.add(label_id);
		label_account=new JLabel("用户名:");
		panel_account.add(label_account);
		label_maxscore=new JLabel("最高分:");
		panel_account.add(label_maxscore);
		label_time=new JLabel("局数:");
		panel_account.add(label_time);
		
		//游戏实时进展
		JPanel panel_game=new JPanel();
		panel_game.setBounds(gamePanelWidth+10,100,150,60);
		panel_game.setBorder(BorderFactory.createTitledBorder("Game"));
		panel_game.setLayout(new BoxLayout(panel_game,BoxLayout.Y_AXIS));
		this.add(panel_game);
		
		label_score=new JLabel("分数:0");
		panel_game.add(label_score);
		label_len=new JLabel("长度:3");
		panel_game.add(label_len);
		 
        //选择多种角色  （特别鸣谢王校长，嘻嘻）
		JComboBox comboBox_playerSkin=new JComboBox();//下拉框选择玩家角色
		String[] select={"角色1","角色2","角色3"};
		comboBox_playerSkin.setModel(new DefaultComboBoxModel(select));
		comboBox_playerSkin.setBounds(gamePanelWidth+10,200,150,20);
		comboBox_playerSkin.addItemListener(new ItemListener() {
			//角色改变触发监听
			public void itemStateChanged(ItemEvent arg0) {
				snake.changeSkin(comboBox_playerSkin.getSelectedIndex()+1);	//+1是因为资源文件夹里的图片的序号比这里都大1
				repaint();
				gamePanel.requestFocus();//重新获取焦点以监听按键
			}
		});
		this.add(comboBox_playerSkin);
		
		//选择多种食物
		JComboBox comboBox_foodSkin=new JComboBox();
		String[] select2={"食物1","食物2","食物3","食物4","食物5","食物6"};
		comboBox_foodSkin.setModel(new DefaultComboBoxModel(select2));
		comboBox_foodSkin.setBounds(gamePanelWidth+10,225,150,20);
		comboBox_foodSkin.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				food.changeSkin(comboBox_foodSkin.getSelectedIndex()+1);	//+1是因为资源文件夹里的图片的序号比这里都大1
				repaint();
				gamePanel.requestFocus();	
			}
		});
		this.add(comboBox_foodSkin);
		
		
		
		JButton btn_begin=new JButton("开始游戏");
		btn_begin.setBounds(gamePanelWidth+10,250,150,30);
		btn_begin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isGameStart && !isGameFailed && !isGameRunning) {	//当前为暂停状态，由暂停恢复游戏的运行
					gamePanel.requestFocus();	//获取焦点以此监听键盘按键
					timer.start();
					isGameRunning=true;
				}else {	//游戏刚开始
					gamePanel.requestFocus();
					snake.reborn();
					score=0;
					timer.start();
					isGameStart=true;
					isGameRunning=true;
					isGameFailed=false;
				}
			}
		});
		this.add(btn_begin);
		
		ImageIcon helpicon=new ImageIcon("pic/help.png");
		JLabel help=new JLabel();
		help.setBounds(gamePanelWidth+10,350,150,250);
		help.setIcon(helpicon);//设置要显示的图标
		this.add(help);
		
		init();	//初始游戏参数
	}
	
	//初始化，初始各个参数
	private void init() {
		snake=new Snake();
		food=new Food();
		score=0;
		users=new Users();
		showAccountInformation();
		gamePanel.setFocusable(true);
		gamePanel.addKeyListener(this);
		timer=new Timer(speedSlow,this);	//一开始，未按下SHIFT蛇的速度应该为speedSlow
		fail=new ImageIcon("pic/fail.png");
		pause=new ImageIcon("pic/pause.png");
	}
	
	//显示用户信息
	private void showAccountInformation() {
		label_account.setText("用户名:"+users.getAccount(ID));
		label_maxscore.setText("最高分:"+users.getMaxScore(ID));
		label_time.setText("局数:"+users.getTime(ID));
	}
	
	//游戏失败后的处理
	private void failed() {
		users.modifyMaxScoreAndTime(ID, users.getMaxScore(ID)>score?users.getMaxScore(ID):score, users.getTime(ID)+1);
		showAccountInformation();
		isGameStart=false;
		isGameRunning=false;
		isGameFailed=true;
	}
	
	private class GamePanel extends JPanel{
		//重写paint方法，用以绘制游戏界面
		public void paint(Graphics g) {
			super.paint(g);
			
			//绘制游戏方格
			g.setColor(new Color(250,250,250));
			for(int i=0;i<gamePanelWidth;i+=25)
				g.drawLine(i, 0, i, gamePanelHeight);
			for(int i=0;i<gamePanelHeight;i+=25)
				g.drawLine(0, i, gamePanelWidth, i);
			
			//绘制食物，food.food是food对象里的food图片
			food.food.paintIcon(this, g, food.x-(food.food.getIconWidth()/2-25/2), food.y-(food.food.getIconHeight()/2-25/2));
			
			//绘制蛇体
			for(int i=1;i<snake.len;i++) {
				snake.body.paintIcon(this, g, snake.x[i], snake.y[i]);
			}
			//绘制蛇头
			switch(snake.direction) {
			case up:snake.up.paintIcon(this, g, snake.x[0]-(snake.up.getIconWidth()/2-25/2),snake.y[0]-(snake.up.getIconHeight()-25)-3);break;
			case down:snake.down.paintIcon(this, g, snake.x[0]-(snake.down.getIconWidth()/2-25/2),snake.y[0]+3);break;
			case left:snake.left.paintIcon(this, g, snake.x[0]-(snake.left.getIconWidth()-25)-3,snake.y[0]-(snake.left.getIconHeight()/2-25/2));break;
			case right:snake.right.paintIcon(this, g, snake.x[0]+3,snake.y[0]-(snake.right.getIconHeight()/2-25/2));break;
			}
			
			if(isGameFailed) {
				fail.paintIcon(this, g, 0, 150);
			}
			
			if(isGameStart && !isGameRunning) {
				pause.paintIcon(this, g, 0, 150);
			}
		}
	}
	
	private class Snake{
		int[] x=new int [gamePanelWidth*gamePanelHeight];
		int[] y=new int [gamePanelWidth*gamePanelHeight];
		int len;
		Direction direction;
		ImageIcon up=new ImageIcon("skin/skin1/up.png");
		ImageIcon down = new ImageIcon("skin/skin1/down.png");
	    ImageIcon left = new ImageIcon("skin/skin1/left.png");
	    ImageIcon right = new ImageIcon("skin/skin1/right.png");
	    ImageIcon body = new ImageIcon("skin/skin1/body.png");
	    
	    Snake(){
	        x[2]=0;x[1]=25;x[0]=50;
	        y[2]=y[1]=y[0]=25;
	        len=3;
	        direction=direction.right;
	    }
	    
	    public void reborn() {
	    	x[2]=0;x[1]=25;x[0]=50;
	        y[2]=y[1]=y[0]=25;
	        len=3;
	        direction=direction.right;
	    }
	    
	    public void changeSkin(int i) {
	    	up=new ImageIcon("skin/skin"+i+"/up.png");
			down = new ImageIcon("skin/skin"+i+"/down.png");
		    left = new ImageIcon("skin/skin"+i+"/left.png");
		    right = new ImageIcon("skin/skin"+i+"/right.png");
		    body = new ImageIcon("skin/skin"+i+"/body.png");
	    }
	    
	}
	
	private class Food{
		Random random=new Random();
		ImageIcon food=new ImageIcon("skin/skin1/food.png");
		int x;
		int y;
		
		Food(){
			reborn();
		}
		
		//随机生成食物的位置
		public void reborn() {
			x=random.nextInt(875/25)*25;
			y=random.nextInt(700/25)*25;
			
			for(int k=0;k<5;k++) {
				boolean flag=true;
				for(int i=0;i<snake.len;i++) {
					if(x==snake.x[i] && y==snake.y[i]) {
						x=random.nextInt(875/25)*25;
						y=random.nextInt(700/25)*25;
						flag=false;
						break;
					}
				}
				if(flag)
					break;
			}
		}
		
		public void changeSkin(int i) {
			food=new ImageIcon("skin/skin"+i+"/food.png");
		}
	}

	//每次游戏逻辑进行处理，并调用repaint重绘游戏界面
	@Override
	public void actionPerformed(ActionEvent e) {
		
		for(int i=snake.len;i>0;i--) {
			snake.x[i]=snake.x[i-1];
			snake.y[i]=snake.y[i-1];
		}
		
		switch(snake.direction) {
		case up:snake.y[0]-=25;break;
		case down:snake.y[0]+=25;break;
		case left:snake.x[0]-=25;break;
		case right:snake.x[0]+=25;break;
		}
		
		if(snake.x[0]==food.x && snake.y[0]==food.y) {
			score++;
			snake.len++;
			food.reborn();
		}
		
		for(int i=1;i<snake.len;i++) {
			if(snake.x[0]==snake.x[i] && snake.y[0]==snake.y[i]) {
				failed();
				timer.stop();
			}
		}
		
		if(snake.x[0]<0 || snake.x[0]>gamePanelWidth-25 || snake.y[0]<0 || snake.y[0]>gamePanelHeight-25) {
			failed();
			timer.stop();
		}
		
		label_score.setText("分数:"+score);
		label_len.setText("长度:"+snake.len);
		
		gamePanel.repaint();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		int keyCode = arg0.getKeyCode();
		
		if(isGameRunning) {
			switch(keyCode) {
			case KeyEvent.VK_UP:snake.direction=(snake.direction==Direction.down)?Direction.down:Direction.up;break;
			case KeyEvent.VK_DOWN:snake.direction=(snake.direction==Direction.up)?Direction.up:Direction.down;break;
			case KeyEvent.VK_LEFT:snake.direction=(snake.direction==Direction.right)?Direction.right:Direction.left;break;
			case KeyEvent.VK_RIGHT:snake.direction=(snake.direction==Direction.left)?Direction.left:Direction.right;break;
			}
			
			if(keyCode==KeyEvent.VK_SHIFT)
				timer.setDelay(speedFast);
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		int keyCode = arg0.getKeyCode();
		

		if(keyCode==KeyEvent.VK_SHIFT)
			timer.setDelay(speedSlow);

		
		if(isGameStart) {
			if(keyCode==KeyEvent.VK_SPACE){
				if(timer.isRunning()) {
					repaint();
					timer.stop();
					isGameRunning=false;
				}
				else {
					timer.start();
					isGameRunning=true;
				}
					
			}
		}
		
		if(keyCode==KeyEvent.VK_R) {
//			gamePanel.requestFocus();
//			snake.reborn();
//			score=0;
			timer.start();
			isGameStart=true;
			isGameRunning=true;
			isGameFailed=false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}
