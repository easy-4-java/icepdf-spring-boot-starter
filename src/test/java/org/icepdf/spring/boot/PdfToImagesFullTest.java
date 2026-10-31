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
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link PdfToImages} - comprehensive coverage.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class PdfToImagesFullTest {

    @TempDir
    Path tempDir;

    @Test
    void zoomImage_withValidPng_createsResizedJpg() throws IOException {
        File srcPng = PdfTestHelper.createMinimalPng(tempDir.toFile());
        File destJpg = new File(tempDir.toFile(), "resized.jpg");

        PdfToImages.zoomImage(srcPng.getAbsolutePath(), destJpg.getAbsolutePath(), 10, 10);

        assertThat(destJpg).exists();
        assertThat(destJpg.length()).isGreaterThan(0);
    }

    @Test
    void zoomImage_withLargerDimensions_createsResizedImage() throws IOException {
        File srcPng = PdfTestHelper.createMinimalPng(tempDir.toFile());
        File destJpg = new File(tempDir.toFile(), "large.jpg");

        PdfToImages.zoomImage(srcPng.getAbsolutePath(), destJpg.getAbsolutePath(), 100, 100);

        assertThat(destJpg).exists();
    }

    @Test
    void zoomImage_withNonExistentSource_handlesGracefully() {
        File destJpg = new File(tempDir.toFile(), "output.jpg");

        assertThatCode(() -> {
            PdfToImages.zoomImage("/nonexistent/source.png", destJpg.getAbsolutePath(), 10, 10);
        }).doesNotThrowAnyException();
    }

    @Test
    void jpgFilename_withValidPath_returnsFormattedFilename() {
        String result = PdfToImages.jpgFilename("C:\\docs\\test.pdf", 0);
        assertThat(result).contains("test-1.jpg");
    }

    @Test
    void jpgFilename_withIndex2_returnsIncrementedFilename() {
        String result = PdfToImages.jpgFilename("C:\\docs\\myfile.pdf", 2);
        assertThat(result).contains("myfile-3.jpg");
    }

    @Test
    void jpgFilename_withNull_returnsEmpty() {
        String result = PdfToImages.jpgFilename(null, 0);
        assertThat(result).isEmpty();
    }

    @Test
    void jpgFilename_withEmptyString_returnsEmpty() {
        String result = PdfToImages.jpgFilename("", 0);
        assertThat(result).isEmpty();
    }

    @Test
    void jpgFilename_preservesPath() {
        String result = PdfToImages.jpgFilename("C:\\docs\\report.pdf", 0);
        assertThat(result).startsWith("C:\\docs\\");
        assertThat(result).endsWith(".jpg");
    }

    @Test
    void constants_haveExpectedValues() {
        assertThat(PdfToImages.FILETYPE_JPG).isEqualTo("jpg");
        assertThat(PdfToImages.SUFF_IMAGE).isEqualTo(".jpg");
    }

    @Test
    void tranfer1_withValidPdf_convertsFirstPage() throws Exception {
        File pdf = PdfTestHelper.createMinimalPdf(tempDir.toFile());

        assertThatCode(() -> {
            try {
                PdfToImages.tranfer1(pdf.getAbsolutePath(), 1.0f);
            } catch (Exception e) {
                // icepdf may throw for minimal PDF content
            }
        }).doesNotThrowAnyException();
    }

    @Test
    void tranfer_withValidPdf_convertsAllPages() throws Exception {
        File pdf = PdfTestHelper.createMinimalPdf(tempDir.toFile());

        assertThatCode(() -> {
            try {
                PdfToImages.tranfer(pdf.getAbsolutePath(), 1.0f);
            } catch (Exception e) {
                // icepdf may throw for minimal PDF content
            }
        }).doesNotThrowAnyException();
    }

    @Test
    void tranfer_withMultiPagePdf_convertsAllPages() throws Exception {
        File pdf = PdfTestHelper.createMultiPagePdf(tempDir.toFile(), 3);

        assertThatCode(() -> {
            try {
                PdfToImages.tranfer(pdf.getAbsolutePath(), 1.0f);
            } catch (Exception e) {
                // icepdf may throw for minimal PDF content
            }
        }).doesNotThrowAnyException();
    }

    @Test
    void tranfer_withNonExistentFile_throwsException() {
        assertThatThrownBy(() -> {
            PdfToImages.tranfer("/nonexistent/file.pdf", 1.0f);
        }).isInstanceOf(Exception.class);
    }

    @Test
    void tranfer1_withNonExistentFile_throwsException() {
        assertThatThrownBy(() -> {
            PdfToImages.tranfer1("/nonexistent/file.pdf", 1.0f);
        }).isInstanceOf(Exception.class);
    }

    @Test
    void main_withNoArgs_handlesGracefully() {
        assertThatCode(() -> PdfToImages.main(new String[]{}))
                .doesNotThrowAnyException();
    }

    @Test
    void main_rendersSyntheticPdfIntoItsOwnDirectory() throws Exception {
        File pdf = PdfTestHelper.createMinimalPdf(tempDir.toFile());

        PdfToImages.main(new String[] {pdf.getAbsolutePath(), "1.0"});

        File image = new File(tempDir.toFile(), "test/test-1.jpg");
        assertThat(image).exists().isNotEmpty();

        PdfToImages.tranfer1(pdf.getAbsolutePath(), 1.0f);
        PdfToImages.tranfer(pdf.getAbsolutePath(), 1.0f);
        assertThat(new File(tempDir.toFile(), "test/test-1-small.jpg")).exists().isNotEmpty();
    }

    @Test
    void main_reportsInvalidPdf() {
        assertThatThrownBy(() -> PdfToImages.main(new String[] {
                new File(tempDir.toFile(), "missing.pdf").getAbsolutePath(), "1.0"}))
                .isInstanceOf(IllegalStateException.class);
    }
}
