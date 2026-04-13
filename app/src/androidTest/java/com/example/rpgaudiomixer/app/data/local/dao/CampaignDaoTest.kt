package com.example.rpgaudiomixer.app.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.rpgaudiomixer.app.data.local.AppDatabase
import com.example.rpgaudiomixer.app.data.local.entities.CampaignEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CampaignDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: CampaignDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.campaignDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeAndReadCampaign() = runBlocking {
        val campaign = CampaignEntity(name = "Test Campaign", lastPlayedAt = 1000L)
        dao.upsert(campaign)

        val allCampaigns = dao.observeAll().first()
        assertEquals(1, allCampaigns.size)
        assertEquals("Test Campaign", allCampaigns[0].name)
    }

    @Test
    fun deleteCampaign() = runBlocking {
        val campaign = CampaignEntity(id = 1, name = "To Delete", lastPlayedAt = 1000L)
        dao.upsert(campaign)
        dao.delete(campaign)

        val allCampaigns = dao.observeAll().first()
        assertTrue(allCampaigns.isEmpty())
    }

    @Test
    fun deleteAllCampaigns() = runBlocking {
        dao.upsert(CampaignEntity(name = "C1", lastPlayedAt = 1000L))
        dao.upsert(CampaignEntity(name = "C2", lastPlayedAt = 2000L))

        dao.deleteAll()

        val allCampaigns = dao.observeAll().first()
        assertTrue(allCampaigns.isEmpty())
    }
}
