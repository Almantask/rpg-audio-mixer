package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.app.screens.SettingsSyncRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SettingsSyncRepositoryEntryPoint {
    fun settingsSyncRepository(): SettingsSyncRepository
}
