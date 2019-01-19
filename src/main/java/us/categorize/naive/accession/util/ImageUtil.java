package us.categorize.naive.accession.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

//TODO in fact need to review this entire class
public class ImageUtil {
	public static final double TARGET_HEIGHT = 200;
	
	//pass in the buffered image so we can also send those bits over
	public static InputStream createThumbnail(BufferedImage image) throws IOException {
		double w = image.getWidth();
		double h = image.getHeight();
		double ratio = TARGET_HEIGHT/h;
		int newWidth =(int) (ratio * w);
		int newHeight =(int) (ratio * h);
		BufferedImage thumb = new BufferedImage(newWidth, newHeight, image.getType());
		Graphics2D g = thumb.createGraphics();
		 
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		 
		g.drawImage(image, 0, 0, newWidth, newHeight, null);
		g.dispose();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(thumb, "jpg", out);
		return new ByteArrayInputStream(out.toByteArray());
	}
	
	//TODO need to review this boilerplate
	public static byte[] toByteArray(InputStream in) throws IOException {

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];
		int len;

		// read bytes from the input stream and store them in buffer
		while ((len = in.read(buffer)) != -1) {
			// write bytes from the buffer into output stream
			os.write(buffer, 0, len);
		}
		os.flush();
		return os.toByteArray();
	}
	
	public static String bytesToHash(byte[] bytes) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(bytes);
		    StringBuffer hexString = new StringBuffer();
		    for (int i = 0; i < hash.length; i++) {
		    	String hex = Integer.toHexString(0xff & hash[i]);
		    	if(hex.length() == 1) hexString.append('0');
		        hexString.append(hex);
		    }
		    return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
