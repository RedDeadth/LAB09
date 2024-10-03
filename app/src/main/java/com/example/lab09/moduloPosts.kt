package com.example.lab09


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ScreenProducts(navController: NavHostController, servicio: ProductApiService) {
    var listaProductos: List<ProductModel> by remember { mutableStateOf(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            Log.d("ScreenProducts", "Iniciando llamada a la API")
            val response = servicio.getProducts()
            Log.d("ScreenProducts", "Respuesta recibida: ${response.products.size} productos")
            listaProductos = response.products
            isLoading = false
        } catch (e: Exception) {
            Log.e("ScreenProducts", "Error al obtener productos", e)
            error = e.message
            isLoading = false
        }
    }

    when {
        isLoading -> Text("Cargando...")
        error != null -> Text("Error: $error")
        listaProductos.isEmpty() -> Text("No se encontraron productos")
        else -> {
            Log.d("ScreenProducts", "Mostrando ${listaProductos.size} productos")
            LazyColumn {
                items(listaProductos) { item ->
                    Row(modifier = Modifier.padding(8.dp)) {
                        Text(text = item.id.toString(), Modifier.weight(0.05f), textAlign = TextAlign.End)
                        Spacer(Modifier.padding(horizontal = 1.dp))
                        Text(text = item.title, Modifier.weight(0.7f))
                        IconButton(
                            onClick = {
                                navController.navigate("productosVer/${item.id}")
                                Log.d("ScreenProducts", "Navegando a producto ${item.id}")
                            },
                            Modifier.weight(0.1f)
                        ) {
                            Icon(imageVector = Icons.Outlined.Search, contentDescription = "Ver")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenProduct(navController: NavHostController, servicio: ProductApiService, id: Int) {
    var product by remember { mutableStateOf<ProductModel?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(id) {
        try {
            Log.d("ScreenProduct", "Cargando producto con ID: $id")
            product = servicio.getProductById(id)
            isLoading = false
            Log.d("ScreenProduct", "Producto cargado exitosamente: ${product?.title}")
        } catch (e: Exception) {
            Log.e("ScreenProduct", "Error al cargar el producto", e)
            error = e.message ?: "Error desconocido al cargar el producto"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
                Text("Cargando detalles del producto...")
            }
            error != null -> {
                Text("Error: $error", color = Color.Red)
            }
            product != null -> {
                Text(
                    text = product!!.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("Precio: $${product!!.price}")
                Text("Descuento: ${product!!.discountPercentage}%")
                Text("Marca: ${product!!.brand}")
                Text("Categoría: ${product!!.category}")
                Text("Stock: ${product!!.stock}")
                Text("Calificación: ${product!!.rating}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product!!.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            else -> {
                Text("No se pudo cargar el producto", color = Color.Red)
            }
        }
    }
}
