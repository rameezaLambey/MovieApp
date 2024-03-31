package com.example.movieapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private const val BASE_OMDB_API_URL = "https://www.omdbapi.com/?type=movie&apikey=9ebf6f79"

// Keys of data fetched by the API URL.
private const val KEY_MOVIE_TITLE = "Title"
private const val KEY_POSTER = "Poster"
private const val KEY_RELEASE_DATE = "Released"
private const val KEY_ACTORS = "Actors"
private const val KEY_DIRECTOR = "Director"
private const val KEY_RATING = "imdbRating"
private const val KEY_GENRE = "Genre"
private const val KEY_BOX_OFFICE = "BoxOffice"
private const val KEY_PLOT = "Plot"
private const val KEY_YEAR = "Year"
private const val KEY_SEARCH = "Search"
private const val KEY_IMDB_ID = "imdbID"

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    val moviesLiveData = MutableLiveData<List<Movie>>()

    fun fetchMovieData() {
        viewModelScope.launch {
            val finalResult = withContext(Dispatchers.IO) {
                // Using search key `s`=`kind` to limit the number of movies fetched from the API.
                // If this is not provided, it fetches a huge number of movies, leading to stall
                // the app. This happens even with the default page size of 1 (`page` query param
                // of omdb api has default value 1). There is no param provided by this omdb api
                // to restrict the number of movies fetched in a single page. Hence, limiting
                // the fetch to movies with `kind` keyword in their names.
                parseDataIntoMoviesList(fireNetworkRequest("$BASE_OMDB_API_URL&s=kind"))
            }
            moviesLiveData.postValue(finalResult)
        }
    }

    private fun fireNetworkRequest(urlString: String): String {
        var result = ""
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(urlString)

            // Setup the connection.
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.connect()

            // Fetch the movie data.
            val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }

            result = stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }

        return result
    }

    private fun parseDataIntoMoviesList(resultData: String): List<Movie> {
        val jsonObject = JSONObject(resultData)
        val moviesListJsonArray = jsonObject.optJSONArray(KEY_SEARCH)
        val moviesList = mutableListOf<Movie>()

        if (moviesListJsonArray != null) {
            for (i in 0 until moviesListJsonArray.length()) {
                val movieObject = moviesListJsonArray.optJSONObject(i)

                // Fetch detailed data of movies with their omdb IDs (`i` param) and full plot
                // (default is `short` plot). This is required as the initial fetch by `s` (search)
                // param only fetches name, year and imageUrl of the movie. The detailed data will
                // then be used to render the detail page (2nd screen of this app).
                val detailedMovieData = fireNetworkRequest(
                    "$BASE_OMDB_API_URL&i=${movieObject.getString(KEY_IMDB_ID)}&plot=full"
                )
                val detailedDataJsonObject = JSONObject(detailedMovieData)

                // Storing the data of only popular movies, with high rating, > 5.
                val rating = detailedDataJsonObject.optString(KEY_RATING)
                if (rating != "N/A" && rating.toFloat() > 5) {
                    moviesList.add(
                        Movie(
                            detailedDataJsonObject.optString(KEY_MOVIE_TITLE),
                            detailedDataJsonObject.optString(KEY_POSTER),
                            detailedDataJsonObject.optString(KEY_RELEASE_DATE),
                            detailedDataJsonObject.optString(KEY_ACTORS),
                            detailedDataJsonObject.optString(KEY_DIRECTOR),
                            rating,
                            detailedDataJsonObject.optString(KEY_GENRE),
                            detailedDataJsonObject.optString(KEY_BOX_OFFICE),
                            detailedDataJsonObject.optString(KEY_PLOT),
                            detailedDataJsonObject.optString(KEY_YEAR)
                        )
                    )
                }
            }
        }
        return moviesList
    }
}
