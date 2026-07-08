package com.gitee.plugins.iconviewer

import com.intellij.ide.FileIconProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

/**
 * Provides image thumbnails directly from VirtualFile for Project View.
 */
class ImageFileIconProvider : FileIconProvider {

    override fun getIcon(file: VirtualFile, flags: Int, project: Project?): Icon? {
        return ImageIconLoader.getIcon(file)
    }
}
