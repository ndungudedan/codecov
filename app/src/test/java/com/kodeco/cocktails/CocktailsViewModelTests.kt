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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kodeco.cocktails.game.factory.CocktailsGameFactory
import com.kodeco.cocktails.game.model.Game
import com.kodeco.cocktails.game.model.QuestionImpl
import com.kodeco.cocktails.game.model.RequestState
import com.kodeco.cocktails.game.model.Score
import com.kodeco.cocktails.network.Cocktail
import com.kodeco.cocktails.repository.CocktailsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class CocktailsViewModelTests {

  @get:Rule
  val taskExecutorRule = InstantTaskExecutorRule()


  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun init_should_CreateAGame_when_FactoryReturnsSuccess() = runTest {
    val game = buildGame()
    val cocktailsViewModel = buildSuccessfulGameViewModel(game, testScheduler)
    cocktailsViewModel.initGame()
    advanceUntilIdle()
    Assert.assertTrue(cocktailsViewModel.game.value is RequestState.Success)
    val successfulRequest = cocktailsViewModel.game.value as RequestState.Success
    Assert.assertEquals(game, successfulRequest.requestObject)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun init_shouldShowFirstQuestion_whenFactoryReturnsSuccess() = runTest {
    val cocktailsViewModel = buildSuccessfulGameViewModel(buildGame(), testScheduler)
    cocktailsViewModel.initGame()
    advanceUntilIdle()
    val question = cocktailsViewModel.question.value
    Assert.assertEquals(questions.first(), question)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun nextQuestion_shouldShowNextQuestion() = runTest {
    val cocktailsViewModel = buildSuccessfulGameViewModel(buildGame(), testScheduler)
    cocktailsViewModel.initGame()
    advanceUntilIdle()
    cocktailsViewModel.nextQuestion()
    advanceUntilIdle()
    val question = cocktailsViewModel.question.value
    Assert.assertEquals(questions.last(), question)
  }

  val questions = arrayListOf(
    QuestionImpl("Beer", "Wine"),
    QuestionImpl("Martini", "Amarula")
  )

  fun buildSuccessfulGameViewModel(game: Game, testScheduler: TestCoroutineScheduler): CocktailsViewModel {
    val cocktailsGameFactorySuccess = buildCocktailsGameFactory(game)
    val standardTestDispatcher = StandardTestDispatcher(testScheduler)
    return CocktailsViewModel(fakeRepository, cocktailsGameFactorySuccess, standardTestDispatcher)
  }

  fun buildGame(): Game {
    return Game(
      questions = questions,
      score = Score(0)
    )
  }

  fun buildCocktailsGameFactory(game: Game): CocktailsGameFactory {
    return object : CocktailsGameFactory {
      override suspend fun buildGame(): RequestState<Game> {
        return RequestState.Success(
          game
        )
      }

    }
  }

  val fakeRepository = object : CocktailsRepository {
    override suspend fun getAlcoholic(): RequestState<List<Cocktail>> {
      TODO("Not yet implemented")
    }

    override suspend fun saveHighScore(score: Int) {
      TODO("Not yet implemented")
    }

    override suspend fun getHighScore(): Result<Int?> {
      TODO("Not yet implemented")
    }
  }
}
