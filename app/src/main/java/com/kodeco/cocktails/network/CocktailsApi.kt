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
package com.kodeco.cocktails.network

import com.kodeco.cocktails.BuildConfig
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

data class CocktailsContainer(val drinks: List<Cocktail>?)

data class Cocktail(val idDrink: String,
                    val strDrink: String,
                    val strDrinkThumb: String)

interface CocktailsApi {

  @GET("filter.php?a=Alcoholic")
  suspend fun getAlcoholic(): ApiResponse<CocktailsContainer>

  companion object Factory {
    fun create(): CocktailsApi {
      val moshi = MoshiConverterFactory.create()

      val client = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
          val interceptor = HttpLoggingInterceptor()
          interceptor.level = HttpLoggingInterceptor.Level.BODY
          addInterceptor(interceptor)
        }
      }.build()

      val retrofit = Retrofit.Builder()
        .baseUrl("https://www.thecocktaildb.com/api/json/v1/1/")
        .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
        .client(client)
        .addConverterFactory(moshi)
        .build()

      return retrofit.create(CocktailsApi::class.java)
    }
  }
}