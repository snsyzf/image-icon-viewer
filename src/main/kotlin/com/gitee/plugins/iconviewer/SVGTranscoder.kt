package com.gitee.plugins.iconviewer

import com.intellij.util.ui.ImageUtil
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import java.awt.image.BufferedImage

/**
 * ImageIconProvider.
 * @author yzf
 */
class SVGTranscoder : ImageTranscoder() {
    var image: BufferedImage? = null
        private set

    override fun createImage(w: Int, h: Int): BufferedImage? {
        image = ImageUtil.createImage(w, h, BufferedImage.TYPE_INT_ARGB)
        return image
    }

    override fun writeImage(img: BufferedImage, out: TranscoderOutput?) {}
}
