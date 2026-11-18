package org.icepdf.spring.boot;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.icepdf.core.pobjects.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class IcePdfRenderingTest {

    @TempDir
    Path directory;

    @Test
    void rendersPdfToPngAndJpegWithoutTrialLibraries() throws Exception {
        Path pdf = directory.resolve("sample.pdf");
        try (InputStream fixture = getClass().getResourceAsStream("/render-fixture.pdf")) {
            Files.copy(fixture, pdf);
        }

        PdfToHtml.pdf2Pic(pdf.toString(), directory.toString());
        BufferedImage png = ImageIO.read(directory.resolve("0.png").toFile());
        assertThat(png.getWidth()).isEqualTo(180);
        assertThat(png.getHeight()).isEqualTo(240);
        assertThat(png.getRGB(25, 200) & 0xffffff).isZero();

        Document document = new Document();
        try {
            document.setFile(pdf.toString());
            PdfToImages.tranferPer(document, 0f, 1f, directory.resolve("page.jpg").toString(), 0);
        } finally {
            document.dispose();
        }
        BufferedImage jpeg = ImageIO.read(directory.resolve("page.jpg").toFile());
        assertThat(jpeg.getWidth()).isEqualTo(72);
        assertThat(jpeg.getHeight()).isEqualTo(96);
        BufferedImage thumbnail = ImageIO.read(directory.resolve("page-small.jpg").toFile());
        assertThat(thumbnail.getWidth()).isEqualTo(88);
        assertThat(thumbnail.getHeight()).isEqualTo(126);
    }
}
