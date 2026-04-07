package com.binariamente.conectatea.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pictogramas")
data class Pictograma(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val imagem: String, // URL (Arasaac) ou URI (Galeria/Câmera)
    val tipoImagem: String // "padrao" ou "personalizada"
)