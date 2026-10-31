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
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link Pdf2ImgUtil}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class Pdf2ImgUtilTest {

    @TempDir
    Path tempDir;

    @Test
    void main_withNoArgs_handlesGracefully() {
        // main method catches all exceptions internally via try-catch
        assertThatCode(() -> Pdf2ImgUtil.main(new String[]{}))
                .doesNotThrowAnyException();
    }

    @Test
    void main_withInvalidPath_handlesGracefully() {
        assertThatCode(() -> Pdf2ImgUtil.main(new String[]{"invalid"}))
                .doesNotThrowAnyException();
    }

    @Test
    void main_convertsEveryPageOfSyntheticPdf() throws Exception {
        File pdf = PdfTestHelper.createMultiPagePdf(tempDir.toFile(), 2);
        File output = new File(tempDir.toFile(), "images");
        assertThat(output.mkdir()).isTrue();

        Pdf2ImgUtil.main(new String[] {pdf.getAbsolutePath(), output.getAbsolutePath()});

        assertThat(new File(output, "icepdf_0.png")).exists().isNotEmpty();
        assertThat(new File(output, "icepdf_1.png")).exists().isNotEmpty();
    }

    @Test
    void main_reportsInvalidPdfInsteadOfClaimingSuccess() {
        assertThatThrownBy(() -> Pdf2ImgUtil.main(new String[] {
                new File(tempDir.toFile(), "missing.pdf").getAbsolutePath(), tempDir.toString()}))
                .isInstanceOf(IllegalStateException.class);
    }
}
