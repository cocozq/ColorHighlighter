/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */

package com.mallowigi.visitors

import com.intellij.codeInsight.daemon.impl.HighlightVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilCore
import com.mallowigi.search.ColorPrefixes.RGB_RUST
import com.mallowigi.search.ColorSearchEngine
import com.mallowigi.search.parsers.ColorParser
import com.mallowigi.search.parsers.RgbCtorParser
import java.awt.Color

class RustVisitor : ColorVisitor() {
  val extensions = setOf(
    "rust",
    "rs",
  )

  private val allowedTypes = setOf(
    "STRING_LITERAL",
    "INTEGER_LITERAL",
    "CALL_EXPR"
  )

  override fun clone(): HighlightVisitor = RustVisitor()

  override fun getParser(text: String): ColorParser = when {
    text.startsWith(RGB_RUST.text) -> RgbCtorParser()
    else                           -> throw IllegalArgumentException("Cannot find a parser for the text: $text")
  }

  override fun shouldParseText(text: String): Boolean = when {
    config.isRustColorCtorEnabled -> text.startsWith(RGB_RUST.text)
    else                          -> false
  }

  override fun suitableForFile(file: PsiFile): Boolean = extensions.contains(file.virtualFile?.extension)

  override fun accept(element: PsiElement): Color? {
    val type = PsiUtilCore.getElementType(element).toString()
    if (type !in allowedTypes) return null

    val value = element.text
    return ColorSearchEngine.getColor(value, this)
  }
}
