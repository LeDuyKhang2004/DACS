	
package FLB;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;


import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class FlappyBird extends JPanel implements ActionListener,KeyListener{
	
    Image backgroundImg;
    Image birdImg;
    Image topPipImg;
    Image bottomImg;
    
    //Birds
    int birdX = 360/8;//Vị trí của con chim cánh mép trái cửa sổ 45px
    int birdY = 640/2;//cách mép trên cửa sổ 
    int birdWidth = 34;
    int birdHeight = 24;
    
    class Bird{
    	int x = birdX;
    	int y = birdY;
    	int width = birdWidth;
    	int height = birdHeight;
    	Image img;
    	
    	Bird(Image img){
    		this.img=img;  
    	}
    }
    
    
    Bird bird;
    
    //vòng lặp ống 
    Timer placePipesTimer;
    
    //vòng lặp game
    Timer gameLoop;
    int v_roi = 0;//vận tốc rơi của chim
    int p = 1; //trọng lực
    int keySpace = -12;//giá trị nút bấm space
    
    //các pipes(ống)
    int pipeX = 360; //vị trí ống xuất hiện
    int pipeY; //Tọa độ Y của ống trên (âm để đẩy ống lên trên)
    int pipeWidth = 64;
    int pipeHeight = 512;
    
    class Pipe{
    	int x = pipeX;
    	int y = pipeY;
    	int width = pipeWidth;
    	int height = pipeHeight;
    	Image img;
    	Boolean passed = false;
    	
    	Pipe(Image img){
    		this.img = img;
    	}
    }
    //Mảng chứa ống
    ArrayList<Pipe> pipes;
    
    //Random ống
    Random random = new Random();
    
    //Dừng lại game
    Boolean gameOver = false;

     FlappyBird() {
        setPreferredSize(new Dimension(360, 640));
        setFocusable(true);//tiếp nhận các sự kiện của phím 
        addKeyListener(this); //kiểm tra 3 hàm của keyList khi nhấn phím

        backgroundImg = new ImageIcon(getClass().getResource("flappybirdbg.png")).getImage();//Tải hình ảnh lên trên Frame
        birdImg = new ImageIcon(getClass().getResource("flappybird.png")).getImage();
        topPipImg = new ImageIcon(getClass().getResource("toppipe.png")).getImage();
        bottomImg = new ImageIcon(getClass().getResource("bottompipe.png")).getImage();
        bird = new Bird(birdImg);
        
        //thời gian ống xuất hiện
        pipes = new ArrayList<Pipe>(); //tạo 1 mảng trống để chứa các ống
        placePipesTimer = new Timer(1500, new ActionListener() { // phải gọi sự kiện để ống được thực hiện - 1,5s sẽ gọi ống 1 lần
			
			@Override
			public void actionPerformed(ActionEvent e) {
				placePipes();
			}
		});
        placePipesTimer.start();
     
        //game timer
        gameLoop = new Timer(20, new ActionListener() {
        	
            @Override
            public void actionPerformed(ActionEvent e) {
                v_roi += p;
                bird.y += v_roi;
                //Chim sẽ ko rơi khi chạm đáy
//                if(bird.y>640-bird.height) {
//                	bird.y = 640-bird.height;
//                	v_roi = 0;
//                }
                
                
                //di chuyển ốngx 1 ống 
//                pipeX -= 4; // làm ống trôi sang trái
//                if(pipeX + pipeWidth < 0) {
//                	pipeX = 360;
//                	pipeY = random.nextInt(200)-200;
//                }
                for (int i = 0; i < pipes.size(); i++) {
                    Pipe pipe = pipes.get(i);
                    pipe.x -= 4;
                }

                // Xoá ống đã đi qua khỏi màn hình
                pipes.removeIf(pipe -> pipe.x + pipe.width < 0);

                repaint();// Vẽ lại màn hình (gọi paintComponent)
               

                //Dừng game
                if(gameOver) {
        			placePipesTimer.stop(); //dừng việc gọi ống thêm vào mảng
        			gameLoop.stop();//Dừng việc vẽ		
        		}
                if(bird.y > 640) {
                	gameOver = true;
                	System.out.println("Game over");
                }
            }
        });
        gameLoop.start();
     }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        g.drawImage(backgroundImg, 0, 0, 360, 640, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        g.drawImage(topPipImg, pipeX, pipeY, pipeWidth, pipeHeight, null);
        
        //vẽ các ống 
        for(int i = 0; i<pipes.size(); i++) {
        	Pipe pipe = pipes.get(i);
        	g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
    }
    
    //Tạo ống
    public void placePipes() {
    	
    	//???
    	int randomPipeY = (int) (pipeY -pipeHeight/4 - Math.random()*(pipeHeight/2));
    	int SpaceOfPipes = 640/4;
    	//Tạo ống trên
    	Pipe topPipe = new Pipe(topPipImg);
    	topPipe.y = randomPipeY;
    	//Thêm ống vào mảng pipes
    	pipes.add(topPipe);
    	
    	//Tạo ống dưới
    	Pipe botPipe = new Pipe(bottomImg);
    	botPipe.y = topPipe.y + SpaceOfPipes +  pipeHeight;
    	pipes.add(botPipe);
    	
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			v_roi = -9;
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}   
}



