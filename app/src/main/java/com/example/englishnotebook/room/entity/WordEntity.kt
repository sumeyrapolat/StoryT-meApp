package com.example.englishnotebook.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words_table")
data class WordEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val word: String

)