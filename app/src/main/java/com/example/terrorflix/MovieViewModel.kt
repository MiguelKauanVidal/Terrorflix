package com.terrorflix.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.terrorflix.data.Movie
import com.terrorflix.database.DBHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = DBHelper(application)

    private val _state = MutableStateFlow(MovieState())
    val state: StateFlow<MovieState> = _state

    init {
        loadMovies()
    }

    // --- Manipulação de Estado da UI ---

    fun onTitleChange(newTitle: String) { _state.update { it.copy(inputTitle = newTitle) } }
    fun onYearChange(newYear: String) { _state.update { it.copy(inputYear = newYear) } }
    fun onTagsChange(newTags: String) { _state.update { it.copy(inputTags = newTags) } }
    fun onPlannedAtChange(newDate: Long) { _state.update { it.copy(inputPlannedAt = newDate) } }
    fun toggleAddForm(show: Boolean? = null) {
        _state.update { it.copy(showAddForm = show ?: !it.showAddForm) }
    }

    fun onFilterTagsChange(newTags: String) {
        _state.update { it.copy(filterTags = newTags) }
        loadMovies()
    }

    fun onFilterWatchedChange(isWatched: Boolean) {
        _state.update { it.copy(filterWatched = isWatched) }
        loadMovies()
    }

    // --- Lógica de Negócios (Database) ---

    fun loadMovies() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val tagsFilter = _state.value.filterTags.trim().lowercase()
            val watchedStatus = if (_state.value.filterWatched) 1 else null

            val movies = dbHelper.listMovies(
                tagsFilter = tagsFilter.ifEmpty { null },
                watchedStatus = watchedStatus
            )

            _state.update { it.copy(movies = movies) }
        }
    }

    fun addMovie() = viewModelScope.launch {
        val currentState = _state.value
        val title = currentState.inputTitle.trim()
        val yearText = currentState.inputYear.trim()
        val tags = currentState.inputTags.trim().lowercase()

        if (title.isEmpty() || yearText.isEmpty()) {
            _state.update { it.copy(userMessage = "Título e Ano são obrigatórios.") }
            return@launch
        }

        val year = yearText.toIntOrNull()
        if (year == null || year > Calendar.getInstance().get(Calendar.YEAR) || year < 1895) {
            _state.update { it.copy(userMessage = "Ano de lançamento inválido.") }
            return@launch
        }

        val newMovie = Movie(
            title = title,
            year = year,
            tags = tags.ifEmpty { "indefinido" },
            plannedAt = currentState.inputPlannedAt,
            isWatched = false,
            rating = null
        )

        withContext(Dispatchers.IO) {
            if (dbHelper.insertMovie(newMovie)) {
                _state.update {
                    it.copy(
                        inputTitle = "", inputYear = "", inputTags = "",
                        inputPlannedAt = Calendar.getInstance().timeInMillis,
                        userMessage = "$title adicionado com sucesso!",
                        showAddForm = false
                    )
                }
                loadMovies()
            } else {
                _state.update { it.copy(userMessage = "Erro ao adicionar filme.") }
            }
        }
    }

    fun saveRating(movieId: Int, rating: Int) = viewModelScope.launch {
        if (rating !in 0..10) return@launch

        withContext(Dispatchers.IO) {
            if (dbHelper.updateRating(movieId, rating)) {
                loadMovies()
                _state.update { it.copy(userMessage = "Nota $rating salva!", showRatingDialog = false, currentMovieToRate = null) }
            } else {
                _state.update { it.copy(userMessage = "Erro ao salvar nota.") }
            }
        }
    }

    fun showRatingDialog(movie: Movie) {
        _state.update { it.copy(showRatingDialog = true, currentMovieToRate = movie) }
    }

    fun dismissRatingDialog() {
        _state.update { it.copy(showRatingDialog = false, currentMovieToRate = null) }
    }

    fun clearMessage() {
        _state.update { it.copy(userMessage = null) }
    }
}