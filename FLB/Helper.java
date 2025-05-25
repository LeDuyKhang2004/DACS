package FLB;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Helper
{
	/** 
	 * 
	 * Hàm tạo font
	 * 
	 * @param path đường dẫn của Font (e.g "/font/Minecraft.ttf")
	 * @return
	 * 
	 */
	public static Font loadCustomFont(String path, float size) 
	{
		Font newFont;
		try {
			InputStream is = Helper.class.getResourceAsStream(path); // e.g. "/FLB/myfont.ttf"
			newFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(newFont);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace(System.err);
			return new Font("Arial", Font.PLAIN, (int)size); // fallback
		}
		return newFont;
	}

	/** 
	 * 
	 * Hàm tùy chỉnh kích thước Image
	 * 
	 * @param Image ảnh muốn chỉnh sửa
	 * @return
	 * 
	 */
	public static BufferedImage resizeImage(Image originalImage, int width, int height) 
	{
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(originalImage, 0, 0, width, height, null);
		g2d.dispose();
		return resized;
	}

}