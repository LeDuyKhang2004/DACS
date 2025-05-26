package FLB;

import Connect.ConnectDatabase;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.Collections;
import javax.swing.*;

public class Dangki {
	private static Font customFont;

	// Khai báo một đối tượng ConnectDatabase
	private static ConnectDatabase authManager;
	
	public static void main(String[] args) {
		
		authManager = new ConnectDatabase();
		
		JFrame frame = new JFrame("Register Flappy Bird");
		frame.setSize(360, 640);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null); // Sử dụng null layout cho frame

		Color customBG = new Color(77, 199, 208); // Nền ngoài
		frame.getContentPane().setBackground(customBG);

		customFont = Helper.loadCustomFont("/t/PressStart2P-Regular.ttf", 25f);
		
		//Title FlappyBird
		BufferedImage resized = Helper.resizeImage(new ImageIcon(DangNhap.class.getResource("/res/flappy-bird-logo.png")).getImage(), 262, 70); // vì title đang kiểu Icon nên phải .getImage() để nhận đối tượng là Image
		JLabel imageLabel = new JLabel(new ImageIcon(resized)); 
		
		imageLabel.setBounds(40, 40, 262, 100); 
		frame.add(imageLabel);

		//title nhỏ
		BufferedImage resizedTtSmall = Helper.resizeImage(new ImageIcon(DangNhap.class.getResource("/res/sprite.png")).getImage(), 170, 20);
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
		JLabel label = new JLabel("REGISTER");
		label.setBounds(32, 30, 200, 50);
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

		// JCheckBox để hiện / ẩn mật khẩu
		JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
		showPasswordCheckBox.setBounds(20, 230, 150, 20); // Đặt vị trí dưới ô mật khẩu
		showPasswordCheckBox.setFont(customFont.deriveFont(Font.PLAIN, 10f)); // Font nhỏ hơn
		showPasswordCheckBox.setForeground(Color.WHITE); // Màu chữ
		// ShowPasswordCheckBox.setBackground(pn.getBackground()); // Nền khớp với panel
		showPasswordCheckBox.setOpaque(false); // Đảm bảo nền trong suốt nếu muốn

		//Nút Sign up
		JButton BTsignUp = new JButton("Register Now");
		BTsignUp.setBounds(32, 270, 200, 40);
		BTsignUp.setFont(customFont.deriveFont(Font.PLAIN, 10f));
		BTsignUp.setForeground(Color.WHITE);
		BTsignUp.setBackground(new Color(230, 120, 50));
		BTsignUp.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

		//Login
		JLabel goToLoginLabel = new JLabel("Login?");
		goToLoginLabel.setBounds(95, 320, 80, 20); // Điều chỉnh vị trí phù hợp
		goToLoginLabel.setForeground(new Color(240, 240, 240)); // Màu chữ nhạt hơn một chút
		goToLoginLabel.setFont(customFont.deriveFont(Font.PLAIN, 10f)); // Cùng font với các nút khác

		// Đặt gạch chân cho chữ "Login?"
		Font originalFont = goToLoginLabel.getFont();
		goToLoginLabel.setFont(originalFont.deriveFont(
			Collections.singletonMap(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)));

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
		showPasswordCheckBox.addActionListener((ActionEvent e) -> {
			if (showPasswordCheckBox.isSelected()) {
				// Nếu checkbox được CHỌN (tức là muốn hiện mật khẩu)
				tfPassword.setEchoChar((char) 0); // Đặt ký tự ẩn thành null (hiện chữ)
			} else {
				// Nếu checkbox KHÔNG được chọn (tức là muốn ẩn mật khẩu)
				tfPassword.setEchoChar('\u2022'); // Đặt ký tự ẩn về mặc định (dấu chấm tròn)
			}
		});

		BTsignUp.addActionListener((ActionEvent e) -> {
			String newUser = tfName.getText();
			String Newpassword = new String(tfPassword.getPassword());

			// Kiểm tra xem trường newUser có phải là placeholder không
			if (newUser.equals("newUser") || newUser.isEmpty()) {
				JOptionPane.showMessageDialog(frame, "Vui lòng nhập tên người dùng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}
			// Kiểm tra xem trường Password có phải là placeholder không
			if (Newpassword.equals("Password") || Newpassword.isEmpty()) {
				JOptionPane.showMessageDialog(frame, "Vui lòng nhập mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if(ConnectDatabase.registerUser(newUser, Newpassword)) {
				JOptionPane.showMessageDialog(frame, "Đăng kí thành công! Chào mừng " + newUser, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
				frame.dispose();
				DangNhap.main(args);
			}else
				JOptionPane.showMessageDialog(frame, "Đăng kí thất bại! Tên người dùng đã tồn tại");
		});

		goToLoginLabel.addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.dispose();
				DangNhap.main(args);
			}
		});

		pn.add(tfName);
		pn.add(tfPassword);
		pn.add(showPasswordCheckBox); // Thêm checkbox vào panel
		pn.add(BTsignUp);
		pn.add(label);
		pn.add(goToLoginLabel);

		frame.add(pn);
		frame.setVisible(true);
	}

	public void draw(Graphics g) {
		BufferedImage resized = Helper.resizeImage(new ImageIcon(getClass().getResource("/res/flappy-bird-logo.png")).getImage(), 262, 60);
		g.drawImage(resized, 50, 100, null);
	}
}
