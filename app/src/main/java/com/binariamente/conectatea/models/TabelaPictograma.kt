package com.binariamente.conectatea.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tabelas",
    foreignKeys = [
        ForeignKey(
            entity = Aluno::class,
            parentColumns = ["id"],
            childColumns = ["alunoId"],
            onDelete = ForeignKey.CASCADE // Se o aluno for apagado, suas tabelas também somem
        )
    ]
)
data class TabelaPictograma(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val alunoId: Int
)