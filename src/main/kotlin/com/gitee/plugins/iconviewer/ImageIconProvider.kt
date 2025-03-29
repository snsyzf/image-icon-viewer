package com.gitee.plugins.iconviewer

import com.google.common.cache.CacheBuilder
import com.intellij.ide.IconProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiElement
import com.intellij.util.IconUtil
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscodingHints
import org.apache.batik.transcoder.image.ImageTranscoder
import org.apache.batik.util.SVGConstants
import java.awt.Image
import java.io.FileInputStream
import java.io.InputStream
import javax.imageio.ImageIO
import javax.swing.Icon

/**
 * ImageIconProvider.
 * @author yzf
 */
class ImageIconProvider : IconProvider(), DumbAware {

    private val cache = CacheBuilder.newBuilder().softValues().build<String, Icon>()

    override fun getIcon(psiElement: PsiElement, flags: Int): Icon? {
        val containingFile = psiElement.containingFile

        val path = containingFile?.virtualFile?.canonicalFile?.canonicalPath
        val name = containingFile?.name

        if (path != null && name != null) {
            var icon: Icon? = cache.getIfPresent(path)
            if (icon != null) {
                return icon
            }
            try {
                var image: Image? = null
                if (isSVG(name)) {
                    image = FileInputStream(path).use { loadSVG(it, DEFAULT_SIZE, DEFAULT_SIZE) }
                } else if (isImage(name)) {
                    image = loadImage(FileInputStream(path).readAllBytes())
                }
                if (image != null) {
                    icon = ScaleIcon(IconUtil.createImageIcon(image), DEFAULT_SIZE, DEFAULT_SIZE)
                    cache.put(path, icon)
                }
            } catch (ignored: Exception) {
            }
            return icon
        }
        return null
    }

    companion object {

        private const val DEFAULT_SIZE = 16

    }
}

private fun isImage(filename: String): Boolean = FileUtilRt.getExtension(filename).let { ext ->
    extensions.any { ext.equals(it, ignoreCase = true) }
}

private fun isSVG(filename: String): Boolean = "svg".equals(FileUtilRt.getExtension(filename), true)
private val extensions = mutableSetOf(
    "jpg",
    "jpeg",
    "png",
    "gif",
    "bmp",
    "webp"
).apply { addAll(ImageIO.getReaderFileSuffixes().toSet()) }

fun loadImage(bytes: ByteArray): Image? {
    var image: Image? = null
    try {
        image = ImageIO.read(bytes.inputStream())
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return image
}

fun loadSVG(stream: InputStream, width: Int, height: Int): Image? {
    val transcoder = SVGTranscoder()
    val hints = TranscodingHints()
    hints[ImageTranscoder.KEY_WIDTH] = width.toFloat()
    hints[ImageTranscoder.KEY_HEIGHT] = height.toFloat()
    hints[ImageTranscoder.KEY_DOM_IMPLEMENTATION] = SVGDOMImplementation.getDOMImplementation()
    hints[ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI] = SVGConstants.SVG_NAMESPACE_URI
    hints[ImageTranscoder.KEY_DOCUMENT_ELEMENT] = SVGConstants.SVG_SVG_TAG
    hints[ImageTranscoder.KEY_XML_PARSER_VALIDATING] = false
    transcoder.transcodingHints = hints
    transcoder.transcode(TranscoderInput(stream), null)
    return transcoder.image
}
