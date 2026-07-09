package com.gitee.plugins.iconviewer

import com.google.common.cache.CacheBuilder
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.IconUtil
import com.intellij.util.ImageLoader
import com.intellij.util.ui.ImageUtil
import java.awt.Image
import java.net.URI
import java.util.Optional
import javax.imageio.ImageIO
import javax.swing.Icon

@Service(Service.Level.APP)
internal class ImageIconLoaderService : Disposable {

    private val LOG = Logger.getInstance(ImageIconLoaderService::class.java)

    private val cache = CacheBuilder.newBuilder()
        .softValues()
        .maximumSize(512)
        .build<String, Optional<Icon>>()

    private val imageExtensions: Set<String> by lazy {
        buildSet {
            addAll(BASIC_IMAGE_EXTENSIONS)
            ImageIO.getReaderFileSuffixes()
                .asSequence()
                .map { it.lowercase() }
                .filter { it != SVG_EXTENSION }
                .forEach(::add)
        }
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

    fun clearCache() {
        cache.invalidateAll()
    }

    override fun dispose() {
        clearCache()
    }

    private fun isSupportedFile(name: String): Boolean {
        val extension = FileUtilRt.getExtension(name).lowercase()
        return extension == SVG_EXTENSION || extension in imageExtensions
    }

    private fun loadIcon(file: VirtualFile): Icon? {
        val image = loadImage(file) ?: return null
        return IconUtil.createImageIcon(scaleToDefaultSize(image))
    }

    private fun loadImage(file: VirtualFile): Image? {
        val extension = FileUtilRt.getExtension(file.name).lowercase()
        return try {
            when (extension) {
                SVG_EXTENSION -> loadSvgImage(file)
                else -> ImageLoader.loadFromBytes(file.contentsToByteArray())
            }
        } catch (e: Exception) {
            LOG.debug("Failed to decode image for ${file.path}", e)
            null
        }
    }

    private fun loadSvgImage(file: VirtualFile): Image? {
        if (file.isInLocalFileSystem) {
            return ImageLoader.loadCustomIcon(file.toNioPath().toFile())
        }

        ImageLoader.loadFromUrl(URI.create(file.url).toURL())?.let { return it }

        val tempFile = FileUtil.createTempFile("image-icon-viewer", ".svg", true)
        return try {
            tempFile.writeBytes(file.contentsToByteArray())
            ImageLoader.loadCustomIcon(tempFile)
        } finally {
            tempFile.delete()
        }
    }

    private fun scaleToDefaultSize(image: Image): Image {
        val userWidth = ImageUtil.getUserWidth(image)
        val userHeight = ImageUtil.getUserHeight(image)
        if (userWidth <= 0 || userHeight <= 0) {
            return image
        }

        val maxUserDim = maxOf(userWidth, userHeight)
        if (maxUserDim <= DEFAULT_ICON_SIZE) {
            return image
        }

        val scale = DEFAULT_ICON_SIZE.toDouble() / maxUserDim
        return ImageLoader.scaleImage(image, scale)
    }

    private companion object {
        private const val DEFAULT_ICON_SIZE = 16
        private const val SVG_EXTENSION = "svg"

        private val BASIC_IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    }
}
