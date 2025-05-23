
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

	int maxScore;

	
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

		// Lấy maxScore

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
					// Chim sẽ ko rơi khi chạm đáy
					v_roi += p;
					bird.y += v_roi;
					
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
					if(bird.y > 640) {
						gameOver = true;
						System.out.println("Game over");
					}
					
					if (gameOver) {
						placePipesTimer.stop();
						gameLoop.stop();
						// Đổi nút Start thành Restart
						startButton.setIcon(new ImageIcon(resizeImage(new ImageIcon(getClass().getResource("/res/restart.png")).getImage(), 120, 40)));
						// Hiện lại nút Start
						startButton.setVisible(true);
						restarting = true;
						isGameStarted = false;
						// Cập nhật maxScore trong data.in
						
					}
				});
		// Nút Start
		startButton = new JButton(new ImageIcon(resizeImage(new ImageIcon(getClass().getResource("/res/start.png")).getImage(), 120, 40)));
		startButton.setBounds(120, 300, 120, 40); // Tuỳ chỉnh vị trí và kích thước
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
		draw(g);
	}
	/** 
	 * 
	 * Hàm vẽ đồ họa
	 * 
	 * @param Graphic
	 * @return
	 */
	public void draw(Graphics g){

		g.drawImage(backgroundImg, 0, 0, 360, 640, null);
		g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
		
		//vẽ các ống 
		for(int i = 0; i<pipes.size(); i++) {
			Pipe pipe = pipes.get(i);
			g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
		}
		
		// Vẽ score (điểm)
		if (isGameStarted)
		{
			

			// Đổ bóng
			g.setColor(Color.DARK_GRAY);
			g.drawString("Score:        " + (int)score, 6, 31);
			g.drawString("Max Score: " + (int)maxScore, 6, 51);

			// Hiển thị điểm
			g.setColor(Color.WHITE);
			g.drawString("Score:        " + (int)score, 5, 30);
			g.drawString("Max Score: " + (int)maxScore, 5, 50);
		}

		if (!isGameStarted && !restarting) {
			Image titleImg = new ImageIcon(getClass().getResource("/res/title.png")).getImage();
			BufferedImage resized = resizeImage(titleImg, 260, 60);
			g.drawImage(resized, 50, 100, null);
		}
		else if (!isGameStarted && restarting)
		{
			Image titleImg = new ImageIcon(getClass().getResource("/res/gameover.png")).getImage();
			BufferedImage resized = resizeImage(titleImg, 260, 60);
			g.drawImage(resized, 50, 100, null);  
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

	/** 
	 * 
	 * Hàm tùy chỉnh kích thước Image
	 * 
	 * @param Image ảnh muốn chỉnh sửa
	 * @return
	 * 
	 */
	public static BufferedImage resizeImage(Image originalImage, int width, int height) {
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(originalImage, 0, 0, width, height, null);
		g2d.dispose();
		return resized;
	}

	/** 
	 * 
	 * Hàm tạo font
	 * 
	 * @param path đường dẫn của Font (e.g "/font/Minecraft.ttf")
	 * @return
	 * 
	 */
	
	
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