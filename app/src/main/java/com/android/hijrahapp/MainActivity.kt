package com.android.hijrahapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.*
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.saveable.rememberSaveable


// --- KONFIGURASI WARNA ---
val DarkGreen = Color(0xFF0F3A12)
val LightGreen = Color(0xFF2E7D32)
val SoftGreen = Color(0xFFE8F5E9)
val AccentGold = Color(0xFFFFB300)
// --- Pastikan Konfigurasi Warna Anda berada di atas ---
val PrimaryGreen = Color(0xFF1B5E20)
// ... warna lainnya

// --- Deklarasi TEMA GLOBAL ---
val LocalThemeState = compositionLocalOf<MutableState<Boolean>> { error("Theme State Not Provided") }


// --- DEFINISI SKEMA WARNA KHUSUS ---
// Anda harus memastikan sudah mengimpor androidx.compose.material3.darkColorScheme/lightColorScheme
private val AppDarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    secondary = LightGreen,
    tertiary = AccentGold,
    background = Color(0xFF121212), // Warna latar belakang gelap
    surface = DarkGreen // Warna permukaan gelap
    // Tambahkan warna lain jika diperlukan
)

private val AppLightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = LightGreen,
    tertiary = AccentGold,
    background = Color(0xFFF0F4F8), // Warna latar belakang terang
    surface = Color.White // Warna permukaan terang
    // Tambahkan warna lain jika diperlukan
)

// --- DUKUNGAN MULTI-BAHASA (I18N) ---
enum class AppLanguage { ID, EN }
val LocalLanguageState = compositionLocalOf<MutableState<AppLanguage>> { error("Language State Not Provided") }

data class Strings(
    val appName: String,
    val greeting: String,
    val locationLabel: String,
    val nextPrayer: String,
    val nextPrayerTime: String,
    val openText: String,
    val mainMenu: String,
    val quran: String,
    val doa: String,
    val sholatSchedule: String,
    val asmaulHusna: String,
    val searchSurahHint: String,
    val searchDoaHint: String,
    val searchLocationPlaceholder: String,
    val findText: String,
    val cancelText: String,
    val translation: String,
    val latinText: String,
    val notAvailable: String,
)

object AppStrings {
    fun get(lang: AppLanguage): Strings {
        return when (lang) {
            AppLanguage.ID -> Strings(
                appName = "HijrahApp",
                greeting = "Assalamu'alaikum, Hamba Allah",
                locationLabel = "Lokasi",
                nextPrayer = "Waktu Sholat Berikutnya",
                nextPrayerTime = "Berikutnya",
                openText = "Buka",
                mainMenu = "Menu Utama",
                quran = "Al-Qur'an",
                doa = "Kumpulan Doa",
                sholatSchedule = "Jadwal Sholat",
                asmaulHusna = "Asmaul Husna",
                searchSurahHint = "Cari Surah (Nama/Nomor)",
                searchDoaHint = "Cari Judul Doa",
                searchLocationPlaceholder = "Masukkan Nama Kota/Tempat",
                findText = "Cari",
                cancelText = "Batal",
                translation = "Terjemahan",
                latinText = "Teks Latin",
                notAvailable = "Tidak tersedia",
            )
            AppLanguage.EN -> Strings(
                appName = "HijrahApp",
                greeting = "Assalamu'alaikum, Servant of Allah",
                locationLabel = "Location",
                nextPrayer = "Next Prayer Time",
                nextPrayerTime = "Next",
                openText = "Open",
                mainMenu = "Main Menu",
                quran = "Al-Qur'an",
                doa = "Prayers Collection",
                sholatSchedule = "Prayer Schedule",
                asmaulHusna = "Asmaul Husna",
                searchSurahHint = "Search Surah (Name/Number)",
                searchDoaHint = "Search Doa Title",
                searchLocationPlaceholder = "Enter City/Place Name",
                findText = "Find",
                cancelText = "Cancel",
                translation = "Translation",
                latinText = "Latin Text",
                notAvailable = "Not available",
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme = rememberSaveable { mutableStateOf(false) }
            val currentLanguage = rememberSaveable { mutableStateOf(AppLanguage.ID) }

            CompositionLocalProvider(
                LocalThemeState provides isDarkTheme,
                LocalLanguageState provides currentLanguage
            ) {
                MaterialTheme(
                    colorScheme = when {
                        isDarkTheme.value -> AppDarkColorScheme
                        else -> AppLightColorScheme
                    }
                ) {
                    val navController = rememberNavController()
                    val viewModel: HijrahViewModel = viewModel()

                    LocationTracker(viewModel = viewModel)

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { HomeScreen(navController, viewModel) }
                        composable("quran") { ListSurahScreen(navController, viewModel) }
                        composable("doa") { ListDoaScreen(navController, viewModel) }
                        composable("sholat") { JadwalSholatScreen(navController, viewModel) }
                        composable("detail_surah/{nomor}") {
                            val no = it.arguments?.getString("nomor")?.toIntOrNull() ?: 1
                            LaunchedEffect(no) { viewModel.getDetailSurah(no) }
                            DetailSurahScreen(navController, viewModel)
                        }
                        composable("asmaulhusna") { AsmaulHusnaScreen(navController, viewModel) }
                    }
                }
            }
        }
    }
}


// --- UTILITIES ---

@Composable
fun rememberCurrentTime(): State<Date> {
    val currentTime = remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = Date()
            delay(TimeUnit.MINUTES.toMillis(1))
        }
    }
    return currentTime
}


fun getNextPrayerInfo(timings: Timings?, currentTime: Date): Pair<String, String> {
    if (timings == null) return "Loading..." to "--:--"

    val prayerTimes = mapOf(
        "Subuh" to timings.subuh, "Dzuhur" to timings.dzuhur, "Ashar" to timings.ashar,
        "Maghrib" to timings.maghrib, "Isya" to timings.isya
    )

    val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(currentTime)

    for ((name, time) in prayerTimes) {
        if (time != null) {
            try {
                val prayerDateTime = SimpleDateFormat("yyyyMMdd HH:mm", Locale.getDefault()).parse("$today $time")
                if (prayerDateTime != null && prayerDateTime.after(currentTime)) {
                    return name to time
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    return "Subuh Besok" to (timings.subuh ?: "--:--" )
}

@Composable
fun LocationTracker(viewModel: HijrahViewModel) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            fetchLocation(fusedLocationClient, context, viewModel)
        } else {
            viewModel.setSholatByManualCity("Jakarta") // Fallback
        }
    }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                fetchLocation(fusedLocationClient, context, viewModel)
            }
            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
}


private fun fetchLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    viewModel: HijrahViewModel
) {
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                viewModel.updateLocation(location.latitude, location.longitude)
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}

    @Composable
    fun ThemeSwitchButton(isDarkTint: Boolean = true) {
        val themeState = LocalThemeState.current
        val isDarkTheme = themeState.value

        // Tentukan warna ikon:
        val iconTint = if (isDarkTint) {
            // Mode Portrait (Di atas header PrimaryGreen yang gelap): Selalu Putih
            Color.White
        } else {
            // Mode Landscape (Di atas background/surface yang terang/gelap):
            // Gunakan warna kontras PrimaryGreen untuk mode terang, dan AccentGold/White untuk mode gelap
            if (isDarkTheme) Color.White else PrimaryGreen // Menggunakan White/PrimaryGreen untuk kontras
        }

        IconButton(
            onClick = { themeState.value = !isDarkTheme }
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = "Toggle Theme",
                tint = iconTint
            )
        }
    }

    @Composable
    fun ThemeAction() {
        val themeState = LocalThemeState.current
        val isDarkTheme = themeState.value

        // Warna ikon disesuaikan dengan TopAppBar (onPrimary)
        val iconTint = MaterialTheme.colorScheme.onPrimary

        IconButton(onClick = { themeState.value = !isDarkTheme }) {
            Icon(
                // PERBAIKAN LOGIKA:
                // Jika TEMA GELAP aktif (isDarkTheme = true), tampilkan ikon TERANG (LightMode)
                // Jika TEMA TERANG aktif (isDarkTheme = false), tampilkan ikon GELAP (DarkMode)
                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = "Toggle Theme",
                tint = iconTint
            )
        }
    }


// --- SCREEN 1: HOME (FIXED UI & LAYOUT) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HijrahViewModel) {
    val jadwal by viewModel.jadwalSholat.collectAsState()
    val currentTime by rememberCurrentTime()
    val cityDisplay by viewModel.currentCityDisplay.collectAsState()

    val (nextPrayerName, nextPrayerTime) = remember(jadwal, currentTime) { getNextPrayerInfo(jadwal, currentTime) }
    val clockDisplay = SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentTime)

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val menuItems = listOf(
        MenuItem("Al-Qur'an", AccentGold, Icons.Default.Book, "quran"),
        MenuItem("Doa-Doa", Color(0xFF4CAF50), Icons.Default.VolunteerActivism, "doa"),
        MenuItem("Jadwal Sholat", Color(0xFF2196F3), Icons.Default.Schedule, "sholat"),
        MenuItem("Asmaul Husna", Color(0xFFE53935), Icons.Default.Favorite, "asmaulhusna") // ITEM BARU
    )

    Scaffold(
        containerColor = Color(0xFFF0F4F8)
    ) { paddingValues ->
        if (isLandscape) {
            // --- LANDSCAPE LAYOUT (TIDAK BERUBAH) ---
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Kiri: Header Info (Waktu, Lokasi, Doa Berikutnya)
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().padding(end = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Row yang berisi Nama Aplikasi dan Waktu
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Kiri: Nama Aplikasi (Ganti DarkGreen/Color.White)
                        Text("HijrahApp",
                            color = LightGreen, // <-- Menggunakan variabel yang benar
                            fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        // Kanan: Tombol Tema
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeSwitchButton(isDarkTint = true) // Tombol di Landscape
                            Text(clockDisplay, color = DarkGreen, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Assalamu'alaikum, Hamba Allah",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )
                    Text("Lokasi: $cityDisplay", color = Color.Gray, fontSize = 14.sp)


                    Spacer(modifier = Modifier.height(16.dp))


                    // Kartu Sholat
                    Card(
                        modifier = Modifier.fillMaxWidth().height(100.dp).clickable { navController.navigate("sholat") },
                        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = LightGreen),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxSize().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, null, tint = Color.White, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(20.dp))
                            Column {
                                Text("$nextPrayerName Berikutnya", color = Color.White.copy(0.8f), fontSize = 16.sp)
                                Text(text = nextPrayerTime, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                            }
                        }
                    }
                }


                // Kanan: Menu Utama Grid
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().padding(start = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Menu Utama",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.heightIn(max = 400.dp)
                    ) {
                        items(menuItems) { item ->
                            LargeMenuButton(item, navController)
                        }
                    }
                }
            }
        } else {
            // --- PORTRAIT LAYOUT (PERBAIKAN TAMPILAN) ---
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF0F4F8))
            ) {
                // Header Background & Info (ITEM 1)
                item {
                    // Header Utama (Background)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp) // Ketinggian tetap 240dp
                            .background(PrimaryGreen)
                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(35.dp),
                            // Hapus 'verticalArrangement = Arrangement.SpaceBetween'
                            verticalArrangement = Arrangement.Top // Atur mulai dari atas
                        ) {
                            // Baris 1: App Name & Clock (DI ATAS)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                // Ganti Arrangement.SpaceBetween dengan Alignment.CenterVertically jika belum ada
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically // Penting
                            ) {
                                // Kiri: Nama Aplikasi
                                Text("HijrahApp", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)

                                // Kanan: Tombol Tema & Jam (DIBUNGKUS DALAM ROW AGAR BERSEBELAHAN)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Tambahkan sedikit jarak horizontal
                                ) {
                                    // Panggil tombol tema di sini
                                    ThemeSwitchButton() // <-- SEHARUSNYA MUNCUL DI SINI

                                    // Jam
                                    Text(clockDisplay, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                                }
                            }

                            // PENTING: Spacer untuk mendorong konten salam ke bawah
                            Spacer(modifier = Modifier.weight(5f))

                            // Baris 2: Salam & Lokasi (DIDORONG KE BAWAH, TAPI TIDAK BERTABRAKAN)
                            Column(modifier = Modifier.padding(bottom = 30.dp)) {
                                Text("Assalamu'alaikum,", color = Color.White.copy(0.9f), fontSize = 16.sp)
                                Text("Hamba Allah", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
                                Text("Lokasi: $cityDisplay", color = Color.White.copy(0.7f), fontSize = 14.sp)
                            }
                        }
                    }
                    // Kartu Sholat (Overlay) - Offset tetap: -55.dp
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .offset(y = (-55).dp)
                            .height(100.dp)
                            .clickable { navController.navigate("sholat") },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = LightGreen),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxSize().padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Icon(Icons.Default.AccessTime, null, tint = Color.White, modifier = Modifier.size(36.dp))
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Waktu Sholat Berikutnya:", color = Color.White.copy(0.8f), fontSize = 14.sp)
                                if (nextPrayerName == "Loading...") {
                                    CircularProgressIndicator(Modifier.size(24.dp), color = Color.White)
                                } else {
                                    Text(text = "$nextPrayerName • $nextPrayerTime", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                                }
                            }
                            Icon(Icons.Default.ArrowForwardIos, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }


                // Menu Utama (ITEM 2 - Layout FIX)
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .offset(y = (-50).dp) // Offset disesuaikan (FIX CRASH/UI)
                            .padding(bottom = 24.dp)
                    ) {
                        Text(
                            "Menu Utama",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // FIX CRASH: Menggunakan struktur Column dan Row (Bukan LazyGrid)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(Modifier.weight(1f)) { LargeMenuButton(menuItems[0], navController) }
                                Box(Modifier.weight(1f)) { LargeMenuButton(menuItems[1], navController) }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(Modifier.weight(1f)) { LargeMenuButton(menuItems[2], navController) }
                                Box(Modifier.weight(1f)) { LargeMenuButton(menuItems[3], navController) }
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- SCREEN 4: LIST SURAH (DENGAN SEARCH) ---
// (Tidak ada perubahan signifikan di sini, kecuali pembersihan sintaks)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSurahScreen(navController: NavController, viewModel: HijrahViewModel) {
    val list by viewModel.surahList.collectAsState()
    var searchQuery by remember { mutableStateOf("") }


    val filteredList = remember(list, searchQuery) {
        if (searchQuery.isBlank()) {
            list
        } else {
            list.filter {
                it.namaLatin.orEmpty().contains(searchQuery, ignoreCase = true) ||
                        it.nomor.toString().contains(searchQuery)
            }
        }
    }


    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Al-Qur'an", color = Color.White) }, navigationIcon = { IconButton(onClick={navController.popBackStack()}){Icon(Icons.Default.ArrowBack,"", tint=Color.White)} }, colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen),
            actions = {
                ThemeAction() // <-- TAMBAHKAN DI SINI!
            }
        )
    }) { p ->
        Column(Modifier.padding(p)) {
            // Kolom Pencarian
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari Surah (Nama/Nomor)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f), focusedLabelColor = PrimaryGreen)
            )


            Divider()


            LazyColumn(Modifier.weight(1f)) {
                items(filteredList) { surah ->
                    ListItem(
                        headlineContent = { Text(surah.namaLatin ?: "") },
                        supportingContent = { Text("${surah.arti} • ${surah.jumlahAyat} Ayat") },
                        leadingContent = { Box(Modifier.size(36.dp).background(PrimaryGreen, CircleShape), contentAlignment = Alignment.Center) { Text("${surah.nomor}", color = Color.White, fontSize = 12.sp) } },
                        trailingContent = { Text(surah.nama ?: "", fontSize = 18.sp, color = PrimaryGreen) },
                        modifier = Modifier.clickable { navController.navigate("detail_surah/${surah.nomor}") }
                    )
                    Divider()
                }
            }
        }
    }
}


// --- SCREEN 3: DAFTAR DOA (DENGAN SEARCH) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDoaScreen(navController: NavController, viewModel: HijrahViewModel) {
    val list by viewModel.doaList.collectAsState()
    var searchQuery by remember { mutableStateOf("") }


    val filteredList = remember(list, searchQuery) {
        if (searchQuery.isBlank()) {
            list
        } else {
            list.filter {
                it.judul.orEmpty().contains(searchQuery, ignoreCase = true)
            }
        }
    }


    Scaffold(topBar = { TopAppBar(title = { Text("Kumpulan Doa", color = Color.White) }, navigationIcon = { IconButton(onClick={navController.popBackStack()}){Icon(Icons.Default.ArrowBack,"", tint=Color.White)} }, colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen),
        actions = {
            ThemeAction() // <-- TAMBAHKAN DI SINI!
        }
    )
    }) { p ->
        Column(Modifier.padding(p)) {
            // Kolom Pencarian
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari Judul Doa") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f), focusedLabelColor = PrimaryGreen)
            )


            LazyColumn(Modifier.weight(1f).padding(horizontal = 16.dp)) {
                if (filteredList.isEmpty()) {
                    item { Text("Tidak ada doa yang ditemukan.", Modifier.fillMaxWidth().padding(top = 16.dp), textAlign = TextAlign.Center) }
                }
                items(filteredList) { doa ->
                    DoaListItem(doa)
                }
            }
        }
    }
}


// --- SCREEN 5: JADWAL SHOLAT (Dinamis Lokasi dan Search) ---
// (Tidak ada perubahan signifikan)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalSholatScreen(navController: NavController, viewModel: HijrahViewModel) {
    val jadwal by viewModel.jadwalSholat.collectAsState()
    val cityDisplay by viewModel.currentCityDisplay.collectAsState()
    val currentTime by rememberCurrentTime() // Dapatkan waktu saat ini

    // Gunakan fungsi utilitas yang sudah ada untuk mendapatkan waktu sholat berikutnya
    val (nextPrayerName, nextPrayerTime) = remember(jadwal, currentTime) {
        getNextPrayerInfo(jadwal, currentTime)
    }

    var showDialog by remember { mutableStateOf(false) }
    var cityInput by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cari Lokasi Lain") },
            text = {
                OutlinedTextField(
                    value = cityInput,
                    onValueChange = { cityInput = it },
                    label = { Text("Masukkan Nama Kota/Tempat") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen, unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f), focusedLabelColor = PrimaryGreen)
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (cityInput.isNotBlank()) {
                        viewModel.setSholatByManualCity(cityInput)
                        showDialog = false
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)) { Text("Cari") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Batal") } }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jadwal Sholat", color = Color.White) },
                navigationIcon = { IconButton(onClick={navController.popBackStack()}){Icon(Icons.Default.ArrowBack,"", tint=Color.White)} },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen),
                actions = {
                    ThemeAction() // Tombol Tema
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Cari Lokasi", tint = Color.White)
                    }
                }
            )
        }
    ) { p ->
        // GANTI COLUMN INDUK MENJADI LAZYCOLUMN
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(p) // Padding dari Scaffold (AppBar)
                .padding(horizontal = 10.dp) // Padding horizontal
        ) {
            // --- PERBAIKAN JARAK HEADER ---
            // Tambahkan Spacer vertikal di awal LazyColumn
            item { Spacer(modifier = Modifier.height(20.dp)) } // Tambahkan jarak 20dp
            // -----------------------------
            // ITEM 1: Kartu Jadwal Utama (Waktu Sholat Berikutnya)
            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = PrimaryGreen)) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(cityDisplay, color = Color.White, fontSize = 18.sp)
                        Text(SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date()), color = Color.White)
                        Spacer(Modifier.height(20.dp))

                        // --- PERBAIKAN DI SINI ---
                        Text(
                            nextPrayerTime, // Tampilkan waktu sholat berikutnya
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "$nextPrayerName Berikutnya", // Tampilkan nama sholat berikutnya
                            color = Color.White
                        )
                        // --- AKHIR PERBAIKAN ---
                    }
                }
            }

            item { Spacer(Modifier.height(30.dp)) }

            // ITEM 2: Daftar Waktu Sholat
            jadwal?.let { timings ->
                // Menggunakan item-item individual di dalam LazyColumn
                item { SholatItem("Subuh", timings.subuh) }
                item { SholatItem("Dzuhur", timings.dzuhur) }
                item { SholatItem("Ashar", timings.ashar) }
                item { SholatItem("Maghrib", timings.maghrib) }
                item { SholatItem("Isya", timings.isya) }
            }
        }
    }
}




// --- SCREEN ASMAUL HUSNA (MENGGANTI HADITS) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsmaulHusnaScreen(navController: NavController, viewModel: HijrahViewModel) {
    val list by viewModel.asmaulHusnaList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Asmaul Husna", color = Color.White) },
            navigationIcon = { IconButton(onClick={navController.popBackStack()}){Icon(Icons.Default.ArrowBack,"", tint=Color.White)} },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen),
            actions = {
                ThemeAction() // <-- TAMBAHKAN DI SINI!
            }
        )
    }) { p ->
        if (isLoading && list.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(p)) {
                items(list) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        // GUNAKAN MaterialTheme.colorScheme.surface (sebelumnya SoftGreen)
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("#${item.number}", color = PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(Modifier.height(8.dp))

                            // TEKS ARAB
                            Text(
                                item.arab,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onSurface // Menggunakan warna kontras
                            )
                            Spacer(Modifier.height(8.dp))

                            // TEKS LATIN
                            Text(item.latin, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                            Divider(Modifier.padding(vertical = 8.dp))

                            // TERJEMAHAN
                            Text("Artinya: ${item.terjemahan}", fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSurahScreen(navController: NavController, viewModel: HijrahViewModel) {
    val detail by viewModel.detailSurah.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()


    Scaffold(topBar = { TopAppBar(title = { Text(detail?.namaLatin ?: "Memuat...", color = Color.White) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)) }) { p ->
        if (isLoading || detail == null) { Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryGreen) } }
        else { LazyColumn(Modifier.fillMaxSize().padding(p).padding(horizontal = 12.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = SoftGreen), modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), shape = RoundedCornerShape(12.dp)) {
                    Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = PrimaryGreen, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        Text(text = "Surah ${detail?.namaLatin ?: "..."} (${detail?.ayat?.size ?: 0} Ayat)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                    }
                }
            }
            items(detail?.ayat ?: emptyList()) { ayat -> AyatReadItem(ayat) }
            item { Spacer(Modifier.height(40.dp)) }
        }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotivasiScreen(navController: NavController) {
    val quotes = listOf( "Jangan pernah lelah untuk memperbaiki diri, karena Allah tidak pernah lelah menerima taubat hamba-Nya.", "Kesabaran bukan hanya kemampuan untuk menunggu, tetapi kemampuan untuk menjaga sikap baik saat menunggu." )


    Scaffold(topBar = { TopAppBar(title = { Text("Motivasi Islami", color = Color.White) }, navigationIcon = { IconButton(onClick={navController.popBackStack()}){Icon(Icons.Default.ArrowBack,"", tint=Color.White)} }, colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)) }) { p ->
        LazyColumn(Modifier.padding(p).padding(16.dp)) {
            items(quotes) { quote ->
                Card(Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = SoftGreen), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Icon(Icons.Default.FormatQuote, null, tint = PrimaryGreen)
                        Spacer(Modifier.height(8.dp))
                        Text(quote, fontSize = 16.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = Color.Black.copy(0.8f))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendidikanScreen(navController: NavController) {
    val materi = listOf( "Rukun Islam", "Rukun Iman", "Tata Cara Wudhu", "Mengenal Asmaul Husna" )


    Scaffold(topBar = { TopAppBar(title = { Text("Pendidikan Islami", color = Color.White) }, navigationIcon = { IconButton(onClick={navController.popBackStack()}){Icon(Icons.Default.ArrowBack,"", tint=Color.White)} }, colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)) }) { p ->
        LazyColumn(Modifier.padding(p).padding(horizontal = 16.dp)) {
            items(materi) { item ->
                ListItem(
                    headlineContent = { Text(item, fontWeight = FontWeight.Medium) },
                    leadingContent = { Icon(Icons.Default.School, contentDescription = null, tint = PrimaryGreen) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
                )
                Divider()
            }
        }
    }
}


// --- KOMPONEN UTILITY ---
data class MenuItem(val title: String, val color: Color, val icon: ImageVector, val route: String)


@Composable
fun LargeMenuButton(item: MenuItem, navController: NavController) {
    Card(
        shape = RoundedCornerShape(16.dp),
        // Ganti warna latar belakang kartu agar merespons tema!
        // Gunakan surface (warna permukaan) atau warna background yang di-tint.
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // <-- Ganti di sini
        ),
        modifier = Modifier.fillMaxWidth().height(180.dp).clickable { navController.navigate(item.route) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.size(48.dp).background(item.color, CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = item.icon, contentDescription = item.title, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Column {
                Text(item.title, color = item.color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Hijrah", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}


@Composable fun SholatItem(name: String, time: String?) {
    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(name, fontWeight = FontWeight.Bold);
        Text(time ?: "-", color = PrimaryGreen, fontWeight = FontWeight.Bold)
    };
    Divider()
}


@Composable
fun AyatReadItem(ayat: Ayat) {
    // Menggunakan warna surface (DarkGreen saat gelap, Putih saat terang)
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)) {

        Column(modifier = Modifier.padding(20.dp)) {
            // 1. NOMOR AYAT (Rata Kanan)
            Box(modifier = Modifier.size(30.dp).background(PrimaryGreen, CircleShape).align(Alignment.End), contentAlignment = Alignment.Center) {
                Text("${ayat.nomorAyat}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            // 2. TEKS ARAB (Rata Kanan, Kontras Tinggi)
            Text(
                text = ayat.teksArab ?: "Teks Arab tidak tersedia.",
                fontSize = 28.sp, // Ukuran font dinaikkan
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )

            // 3. TERJEMAHAN (Jelas)
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))

            // Label Terjemahan
            Text(
                text = "Terjemahan:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            // Isi terjemahan
            Text(
                text = ayat.teksIndonesia ?: "Terjemahan tidak tersedia.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )

            // 4. Latin
            Spacer(Modifier.height(8.dp))
            Text(
                text = ayat.teksLatin ?: "Transliterasi tidak tersedia.",
                fontSize = 13.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color.Gray.copy(alpha = 0.8f)
            )
        }
    }
}


@Composable
fun DoaListItem(doa: Doa) {
    // Menggunakan warna surface (DarkGreen saat gelap, Putih saat terang)
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(12.dp)) {

        Column(modifier = Modifier.padding(20.dp)) {
            // 1. Judul Doa (Menggunakan onSurface untuk kontras, jika ada)
            Text(
                text = doa.judul ?: "Doa Tidak Diketahui",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary // Warna Primary tetap hijau
            )
            Spacer(Modifier.height(16.dp))

            // 2. TEKS ARAB (Rata Kanan, Kontras Putih/Terang)
            Text(
                text = doa.arab ?: "Teks Arab tidak tersedia.",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 40.sp,
                color = MaterialTheme.colorScheme.onSurface // Kontras Tinggi (Putih saat gelap)
            )

            // 3. TERJEMAHAN (Jelas)
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))

            // Label "Terjemahan" menggunakan warna kontras sekunder yang lebih lembut
            Text(
                text = "Terjemahan:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary // Hijau lebih lembut
            )
            // Isi terjemahan: Menggunakan warna onSurface (Putih/Terang)
            Text(
                text = doa.terjemah ?: "Terjemahan tidak tersedia.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )

            Spacer(Modifier.height(8.dp))

            // 4. Latin (Sebagai info tambahan, warna abu-abu/sekunder)
            Text(
                text = doa.latin ?: "Transliterasi tidak tersedia.",
                fontSize = 13.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color.Gray.copy(alpha = 0.8f) // Abu-abu tetap terlihat di dark mode
            )
        }
    }
}


@Composable
fun MenuButton(item: MenuItem, navController: NavController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp).clickable { navController.navigate(item.route) }) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = item.color), modifier = Modifier.size(70.dp), elevation = CardDefaults.cardElevation(2.dp)) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(imageVector = item.icon, contentDescription = item.title, tint = Color.White, modifier = Modifier.size(32.dp)) }
        }
        Spacer(Modifier.height(8.dp))
        Text(text = item.title, fontSize = 12.sp, textAlign = TextAlign.Center, color = Color.Black.copy(0.7f))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(navController: NavController, title: String) {
    Scaffold(topBar = { TopAppBar(title = { Text(title, color = Color.White) }, navigationIcon = { IconButton(onClick={navController.popBackStack()}){Icon(Icons.Default.ArrowBack,"", tint=Color.White)} }, colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryGreen)) }) { p ->
        Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.Construction, "", modifier = Modifier.size(64.dp), tint = Color.Gray); Spacer(Modifier.height(16.dp)); Text("Fitur sedang dikembangkan", color = Color.Gray) } }
    }
}

