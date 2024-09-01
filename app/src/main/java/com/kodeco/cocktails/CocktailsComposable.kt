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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.kodeco.cocktails.game.factory.CocktailsGameFactory
import com.kodeco.cocktails.game.model.RequestState
import com.kodeco.cocktails.game.model.Score
import com.kodeco.cocktails.repository.CocktailsRepository
import com.kodeco.cocktails.ui.theme.CocktailsTheme

@Composable
fun CocktailsScreen(
  repository: CocktailsRepository,
  gameFactory: CocktailsGameFactory
) {

  val cocktailsViewModel = viewModel<CocktailsViewModel>(
    factory = CocktailsViewModelFactory(
      repository = repository,
      gameFactory = gameFactory
    )
  )

  val game = cocktailsViewModel.game.collectAsState().value
  val question = cocktailsViewModel.question.collectAsState().value

  val score = when(game) {
    is RequestState.Success -> {
      game.requestObject.score
    }
    else -> {
      Score(0)
    }
  }


  LaunchedEffect(Unit) {
    cocktailsViewModel.initGame()
  }

  val options = question?.getOptions()
  CocktailsComposable(
    score = score,
    cocktailImage = question?.imageUrl ?: "",
    cocktailNameChoiceOne = options?.get(0) ?: "",
    cocktailNameChoiceTwo = options?.get(1) ?: "",
    nextQuestion = { cocktailsViewModel.nextQuestion() },
    answerQuestion = { answer ->
      question?.let {
        cocktailsViewModel.answerQuestion(question, answer)
      }
    }
  )

}

@Composable
fun CocktailsComposable(
  score: Score,
  cocktailImage: String,
  cocktailNameChoiceOne: String,
  cocktailNameChoiceTwo: String,
  nextQuestion: () -> Unit,
  answerQuestion: (answer: String) -> Unit,
  modifier: Modifier = Modifier
) {

  Column(
    modifier.fillMaxWidth()
  ) {
    val centeredModifier = modifier.align(CenterHorizontally)
    Column(
      modifier = modifier
        .weight(1f)
        .fillMaxWidth()
    ) {
      Text(
        text = "High Score: " + score.highest,
        modifier = centeredModifier
      )
      Text(
        text = "Score: " + score.current,
        modifier = centeredModifier,
        fontSize = 22.sp
      )

      AsyncImage(
        model = cocktailImage,
        contentDescription = null,
        modifier = centeredModifier
      )

      Text(
        text = "What is the name of this cocktail?",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = centeredModifier
      )

      Button(
        onClick = { answerQuestion(cocktailNameChoiceOne) },
        modifier = centeredModifier
      ) {
        Text(
          text = cocktailNameChoiceOne
        )
      }

      Button(
        onClick = { answerQuestion(cocktailNameChoiceTwo) },
        modifier = centeredModifier
      ) {
        Text(
          text = cocktailNameChoiceTwo
        )
      }
    }
    Column(
      modifier = Modifier.fillMaxWidth()
    ) {
      Button(
        onClick = { nextQuestion() },
        modifier = centeredModifier
      ) {
        Text(
          text = "Next"
        )
      }
    }

  }

}


@Preview
@Composable
fun ContailsComposablePreview() {
  CocktailsTheme {
    // A surface container using the 'background' color from the theme
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      CocktailsComposable(
        Score(2),
        "https://www.thecocktaildb.com/images/media/drink/rtpxqw1468877562.jpg",
        "beer",
        "margarita",
        {},
        {}
      )
    }
  }
}