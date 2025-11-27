package com.android.hijrahapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiService {
    @GET("surat") suspend fun getSurah(): QuranResponse
    @GET("surat/{nomor}") suspend fun getDetailSurah(@Path("nomor") nomor: Int): DetailSurahResponse
    @GET suspend fun getJadwalSholat(@Url url: String): SholatResponse
    @GET suspend fun getDoa(@Url url: String): List<Doa>
    @GET("https://raw.githubusercontent.com/mazel-api/asmaul-husna/main/data/id.json")
    suspend fun getAsmaulHusna(): List<AsmaulHusnaItem>
}

object RetrofitClient {
    private const val BASE_URL = "https://equran.id/api/v2/"
    val instance: ApiService by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)
    }
}
