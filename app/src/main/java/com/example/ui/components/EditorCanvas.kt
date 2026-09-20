package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.syntax.LanguageDefinition
import com.example.ui.syntax.SyntaxHighlighter
import com.example.ui.theme.EditorThemeColors

class SyntaxVisualTransformation(
    private val language: LanguageDefinition,
    private val theme: EditorThemeColors,
    private val searchQuery: String,
    private val activeSearchIndex: Int,
    private val searchCaseSensitive: Boolean,
    private val searchWholeWord: Boolean,
    private val searchRegex: Boolean,
    private val showWhitespace: Boolean
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val highlighted = SyntaxHighlighter.highlight(
            text = text.text,
            language = language,
            colors = theme,
            searchQuery = searchQuery,
            activeSearchIndex = activeSearchIndex,
            searchCaseSensitive = searchCaseSensitive,
            searchWholeWord = searchWholeWord,
            searchRegex = searchRegex,
            showWhitespace = showWhitespace
        )
        return TransformedText(highlighted, OffsetMapping.Identity)
    }
}

@Composable
fun EditorCanvas(
    editorValue: TextFieldValue,
    language: LanguageDefinition,
    theme: EditorThemeColors,
    bookmarks: Set<Int>,
    currentLineNumber: Int,
    wordWrap: Boolean,
    showWhitespace: Boolean,
    fontSizeSp: Float,
    isReadOnly: Boolean,
    searchQuery: String,
    activeSearchIndex: Int,
    searchCaseSensitive: Boolean,
    searchWholeWord: Boolean,
    searchRegex: Boolean,
    onValueChange: (TextFieldValue) -> Unit,
    onToggleBookmark: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val visualTransformation = remember(
        language,
        theme,
        searchQuery,
        activeSearchIndex,
        searchCaseSensitive,
        searchWholeWord,
        searchRegex,
        showWhitespace
    ) {
        SyntaxVisualTransformation(
            language = language,
            theme = theme,
            searchQuery = searchQuery,
            activeSearchIndex = activeSearchIndex,
            searchCaseSensitive = searchCaseSensitive,
            searchWholeWord = searchWholeWord,
            searchRegex = searchRegex,
            showWhitespace = showWhitespace
        )
    }

    val lines = remember(editorValue.text) {
        editorValue.text.split("\n")
    }
    val lineCount = lines.size.coerceAtLeast(1)

    val gutterWidth = remember(lineCount) {
        when {
            lineCount < 100 -> 40.dp
            lineCount < 1000 -> 48.dp
            lineCount < 10000 -> 56.dp
            else -> 66.dp
        }
    }

    val editorTextStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = fontSizeSp.sp,
        lineHeight = (fontSizeSp * 1.45f).sp,
        color = theme.text
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
        ) {
            // Line numbers gutter
            Column(
                modifier = Modifier
                    .width(gutterWidth)
                    .background(theme.gutterBackground)
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                for (i in 1..lineCount) {
                    val isBookmarked = bookmarks.contains(i)
                    val isCurrentLine = (i == currentLineNumber)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((fontSizeSp * 1.45f).dp)
                            .clickable { onToggleBookmark(i) }
                            .padding(end = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (isBookmarked) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Bookmark on line $i",
                                tint = theme.bookmarkColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        }

                        Text(
                            text = i.toString(),
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = (fontSizeSp * 0.85f).sp,
                                fontWeight = if (isCurrentLine) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrentLine) theme.text else theme.gutterText
                            ),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            // Gutter vertical separator line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(theme.gutterText.copy(alpha = 0.25f))
            )

            // Text Editor Canvas Area
            val textContainerModifier = if (wordWrap) {
                Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            } else {
                Modifier
                    .weight(1f)
                    .horizontalScroll(horizontalScrollState)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .widthIn(min = 800.dp)
            }

            Box(modifier = textContainerModifier) {
                BasicTextField(
                    value = editorValue,
                    onValueChange = {
                        if (!isReadOnly) {
                            onValueChange(it)
                        }
                    },
                    readOnly = isReadOnly,
                    textStyle = editorTextStyle,
                    cursorBrush = SolidColor(theme.bookmarkColor),
                    visualTransformation = visualTransformation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("editor_text_field")
                )
            }
        }
    }
}
