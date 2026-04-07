package com.binariamente.conectatea.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.binariamente.conectatea.models.Aluno
import com.binariamente.conectatea.models.Pictograma
import com.binariamente.conectatea.repository.ConectaTEARepository
import com.binariamente.conectatea.utils.ArasaacApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ConectaTEAViewModel(private val repository: ConectaTEARepository) : ViewModel() {

    // --- ALUNOS ---
    val alunos: StateFlow<List<Aluno>> = repository.todosAlunos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun salvarAluno(nome: String) {
        if (nome.isNotBlank()) viewModelScope.launch { repository.inserirAluno(Aluno(nome = nome)) }
    }

    // --- ESTADO DA TABELA ATUAL ---
    var alunoSelecionado: Aluno? = null
        private set

    // Lista de pictogramas que o aluno selecionado já possui na sua tabela
    private val _pictogramasDoAluno = MutableStateFlow<List<Pictograma>>(emptyList())
    val pictogramasDoAluno: StateFlow<List<Pictograma>> = _pictogramasDoAluno.asStateFlow()

    fun selecionarAlunoParaTabela(aluno: Aluno) {
        alunoSelecionado = aluno
        // Quando um aluno é selecionado, puxamos a tabela dele no banco (Fake ID 1 temporário para inicializar o flow, a lógica correta está no repositório)
        viewModelScope.launch {
            // Como é um MVP, assumimos que o aluno sempre usa a primeira tabela dele.
            // Para simplificar a consulta em tempo real, usamos um truque de re-coletar sempre que necessário
            carregarTabelaLocal()
        }
    }

    private suspend fun carregarTabelaLocal() {
        alunoSelecionado?.let { aluno ->
            // Escuta as mudanças no banco de dados e atualiza a tela automaticamente
            repository.getPictogramasDaTabela(aluno.id).collect { lista ->
                _pictogramasDoAluno.value = lista
            }
        }
    }

    // --- BUSCA NA API E SALVAMENTO ---
    private val _resultadosBusca = MutableStateFlow<List<Pictograma>>(emptyList())
    val resultadosBusca: StateFlow<List<Pictograma>> = _resultadosBusca.asStateFlow()

    private val _buscando = MutableStateFlow(false)
    val buscando: StateFlow<Boolean> = _buscando.asStateFlow()

    fun buscarPictogramaNaApi(palavra: String) {
        if (palavra.isNotBlank()) {
            viewModelScope.launch {
                _buscando.value = true
                _resultadosBusca.value = ArasaacApi.buscarPictogramas(palavra)
                _buscando.value = false
            }
        }
    }

    fun vincularPictogramaAoAluno(pictograma: Pictograma) {
        alunoSelecionado?.let { aluno ->
            viewModelScope.launch {
                repository.adicionarPictogramaAoAluno(aluno.id, pictograma)
                limparBusca()
            }
        }
    }

    fun limparBusca() {
        _resultadosBusca.value = emptyList()
    }
}

class ConectaTEAViewModelFactory(private val repository: ConectaTEARepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConectaTEAViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConectaTEAViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}