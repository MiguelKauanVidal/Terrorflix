package com.terrorflix

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
// Importação Material 3 API Experimental
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.AlertDialog // Adicionado, pois é usado em RatingDialog
import androidx.compose.material3.Button // Adicionado, pois é usado em AddMovieSection
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.terrorflix.data.Movie
import com.terrorflix.presentation.MovieState
import com.terrorflix.presentation.MovieViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Tema escuro
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Color(0xFFE50914),
                background = Color(0xFF141414),
                surface = Color(0xFF1F1F1F)
            )) {
                MovieApp()
            }
        }
    }
}

private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieApp(viewModel: MovieViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Efeito colateral para exibir Toast
    LaunchedEffect(state.userMessage) {
        state.userMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TerrorFlix Outubro", color = Color.White) },
                // CORREÇÃO: Usando TopAppBarDefaults.topAppBarColors do Material 3
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1F1F1F)),
                actions = {
                    IconButton(onClick = { viewModel.toggleAddForm() }) {
                        Icon(
                            imageVector = if (state.showAddForm) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = if (state.showAddForm) "Fechar" else "Adicionar Filme",
                            tint = Color(0xFFE50914)
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFF141414)
    ) { paddingValues ->
        Column( // Componente Column usado na linha 34 (referência do erro)
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Formulário de Adição
            AnimatedVisibility(
                visible = state.showAddForm,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                AddMovieSection(state = state, viewModel = viewModel)
            }

            MovieFilterAndList(state = state, viewModel = viewModel)
        }
    }

    // Diálogo de Rating
    if (state.showRatingDialog && state.currentMovieToRate != null) {
        RatingDialog(movie = state.currentMovieToRate!!, viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMovieSection(state: MovieState, viewModel: MovieViewModel) {
    // CORREÇÃO: Definindo um Color object para reuso e legibilidade
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF141414),
        unfocusedContainerColor = Color(0xFF141414),
        focusedBorderColor = Color(0xFFE50914),
        unfocusedBorderColor = Color(0xFF555555),
        focusedLabelColor = Color(0xFFE50914),
        unfocusedLabelColor = Color(0xFF777777),
        cursorColor = Color(0xFFE50914),
        unfocusedTextColor = Color.White
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = state.inputTitle,
            onValueChange = viewModel::onTitleChange,
            label = { Text("Título do Filme") },
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.inputYear,
                onValueChange = viewModel::onYearChange,
                label = { Text("Ano") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.inputTags,
                onValueChange = viewModel::onTagsChange,
                label = { Text("Categorias (ex: slasher)") },
                colors = textFieldColors,
                modifier = Modifier.weight(2f)
            )
        }
        Spacer(Modifier.height(8.dp))

        // Seletor de Data
        DateSelector(
            plannedAt = state.inputPlannedAt,
            onDateSelected = viewModel::onPlannedAtChange
        )

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = viewModel::addMovie,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Filme", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Divider(color = Color(0xFF333333), thickness = 1.dp, modifier = Modifier.padding(top = 16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieFilterAndList(state: MovieState, viewModel: MovieViewModel) {
    // CORREÇÃO: Definindo um Color object para reuso e legibilidade
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF141414),
        unfocusedContainerColor = Color(0xFF141414),
        focusedBorderColor = Color(0xFFE50914),
        unfocusedBorderColor = Color(0xFF555555),
        focusedLabelColor = Color(0xFFE50914),
        unfocusedLabelColor = Color(0xFF777777),
        cursorColor = Color(0xFFE50914),
        unfocusedTextColor = Color.White
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // --- FILTROS ---
        item {
            OutlinedTextField(
                value = state.filterTags,
                onValueChange = viewModel::onFilterTagsChange,
                label = { Text("Filtrar por Categoria") },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.filterWatched,
                    onCheckedChange = viewModel::onFilterWatchedChange,
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFE50914), uncheckedColor = Color(0xFF555555))
                )
                Text(
                    "Mostrar apenas filmes assistidos",
                    color = Color.White,
                    modifier = Modifier.clickable { viewModel.onFilterWatchedChange(!state.filterWatched) }
                )
            }
            Divider(color = Color(0xFF333333), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))
        }

        // --- LISTA DE FILMES ---
        if (state.movies.isEmpty()) {
            item {
                Text(
                    "Nenhum filme encontrado. Adicione um ou ajuste seus filtros.",
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                )
            }
        } else {
            items(state.movies, key = { it.id }) { movie ->
                MovieItem(movie = movie, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, viewModel: MovieViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Clique abre o diálogo para Classificar
            .clickable { viewModel.showRatingDialog(movie) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${movie.title} (${movie.year})",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))

            Text(
                "Categorias: ${movie.tags.replace(",", ", ").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }}",
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Data: ${dateFormatter.format(Date(movie.plannedAt))}",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )

                if (movie.isWatched) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Assistido", color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(8.dp))
                        Text("Nota: ${movie.rating ?: "N/A"}", color = Color.Yellow, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Text("Planejado", color = Color(0xFFE50914), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun DateSelector(plannedAt: Long, onDateSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = plannedAt }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            onDateSelected(calendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Data Planejada: ${dateFormatter.format(Date(plannedAt))}",
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Selecionar Data",
            tint = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingDialog(movie: Movie, viewModel: MovieViewModel) {
    var ratingInput by remember { mutableStateOf(movie.rating?.toString() ?: "") }

    // CORREÇÃO: Definindo o objeto de cores para o TextField
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF1F1F1F),
        unfocusedContainerColor = Color(0xFF1F1F1F),
        focusedBorderColor = Color(0xFFE50914),
        unfocusedBorderColor = Color(0xFF555555),
        focusedLabelColor = Color(0xFFE50914),
        unfocusedLabelColor = Color(0xFF777777),
        cursorColor = Color(0xFFE50914),
        unfocusedTextColor = Color.White
    )

    AlertDialog(
        onDismissRequest = viewModel::dismissRatingDialog,
        title = { Text("Classificar ${movie.title}", color = Color.White) },
        text = {
            Column {
                Text("Insira a nota de 0 a 10.", color = Color.LightGray)
                OutlinedTextField(
                    value = ratingInput,
                    onValueChange = {
                        if (it.length <= 2) ratingInput = it.filter { char -> char.isDigit() }
                    },
                    label = { Text("Nota (0-10)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = textFieldColors,
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rating = ratingInput.toIntOrNull()
                    if (rating != null && rating in 0..10) {
                        viewModel.saveRating(movie.id, rating)
                    } else {
                        // Usando o contexto da aplicação para Toast
                        Toast.makeText(viewModel.getApplication(), "Nota inválida. Use um valor entre 0 e 10.", Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE50914))
            ) {
                Text("Salvar Nota", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = viewModel::dismissRatingDialog) {
                Text("Cancelar", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1F1F1F)
    )
}
