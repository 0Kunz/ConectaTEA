package com.binariamente.conectatea.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.binariamente.conectatea.viewmodel.ConectaTEAViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PictogramScreen(
    viewModel: ConectaTEAViewModel,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val resultados by viewModel.resultadosBusca.collectAsState()
    val isLoading by viewModel.buscando.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar Pictogramas", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.limparBusca()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {

            // Barra de Busca
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("O que você procura? (Ex: Comer)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { viewModel.buscarPictogramaNaApi(query) }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Loading ou Grid de Imagens
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (resultados.isEmpty() && query.isNotBlank()) {
                Text("Nenhum resultado ou digite para buscar.")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // 2 Imagens por linha
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(resultados) { pictograma ->
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable {
                                    // A MÁGICA DO CLIQUE!
                                    viewModel.vincularPictogramaAoAluno(pictograma)
                                    onBack() // Volta pra tela da tabela
                                }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize().padding(8.dp)
                            ) {
                                // O Coil carrega a URL aqui!
                                AsyncImage(
                                    model = pictograma.imagem,
                                    contentDescription = pictograma.nome,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.weight(1f).fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(pictograma.nome, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}