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

import com.kodeco.cocktails.game.model.Game
import com.kodeco.cocktails.game.model.Question
import com.kodeco.cocktails.game.model.QuestionImpl
import com.kodeco.cocktails.game.model.Score
import org.junit.Assert
import org.junit.Test

class GameUnitTests {
  @Test
  fun whenGettingNextQuestion_shouldReturnIt() {
    val question1 = QuestionImpl("CORRECT", "INCORRECT")
    val questions = listOf(question1)
    val game = Game(questions)

    val nextQuestion = game.nextQuestion()

    Assert.assertSame(question1, nextQuestion)
  }

  @Test
  fun whenGettingNextQuestion_withoutMoreQuestions_shouldReturnNull() {
    val question1 = QuestionImpl("CORRECT", "INCORRECT")
    val questions = listOf(question1)
    val game = Game(questions)

    game.nextQuestion()
    val nextQuestion = game.nextQuestion()

    Assert.assertNull(nextQuestion)
  }

  @Test
  fun whenAnsweringCorrectly_shouldIncrementCurrentScore() {
    val question = createQuestion(true)
    val score = Score()
    val game = Game(listOf(question), score)

    game.answer(question, "OPTION")

    Assert.assertEquals(score.current, 1)
  }

  @Test
  fun whenAnsweringIncorrectly_shouldNotIncrementCurrentScore() {
    val question = createQuestion(false)
    val score = Score()
    val game = Game(listOf(question), score)

    game.answer(question, "OPTION")

    Assert.assertEquals(score.current, 0)
  }

  fun createQuestion(answerReturn: Boolean): Question {
    return object : Question("", ""){
      override fun answer(answer: String): Boolean{
        return answerReturn
      }
    }
  }
}