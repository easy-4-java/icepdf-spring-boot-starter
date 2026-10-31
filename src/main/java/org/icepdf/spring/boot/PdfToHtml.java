package org.icepdf.spring.boot;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.util.GraphicsRenderingHints;

/**\n * Converter for transforming PDF documents to HTML format.\n *\n * @author <a href="https://github.com/loong10k">Loong Wan</a>\n * @since 1.0.0\n */
public class PdfToHtml {
	/**
	 * <p>Pdf2 pic.</p>
	 * @param pdfPath the pdf path
	 * @param path the path
	 * @return the static void
	 * @throws IOException if an error occurs
	 * @throws PDFException if an error occurs
	 * @throws PDFSecurityException if an error occurs
	 * @throws InterruptedException if an error occurs
	 */

	public static void pdf2Pic(String pdfPath, String path) throws IOException, PDFException, PDFSecurityException, InterruptedException {
		render(pdfPath, path);
	}

	private static List<String> render(String pdfPath, String path)
			throws IOException, PDFException, PDFSecurityException, InterruptedException {
		List<String> images = new ArrayList<>();
		Document document = new Document();
		try {
			document.setFile(pdfPath);
		// 缩放比例
		float scale = 2.5f;
		// 旋转角度
		float rotation = 0f;

		for (int i = 0; i < document.getNumberOfPages(); i++) {
			BufferedImage image = (BufferedImage) document.getPageImage(i, GraphicsRenderingHints.SCREEN,
					org.icepdf.core.pobjects.Page.BOUNDARY_CROPBOX, rotation, scale);
			RenderedImage rendImage = image;
			try {
				String imgName = i + ".png";
				File file = new File(path, imgName);
				if (!ImageIO.write(rendImage, "png", file)) {
					throw new IOException("PNG writer unavailable");
				}
				images.add(imgName);
			} finally {
				image.flush();
			}
		}
		} finally {
			document.dispose();
		}
		return images;
	}
	/**
	 * <p>Main.</p>
	 * @param arguments the arguments
	 * @return the static void
	 */

	public static void main(String[] arguments) {
		if (arguments.length != 2) {
			throw new IllegalArgumentException("Usage: PdfToHtml <pdf-file> <output-directory>");
		}
		try {
			List<String> images = render(arguments[0], arguments[1]);
			if (!images.isEmpty()) {
				Files.writeString(new File(arguments[1], "index.html").toPath(),
						HtmlHelper.getHtml(images), StandardCharsets.UTF_8);
			}
		} catch (IOException | PDFException | PDFSecurityException e) {
			throw new IllegalStateException("PDF conversion failed", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("PDF conversion interrupted", e);
		}
	}
}
