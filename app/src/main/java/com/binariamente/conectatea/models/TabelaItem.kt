package com.binariamente.conectatea.models

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "tabela_itens",
    primaryKeys = ["tabelaId", "pictogramaId"],
    foreignKeys = [
        ForeignKey(
            entity = TabelaPictograma::class,
            parentColumns = ["id"],
            childColumns = ["tabelaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Pictograma::class,
            parentColumns = ["id"],
            childColumns = ["pictogramaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TabelaItem(
    val tabelaId: Int,
    val pictogramaId: Int
)