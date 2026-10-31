/*
 * Copyright (c) 2018, hiwepy (https://github.com/easy-4-java).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.icepdf.spring.boot;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

/**
 * <p>Utility class for Pdf2 Img operations.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class Pdf2ImgUtil {
	/**
	 * <p>Main.</p>
	 * @param args the args
	 * @return the static void
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			return;
		}
		try {
			convert(args[0], new File(args[1]));
		} catch (IOException | PDFException | PDFSecurityException e) {
			throw new IllegalStateException("PDF conversion failed", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("PDF conversion interrupted", e);
		}
	}

	public static void convert(String filePath, File outputDirectory)
			throws IOException, PDFException, PDFSecurityException, InterruptedException {
		Document document = new Document();
		try {
			document.setFile(filePath);
			for (int i = 0; i < document.getNumberOfPages(); i++) {
				BufferedImage image = (BufferedImage) document.getPageImage(i,
						GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, 0f, 1.0f);
				try {
					if (!ImageIO.write(image, "png", new File(outputDirectory, "icepdf_" + i + ".png"))) {
						throw new IOException("PNG writer unavailable");
					}
				} finally {
					image.flush();
				}
			}
		} finally {
			document.dispose();
		}
	}
}
