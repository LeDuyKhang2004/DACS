

package FLB;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;


public class FlappyBird extends JPanel implements ActionListener,KeyListener{
	
	JButton startButton;
	
	Image fastUp, fastMid, fastDown;
	Image slowUp, slowMid, slowDown;
	Image backgroundImg, birdImg, topPipImg, bottomImg;

	boolean restarting = false;
	boolean New = false;

	int best;

	String username;

//	ScoreDB scoreDB = new ScoreDB();
	
	// Birds
	int birdX = 45; // Vị trí của con chim cánh mép trái cửa sổ 45px
	int birdY = 320; // Vị trí của con chim cánh mép trên cửa sổ 320px
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
	
	// Vòng lặp ống 
	Timer placePipesTimer;
	
	// Vòng lặp game
	Timer gameLoop;
	int v_roi = 0;// Vận tốc rơi của chim
	int p = 1; // Trọng lực
	
	// Các pipes(ống)
	int pipeX = 360; // Vị trí ống xuất hiện
	int pipeY; // Tọa độ Y của ống trên (âm để đẩy ống lên trên)
	int pipeWidth = 64; //Chiều rộng của ống
	int pipeHeight = 512;// Chiều cao của ống
	
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
	
	//Dừng lại game
	Boolean gameOver = false;
	
	//
	Boolean isGameStarted = false;
	//Tính điểm
	double score = 0; 
	
	//Tăng thời gian ống chạy nhanh hơn
	int pipesPassed = 0; //số ống đã vượt qua
	int pipeSpeed = 7; //tốc độ ban đầu của ống
	int maxPipeSpeed = 20; // Giới hạn tốc độ ống
	int pipeInterval = 1500;       // Thời gian xuất hiện ống (ms)
	int lastSpeedUpdate = 0; // để nhớ lần cuối đã tăng tốc độ

	
	
	public void startGame() {
		if(isGameStarted = true);
		// Reset trạng thái game
		bird.y = birdY;
		v_roi = 0;
		pipes.clear();
		gameOver = false;
		score = 0;
		pipesPassed = 0;
		pipeSpeed = 4;
		pipeInterval = 1500;
		lastSpeedUpdate = 0;
		New = false;

		// Lấy maxScore
//		best = scoreDB.getMaxScore(username);

		// Reset Timer
		placePipesTimer.setDelay(pipeInterval);
		placePipesTimer.start();
		gameLoop.start();

		// Ẩn nút Start sau khi nhấn
		startButton.setVisible(false);

		// Gọi repaint để cập nhật lại màn hình
		repaint();
	}

	 FlappyBird() {

		setPreferredSize(new Dimension(360, 640));
		// Tiếp nhận các sự kiện của phím
		setFocusable(true); 
		// Kiểm tra 3 hàm của keyList khi nhấn phím
		extracted();

		// Animation của Bird lúc chậm (vàng)
		slowDown = new ImageIcon(getClass().getResource("/res/bird1_yellow.png")).getImage();
		slowMid = new ImageIcon(getClass().getResource("/res/bird2_yellow.png")).getImage();
		slowUp = new ImageIcon(getClass().getResource("/res/bird3_yellow.png")).getImage();

		// Animation của Bird lúc nhanh (đỏ)
		fastDown = new ImageIcon(getClass().getResource("/res/bird1_red.png")).getImage();
		fastMid = new ImageIcon(getClass().getResource("/res/bird2_red.png")).getImage();
		fastUp = new ImageIcon(getClass().getResource("/res/bird3_red.png")).getImage();

		//Tải hình ảnh lên trên Frame
		backgroundImg = new ImageIcon(getClass().getResource("/res/flappybirdbg.png")).getImage();
		birdImg = new ImageIcon(getClass().getResource("/res/flappybird.png")).getImage();
		topPipImg = new ImageIcon(getClass().getResource("/res/toppipe.png")).getImage();
		bottomImg = new ImageIcon(getClass().getResource("/res/bottompipe.png")).getImage();
		bird = new Bird(birdImg);

		// Thời gian ống xuất hiện
		pipes = new ArrayList<>(); // Tạo 1 mảng trống để chứa các ống
		placePipesTimer = new Timer(1500, (ActionEvent e) -> {
					placePipes();
				}); // phải gọi sự kiện để ống được thực hiện - 1,5s sẽ gọi ống 1 lần

		// Game timer
		gameLoop = new Timer(20, (ActionEvent e) -> {
					
					v_roi += p; // tăng vận tốc rơi
					bird.y += v_roi; //vị trí của chim sẽ rơi th
					// Thresshold điểm (quyết định số điểm tối thiểu để chuyển sang màu đỏ)
					if ((int)score < 20)
					{
						// Animation màu vàng
						if (v_roi < -5)
							bird.img = slowUp;
						else if (v_roi < 5)
							bird.img = slowMid;
						else
							bird.img = slowDown;
					}
					else
					{
						// Animation màu đỏ
						if (v_roi < -5)
							bird.img = fastUp;
						else if (v_roi < 5)
							bird.img = fastMid;
						else
							bird.img = fastDown;
					}
					
					// Di chuyển các ống sang trái
					for (int i = 0; i < pipes.size(); i++) {
						Pipe pipe = pipes.get(i);
						pipe.x -= pipeSpeed; // thời gian trôi của ống
						
						// Nếu chim đã bay qua ống và chưa được đánh dấu
						if (!pipe.passed && pipe.x + pipe.width < bird.x) {
							pipe.passed = true;
							score += 0.5;
							pipesPassed++;
							
							// Chỉ tăng tốc 1 lần mỗi khi đạt mốc mới
							if (pipesPassed != 0 && pipesPassed % 8 == 0 &&  pipesPassed != lastSpeedUpdate) { //% 8 là 2 cặp ống (4 ống) vì mỗi ống là 0,5 nên
								if (pipeSpeed < maxPipeSpeed) {
									pipeSpeed++; // Tăng tốc độ ống
									pipeInterval = Math.min(2000, pipeInterval + 150); // Giãn khoảng cách tối đa 2s
									placePipesTimer.setDelay(pipeInterval); // Cập nhật thời gian gọi ống
									System.out.println("Speed: " + pipeSpeed + " | Pipe distance: " + pipeInterval);
								}
								lastSpeedUpdate = pipesPassed;
							}
						}
						
						//Xử lí khi va chạm ống thì game dừng
						if (bird.x < pipe.x + pipe.width && bird.x + bird.width > pipe.x && bird.y < pipe.y + pipe.height && bird.y + bird.height > pipe.y) {
							gameOver = true;
						}
					}
					
					// Xoá ống đã đi qua khỏi màn hình
					pipes.removeIf(pipe -> pipe.x + pipe.width < 0);

					// Vẽ lại màn hình (gọi paintComponent)
					repaint();
					
					
					// Dừng game
					if(bird.y > 640 || bird.y < 0 ) {
						gameOver = true;
						System.out.println("Game over");
					}
//					else if(bird.y < 0) {
//						gameOver = true;
//					}
					
					if (gameOver) {
						placePipesTimer.stop();
						gameLoop.stop();
						// Đổi nút Start thành Restart
						startButton.setIcon(new ImageIcon(Helper.resizeImage(new ImageIcon(getClass().getResource("/res/restart.png")).getImage(), 120, 40)));
						// Hiện lại nút Start
						startButton.setVisible(true);
						restarting = true;
						isGameStarted = false;
						// Cập nhật maxScore
						if ((int) score > best)
						{
							New = true;
							best = (int) score;
//							scoreDB.updateMaxScore((int) score, username);
						}
					}
				});
		// Nút Start
		startButton = new JButton(new ImageIcon(Helper.resizeImage(new ImageIcon(getClass().getResource("/res/start.png")).getImage(), 120, 40)));
		startButton.setBounds(120, 500, 120, 40); // Tuỳ chỉnh vị trí và kích thước
		startButton.addActionListener((ActionEvent e) -> {
					startGame();
				});
		// Bắt buộc để setBounds hoạt động
		this.setLayout(null);
		this.add(startButton);

	}

	private void extracted() {
		addKeyListener(this);
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D G2D = (Graphics2D) g;


		G2D.drawImage(backgroundImg, 0, 0, 360, 640, null);

		if(!isGameStarted && gameOver == false) {
		BufferedImage logo = Helper.resizeImage(new ImageIcon(getClass().getResource("/res/logo.png")).getImage(), 153, 24);
		G2D.drawImage(logo, 5, 5, null);
		}
		// Xoay chim theo vận tốc

		/* Lấy góc của chim quay, v_roi * 3 để khiến cho góc quay dễ nhìn hơn */
		/* Góc cao nhất:-45° (hướng lên trên 45 độ) */
		/* Góc thấp nhất: +90° (hướng thẳng xuống dưới) */
		double angle = Math.toRadians(Math.max(-45, Math.min(90, v_roi * 3)));

		/* Lưu trạng thái đồ họa */
		AffineTransform old = G2D.getTransform();

		/* Di chuyển tâm của chim từ góc trái sang chính giữa con chim */
		/* Do rotate() xoay ảnh quanh tâm --> tâm phải ở chính giữa */
		G2D.translate(bird.x + bird.width / 2, bird.y + bird.height / 2);
		G2D.rotate(angle);

		/* Do thay đổi tâm, vị trí drawImage() cũng thay đổi, do đó phải vẽ lại chim theo tâm */
		G2D.drawImage(bird.img, -bird.width / 2, -bird.height / 2, bird.width, bird.height, null);

		/* Reset */
		G2D.setTransform(old);
		
		//vẽ các ống 
		for(Pipe pipe: pipes)
			G2D.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
		
		// Vẽ score (điểm)
		if (isGameStarted)
		{
			// Khởi tạo font chữ
			G2D.setFont(Helper.loadCustomFont("/t/flappy-font.ttf", 40f));

			// Đổ bóng
			G2D.setColor(Color.DARK_GRAY);
			G2D.drawString("" + (int) score, 182, 102);

			// Hiển thị điểm
			G2D.setColor(Color.WHITE);
			G2D.drawString("" + (int)score, 180, 100);
			
			//Thêm chữ user: "Name" trên góc trái. ĐỔ BÓNG
			G2D.setColor(Color.DARK_GRAY);
			G2D.setFont(Helper.loadCustomFont("/t/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + username , 12, 22);
			
			//Thêm chữ usee: "Name: trên góc trái
			G2D.setColor(Color.WHITE);
			G2D.setFont(Helper.loadCustomFont("/t/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + username , 10, 20);
		}

		if (!isGameStarted && !restarting) {
			BufferedImage getReady = Helper.resizeImage(new ImageIcon(getClass().getResource("/res/getready.png")).getImage(), 260, 60);
			G2D.drawImage(getReady, 50, 100, null);
		}
	
		else if (!isGameStarted && restarting)
		{
			BufferedImage banner = Helper.resizeImage(new ImageIcon(getClass().getResource("/res/banner.png")).getImage(), 300, 150);
			BufferedImage title = Helper.resizeImage(new ImageIcon(getClass().getResource("/res/gameover.png")).getImage(), 260, 60);
			G2D.drawImage(banner, 30, 275, null);
			G2D.drawImage(title, 50, 100, null);
			G2D.setFont(Helper.loadCustomFont("/t/flappy-font.ttf", 18f));

			// Đổ bóng
			G2D.setColor(Color.DARK_GRAY);
			G2D.drawString("MEDAL", 69, 321);
			G2D.drawString("SCORE: " + (int) score, 201, 321);
			G2D.drawString("BEST:  " + (int) best, 201, 391);

			// Hiển thị banner điểm
			G2D.setColor(new Color(249, 121, 93));
			G2D.drawString("MEDAL", 68, 320);
			G2D.drawString("SCORE: " + (int) score, 200, 320);
			G2D.drawString("BEST:  " + (int) best, 200, 390);

			//Thêm chữ user: "Name" trên góc trái. ĐỔ BÓNG
			G2D.setColor(Color.DARK_GRAY);
			G2D.setFont(Helper.loadCustomFont("/t/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + username , 12, 22);
			
			//Thêm chữ usee: "Name: trên góc trái
			G2D.setColor(Color.WHITE);
			G2D.setFont(Helper.loadCustomFont("/t/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + username , 10, 20);
			
			// Nếu là kỉ lục
			if (New)
			{
				Image newRecord = Helper.resizeImage(new ImageIcon(getClass().getResource("/res/newScore.png")).getImage(), 32, 16);
				G2D.drawImage(newRecord, 160, 375, null);
			}

			// Hiển thị Medal
			Image medal;
			if ((int) score < 10)
				medal = Helper.resizeImage(new ImageIcon(getClass().getResource("/res/Iron.png")).getImage(), 60, 60);
			else if ((int) score < 30)
				medal = Helper.resizeImage(new ImageIcon(getClass().getResource("/res/Copper.png")).getImage(), 60, 60);
			else if ((int) score < 60)
				medal = Helper.resizeImage(new ImageIcon(getClass().getResource("/res/Silver.png")).getImage(), 60, 60);
			else
				medal = Helper.resizeImage(new ImageIcon(getClass().getResource("/res/Gold.png")).getImage(), 60, 60);
			G2D.drawImage(medal, 64, 331, null);
		}
	}

	/** 
	 * 
	 * Hàm tạo ống
	 * 
	 */
	public void placePipes() {
		
		//???
		int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight/2));
		int SpaceOfPipes = 640 / 4;
		//Tạo ống trên
		Pipe topPipe = new Pipe(topPipImg);
		topPipe.y = randomPipeY;
		//Thêm ống vào mảng pipes
		pipes.add(topPipe);
		
		//Tạo ống dưới
		Pipe botPipe = new Pipe(bottomImg);
		botPipe.y = topPipe.y + SpaceOfPipes + pipeHeight;
		pipes.add(botPipe);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (!isGameStarted) return; 
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