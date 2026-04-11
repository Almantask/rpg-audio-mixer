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
        val repository = entryPoint.campaignRepository()
        
        PicoToHiltBridge.campaignRepository = repository
        
        runBlocking {
            repository.deleteAll()
        }
    }
}
