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

class BuildBell : GradleBuildListener {
    override fun buildStarted(context: BuildContext) = Unit

    override fun buildFinished(status: BuildStatus, context: BuildContext?) {
        playEndSound(status)
    }

    private fun playEndSound(status: BuildStatus) {
        when (status) {
            BuildStatus.SUCCESS -> UIUtil.playSoundFromResource("/success_sound_1.wav")
            BuildStatus.FAILED -> UIUtil.playSoundFromResource("/fail_sound_1.wav")
            else -> Unit
        }
    }
}
