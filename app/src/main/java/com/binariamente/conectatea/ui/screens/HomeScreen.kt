package com.binariamente.conectatea.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.binariamente.conectatea.models.Aluno
import com.binariamente.conectatea.viewmodel.ConectaTEAViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ConectaTEAViewModel,
    onNavigateToAddAluno: () -> Unit,
    onNavigateToPictogramas: () -> Unit // <-- O parâmetro que estava faltando!
) {
    // Coleta a lista de alunos do banco de dados em tempo real
    val alunos by viewModel.alunos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ConectaTEA", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {
                    // Botão que chama a navegação para a tela de pictogramas
                    TextButton(onClick = onNavigateToPictogramas) {
                        Text("Pictogramas", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddAluno) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Aluno")
            }
        }
    ) { paddingValues ->
        if (alunos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhum aluno cadastrado ainda.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(alunos) { aluno ->
                    AlunoCard(
                        aluno = aluno,
                        onClick = {
                            viewModel.selecionarAlunoParaTabela(aluno)
                            onNavigateToPictogramas() // Vai usar esse atalho para ir pra Tabela
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AlunoCard(aluno: Aluno, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(text = aluno.nome, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}