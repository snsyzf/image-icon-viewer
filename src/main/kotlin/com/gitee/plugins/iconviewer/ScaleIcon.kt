package com.gitee.plugins.iconviewer

import com.intellij.ui.JBColor
import com.intellij.util.ui.ImageUtil

import java.awt.Component
import java.awt.Graphics
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.swing.Icon

/**
 * ImageIconProvider.
 * @author yzf
 */
class ScaleIcon(icon: Icon, width: Int = 16, height: Int = 16) : Icon {

    private val image: BufferedImage = ImageUtil.createImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)

    init {
        val g2d = this.image.createGraphics()
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)

        val sx = width.toDouble() / icon.iconWidth
        val sy = height.toDouble() / icon.iconHeight

        g2d.scale(sx, sy)
        icon.paintIcon(null, g2d, 0, 0)
        g2d.dispose()
    }

    override fun getIconWidth(): Int = this.image.width
    override fun getIconHeight(): Int = this.image.height

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        g.drawImage(this.image, 0, 0, c)
        g.color = JBColor.MAGENTA
        g.drawRect(0, 0, iconWidth, iconHeight)
    }
}
