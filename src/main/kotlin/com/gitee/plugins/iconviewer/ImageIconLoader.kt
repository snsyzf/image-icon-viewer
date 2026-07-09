package com.gitee.plugins.iconviewer

import com.google.common.cache.CacheBuilder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.scale.DerivedScaleType
import com.intellij.ui.scale.ScaleContext
import com.intellij.ui.svg.renderSvg
import com.intellij.util.IconUtil
import com.intellij.util.ImageLoader
import com.intellij.util.ui.ImageUtil
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
        val bytes = try {
            file.contentsToByteArray()
        } catch (e: Exception) {
            LOG.debug("Failed to read file ${file.path}", e)
            return null
        }

        val extension = FileUtilRt.getExtension(file.name).lowercase()
        return try {
            val scaleContext = ScaleContext.create()
            when (extension) {
                SVG_EXTENSION -> {
                    val pixScale = scaleContext.getScale(DerivedScaleType.PIX_SCALE).toFloat()
                    ImageUtil.ensureHiDPI(renderSvg(data = bytes, scale = pixScale), scaleContext)
                }
                else -> ImageLoader.loadFromBytes(bytes)
            }
        } catch (e: Exception) {
            LOG.debug("Failed to decode image for ${file.path}", e)
            null
        }
    }

    private fun scaleToDefaultSize(image: Image): Image {
        val userWidth = ImageUtil.getUserWidth(image)
        val userHeight = ImageUtil.getUserHeight(image)
        if (userWidth <= 0 || userHeight <= 0) {
            return image
        }

        val maxUserDim = maxOf(userWidth, userHeight)
        if (maxUserDim <= DEFAULT_SIZE) {
            return image
        }

        val scale = DEFAULT_SIZE.toDouble() / maxUserDim
        return ImageLoader.scaleImage(image, scale)
    }
}
