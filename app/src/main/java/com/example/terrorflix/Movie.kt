package com.terrorflix.data

/**
 * Classe de dados que representa um filme na lista.
 * Corresponde ao schema da tabela 'movie' no SQLite.
 */
data class Movie(
    val id: Int = 0,
    val title: String,
    val year: Int,
    /** As categorias são armazenadas como uma string separada por vírgulas (ex: "slasher,clássico"). */
    val tags: String,
    /** A data planejada é armazenada como um timestamp Unix (Long) para facilitar a ordenação e armazenamento. */
    val plannedAt: Long,
    /** Representa o status 'watched' na tabela. 0 = não assistido, 1 = assistido. */
    val isWatched: Boolean,
    /** Nota de 0 a 10. Pode ser nula se o filme ainda não foi assistido. */
    val rating: Int? = null
)