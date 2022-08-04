package com.gitee.plugins.iconviewer

import com.intellij.ide.IconProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.psi.PsiElement
import com.intellij.util.IconUtil
import java.awt.Image
import java.io.FileInputStream
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.Icon

class ImageIconProvider : IconProvider(), DumbAware {

    override fun getIcon(psiElement: PsiElement, flags: Int): Icon? {
        val containingFile = psiElement.containingFile

        val path = containingFile?.virtualFile?.canonicalFile?.canonicalPath
        val name = containingFile?.name

        if (path != null && name != null) {
            try {
                if (isImage(name)) {
                    val image: Image? = FileInputStream(path).use { ImageIO.read(it) }
                    if (image != null) {
                        return ScaleIcon(IconUtil.createImageIcon(image), DEFAULT_SIZE, DEFAULT_SIZE)
                    }
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

    }
}
