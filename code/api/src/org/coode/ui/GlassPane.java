/*******************************************************************************
 * Copyright (c) 2012 Eleni Mikroyannidi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Eleni Mikroyannidi, Luigi Iannone - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package org.coode.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;

/** @author Luigi Iannone */
public class GlassPane extends JComponent {
    private static final long serialVersionUID = -7970489771369242157L;
    private static final int BAR_WIDTH = 200;
    private static final int BAR_HEIGHT = 10;
    private static final Color TEXT_COLOR = new Color(0x333333);
    private String message = "Agglomerating...";

    /** default constructor */
    public GlassPane() {
        setBackground(Color.WHITE);
        setFont(new Font("Default", Font.BOLD, 16));
    }

    /** @param message
     *            message */
    public void setMessage(String message) {
        // computes the damaged area
        this.message = message;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // enables anti-aliasing
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        // gets the current clipping area
        Rectangle clip = g.getClipBounds();
        // sets a 65% translucent composite
        AlphaComposite alpha = AlphaComposite.SrcOver.derive(0.65f);
        Composite composite = g2.getComposite();
        g2.setComposite(alpha);
        // fills the background
        g2.setColor(getBackground());
        g2.fillRect(clip.x, clip.y, clip.width, clip.height);
        // centers the progress bar on screen
        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - BAR_WIDTH) / 2;
        int y = (getHeight() - BAR_HEIGHT - metrics.getDescent()) / 2;
        // draws the text
        g2.setColor(TEXT_COLOR);
        g2.drawString(message, x, y);
        g2.setComposite(composite);
    }
}
