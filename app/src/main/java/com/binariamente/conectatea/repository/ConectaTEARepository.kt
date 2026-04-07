package com.binariamente.conectatea.repository

import com.binariamente.conectatea.database.ConectaTEADao
import com.binariamente.conectatea.models.Aluno
import com.binariamente.conectatea.models.Pictograma
import com.binariamente.conectatea.models.TabelaItem
import com.binariamente.conectatea.models.TabelaPictograma
import kotlinx.coroutines.flow.Flow

class ConectaTEARepository(private val dao: ConectaTEADao) {

    val todosAlunos: Flow<List<Aluno>> = dao.getAllAlunos()

    suspend fun inserirAluno(aluno: Aluno) {
        dao.insertAluno(aluno)
    }

    // Retorna a lista de pictogramas vinculados a uma tabela
    fun getPictogramasDaTabela(tabelaId: Int): Flow<List<Pictograma>> {
        return dao.getPictogramasDaTabela(tabelaId)
    }

    // A mágica acontece aqui: vincula a imagem da internet ao banco local do aluno
    suspend fun adicionarPictogramaAoAluno(alunoId: Int, pictograma: Pictograma) {
        // 1. Pega a tabela do aluno ou cria uma nova se ele não tiver
        var tabela = dao.getTabelaByAlunoId(alunoId)
        if (tabela == null) {
            val novaTabelaId = dao.insertTabela(TabelaPictograma(nome = "Tabela Principal", alunoId = alunoId))
            tabela = TabelaPictograma(id = novaTabelaId.toInt(), nome = "Tabela Principal", alunoId = alunoId)
        }

        // 2. Salva o pictograma no banco local para poder acessar offline depois
        val pictogramaId = dao.insertPictograma(pictograma)

        // 3. Cria a relação entre a tabela do aluno e o pictograma salvo
        dao.insertTabelaItem(TabelaItem(tabelaId = tabela.id, pictogramaId = pictogramaId.toInt()))
    }
}