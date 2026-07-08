package com.gitee.plugins.iconviewer

import com.intellij.ide.IconProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import javax.swing.Icon

/**
 * ImageIconProvider.
 * @author yzf
 */
class ImageIconProvider : IconProvider(), DumbAware {

    override fun getIcon(psiElement: PsiElement, flags: Int): Icon? {
        val virtualFile = psiElement.containingFile?.virtualFile ?: return null
        return ImageIconLoader.getIcon(virtualFile)
    }
}
