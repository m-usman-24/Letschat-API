package com.letschat.chat.service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


/**
 * Image Compressing Service compression time solemnly depends on the uploaded image,
 * png relatively take less time than jpg,
 * images will not be compressed if the size is <= compressionSize,
 * image will be in jpg format after compression.
 * Size measurement is (compressionSize * 1024 * 1024)
 */
public final class ImageCompressionService {
	
	private ImageCompressionService() {}
	
	
	public static byte[] compressImage(byte[] image, float compressionSize) throws IOException {
		
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
		
		long maxFileSizeInBytes = (long) (compressionSize * 1024 * 1024);
		long originalFileSize = image.length;
		
//		Image is already smaller than the limit, no need to compress
		if (originalFileSize <= maxFileSizeInBytes) return image;
		
		float compressionRatio = (float) maxFileSizeInBytes / originalFileSize;
		
		BufferedImage compressedImage = new BufferedImage(
			(int) (bufferedImage.getWidth() * compressionRatio),
			(int) (bufferedImage.getHeight() * compressionRatio),
			BufferedImage.TYPE_INT_RGB
		);
		
		compressedImage
			.createGraphics().drawImage(
				bufferedImage.getScaledInstance(
				(int) (bufferedImage.getWidth() * compressionRatio),
				(int) (bufferedImage.getHeight() * compressionRatio),
				Image.SCALE_SMOOTH),0, 0, null
			);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(compressedImage, "jpg", outputStream);
		
		return outputStream.toByteArray();
	}
	
	public static byte[] bufferedImageToByteArray(File inputFile) throws IOException {
		
		BufferedImage image = ImageIO.read(inputFile);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", outputStream);
		
		return outputStream.toByteArray();
	}
	
	public static void byteArrayToImageFile(byte[] imageBytes, String fullyQualifiedPathNameWithFileName)
		throws IOException {
		
		BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
		File output = new File(fullyQualifiedPathNameWithFileName);
		ImageIO.write(bufferedImage, "jpg", output);
		
	}
}
