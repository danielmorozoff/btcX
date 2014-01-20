package qr.code;

import java.io.FileInputStream;

import javax.imageio.ImageIO;

import server.loggers.ServerLoggers;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class QRReader {

	/**
	 * Decode QR code into a String
	 * @param qrFileLoc
	 * @return
	 */
	public String qrCodeReader(String qrFileLoc){
		Result result = null;
		BinaryBitmap binaryBitmap;
		try{
			binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(
					new FileInputStream(qrFileLoc)))));
			result = new MultiFormatReader().decode(binaryBitmap);
			return result.getText();
		}catch(Exception ex){
			ex.printStackTrace();
			ServerLoggers.errorLog.error("*** Error reading QR code Image. QRReader.qrCodeReader *** ");
		}
		return null;
	}
}
