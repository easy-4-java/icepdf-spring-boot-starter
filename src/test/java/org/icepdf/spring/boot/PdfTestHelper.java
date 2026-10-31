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

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 * Helper for creating test PDF files.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
final class PdfTestHelper {

    private PdfTestHelper() {
    }

    /**
     * Creates a single-page PDF for testing.
     */
    static File createMinimalPdf(File dir) throws IOException {
        File pdfFile = new File(dir, "test.pdf");
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.setLineWidth(1f);
                cs.moveTo(50, 700);
                cs.lineTo(200, 700);
                cs.stroke();
            }
            doc.save(pdfFile);
        }
        return pdfFile;
    }

    /**
     * Creates a multi-page PDF file for testing.
     */
    static File createMultiPagePdf(File dir, int pages) throws IOException {
        File pdfFile = new File(dir, "multipage.pdf");
        try (PDDocument doc = new PDDocument()) {
            for (int i = 0; i < pages; i++) {
                PDPage page = new PDPage(PDRectangle.A4);
                doc.addPage(page);
                try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                    cs.setLineWidth(1f);
                    cs.moveTo(50, 700);
                    cs.lineTo(200, 700);
                    cs.stroke();
                }
            }
            doc.save(pdfFile);
        }
        return pdfFile;
    }

    static File createPdfWithDistinctCropAndTrimBoxes(File dir) throws IOException {
        File pdfFile = new File(dir, "distinct-boxes.pdf");
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(400, 400));
            page.setCropBox(new PDRectangle(0, 0, 400, 400));
            page.setTrimBox(new PDRectangle(50, 60, 200, 100));
            doc.addPage(page);
            doc.save(pdfFile);
        }
        return pdfFile;
    }

    /**
     * Creates a minimal PNG image file for testing.
     */
    static File createMinimalPng(File dir) throws IOException {
        File pngFile = new File(dir, "test.png");
        java.awt.image.BufferedImage img =
                new java.awt.image.BufferedImage(2, 2, java.awt.image.BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, 0xFF0000);
        img.setRGB(1, 0, 0x00FF00);
        img.setRGB(0, 1, 0x0000FF);
        img.setRGB(1, 1, 0xFFFFFF);
        javax.imageio.ImageIO.write(img, "png", pngFile);
        return pngFile;
    }
}
