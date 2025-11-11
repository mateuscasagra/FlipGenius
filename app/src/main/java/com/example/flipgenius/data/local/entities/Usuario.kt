data class Usuario (
    val nome: String,
    val senhaHash: String,
    val isAdmin: Boolean = false,
    val dataCriacao: Long = System.currentTimeMillis()
)