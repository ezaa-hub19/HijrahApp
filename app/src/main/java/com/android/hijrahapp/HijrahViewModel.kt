package com.android.hijrahapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class HijrahViewModel : ViewModel() {
    private val _asmaulHusnaList = MutableStateFlow<List<AsmaulHusnaItem>>(emptyList())
    val asmaulHusnaList = _asmaulHusnaList.asStateFlow()

    private val _surahList = MutableStateFlow<List<Surah>>(emptyList())
    val surahList = _surahList.asStateFlow()
    private val _jadwalSholat = MutableStateFlow<Timings?>(null)
    val jadwalSholat = _jadwalSholat.asStateFlow()
    private val _doaList = MutableStateFlow<List<Doa>>(emptyList())
    val doaList = _doaList.asStateFlow()
    private val _detailSurah = MutableStateFlow<DetailSurahData?>(null)
    val detailSurah = _detailSurah.asStateFlow()

    private val _currentLat = MutableStateFlow(0.0)
    private val _currentLng = MutableStateFlow(0.0)
    private val _currentCityDisplay = MutableStateFlow("Jakarta")
    val currentCityDisplay = _currentCityDisplay.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. LOAD SURAH DATA (Al-Qur'an)
                if (_surahList.value.isEmpty()) {
                    try {
                        _surahList.value = RetrofitClient.instance.getSurah().data ?: emptyList()
                    } catch (e: Exception) {
                        System.err.println("API SURAH GAGAL: ${e.message}")
                        // FALLBACK: Placeholder Surah jika network gagal
                        _surahList.value = listOf(Surah(1, "الفاتحة", "Al-Fatihah", "Pembukaan", 7))
                    }
                }

                // 2. LOAD DOA DATA
                if (_doaList.value.isEmpty()) {
                    try {
                        _doaList.value = RetrofitClient.instance.getDoa("https://open-api.my.id/api/doa")
                    } catch (e: Exception) {
                        System.err.println("API DOA GAGAL: ${e.message}")
                        // FALLBACK: Placeholder Doa jika network/GSON gagal
                        _doaList.value = listOf(Doa(1, "Doa Makan", "بِسْمِ اللَّهِ", "Bismillah", "Dengan nama Allah"))
                    }
                }

                // 3. LOAD ASMAUL HUSNA (Lokal)
                if (_asmaulHusnaList.value.isEmpty()) {
                    val type = object : TypeToken<List<AsmaulHusnaItem>>() {}.type
                    val list: List<AsmaulHusnaItem> = Gson().fromJson(ASMAUL_HUSNA_JSON, type)
                    _asmaulHusnaList.value = list
                }

            } catch (e: Exception) {
                System.err.println("VIEWMODEL ROOT ERROR: ${e.message}")
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    fun setSholatByManualCity(city: String) {
        _currentCityDisplay.value = city
        viewModelScope.launch { fetchSholatByCity(city) }
    }

    private suspend fun fetchSholatByCity(city: String) {
        _currentCityDisplay.value = "Mencari jadwal di $city..."
        try {
            val url = "https://api.aladhan.com/v1/timingsByCity?city=$city&country=Indonesia&method=4"
            _jadwalSholat.value = RetrofitClient.instance.getJadwalSholat(url).data?.timings
            _currentCityDisplay.value = city
        } catch (e: Exception) {
            _currentCityDisplay.value = "Gagal menemukan: $city"
            e.printStackTrace()
        }
    }

    fun updateLocation(lat: Double, lng: Double) {
        if (lat == 0.0 && lng == 0.0) return
        _currentLat.value = lat
        _currentLng.value = lng
        _currentCityDisplay.value = "Lokasi Saat Ini"
        viewModelScope.launch { fetchSholatByCoordinates(lat, lng) }
    }

    private suspend fun fetchSholatByCoordinates(lat: Double, lng: Double) {
        _currentCityDisplay.value = "Fetching Jadwal GPS..."
        try {
            val url = "https://api.aladhan.com/v1/timings?latitude=$lat&longitude=$lng&method=4"
            _jadwalSholat.value = RetrofitClient.instance.getJadwalSholat(url).data?.timings
            _currentCityDisplay.value = "GPS Aktif"
        } catch (e: Exception) {
            _currentCityDisplay.value = "Gagal Ambil Jadwal GPS"
            e.printStackTrace()
        }
    }

    fun getDetailSurah(nomor: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _detailSurah.value = null
            try { _detailSurah.value = RetrofitClient.instance.getDetailSurah(nomor).data }
            catch (e: Exception) { e.printStackTrace() }
            finally { _isLoading.value = false }
        }
    }
}
