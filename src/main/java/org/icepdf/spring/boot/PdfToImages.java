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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.PDimension;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

/**
 * *********************************************
 * 
 * @author Administrator
 * @FileName PdfToImages.java
 * @Description 转换pfd每一页或首页为jpg缩略图大图、小图
 **********************************************
 * @since 1.0.0
 */
public class PdfToImages {

	public static final String FILETYPE_JPG = "jpg";
	public static final String SUFF_IMAGE = "." + FILETYPE_JPG;
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
			tranfer(args[0], Float.parseFloat(args[1]));
		} catch (PDFException | PDFSecurityException | IOException e) {
			throw new IllegalStateException("PDF conversion failed", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("PDF conversion interrupted", e);
		}
	}

	/**
	 * 转换pfd第一页为jpg缩略图大图
	 * 
	 * @param filepath
	 * @param zoom
	 * @throws PDFException
	 * @throws PDFSecurityException
	 * @throws IOException
	 */
	public static void tranfer1(String filepath, float zoom) throws PDFException, PDFSecurityException, IOException, InterruptedException {
// ICEpdf document class
		Document document = null;
		float rotation = 0f;
		document = new Document();
		document.setFile(filepath);

// 创建以pdf文件名为名称的文件夹保存pdf缩略图
		File file = new File(filepath.substring(0, filepath.lastIndexOf(".")));
		if (!file.exists() && !file.isDirectory()) {
			System.out.println("//不存在");
			file.mkdir();
		} else {
			System.out.println("//目录存在");
		}

		String imagepath = "";
// 设置文件名
		imagepath = jpgFilename(filepath, 0);
// 转换首页为jpg缩略图
		tranferPer(document, rotation, zoom, imagepath, 0);
	}

	/**
	 * 转换pfd每一页为jpg缩略图大图
	 * 
	 * @param filepath pfd文件路径
	 * @param zoom     缩略图缩放比例
	 * @throws PDFException
	 * @throws PDFSecurityException
	 * @throws IOException
	 */
	public static void tranfer(String filepath, float zoom) throws PDFException, PDFSecurityException, IOException, InterruptedException {
		// ICEpdf document class
		Document document = null;
		float rotation = 0f;
		document = new Document();
		document.setFile(filepath);
		// 获取pdf总页数
		int pages = document.getNumberOfPages();
		if (pages > 0) {
			// 创建以pdf文件名为名称的文件夹保存pdf缩略图
			File file = new File(filepath.substring(0, filepath.lastIndexOf(".")));
			if (!file.exists() && !file.isDirectory()) {
				System.out.println("//不存在");
				file.mkdir();
			} else {
				System.out.println("//目录存在");
			}

			String imagepath = "";
			String smallimagepath = "";
			// 将每一页的pdf转换为jpg缩略图
			for (int i = 0; i < pages; i++) {
				// 设置文件名
				imagepath = jpgFilename(filepath, i);
				tranferPer(document, rotation, zoom, imagepath, i);
				//由缩略图生成指定宽高的jpg小图
				smallimagepath = imagepath.substring(0, imagepath.lastIndexOf(".")) + "-small" + ".jpg";
				zoomImage(imagepath, smallimagepath, 88, 126);
			}
		}
	}

	/**
	 * 设置jpg文件名
	 * 
	 * @param filepath
	 * @param index
	 * @return
	 */
	public static String jpgFilename(String filepath, int index) {
		String jpgFilename = "";
		String folder = "";
		index++;
		if (filepath != null && !filepath.equals("")) {
			int separatorPosition = Math.max(filepath.lastIndexOf("\\"), filepath.lastIndexOf("/"));
			folder = filepath.substring(separatorPosition + 1, filepath.lastIndexOf("."));
			String separator = filepath.indexOf("\\") >= 0 && filepath.indexOf("/") < 0 ? "\\" : File.separator;
			jpgFilename = filepath.substring(0, filepath.lastIndexOf(".")) + separator + folder + "-" + index + "."
					+ FILETYPE_JPG;
		}
		return jpgFilename;
	}

	/**
	 * 转换一页pdf为jpg缩略图大图
	 * 
	 * @param document
	 * @param rotation
	 * @param zoom
	 * @param imagepath
	 * @throws PDFException
	 * @throws PDFSecurityException
	 * @throws IOException
	 */
	public static void tranferPer(Document document, float rotation, float zoom, String imagepath, int index)
			throws PDFException, PDFSecurityException, IOException, InterruptedException {
		float scale = 1f;
		Page page = document.getPageTree().getPage(index);
		page.init();
		PDimension sz = page.getSize(Page.BOUNDARY_CROPBOX, rotation, scale);

		int pageWidth = (int) sz.getWidth();
		int pageHeight = (int) sz.getHeight();

		BufferedImage image = new BufferedImage(pageWidth, pageHeight, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.createGraphics();

		page.paint(g, GraphicsRenderingHints.PRINT, Page.BOUNDARY_CROPBOX, rotation, scale);
		g.dispose();
// capture the page image to file
		try {
			System.out.println("转换第 " + (index + 1) + " 页");
			File file = new File(imagepath);
			ImageIO.write(image, "jpg", file);

		} catch (Throwable e) {
			e.printStackTrace();
		}
		image.flush();

		// 由缩略图生成指定宽高的jpg小图
		String smallimagepath = imagepath.substring(0, imagepath.lastIndexOf(".")) + "-small" + ".jpg";
		zoomImage(imagepath, smallimagepath, 88, 126);
	}

	/**
	 * 由缩略图生成指定宽高的jpg小图
	 * 
	 * @param srcFileName
	 * @param tagFileName
	 * @param width
	 * @param height
	 */
	public static void zoomImage(String srcFileName, String tagFileName, int width, int height) {
		try {
			BufferedImage bi = ImageIO.read(new File(srcFileName));
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(bi, 0, 0, width, height, null);
			ImageIO.write(tag, "jpg", new File(tagFileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
