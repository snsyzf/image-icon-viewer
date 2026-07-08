package com.gitee.plugins.iconviewer

import com.google.common.cache.CacheBuilder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.IconUtil
import com.intellij.util.ImageLoader
import java.awt.Image
import java.util.Optional
import javax.imageio.ImageIO
import javax.swing.Icon

internal object ImageIconLoader {

    private val LOG = Logger.getInstance(ImageIconLoader::class.java)

    private const val DEFAULT_SIZE = 16
    private const val SVG_EXTENSION = "svg"

    private val basicImageExtensions = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp")

    val imageExtensions: Set<String> by lazy {
        buildSet {
            addAll(basicImageExtensions)
            ImageIO.getReaderFileSuffixes()
                .asSequence()
                .map { it.lowercase() }
                .filter { it != SVG_EXTENSION }
                .forEach(::add)
        }
    }

    private val cache = CacheBuilder.newBuilder()
        .softValues()
        .maximumSize(512)
        .build<String, Optional<Icon>>()

    fun isSupportedFile(name: String): Boolean {
        val extension = FileUtilRt.getExtension(name).lowercase()
        return extension == SVG_EXTENSION || extension in imageExtensions
    }

    fun getIcon(file: VirtualFile): Icon? {
        if (!file.isValid || file.isDirectory || !isSupportedFile(file.name)) {
            return null
        }

        val path = file.path
        return try {
            cache.get(path) {
                Optional.ofNullable(loadIcon(file))
            }.orElse(null)
        } catch (e: Exception) {
            LOG.debug("Failed to load icon for ${file.path}", e)
            null
        }
    }

    private fun loadIcon(file: VirtualFile): Icon? {
        val image = loadImage(file) ?: return null
        return IconUtil.createImageIcon(scaleToDefaultSize(image))
    }

    private fun loadImage(file: VirtualFile): Image? {
        return try {
            ImageLoader.loadFromBytes(file.contentsToByteArray())
        } catch (e: Exception) {
            LOG.debug("Failed to decode image for ${file.path}", e)
            null
        }
    }

    private fun scaleToDefaultSize(image: Image): Image {
        val width = image.getWidth(null)
        val height = image.getHeight(null)
        if (width <= 0 || height <= 0 || (width == DEFAULT_SIZE && height == DEFAULT_SIZE)) {
            return image
        }
        return ImageLoader.scaleImage(image, DEFAULT_SIZE)
    }
}
