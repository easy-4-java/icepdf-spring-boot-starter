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
package org.icepdf.core.pobjects.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import org.icepdf.core.application.ProductInfo;
 
/**\n * Padding configuration for PDF rendering.\n *\n * @author <a href="https://github.com/loong10k">Loong Wan</a>\n * @since 1.0.0\n */
public class Padding {
	/*
	private static byte[] padding1 = { (byte) 0x36, (byte) 0x30, (byte) 0x32,
			(byte) 0x5D, (byte) 0x51, (byte) 0x53, (byte) 0xD };
	*/
	private static byte[] padding1={};
	/*
	private static byte[] padding3 = { (byte) 0x33, (byte) 0x5C, (byte) 0x5F,
			(byte) 0xD, (byte) 0x32, (byte) 0x63, (byte) 0x4E, (byte) 0x59,
			(byte) 0x62, (byte) 0x4E, (byte) 0x61, (byte) 0x56, (byte) 0x5C,
			(byte) 0x5B, (byte) 0xD, (byte) 0x3D, (byte) 0x62, (byte) 0x5F,
			(byte) 0x5D, (byte) 0x5C, (byte) 0x60, (byte) 0x52, (byte) 0x60,
			(byte) 0xD, (byte) 0x3C, (byte) 0x5B, (byte) 0x59, (byte) 0x66 };
	*/
	private static byte[] padding3={};
 
	public static final void getPadding(final Graphics2D g2,
			final Rectangle2D.Float mediaBox) {
 
		// setup painting parameters
		g2.scale(1, -1);
		g2.setColor(new Color(186, 0, 0));
 
		// setup strings for drawing water marks
		String version = new ProductInfo().getVersion();
 
		String headerText = new String(padding1) + version;
		String footerText = new String(padding3);
		int marginPadding = 5; // margin padding
 
		// draw footer
		g2.setFont(new Font("Dialog", Font.BOLD, 14));
		FontMetrics fontMetrics = g2.getFontMetrics();
		Rectangle2D fontBounds = fontMetrics.getStringBounds(
				footerText.toCharArray(), 0, footerText.length(), g2);
		int x = (int) (mediaBox.x + (mediaBox.width - fontBounds.getWidth()) / 2.0);
		int y = -1
				* (int) (mediaBox.getY() - mediaBox.getHeight() + marginPadding);
		g2.drawString(footerText, x, y);
 
		// draw header
		fontBounds = fontMetrics.getStringBounds(headerText.toCharArray(), 0,
				headerText.length(), g2);
		x = (int) (mediaBox.x + (mediaBox.width - fontBounds.getWidth()) / 2.0);
		y = -1 * (int) (mediaBox.y - fontBounds.getHeight() - marginPadding);
		g2.drawString(headerText, x, y);
 
	}
}
