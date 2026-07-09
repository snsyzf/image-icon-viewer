package com.gitee.plugins.iconviewer

import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.serviceIfCreated
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.IconDeferrer

private const val PLUGIN_ID = "com.plugin.image-icon-viewer"

internal class ImageIconDynamicPluginListener : DynamicPluginListener {

    override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor) {
        if (pluginDescriptor.pluginId.idString != PLUGIN_ID) {
            return
        }
        refreshProjectViews()
    }

    override fun beforePluginUnload(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean) {
        if (pluginDescriptor.pluginId.idString != PLUGIN_ID) {
            return
        }
        serviceIfCreated<ImageIconLoaderService>()?.clearCache()
        IconDeferrer.getInstance().clearCache()
        IconLoader.clearCache()
        refreshProjectViews()
    }
}

private fun refreshProjectViews() {
    ApplicationManager.getApplication().invokeLater {
        val application = ApplicationManager.getApplication()
        if (application.isDisposed) {
            return@invokeLater
        }
        for (project in ProjectManager.getInstance().openProjects) {
            if (!project.isDisposed) {
                ProjectView.getInstance(project).refresh()
            }
        }
    }
}
