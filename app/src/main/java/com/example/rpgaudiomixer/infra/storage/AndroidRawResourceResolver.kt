package com.example.rpgaudiomixer.infra.storage

import android.content.Context

class AndroidRawResourceResolver(
    private val appContext: Context,
) : RawResourceResolver {

    override fun rawResIdOrNull(name: String): Int? {
        val id = appContext.resources.getIdentifier(name, "raw", appContext.packageName)
        return id.takeIf { it != 0 }
    }
}

