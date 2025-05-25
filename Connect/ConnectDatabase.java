package Connect; // Đảm bảo đúng package của bạn

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectDatabase {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=DACS1;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456789";

    // Bạn có thể giữ lại phương thức main này để kiểm tra độc lập,
    // nhưng nó sẽ không được gọi khi chạy từ DangNhap_DangKi.
    public static void main(String[] args) {
        String testUser = "A"; // Thay bằng user có trong DB
        String testPass = "123"; // Thay bằng pass tương ứng
        System.out.println("Kết nối thành công");
        System.out.println(testUser + " ");
        
        System.out.println("--Kiểm tra người dùng mới--");
        String newUser = "newPlayer";
        String newPass = "newPass123";
        
        if (registerUser(newUser, newPass)) {
            System.out.println("Đăng ký người dùng '" + newUser + "' thành công!");
        } else {
            System.out.println("Đăng ký người dùng '" + newUser + "' thất bại hoặc đã tồn tại.");
        }

        // Có thể thử đăng ký lại user đã có để xem lỗi
        if (registerUser("newPlayer", "newPass123")) {
            System.out.println("Đăng ký người dùng 'newPlayer' lần 2 thành công! (Đây là lỗi nếu user đã tồn tại)");
        } else {
            System.out.println("Đăng ký người dùng 'newPlayer' lần 2 thất bại như mong đợi.");
        }
       
    }

    /**
     * Phương thức tĩnh để kiểm tra đăng nhập người dùng với CSDL.
     * Đây là phương thức mà lớp DangNhap_DangKi sẽ gọi.
     * @param username Tên người dùng cần kiểm tra.
     * @param password Mật khẩu của người dùng.
     * @return true nếu tên người dùng và mật khẩu khớp, ngược lại false.
     */
    public static boolean loginUser(String username, String password) { // <--- QUAN TRỌNG: Thêm static
        String sql = "SELECT Name FROM Users WHERE Name = ? AND Password = ?";
        Connection conn = null; // Khai báo conn ở đây để có thể quản lý trong finally

        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            if (conn == null) {
                System.err.println("Không thể kết nối CSDL trong loginUser.");
                return false;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);

                ResultSet rs = pstmt.executeQuery();
                return rs.next(); // true nếu tìm thấy bản ghi (đăng nhập thành công)

            } // pstmt và rs sẽ tự động đóng ở đây do try-with-resources
        } catch (SQLException e) {
            System.err.println("Lỗi CSDL khi kiểm tra đăng nhập:");
            System.err.println("  User: '" + username + "', Pass: '" + password + "'");
            System.err.println("  Message: " + e.getMessage());
            // e.printStackTrace(); // Bỏ comment để xem chi tiết lỗi nếu cần gỡ lỗi
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    // System.out.println("Kết nối CSDL đã đóng sau kiểm tra login."); // Có thể bỏ comment để gỡ lỗi
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối CSDL trong loginUser: " + e.getMessage());
                }
            }
        }
    }
    
    public static boolean registerUser(String username, String password) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            if (conn == null) {
                System.err.println("Không thể kết nối CSDL trong registerUser.");
                return false;
            }

            // 1. Kiểm tra xem tên người dùng đã tồn tại chưa
            if (userExists(username, conn)) {
                System.err.println("Đăng ký thất bại: Tên người dùng '" + username + "' đã tồn tại.");
                return false;
            }

            // 2. Chèn người dùng mới vào bảng Users
            String sqlInsert = "INSERT INTO Users (Name, Password) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                int rowsAffected = pstmt.executeUpdate(); // Thực thi lệnh INSERT
                return rowsAffected > 0; // Trả về true nếu có ít nhất 1 hàng được chèn
            }
        } catch (SQLException e) {
            System.err.println("Lỗi CSDL khi đăng ký người dùng:");
            System.err.println("  User: '" + username + "', Pass: '" + password + "'");
            System.err.println("  Message: " + e.getMessage());
            // e.printStackTrace(); // Bỏ comment để xem chi tiết lỗi nếu cần gỡ lỗi
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối CSDL trong registerUser: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Phương thức trợ giúp để kiểm tra xem một người dùng có tồn tại trong CSDL hay không.
     * @param username Tên người dùng cần kiểm tra.
     * @param conn Đối tượng Connection đã được tạo.
     * @return true nếu người dùng tồn tại, false nếu không.
     * @throws SQLException nếu có lỗi CSDL.
     */
    private static boolean userExists(String username, Connection conn) throws SQLException {
        String sqlCheck = "SELECT Name FROM Users WHERE Name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // true nếu tìm thấy người dùng
        }
    }
    
}