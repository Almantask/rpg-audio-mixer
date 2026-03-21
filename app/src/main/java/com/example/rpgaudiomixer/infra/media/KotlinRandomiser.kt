package com.example.rpgaudiomixer.infra.media

import com.example.rpgaudiomixer.domain.media.Randomiser
import kotlin.random.Random

class KotlinRandomiser : Randomiser {
    override fun nextInt(until: Int): Int = Random.nextInt(until)
}
