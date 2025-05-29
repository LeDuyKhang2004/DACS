package FLB;

import Connect.ConnectDatabase;
import Connect.PlayerScore;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class FlappyBird extends JPanel implements ActionListener,KeyListener{
	// Gọi biến để lưu tên user
	private String playerUsername;	
	
	// Biến khai báo ảnh leaderboard
	
	private JButton startButton;
	private JButton rateButton;

	private Image fastUp, fastMid, fastDown;
	private Image slowUp, slowMid, slowDown;
	Image backgroundImg, birdImg, topPipImg, bottomImg;

	private JScrollPane leaderboardScrollPane;
	private boolean showLeaderboard = false;
	private boolean New = false;

	
	private int bestScore;

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
		
		Bird (Image img) {
			this.img=img;  
		}
	}
	
	private Bird bird;
	
	// Vòng lặp ống 
	private Timer placePipesTimer;
	
	// Vòng lặp game
	private Timer gameLoop;
	private int v_roi = 0;// Vận tốc rơi của chim
	private int p = 1; // Trọng lực
	
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
		
		Pipe (Image img) {
			this.img = img;
		}
	}
	//Mảng chứa ống
	private ArrayList<Pipe> pipes;
	
	//Dừng lại game
	private Boolean gameOver = false;
	
	//
	private Boolean isGameStarted = false;
	//Tính điểm
	private double score = 0; 
	
	//Tăng thời gian ống chạy nhanh hơn
	private int pipesPassed = 0; //số ống đã vượt qua
	private int pipeSpeed = 7; //tốc độ ban đầu của ống
	private int maxPipeSpeed = 20; // Giới hạn tốc độ ống
	private int pipeInterval = 1500;       // Thời gian xuất hiện ống (ms)
	private int lastSpeedUpdate = 0; // để nhớ lần cuối đã tăng tốc độ

	
	
	public void startGame() {
		
		//Ngăn không cho start khi game đã chạy
		if (isGameStarted ) 
			return;

		//Đánh dấu game bắt đầu chạy
		isGameStarted = true;
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
		showLeaderboard = false;
		showLeaderboardTable();
		New = false;

		// Reset Timer
		placePipesTimer.setDelay(pipeInterval);
		placePipesTimer.start();
		gameLoop.start();

		// Ẩn nút Start sau khi nhấn
		startButton.setVisible(false);
		rateButton.setVisible(false);

		// Gọi repaint để cập nhật lại màn hình
		repaint();
	}

	

	FlappyBird() {

		
		//gọi lại lớp connect để lấy tên user
		this.playerUsername = ConnectDatabase.currentUsername;
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
					if ((int)score < 10)
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
					if (bird.y > 640 || bird.y < 0 ) {
						gameOver = true;
						System.out.println("Game over");
					}
					
					if (gameOver) {
						placePipesTimer.stop();
						gameLoop.stop();
						
						try {
							saveCurrentScore();
							getHighCurrentScore();
							getHighCurrentScoreRanking();
						
						// Đổi nút Start thành Restart
						startButton.setIcon(new ImageIcon(Helper.resizeImage(new ImageIcon(getClass().getResource("/res/restart.png")).getImage(), 120, 40)));
						// Hiện lại nút Start
						startButton.setVisible(true);
						startButton.setBounds(120, 500, 120, 40);
						rateButton.setVisible(false);
						isGameStarted = false;
						// Cập nhật maxScore
						if ((int) score > bestScore)
						{
							New = true;
							bestScore = (int) score;
						}
					
						} catch (SQLException e1) {
							e1.printStackTrace(System.err);
						}
					}
				});

		// Nút Start
		startButton = new JButton(new ImageIcon(Helper.resizeImage(new ImageIcon(getClass().getResource("/res/start.png")).getImage(), 104, 58)));
		startButton.setBounds(51, 500, 104, 58);
		startButton.addActionListener((ActionEvent e) -> {
					startGame();
				});
	
		// Nút Rate
		rateButton = new JButton(new ImageIcon(Helper.resizeImage(new ImageIcon(getClass().getResource("/res/rate.png")).getImage(), 104, 58)));
		rateButton.setBounds(206, 500, 104, 58);
		rateButton.addActionListener((ActionEvent e) -> {
					showRate();
				});

		// Bắt buộc để setBounds hoạt động
		this.setLayout(null);
		this.add(startButton);
		this.add(rateButton);
	}

	

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D G2D = (Graphics2D) g;


		G2D.drawImage(backgroundImg, 0, 0, 360, 640, null);

		if(!isGameStarted && !gameOver) {
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
		if (isGameStarted && !showLeaderboard)
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
			G2D.drawString("USER: " + playerUsername , 12, 22);
			
			//Thêm chữ usee: "Name: trên góc trái
			G2D.setColor(Color.WHITE);
			G2D.setFont(Helper.loadCustomFont("/t/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + playerUsername , 10, 20);
		}

		if (showLeaderboard) {
			BufferedImage leaderBoard = Helper.resizeImage(new ImageIcon(getClass().getResource("/res/leaderboard.png")).getImage(), 260, 260);
			G2D.drawImage(leaderBoard, 50, 150, null);
		}

		if (!isGameStarted && !gameOver && !showLeaderboard) {
			BufferedImage getReady = Helper.resizeImage(new ImageIcon(getClass().getResource("/res/getready.png")).getImage(), 260, 60);
			G2D.drawImage(getReady, 50, 100, null);
		}
	
		else if (!isGameStarted && gameOver && !showLeaderboard)
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
			G2D.drawString("BEST:  " +  (int) bestScore, 201, 391);

			// Hiển thị banner điểm
			G2D.setColor(new Color(249, 121, 93));
			G2D.drawString("MEDAL", 68, 320);
			G2D.drawString("SCORE: " + (int) score, 200, 320);
			G2D.drawString("BEST:  " + (int) bestScore, 200, 390);

			//Thêm chữ user: "Name" trên góc trái. ĐỔ BÓNG
			G2D.setColor(Color.DARK_GRAY);
			G2D.setFont(Helper.loadCustomFont("/t/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + playerUsername , 12, 22);
			
			//Thêm chữ usee: "Name: trên góc trái
			G2D.setColor(Color.WHITE);
			G2D.setFont(Helper.loadCustomFont("/t/flappy-font.ttf", 14f));
			G2D.drawString("USER: " + playerUsername , 10, 20);
			
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
	
	public void saveCurrentScore() throws SQLException {
		if (playerUsername != null && !playerUsername.equalsIgnoreCase("Guest") && score > 0) { // Chỉ lưu nếu có người dùng hợp lệ và điểm > 0
            if (ConnectDatabase.saveScore(playerUsername, (int) score)) {
                System.out.println("Đã lưu điểm " + (int) score + " cho người chơi: " + playerUsername);
            } else {
                System.err.println("Lỗi khi lưu điểm cho " + playerUsername + ". Có thể người dùng không tồn tại trong DB hoặc lỗi kết nối.");
            }
        } else {
            System.out.println("Không lưu điểm vì không có người dùng đăng nhập hợp lệ hoặc điểm = 0.");
        }
	}
	
	public void getHighCurrentScore() {
		bestScore = ConnectDatabase.getHighScore(playerUsername);
		System.out.println("Điểm cao nhất: " + bestScore + " || User: " + playerUsername);
	}
	
	public void getHighCurrentScoreRanking() {
		 // Lấy danh sách điểm cao nhất của tất cả người chơi
        List<PlayerScore> leaderboard = ConnectDatabase.getHighScoreRanking();

        System.out.println("Bảng xếp hạng điểm cao:");
        int rank = 1;
        for (PlayerScore ps : leaderboard) {
            System.out.println(rank + ". " + ps.getName() + " - " + ps.getScore());
            rank++;
        }
	}

	//Đây là hàm thực sự làm cho bảng xếp hạng hiện hoặc ẩn trên màn hình
	public void showLeaderboardTable() {
		if (leaderboardScrollPane != null) {
			leaderboardScrollPane.setVisible(showLeaderboard);
			return;
		}

		List<PlayerScore> leaderboard = ConnectDatabase.getHighScoreRanking();
		// Dummy data
		
		String[] columns = {"Rank", "Username", "Score"};
		DefaultTableModel model = new DefaultTableModel(columns, 0);
		int rank = 1;
		for(PlayerScore ps : leaderboard) {
			Object row[] = new Object[3];
			row[0] = rank++;
			row[1] = ps.getName();
			row[2] = ps.getScore();
			model.addRow(row);
		}
		
		JTable table = new JTable(model) {		
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Hide header
		table.setTableHeader(null);

		// Transparent background
		table.setOpaque(false);
		table.setBackground(new Color(0, 0, 0, 0));
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setForeground(Color.WHITE);
		table.setFont(Helper.loadCustomFont("/t/flappy-font.ttf", 14));
		table.setRowHeight(30);
		table.setFocusable(false);
		table.setBorder(BorderFactory.createEmptyBorder());

		// === Column alignments ===
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Rank
		table.getColumnModel().getColumn(0).setPreferredWidth(30);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
		table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer); // Username

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer); // Score

		// Create scroll pane and strip all visuals
		leaderboardScrollPane = new JScrollPane(table);
		leaderboardScrollPane.setBounds(101, 210, 160, 190);
		leaderboardScrollPane.setOpaque(false);
		leaderboardScrollPane.getViewport().setOpaque(false);
		leaderboardScrollPane.setBorder(BorderFactory.createEmptyBorder());
		leaderboardScrollPane.setViewportBorder(BorderFactory.createEmptyBorder());

		// Hide scrollbars but keep scrolling
		JScrollBar vScroll = leaderboardScrollPane.getVerticalScrollBar();
		vScroll.setPreferredSize(new Dimension(0, 0));
		vScroll.setOpaque(false);
		vScroll.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override protected void configureScrollBarColors() {
				this.thumbColor = new Color(0, 0, 0, 0);
				this.trackColor = new Color(0, 0, 0, 0);
			}

			@Override protected JButton createDecreaseButton(int orientation) {
				return createZeroButton();
			}

			@Override protected JButton createIncreaseButton(int orientation) {
				return createZeroButton();
			}

			private JButton createZeroButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				button.setBorder(BorderFactory.createEmptyBorder());
				button.setOpaque(false);
				return button;
			}
		});

		// Final container styling
		setLayout(null);
		add(leaderboardScrollPane);
		repaint();
	}
	public void showRate() {
		showLeaderboard = !showLeaderboard;
		showLeaderboardTable();
		repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (!isGameStarted && gameOver && e.getKeyCode() == KeyEvent.VK_R) //ngăn nút space khi game chưa bắt đầu
			startGame();		
		else if(isGameStarted && e.getKeyCode() == KeyEvent.VK_SPACE ) 
			v_roi = -9;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void keyReleased(KeyEvent e) {
	}
	private void extracted() {
		addKeyListener(this);
	}
} 

