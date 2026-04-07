package com.binariamente.conectatea.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.binariamente.conectatea.viewmodel.ConectaTEAViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlunoScreen(
    viewModel: ConectaTEAViewModel,
    onBack: () -> Unit
) {
    var nomeAluno by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Aluno", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = nomeAluno,
                onValueChange = { nomeAluno = it },
                label = { Text("Nome do Aluno") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.salvarAluno(nomeAluno)
                    onBack() // Volta para a tela inicial após salvar
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nomeAluno.isNotBlank()
            ) {
                Text("Salvar Aluno")
            }
        }
    }
}