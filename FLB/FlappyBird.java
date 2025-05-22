	
package FLB;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;


import javax.swing.ImageIcon;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
//    int keySpace = -12;//giá trị nút bấm space
    
    //các pipes(ống)
    int pipeX = 360; //vị trí ống xuất hiện
    int pipeY; //Tọa độ Y của ống trên (âm để đẩy ống lên trên)
    int pipeWidth = 64; //chiều rộng của ống
    int pipeHeight = 512;// chiều cao của ống
    
    class Pipe{
    	int x = pipeX;
    	int y = pipeY;
    	int width = pipeWidth;
    	int height = pipeHeight;
    	Image img;
    	Boolean passed = false;// đánh dấu chim đã qua 1 ống 
    	
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
    
    //Tính điểm
    double score = 0; 
    
    //Tăng thời gian ống chạy nhanh hơn
    int pipesPassed = 0; //số ống đã vượt qua
    int pipeSpeed = 4; //tốc độ ban đầu của ống
    int maxPipeSpeed = 10; // Giới hạn tốc độ ống
    int pipeInterval = 1500;       // Thời gian xuất hiện ống (ms)
    int lastSpeedUpdate = 0; // để nhớ lần cuối đã tăng tốc độ

    
    
    //Va chạm ống
    public boolean collision(Bird a, Pipe b) {
    	return a.x < b.x + b.width &&
    	           a.x + a.width > b.x &&
    	           a.y < b.y + b.height &&
    	           a.y + a.height > b.y;
    }
    

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
                
                //di chuyển các ống sang trái
                for (int i = 0; i < pipes.size(); i++) {
                    Pipe pipe = pipes.get(i);
                    pipe.x -= pipeSpeed; // thời gian trôi của ống
                    
                    
                    // Nếu chim đã bay qua ống và chưa được đánh dấu
                    if (!pipe.passed && pipe.x + pipe.width < bird.x) {
                        pipe.passed = true;
                        score += 0.5;
                        pipesPassed++;

                        // Chỉ tăng tốc 1 lần mỗi khi đạt mốc mới
                        if (pipesPassed != 0 && pipesPassed % 8 == 0 &&  pipesPassed != lastSpeedUpdate) { //% 8 là 2 cặp ống(4 ống) vì mỗi ống là 0,5 nên 
                            if (pipeSpeed < maxPipeSpeed) {
                                pipeSpeed++; // tăng tốc độ ống
                                pipeInterval = Math.min(2000, pipeInterval + 150); // giãn khoảng cách tối đa 2s
                                placePipesTimer.setDelay(pipeInterval); // cập nhật thời gian gọi ống
                                System.out.println("Tốc độ: " + pipeSpeed + " | Khoảng cách giãn: " + pipeInterval);
                            }
                            lastSpeedUpdate = pipesPassed;
                        }
                    }
 
                    
                    //Xử lí khi va chạm ống thì game dừng
                    if(collision(bird, pipe)) {
                    	gameOver = true;
                    }
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
        
        //vẽ các ống 
        for(int i = 0; i<pipes.size(); i++) {
        	Pipe pipe = pipes.get(i);
        	g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        
     // Hiển thị điểm số
        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(40f)); // font size 18
        
        // Vẽ score (điểm)
        g.drawString("" + (int)score, 180, 80);
        
//        // Vẽ số ống đã vượt (pipesPassed)
//        g.drawString("Pipes Passed: " + pipesPassed, 10, 60);
//        
//        // Vẽ tốc độ ống
//        g.drawString("Pipe Speed: " + pipeSpeed, 10, 90);
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



