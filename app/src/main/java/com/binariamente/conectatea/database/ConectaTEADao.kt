package com.binariamente.conectatea.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.binariamente.conectatea.models.Aluno
import com.binariamente.conectatea.models.Pictograma
import com.binariamente.conectatea.models.TabelaItem
import com.binariamente.conectatea.models.TabelaPictograma
import kotlinx.coroutines.flow.Flow

@Dao
interface ConectaTEADao {
    // Alunos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAluno(aluno: Aluno): Long

    @Query("SELECT * FROM alunos")
    fun getAllAlunos(): Flow<List<Aluno>>

    // Pictogramas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictograma(pictograma: Pictograma): Long

    @Query("SELECT * FROM pictogramas")
    fun getAllPictogramas(): Flow<List<Pictograma>>

    // Tabelas de Comunicação e Associação
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTabela(tabela: TabelaPictograma): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTabelaItem(tabelaItem: TabelaItem)

    // Puxa apenas os pictogramas que pertencem a uma tabela específica
    @Query("""
        SELECT p.* FROM pictogramas p 
        INNER JOIN tabela_itens ti ON p.id = ti.pictogramaId 
        WHERE ti.tabelaId = :tabelaId
    """)
    fun getPictogramasDaTabela(tabelaId: Int): Flow<List<Pictograma>>

    // Busca a tabela de um aluno específico
    @Query("SELECT * FROM tabelas WHERE alunoId = :alunoId LIMIT 1")
    suspend fun getTabelaByAlunoId(alunoId: Int): TabelaPictograma?
}