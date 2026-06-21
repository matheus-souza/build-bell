package br.com.matheuhsouza.buildbell

import com.android.tools.idea.gradle.project.build.BuildContext
import com.android.tools.idea.gradle.project.build.BuildStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BuildBellTest {

    @Test
    fun `success build plays success sound`() {
        val played = mutableListOf<String>()
        val bell = BuildBell(soundPlayer = { played.add(it) })

        bell.buildFinished(BuildStatus.SUCCESS, null)

        assertEquals(listOf("/success_sound_1.wav"), played)
    }

    @Test
    fun `failed build plays failure sound`() {
        val played = mutableListOf<String>()
        val bell = BuildBell(soundPlayer = { played.add(it) })

        bell.buildFinished(BuildStatus.FAILED, null)

        assertEquals(listOf("/fail_sound_1.wav"), played)
    }

    @Test
    fun `canceled build plays no sound`() {
        val played = mutableListOf<String>()
        val bell = BuildBell(soundPlayer = { played.add(it) })

        bell.buildFinished(BuildStatus.CANCELED, null)

        assertTrue(played.isEmpty())
    }

    @Test
    fun `each build event plays exactly one sound`() {
        val played = mutableListOf<String>()
        val bell = BuildBell(soundPlayer = { played.add(it) })

        bell.buildFinished(BuildStatus.SUCCESS, null)

        assertEquals(1, played.size)
    }

    @Test
    fun `no-arg constructor is usable without IntelliJ runtime`() {
        val bell = BuildBell()
        bell.buildFinished(BuildStatus.CANCELED, null)
    }

    @Test
    fun `started build event plays no sound`() {
        val played = mutableListOf<String>()
        val bell = BuildBell(soundPlayer = { played.add(it) })

        // BuildContext requires IntelliJ runtime to construct; allocateInstance bypasses
        // all constructors so we can pass a valid (empty) instance to the no-op method.
        val unsafeField = sun.misc.Unsafe::class.java.getDeclaredField("theUnsafe")
        unsafeField.isAccessible = true
        val context = (unsafeField.get(null) as sun.misc.Unsafe)
            .allocateInstance(BuildContext::class.java) as BuildContext

        bell.buildStarted(context)

        assertTrue(played.isEmpty())
    }
}
