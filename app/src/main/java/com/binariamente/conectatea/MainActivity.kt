package com.binariamente.conectatea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.binariamente.conectatea.database.ConectaTEADatabase
import com.binariamente.conectatea.repository.ConectaTEARepository
import com.binariamente.conectatea.ui.screens.AddAlunoScreen
import com.binariamente.conectatea.ui.screens.HomeScreen
import com.binariamente.conectatea.viewmodel.ConectaTEAViewModel
import com.binariamente.conectatea.viewmodel.ConectaTEAViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializa o Banco de Dados
        val database = ConectaTEADatabase.getDatabase(this)

        // 2. Inicializa o Repositório
        val repository = ConectaTEARepository(database.conectaTEADao())

        setContent {
            // 3. Inicializa o ViewModel usando a Factory
            val viewModel: ConectaTEAViewModel = viewModel(
                factory = ConectaTEAViewModelFactory(repository)
            )

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Sistema simples de navegação (sem bibliotecas externas)
                    var currentScreen by remember { mutableStateOf("HOME") }

                    when (currentScreen) {
                        "HOME" -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToAddAluno = { currentScreen = "ADD_ALUNO" },
                            onNavigateToPictogramas = { currentScreen = "TABELA_ALUNO" } // Agora o clique no aluno leva pra cá
                        )
                        "ADD_ALUNO" -> AddAlunoScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = "HOME" }
                        )
                        "TABELA_ALUNO" -> com.binariamente.conectatea.ui.screens.TabelaAlunoScreen(
                            viewModel = viewModel,
                            onNavigateToBuscar = { currentScreen = "BUSCAR_PICTOGRAMAS" },
                            onBack = { currentScreen = "HOME" }
                        )
                        "BUSCAR_PICTOGRAMAS" -> com.binariamente.conectatea.ui.screens.PictogramScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = "TABELA_ALUNO" }
                        )
                    }
                }
            }
        }
    }
}