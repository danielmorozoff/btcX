package qr.code;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import server.loggers.ServerLoggers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;



public class QRGenerator {
	//Default settings
	  String filePath;
	  int height = 125;
	  int width = 125;
	  String fileType;
	
	  
	public QRGenerator(String qrCodeText, String filePath, int height,int width, String fileType){
		 if(filePath!=null) this.filePath = filePath;
		 if(height>0) this.height = height;
		 if(width>0) this.width = width;
		 if(fileType!=null) this.fileType = fileType;
		 
			generateQRImage( qrCodeText, height, width, this.fileType, this.filePath);
	}
	/**
	 * Generates a qr image file based on params
	 * @param qrFile
	 * @param qrCodeText
	 * @param size
	 * @param fileType
	 * @throws WriterException
	 * @throws IOException
	 */
	public void generateQRImage( String qrCodeText, int height, int width, String fileType, String filePath) {
			  // Create the ByteMatrix for the QR-Code that encodes the given String
			  Hashtable hintMap = new Hashtable();
			  hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			  QRCodeWriter qrCodeWriter = new QRCodeWriter();
			  BitMatrix byteMatrix=null;
			try {
				byteMatrix = qrCodeWriter.encode(qrCodeText,
				    BarcodeFormat.QR_CODE, height, width, hintMap);
			
			  // Make the BufferedImage that are to hold the QRCode
			  int matrixWidth = byteMatrix.getWidth();
			  BufferedImage image = new BufferedImage(matrixWidth, matrixWidth,
			    BufferedImage.TYPE_INT_RGB);
			  image.createGraphics();

			  Graphics2D graphics = (Graphics2D) image.getGraphics();
			  graphics.setColor(Color.WHITE);
			  graphics.fillRect(0, 0, matrixWidth, matrixWidth);
			  // Paint and save the image using the ByteMatrix
			  graphics.setColor(Color.BLACK);

			  for (int i = 0; i < matrixWidth; i++) {
			   for (int j = 0; j < matrixWidth; j++) {
			    if (byteMatrix.get(i, j)) {
			     graphics.fillRect(i, j, 1, 1);
			    }
			   }
			  }
			  try {
				ImageIO.write(image, fileType, new File(filePath));
			} catch (IOException e) {
				e.printStackTrace();
				ServerLoggers.errorLog.error("*** Error generating QR code Image. QRGenerator.generateQRImage -- ImageIO *** ");
			}
			} catch (WriterException e) {
				e.printStackTrace();
				ServerLoggers.errorLog.error("*** Error generating QR code Image. QRGenerator.generateQRImage -- ZXing *** ");
			}
	 }
}
