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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link PdfToHtml}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class PdfToHtmlTest {

    @TempDir
    Path tempDir;

    @Test
    void pdf2Pic_withValidPdf_convertsPages() throws Exception {
        File pdf = PdfTestHelper.createMinimalPdf(tempDir.toFile());
        String outputDir = tempDir.toString() + File.separator;

        assertThatCode(() -> PdfToHtml.pdf2Pic(pdf.getAbsolutePath(), outputDir))
                .doesNotThrowAnyException();
        assertThat(new File(outputDir, "0.png")).exists().isNotEmpty();
    }

    @Test
    void pdf2Pic_withMultiPagePdf_convertsAllPages() throws Exception {
        File pdf = PdfTestHelper.createMultiPagePdf(tempDir.toFile(), 2);
        String outputDir = tempDir.toString() + File.separator;

        assertThatCode(() -> PdfToHtml.pdf2Pic(pdf.getAbsolutePath(), outputDir))
                .doesNotThrowAnyException();
        assertThat(new File(outputDir, "0.png")).exists().isNotEmpty();
        assertThat(new File(outputDir, "1.png")).exists().isNotEmpty();
    }

    @Test
    void pdf2Pic_withNonExistentFile_throwsException() {
        assertThatThrownBy(() -> {
            PdfToHtml.pdf2Pic("/nonexistent/file.pdf", "/tmp/");
        }).isInstanceOf(Exception.class);
    }

    @Test
    void main_convertsSyntheticPdfIntoRequestedDirectory() throws Exception {
        File pdf = PdfTestHelper.createPdfWithDistinctCropAndTrimBoxes(tempDir.toFile());
        File output = new File(tempDir.toFile(), "rendered");
        assertThat(output.mkdir()).isTrue();

        PdfToHtml.main(new String[] {pdf.getAbsolutePath(), output.getAbsolutePath()});

        File jpeg = new File(output, "c_0.jpg");
        assertThat(jpeg).exists().isNotEmpty();
        byte[] encoded = Files.readAllBytes(jpeg.toPath());
        assertThat(encoded[0] & 0xff).isEqualTo(0xff);
        assertThat(encoded[1] & 0xff).isEqualTo(0xd8);
        BufferedImage rendered = ImageIO.read(jpeg);
        assertThat(rendered).isNotNull();
        assertThat(rendered.getWidth()).isEqualTo(700);
        assertThat(rendered.getHeight()).isEqualTo(350);
        assertThat(new File(output, "c.html")).content().contains("c_0.jpg", "<html>");

        Files.delete(jpeg.toPath());
        Files.delete(new File(output, "c.html").toPath());
        assertThat(jpeg).doesNotExist();
        PdfToHtml.main(new String[] {pdf.getAbsolutePath(), output.getAbsolutePath()});
        assertThat(jpeg).exists().isNotEmpty();
        assertThat(new File(output, "c.html")).content().contains("c_0.jpg");
    }

    @Test
    void main_rejectsMissingArgumentsAndBadPdf() {
        assertThatThrownBy(() -> PdfToHtml.main(new String[0]))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PdfToHtml.main(new String[] {
                new File(tempDir.toFile(), "missing.pdf").getAbsolutePath(), tempDir.toString()}))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void main_writesEveryPageUsingLegacyNames() throws Exception {
        File pdf = PdfTestHelper.createMultiPagePdf(tempDir.toFile(), 2);
        File output = new File(tempDir.toFile(), "legacy-pages");
        assertThat(output.mkdir()).isTrue();

        PdfToHtml.main(new String[] {pdf.getAbsolutePath(), output.getAbsolutePath()});

        assertThat(new File(output, "c_0.jpg")).exists().isNotEmpty();
        assertThat(new File(output, "c_1.jpg")).exists().isNotEmpty();
        assertThat(new File(output, "c.html")).content().contains("c_0.jpg", "c_1.jpg");
    }

    @Test
    void pdf2Pic_keepsCropBoxPngRendering() throws Exception {
        File pdf = PdfTestHelper.createPdfWithDistinctCropAndTrimBoxes(tempDir.toFile());
        File output = new File(tempDir.toFile(), "png-pages");
        assertThat(output.mkdir()).isTrue();

        PdfToHtml.pdf2Pic(pdf.getAbsolutePath(), output.getAbsolutePath());

        BufferedImage image = ImageIO.read(new File(output, "0.png"));
        assertThat(image).isNotNull();
        assertThat(image.getWidth()).isEqualTo(1000);
        assertThat(image.getHeight()).isEqualTo(1000);
    }

    @Test
    void main_reportsOutputWriteFailure() throws Exception {
        File pdf = PdfTestHelper.createMinimalPdf(tempDir.toFile());
        File missingDirectory = new File(tempDir.toFile(), "missing-html-output");

        assertThatThrownBy(() -> PdfToHtml.main(new String[] {
                pdf.getAbsolutePath(), missingDirectory.getAbsolutePath()}))
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(java.io.IOException.class);
        assertThat(missingDirectory).doesNotExist();
    }

    @Test
    void pdf2Pic_reportsOutputWriteFailure() throws Exception {
        File pdf = PdfTestHelper.createMinimalPdf(tempDir.toFile());
        File missingDirectory = new File(tempDir.toFile(), "missing-output");

        assertThatThrownBy(() -> PdfToHtml.pdf2Pic(pdf.getAbsolutePath(), missingDirectory.getAbsolutePath()))
                .isInstanceOf(java.io.IOException.class);
        assertThat(missingDirectory).doesNotExist();
    }
}
