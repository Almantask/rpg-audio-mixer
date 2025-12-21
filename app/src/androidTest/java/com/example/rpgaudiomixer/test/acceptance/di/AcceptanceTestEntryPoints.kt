package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.test.acceptance.world.FakeMusicPlayer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AcceptanceTestEntryPoint {
    fun fakeMusicPlayer(): FakeMusicPlayer
}

