package com.gitee.plugins.iconviewer

import com.intellij.ide.IconProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.ui.scale.ScaleContext
import com.intellij.util.IconUtil
import com.intellij.util.SVGLoader

import java.awt.Image
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.Icon

class ImageIconProvider : IconProvider(), DumbAware {

    override fun getIcon(psiElement: PsiElement, flags: Int): Icon? {
        val containingFile = psiElement.containingFile

        var image: Image? = null
        val path = containingFile?.virtualFile?.canonicalFile?.canonicalPath
        val name = containingFile?.name

        if (path != null && name != null) {
            try {
                if (isSvg(name)) {
                    image = loadSVG(containingFile, path)
                } else if (isImage(name)) {
                    image = FileInputStream(path).use { ImageIO.read(it) }
                }
                if (image != null) {
                    return ScaleIcon(IconUtil.createImageIcon(image), DEFAULT_SIZE, DEFAULT_SIZE)
                }
            } catch (ignored: IOException) {
            }
        }
        return null
    }

    companion object {

        private const val DEFAULT_SIZE = 16

        private val IMAGE_EXTENSIONS = ImageIO.getReaderFileSuffixes()

        private fun isImage(filename: String): Boolean = FileUtilRt.getExtension(filename).let { ext ->
            IMAGE_EXTENSIONS.any { ext.equals(it, ignoreCase = true) }
        }

        private fun isSvg(filename: String): Boolean =
            "svg".equals(FileUtilRt.getExtension(filename), ignoreCase = true)

        private fun loadSVG(containingFile: PsiFile, canonicalPath: String): Image? {
            val file = containingFile.virtualFile
            val url = Ref.create<Any>()
            try {
                url.set(File(file.path).toURI().toURL())
            } catch (ignored: MalformedURLException) {
            }
            return FileInputStream(canonicalPath).use {
                SVGLoader.loadHiDPI(
                    url.get() as URL,
                    it,
                    ScaleContext.create()
                )
            }
        }
    }
}
