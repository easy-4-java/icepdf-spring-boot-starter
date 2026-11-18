/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
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
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

/**
 * @author: Luckly
 * @Description: pdf 转单张高清图片
 * @Date 2019-05-27
 * @Modified By:
 */
public class IcePdf {
	
    /**
     *
     * @param pdfPath D://upFiles//bill//PT-011001900321-18675195.pdf
     * @param pngPath D://upFiles//bill//
     * @param imgName PT-011001900321-18675195
     */
    public static void pdf2Pic(String pdfPath, String pngPath,String imgName) throws IOException, PDFException, PDFSecurityException, InterruptedException {
      //  log.info("IcePdf pdfPath : {},  pngPath : {} ,  imgName : {} ", pdfPath,pngPath,imgName);
        Document document = new Document();
        document.setFile(pdfPath);
        //缩放比例
        float scale = 2.5f;
        //旋转角度
        float rotation = 0f;
        //for循环在pdf文件资料中使用，目前税票就固定一张，所有就注释了
        //for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage image = (BufferedImage)
                    document.getPageImage(0, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, rotation, scale);
            RenderedImage rendImage = image;
            try {
                imgName = imgName + ".png";
                File file = new File(pngPath + File.separator + imgName);
                ImageIO.write(rendImage, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.flush();
        //}
        document.dispose();
       // log.info("======================pdf2Pic 完成============================");
    }

    public static void main(String[] args) throws IOException, PDFException, PDFSecurityException, InterruptedException {
        String filePath = "D:\\upFiles\\bill\\PT-011001900321-18675195.pdf";
        pdf2Pic(filePath, "D:\\upFiles\\bill", "PT-011001900321-18675195");

        String filePath2 = "D:\\upFiles\\bill\\YDY-011001900332-18675195.pdf";
        pdf2Pic(filePath2, "D:\\upFiles\\bill", "YDY-011001900332-18675195");
    }
}
