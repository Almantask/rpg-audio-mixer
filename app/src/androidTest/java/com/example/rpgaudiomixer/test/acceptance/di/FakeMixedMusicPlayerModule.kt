package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.app.di.MusicPlayerModule
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

/**
 * Replaces the prod player with a fake that acceptance tests can control per scenario.
 */
/**
 * Bridges PicoContainer's per-scenario [AcceptanceTestPlayerHolder] to Hilt's singleton graph.
 *
 * NOTE: No @Singleton scope here. We want Hilt to call the provider each time to read
 * the latest per-scenario fake from the holder. This allows Cucumber scenarios to swap
 * fakes without restarting the entire Hilt component.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [MusicPlayerModule::class],
)
object FakeMixedMusicPlayerModule {

    @Provides
    fun provideMixedMusicPlayer(): MixedMusicPlayer = AcceptanceTestPlayerHolder.player
}
