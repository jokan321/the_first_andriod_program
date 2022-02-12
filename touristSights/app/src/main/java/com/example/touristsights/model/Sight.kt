package com.example.touristsights.model

import android.content.res.Resources
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader

@Serializable
class Sight(
    val name: String,
    val imageName: String,
    val description: String,
    val kind: String
)

fun getSights(resources: Resources): List<Sight> {
    val assetManager = resources.assets
    val inputStream = assetManager.open("sights.json")
    val bufferReader = BufferedReader(InputStreamReader(inputStream))
    val str: String = bufferReader.readText()

    return Json.decodeFromString(str)
}