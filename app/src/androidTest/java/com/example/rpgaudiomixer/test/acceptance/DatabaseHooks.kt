package com.example.rpgaudiomixer.test.acceptance

import androidx.test.platform.app.InstrumentationRegistry
import com.example.rpgaudiomixer.test.acceptance.di.PicoToHiltBridge
import com.example.rpgaudiomixer.test.acceptance.di.RepositoryEntryPoint
import dagger.hilt.android.EntryPointAccessors
import io.cucumber.java.Before
import kotlinx.coroutines.runBlocking

class DatabaseHooks {

    @Before(order = 0)
    fun clearDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(context, RepositoryEntryPoint::class.java)

        val sceneRepository = entryPoint.sceneRepository()
        PicoToHiltBridge.sceneRepository = sceneRepository

        val campaignRepository = entryPoint.campaignRepository()
        PicoToHiltBridge.campaignRepository = campaignRepository

        val sessionRepository = entryPoint.sessionRepository()
        PicoToHiltBridge.sessionRepository = sessionRepository

        val soundscapeCategoryRepository = entryPoint.soundscapeCategoryRepository()
        PicoToHiltBridge.soundscapeCategoryRepository = soundscapeCategoryRepository

        runBlocking {
            soundscapeCategoryRepository.deleteAll()
            sceneRepository.deleteAll()
            sessionRepository.deleteAll()
            campaignRepository.deleteAll()
        }
    }
}
