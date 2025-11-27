package com.android.hijrahapp

import com.google.gson.annotations.SerializedName

data class QuranResponse(val data: List<Surah>?)
data class Surah(val nomor: Int?, val nama: String?, val namaLatin: String?, val arti: String?, val jumlahAyat: Int?)
data class DetailSurahResponse(val data: DetailSurahData?)
data class DetailSurahData(val nomor: Int?, val nama: String?, val namaLatin: String?, val ayat: List<Ayat>?)
data class Ayat(val nomorAyat: Int?, val teksArab: String?, val teksLatin: String?, val teksIndonesia: String?)
data class SholatResponse(val data: SholatData?)
data class SholatData(val timings: Timings?)
data class Timings(
    @SerializedName("Fajr") val subuh: String?, @SerializedName("Dhuhr") val dzuhur: String?, @SerializedName("Asr") val ashar: String?,
    @SerializedName("Maghrib") val maghrib: String?, @SerializedName("Isha") val isya: String?
)
data class Doa(
    val id: Int?, @SerializedName("judul") val judul: String?, @SerializedName("arab") val arab: String?,
    val latin: String?, @SerializedName("terjemah") val terjemah: String?
)
data class AsmaulHusnaItem(
    val number: Int,
    val latin: String,
    val arab: String,
    @SerializedName("translation_id") val terjemahan: String
)
data class AsmaulHusnaResponse(
    val data: List<AsmaulHusnaItem>?
)