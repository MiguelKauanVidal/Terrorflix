package com.terrorflix.presentation

import com.terrorflix.data.Movie
import java.util.Calendar

/**
 * Representa o estado completo da tela de lista de filmes.
 */
data class MovieState(
    // Lista de filmes exibida (após filtros)
    val movies: List<Movie> = emptyList(),

    // CAMPOS DE ENTRADA (NOVO FILME)
    val inputTitle: String = "",
    val inputYear: String = "",
    val inputTags: String = "",
    val inputPlannedAt: Long = Calendar.getInstance().timeInMillis,

    // CAMPOS DE FILTRO
    val filterTags: String = "",
    val filterWatched: Boolean = false,

    // FEEDBACK DO USUÁRIO
    val userMessage: String? = null,

    // Controles de UI
    val showAddForm: Boolean = false,
    val showRatingDialog: Boolean = false,
    val currentMovieToRate: Movie? = null
)