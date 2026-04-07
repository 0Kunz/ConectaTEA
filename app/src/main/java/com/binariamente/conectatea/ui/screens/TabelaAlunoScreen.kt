package com.binariamente.conectatea.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.binariamente.conectatea.viewmodel.ConectaTEAViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabelaAlunoScreen(
    viewModel: ConectaTEAViewModel,
    onNavigateToBuscar: () -> Unit,
    onBack: () -> Unit
) {
    val aluno = viewModel.alunoSelecionado
    // Lista de pictogramas já salvos para este aluno
    val pictogramas by viewModel.pictogramasDoAluno.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prancheta: ${aluno?.nome ?: ""}", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToBuscar) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Pictograma")
            }
        }
    ) { paddingValues ->
        if (pictogramas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Nenhum pictograma na tabela ainda. Clique no + para buscar.")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 colunas para ficar grande e visual para a criança
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(pictogramas) { pictograma ->
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.aspectRatio(1f).clickable {
                            /* Futuro: Ao clicar, o app poderia "falar" a palavra usando TextToSpeech! */
                        }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize().padding(8.dp)
                        ) {
                            AsyncImage(
                                model = pictograma.imagem,
                                contentDescription = pictograma.nome,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.weight(1f).fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(pictograma.nome, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}