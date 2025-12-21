package com.example.rpgaudiomixer.test.acceptance.di

import com.example.rpgaudiomixer.app.di.AppModule
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.test.acceptance.world.FakeMusicPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

/**
 * Replaces the prod player with a fake that acceptance tests can control per scenario.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class],
)
object FakeMixedMusicPlayerModule {

    @Provides
    fun provideMixedMusicPlayer(): MixedMusicPlayer = AcceptanceTestPlayerHolder.player

    @Provides
    fun provideFakeMusicPlayer(): FakeMusicPlayer =
        (AcceptanceTestPlayerHolder.player as? FakeMusicPlayer)
            ?: error(
                "AcceptanceTestPlayerHolder.player is not a FakeMusicPlayer. " +
                    "Make sure your scenario sets AcceptanceTestPlayerHolder.player before launching the Activity."
            )
}
