/*
 * Originally created by WonJoongLee, licensed under the Apache License, Version 2.0.
 * https://github.com/WonJoongLee/BuildFinishNotifier
 *
 * Modified by Matheus Souza (matheuhsouza.com.br)
 */
package br.com.matheuhsouza.buildbell

import com.android.tools.idea.gradle.project.build.BuildContext
import com.android.tools.idea.gradle.project.build.BuildStatus
import com.android.tools.idea.gradle.project.build.GradleBuildListener
import com.intellij.util.ui.UIUtil

class BuildBell(
    private val soundPlayer: (String) -> Unit = { UIUtil.playSoundFromResource(it) }
) : GradleBuildListener {
    override fun buildStarted(context: BuildContext) = Unit

    override fun buildFinished(status: BuildStatus, context: BuildContext?) {
        when (status) {
            BuildStatus.SUCCESS -> soundPlayer("/success_sound_1.wav")
            BuildStatus.FAILED -> soundPlayer("/fail_sound_1.wav")
            BuildStatus.CANCELED -> Unit
        }
    }
}
