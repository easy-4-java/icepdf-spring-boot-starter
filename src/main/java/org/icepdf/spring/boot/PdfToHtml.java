package org.icepdf.spring.boot;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

public class PdfToHtml {

	public static void pdf2Pic(String pdfPath, String path) throws IOException, PDFException, PDFSecurityException {
		
        Document document = new Document();
        try {
            document.setFile(pdfPath);
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage image = (BufferedImage) document.getPageImage(page, GraphicsRenderingHints.SCREEN,
                        Page.BOUNDARY_CROPBOX, 0f, 2.5f);
                try {
                    if (!ImageIO.write(image, "png", new File(path, page + ".png"))) {
                        throw new IOException("PNG writer unavailable");
                    }
                } finally {
                    image.flush();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("PDF rendering interrupted", e);
        } finally {
            document.dispose();
        }
    }

	public static void main(String[] arguments) {
		String p = "d://w1.pdf";

		Document document = new Document();

		try {
			// document.setFile(p);
			document.setInputStream(new FileInputStream(new File(p)), "");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		int pages = document.getNumberOfPages();

		System.out.println("pages  " + pages);

		List<String> imgPaths = new ArrayList<String>();

		BufferedImage image = null;

		RenderedImage rendImage = null;

		for (int i = 0; i < pages; i++) {

            try {
                image = (BufferedImage) document.getPageImage(i, GraphicsRenderingHints.PRINT,
                        Page.BOUNDARY_TRIMBOX, 0f, 3.5f);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

			rendImage = image;

			System.out.println("/t capturing page " + i);

			File file = new File("D:\\c_" + i + ".jpg");

			imgPaths.add("c_" + i + ".jpg");

			file.delete();

			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				ImageIO.write(rendImage, "jpg", file);
			} catch (IOException e) {
				e.printStackTrace();
			}

			image.flush();

		}

		document.dispose();

		String html = HtmlHelper.getHtml(imgPaths);

		File file = new File("D:\\c.html");

		if (file.exists()) {
			file.delete();
		}

		try {
			FileOutputStream out = new FileOutputStream(file);

			out.write(html.getBytes());

			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(html);
	}
}
