/*
 * Copyright (c) 2024 Kodeco Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.kodeco.cocktails.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.kodeco.cocktails.game.model.RequestState
import com.kodeco.cocktails.network.Cocktail
import com.kodeco.cocktails.network.CocktailsApi
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException

class CocktailsRepositoryImpl(
  private val api: CocktailsApi,
  private val preferencesDataStore: DataStore<Preferences>
) : CocktailsRepository {

  override suspend fun getAlcoholic(): RequestState<List<Cocktail>> {
    val cocktailsResponse = api.getAlcoholic()
    var requestState: RequestState<List<Cocktail>> = RequestState.Loading()

    cocktailsResponse.onSuccess {
      requestState = RequestState.Success(data.drinks ?: arrayListOf())
    }
    cocktailsResponse.onError {
      requestState = RequestState.Error()
    }
    cocktailsResponse.onFailure {
      requestState = RequestState.Error()
    }
    return requestState
  }

  override suspend fun saveHighScore(score: Int) {
    Result.runCatching {
      preferencesDataStore.edit { preferences ->
        preferences[HIGH_SCORE_KEY] = score
      }
    }
  }

  override suspend fun getHighScore(): Result<Int> {
    return Result.runCatching {
      val flow = preferencesDataStore.data
        .catch { exception ->
          /*
           * dataStore.data throws an IOException when an error
           * is encountered when reading data
           */
          if (exception is IOException) {
            emit(emptyPreferences())
          } else {
            throw exception
          }
        }
        .map { preferences ->
          preferences[HIGH_SCORE_KEY]
        }
      flow.firstOrNull() ?: 0
    }
  }

  private companion object {
    private val HIGH_SCORE_KEY = intPreferencesKey("HIGH_SCORE_KEY")
  }
}
