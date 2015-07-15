import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class ImageSubtractor {

	public void main(String args[]) throws Exception{
		File img1 = new File("");
		File img2 = new File("");
		BufferedImage bi1 = ImageIO.read(img1);
		BufferedImage bi2 = ImageIO.read(img2);
	}
}
