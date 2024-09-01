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
package com.kodeco.cocktails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kodeco.cocktails.game.factory.CocktailsGameFactory
import com.kodeco.cocktails.game.model.Game
import com.kodeco.cocktails.game.model.Question
import com.kodeco.cocktails.game.model.RequestState
import com.kodeco.cocktails.repository.CocktailsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CocktailsViewModel(
  private val repository: CocktailsRepository,
  private val factory: CocktailsGameFactory,
  private val dispatcher: CoroutineDispatcher = IO
) : ViewModel() {

  private val _question = MutableStateFlow<Question?>(null)
  val question: StateFlow<Question?>
    get() = _question

  private val _game = MutableStateFlow<RequestState<Game>>(RequestState.Loading())
  val game: StateFlow<RequestState<Game>>
    get() = _game

  fun initGame() {
    viewModelScope.launch(dispatcher) {
      _game.update {
        factory.buildGame()
      }
      when (game.value) {
        is RequestState.Success -> {
          _question.update {
            (game.value as RequestState.Success<Game>).requestObject.nextQuestion()
          }
        }

        is RequestState.Error -> {

        }

        else -> {

        }
      }
    }
  }

  fun nextQuestion() {
    getGameObject()?.let { rawGame ->
      _question.update {
        rawGame.nextQuestion()
      }
    }
  }

  fun answerQuestion(question: Question, option: String) {
    if (game.value is RequestState.Success) {
      val gameObject = (game.value as RequestState.Success).requestObject
      gameObject.answer(question, option)
      viewModelScope.launch(IO) {
        repository.saveHighScore(gameObject.score.highest)
      }
      nextQuestion()
    }
  }

  private fun getGameObject(): Game? {
    return if (game.value is RequestState.Success) {
      (game.value as RequestState.Success<Game>).requestObject
    } else {
      null
    }
  }
}