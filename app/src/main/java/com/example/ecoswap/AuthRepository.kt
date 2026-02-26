package com.example.ecoswap

class AuthRepository {

    companion object {
        private val users = mutableListOf<User>()
    }

    fun registerUser(
        name: String,
        nis: String,
        password: String
    ): Result<Unit> {

        if (users.any { it.nis == nis }) {
            return Result.failure(Exception("NIS sudah terdaftar"))
        }

        val hashed = SecurityUtils.hashPassword(password)

        users.add(
            User(
                name = name,
                nis = nis,
                passwordHash = hashed,
                role = "siswa"
            )
        )

        return Result.success(Unit)
    }

    fun loginUser(
        nis: String,
        password: String
    ): Result<String> {

        val hashed = SecurityUtils.hashPassword(password)

        val user = users.find {
            it.nis == nis && it.passwordHash == hashed
        }

        return if (user != null) {
            Result.success(user.role)
        } else {
            Result.failure(Exception("Login gagal"))
        }
    }
}