package com.example.data.remote

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class AlQuranMultiEditionResponse(
    val code: Int,
    val status: String,
    val data: List<AlQuranEditionData>?
)

@JsonClass(generateAdapter = true)
data class AlQuranEditionData(
    val number: Int,
    val name: String,
    val englishName: String,
    val ayahs: List<AlQuranAyahDto>
)

@JsonClass(generateAdapter = true)
data class AlQuranAyahDto(
    val number: Int,
    val text: String,
    val numberInSurah: Int
)

/**
 * Authoritative AlQuran Cloud API (free, open, zero API key required)
 * Fetches verified Tanzil Uthmani script (quran-uthmani) and Al-Muyassar Tafsir (ar.muyassar).
 */
interface QuranApiService {
    @GET("v1/surah/{surahNumber}/editions/quran-uthmani,ar.muyassar")
    suspend fun fetchSurahWithTafsir(
        @Path("surahNumber") surahNumber: Int
    ): AlQuranMultiEditionResponse

    companion object {
        private const val BASE_URL = "https://api.alquran.cloud/"

        fun create(): QuranApiService {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(QuranApiService::class.java)
        }
    }
}
