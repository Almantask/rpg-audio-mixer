package com.example.rpgaudiomixer.domain.media

interface Randomiser {
    fun nextInt(until: Int): Int
}
