package FLB;

import java.io.InputStream;
import javax.swing.*;

import Connect.ConnectDatabase;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent; // Import ItemEvent
import java.awt.event.ItemListener; // Import ItemListener 
import java.awt.image.BufferedImage;

public class DangNhap {
	private static Font customFont;

	 // Khai báo một đối tượng ConnectDatabase
    private static ConnectDatabase authManager;
    
    public static void main(String[] args) {
    	
        authManager = new ConnectDatabase();
        
        JFrame frame = new JFrame("Log in Flappy Bird");
        frame.setSize(360, 640);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null); // Sử dụng null layout cho frame

        Color customBG = new Color(77, 199, 208); // Nền ngoài
        frame.getContentPane().setBackground(customBG);

  
       
                
        

        try {
            InputStream is = DangNhap.class.getResourceAsStream("/t/PressStart2P-Regular.ttf");
            if (is != null) { // Kiểm tra nếu InputStream không null
                customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(25f);
            } else {
                System.err.println("Lỗi: Không tìm thấy file font tại .ttf");
                customFont = new Font("Monospaced", Font.BOLD, 14); // fallback
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tải font tùy chỉnh. Sử dụng font dự phòng.");
            customFont = new Font("Monospaced", Font.BOLD, 14); // fallback
        }
        
        //Title FlappyBird
        ImageIcon title = new ImageIcon(DangNhap.class.getResource("/res/flappy-bird-logo.png"));
        BufferedImage resized = resizeImage(title.getImage(), 262, 70); // vì title đang kiểu Icon nên phải .getImage() để nhận đối tượng là Image
        JLabel imageLabel = new JLabel(new ImageIcon(resized)); 
        
        imageLabel.setBounds(40, 40, 262, 100); 
        frame.add(imageLabel);

        //title nhỏ
        ImageIcon titleSmall = new ImageIcon(DangNhap.class.getResource("/res/sprite.png"));
        BufferedImage resizedTtSmall = resizeImage(titleSmall.getImage(), 170, 20);
        JLabel imageLabel2 = new JLabel(new ImageIcon(resizedTtSmall));
        imageLabel2.setBounds(95, 130, 244, 20);
        frame.add(imageLabel2);
        

        // Panel chứa form
        JPanel pn = new JPanel();
        pn.setLayout(null); // Quan trọng để setBounds có hiệu lực trong panel
        pn.setBounds(40, 180, 260, 370);
        Color BG = new Color(63, 163, 169); // Màu nền của panel
        pn.setBackground(BG);

        // Màu cho TextField khi focus và không focus
        Color textFieldBgFocus = new Color(52, 107, 108); // Màu xanh đậm khi focus
        Color textFieldBgNoFocus = new Color(210, 230, 228); // Màu nền khi không focus (màu xám nhạt như hình mẫu)


     // Tiêu đề "Sign Up"
        JLabel label = new JLabel("LOGIN");
        label.setBounds(70, 30, 200, 50);
        label.setForeground(Color.WHITE);
        label.setBackground(BG);
        label.setOpaque(true);
        label.setFont(customFont);
        
        
        // Ô nhập tên/email
        JTextField tfName = new JTextField("Users"); // Khởi tạo với "Users"
        tfName.setBounds(20, 100, 220, 50);
        tfName.setFont(customFont.deriveFont(14f));
        tfName.setForeground(Color.GRAY); // Màu chữ placeholder
        tfName.setBackground(textFieldBgNoFocus); // Đặt màu nền ban đầu
        tfName.setCaretColor(Color.WHITE); // Màu con trỏ nháy

        // Ô nhập mật khẩu
        JPasswordField tfPassword = new JPasswordField("Password"); // Nên dùng JPasswordField cho mật khẩu
        tfPassword.setBounds(20, 170, 220, 50);
        tfPassword.setFont(customFont.deriveFont(14f));
        tfPassword.setForeground(Color.GRAY); // Màu chữ placeholder
        tfPassword.setBackground(textFieldBgNoFocus); // Đặt màu nền ban đầu
        tfPassword.setCaretColor(Color.WHITE);

        // JCheckBox để hiện/ẩn mật khẩu
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setBounds(20, 230, 150, 20); // Đặt vị trí dưới ô mật khẩu
        showPasswordCheckBox.setFont(customFont.deriveFont(Font.PLAIN, 10f)); // Font nhỏ hơn
        showPasswordCheckBox.setForeground(Color.WHITE); // Màu chữ
//        showPasswordCheckBox.setBackground(pn.getBackground()); // Nền khớp với panel
        showPasswordCheckBox.setOpaque(false); // Đảm bảo nền trong suốt nếu muốn
        
        //Nút Sign up
        JButton BTsignUp = new JButton("Log in Now");
        BTsignUp.setBounds(32, 270, 200, 40);
        BTsignUp.setFont(customFont.deriveFont(Font.PLAIN, 10f));
        BTsignUp.setForeground(Color.WHITE);
        BTsignUp.setBackground(new Color(230, 120, 50));
        BTsignUp.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        // === FocusListener cho tfName ===
        tfName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tfName.getText().equals("Users")) {
                    tfName.setText("");
                    tfName.setForeground(Color.WHITE);
                    tfName.setBackground(textFieldBgFocus);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (tfName.getText().isEmpty()) {
                    tfName.setForeground(Color.GRAY);
                    tfName.setText("Users");
                    tfName.setBackground(textFieldBgNoFocus);
                }
            }
        });    
        
        // === FocusListener cho tfPassword ===
        tfPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                // Lấy mật khẩu dạng char[] và chuyển về String để so sánh
                if (String.valueOf(tfPassword.getPassword()).equals("Password")) {
                    tfPassword.setText(""); // Xóa placeholder
                    tfPassword.setForeground(Color.WHITE);
                    tfPassword.setBackground(textFieldBgFocus);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(tfPassword.getPassword()).isEmpty()) {
                    tfPassword.setForeground(Color.GRAY);
                    tfPassword.setText("Password"); // Đặt lại đúng "Password"
                    tfPassword.setBackground(textFieldBgNoFocus);
                }
            }
        });        
        
     // === ActionListener cho showPasswordCheckBox (cách dễ hiểu hơn) ===
        showPasswordCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPasswordCheckBox.isSelected()) {
                    // Nếu checkbox được CHỌN (tức là muốn hiện mật khẩu)
                    tfPassword.setEchoChar((char) 0); // Đặt ký tự ẩn thành null (hiện chữ)
                } else {
                    // Nếu checkbox KHÔNG được chọn (tức là muốn ẩn mật khẩu)
                    tfPassword.setEchoChar('\u2022'); // Đặt ký tự ẩn về mặc định (dấu chấm tròn)
                }
            }
        });
          
        BTsignUp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = tfName.getText();
				String password = new String(tfPassword.getPassword());
				

                // Kiểm tra xem trường Username có phải là placeholder không
                if (username.equals("Username") || username.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Vui lòng nhập tên người dùng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Kiểm tra xem trường Password có phải là placeholder không
                if (password.equals("Password") || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Vui lòng nhập mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if(ConnectDatabase.loginUser(username, password)) {
                	 JOptionPane.showMessageDialog(frame, "Đăng nhập thành công! Chào mừng " + username, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                	 frame.dispose();
                	 App.main(args);
                }else
                	JOptionPane.showMessageDialog(frame, "Đăng nhập thất bại");
			}
		});
        
        pn.add(tfName);
        pn.add(tfPassword);
        pn.add(showPasswordCheckBox); // Thêm checkbox vào panel
        pn.add(BTsignUp);
        pn.add(label);

        frame.add(pn);
        frame.setVisible(true);
        
    }
    public static BufferedImage resizeImage(Image originalImage, int width, int height) {
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(originalImage, 0, 0, width, height, null);
		g2d.dispose();
		return resized;
	}

    public void draw(Graphics g) {
    	Image titleImg = new ImageIcon(getClass().getResource("/res/flappy-bird-logo.png")).getImage();
		BufferedImage resized = resizeImage(titleImg, 262, 60);
		g.drawImage(resized, 50, 100, null);
}

}
