package com.terrorflix.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.terrorflix.data.Movie

/**
 * Classe de ajuda para o gerenciamento do banco de dados SQLite nativo.
 * Implementa as operações CRUD.
 */
class DBHelper (context: Context): SQLiteOpenHelper(context, "HorrorDB", null, 1) {

    private val tableName = "movie"

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSql = """
            CREATE TABLE $tableName (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                year INTEGER,
                tags TEXT,
                planned_at INTEGER,
                watched INTEGER DEFAULT 0,
                rating INTEGER CHECK (rating BETWEEN 0 AND 10)
            )
        """.trimIndent()
        db.execSQL(createTableSql)
    }

    override fun onUpgrade (db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $tableName")
        onCreate(db)
    }

    private fun movieToContentValues(movie: Movie): ContentValues {
        return ContentValues().apply {
            put("title", movie.title)
            put("year", movie.year)
            put("tags", movie.tags)
            put("planned_at", movie.plannedAt)
            put("watched", if (movie.isWatched) 1 else 0)
            movie.rating?.let { put("rating", it) }
        }
    }

    private fun cursorToMovieList(cursor: Cursor): List<Movie> {
        val movies = mutableListOf<Movie>()
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow("id"))
                val title = it.getString(it.getColumnIndexOrThrow("title"))
                val year = it.getInt(it.getColumnIndexOrThrow("year"))
                val tags = it.getString(it.getColumnIndexOrThrow("tags"))
                val plannedAt = it.getLong(it.getColumnIndexOrThrow("planned_at"))
                val isWatched = it.getInt(it.getColumnIndexOrThrow("watched")) == 1

                val ratingIndex = it.getColumnIndexOrThrow("rating")
                val rating = if (it.isNull(ratingIndex)) null else it.getInt(ratingIndex)

                movies.add(
                    Movie(
                        id = id,
                        title = title,
                        year = year,
                        tags = tags,
                        plannedAt = plannedAt,
                        isWatched = isWatched,
                        rating = rating
                    )
                )
            }
        }
        return movies
    }

    fun insertMovie(movie: Movie): Boolean {
        val db = writableDatabase
        val valores = movieToContentValues(movie)
        val resultado = db.insert(tableName, null, valores)
        db.close()
        return resultado != -1L
    }

    fun updateRating(id: Int, rating: Int): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("rating", rating)
            put("watched", 1)
        }
        val linhasAfetadas = db.update(tableName, valores, "id = ?", arrayOf(id.toString()))
        db.close()
        return linhasAfetadas > 0
    }

    fun listMovies(tagsFilter: String? = null, watchedStatus: Int? = null): List<Movie> {
        val db = readableDatabase
        val selection = ArrayList<String>()
        val selectionArgs = ArrayList<String>()

        tagsFilter?.takeIf { it.isNotEmpty() }?.let {
            selection.add("tags LIKE ?")
            selectionArgs.add("%$it%")
        }

        watchedStatus?.let {
            selection.add("watched = ?")
            selectionArgs.add(it.toString())
        }

        val whereClause = if (selection.isNotEmpty()) selection.joinToString(" AND ") { it } else null

        val cursor = db.query(
            tableName,
            null,
            whereClause,
            selectionArgs.toTypedArray(),
            null,
            null,
            "planned_at ASC"
        )
        val movieList = cursorToMovieList(cursor)
        db.close()
        return movieList
    }
}