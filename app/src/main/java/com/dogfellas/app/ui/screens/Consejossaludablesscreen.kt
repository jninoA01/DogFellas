package com.dogfellas.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ── NewsAPI DTOs ───────────────────────────────────────────────────────────
data class NewsResponse(val articles: List<ArticuloDto> = emptyList())
data class ArticuloDto(
    val title: String?,
    val description: String?,
    @SerializedName("urlToImage") val urlToImage: String?,
    val url: String?,
    @SerializedName("publishedAt") val publishedAt: String?,
    val source: SourceDto?
)
data class SourceDto(val name: String?)

// ── NewsAPI Retrofit service ───────────────────────────────────────────────
interface NewsApiService {
    @GET("v2/everything")
    suspend fun getNoticias(
        @Query("q")        query: String,
        @Query("language") language: String = "es",
        @Query("pageSize") pageSize: Int = 20,
        @Query("sortBy")   sortBy: String = "publishedAt",
        @Query("apiKey")   apiKey: String
    ): NewsResponse
}

object NewsRetrofit {
    val api: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }
}

// ── ViewModel ─────────────────────────────────────────────────────────────
class ConsejosViewModel : ViewModel() {

    // ⚠️ Reemplaza con tu API key de https://newsapi.org
    private val API_KEY = "TU_API_KEY_AQUI"

    var articulos = mutableStateListOf<ArticuloDto>()
    var cargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    val categorias = listOf("Todos", "Perros", "Gatos", "Salud", "Nutrición", "Adopción")
    var categoriaActiva by mutableStateOf("Todos")

    private val queryPorCategoria = mapOf(
        "Todos"     to "mascotas OR animales OR veterinaria",
        "Perros"    to "perros salud cuidado",
        "Gatos"     to "gatos salud cuidado",
        "Salud"     to "salud animal veterinaria",
        "Nutrición" to "alimentacion mascotas nutricion",
        "Adopción"  to "adopcion mascotas animales"
    )

    fun cargarNoticias() {
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                val query = queryPorCategoria[categoriaActiva] ?: "mascotas"
                val response = NewsRetrofit.api.getNoticias(query = query, apiKey = API_KEY)
                articulos.clear()
                articulos.addAll(
                    response.articles.filter {
                        !it.title.isNullOrBlank() && it.title != "[Removed]"
                    }
                )
            } catch (e: Exception) {
                error = "No se pudieron cargar las noticias.\n${e.message}"
            }
            cargando = false
        }
    }
}

// ── Pantalla ───────────────────────────────────────────────────────────────
@Composable
fun ConsejosSaludablesScreen(
    navController: NavHostController,
    viewModel: ConsejosViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.categoriaActiva) { viewModel.cargarNoticias() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF4A148C), Color(0xFF6A1B9A))))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                )
            ) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color.White) }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Consejos Saludables", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold, color = Color.White)
                Text("Noticias y tips para tu mascota",
                    style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.7f))
            }

            IconButton(
                onClick = { viewModel.cargarNoticias() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                )
            ) { Icon(Icons.Rounded.Refresh, null, tint = Color.White) }
        }

        // Chips categoría
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            items(viewModel.categorias) { cat ->
                val sel = viewModel.categoriaActiva == cat
                FilterChip(
                    selected = sel,
                    onClick = { viewModel.categoriaActiva = cat },
                    label = { Text(cat, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.White,
                        selectedLabelColor = Color(0xFF4A148C),
                        containerColor = Color.White.copy(0.2f), labelColor = Color.White
                    )
                )
            }
        }

        // Contenido
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color(0xFFF9F0FF)
        ) {
            when {
                viewModel.cargando -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF6A1B9A))
                            Spacer(Modifier.height(12.dp))
                            Text("Cargando noticias…", color = Color.Gray)
                        }
                    }
                }
                viewModel.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text("📰", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(viewModel.error ?: "", color = Color.Gray)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.cargarNoticias() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.articulos) { articulo ->
                            TarjetaArticulo(articulo) {
                                articulo.url?.let { url ->
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                }
                            }
                        }
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaArticulo(articulo: ArticuloDto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            if (!articulo.urlToImage.isNullOrBlank()) {
                AsyncImage(
                    model = articulo.urlToImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    articulo.source?.name?.let { fuente ->
                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFEDE7F6)) {
                            Text(fuente,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4A148C), fontWeight = FontWeight.SemiBold)
                        }
                    }
                    articulo.publishedAt?.take(10)?.let { fecha ->
                        Text(fecha, style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    articulo.title ?: "",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF2C1A0E),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                articulo.description?.let { desc ->
                    Spacer(Modifier.height(4.dp))
                    Text(desc, style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Leer más", style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF6A1B9A), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    // 🔥 AQUÍ QUEDÓ APLICADO EL FIX USANDO ICONS.DEFAULT 🔥
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF6A1B9A)
                    )
                }
            }
        }
    }
}