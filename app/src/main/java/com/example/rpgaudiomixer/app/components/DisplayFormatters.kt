package com.example.rpgaudiomixer.app.components

import java.text.DateFormat
import java.util.Date

fun Long.toDisplayDate(): String {
    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(this))
}
