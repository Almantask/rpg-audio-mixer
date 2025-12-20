package com.example.rpgaudiomixer.infra.storage

/**
 * Minimal abstraction to allow checking for a raw resource by name from JVM unit tests.
 */
fun interface RawResourceResolver {
    /**
     * @return the raw resource id if present, otherwise null.
     */
    fun rawResIdOrNull(name: String): Int?
}
