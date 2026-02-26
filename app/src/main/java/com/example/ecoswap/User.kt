package com.example.ecoswap

data class User(
    val name: String,
    val nis: String,
    val passwordHash: String,
    val role: String
)