package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EditorThemeColors

@Composable
fun EditorFindReplaceBar(
    isReplaceMode: Boolean,
    searchQuery: String,
    replaceQuery: String,
    searchCaseSensitive: Boolean,
    searchWholeWord: Boolean,
    searchRegex: Boolean,
    matchCount: Int,
    activeMatchIndex: Int,
    theme: EditorThemeColors,
    onSearchQueryChange: (String) -> Unit,
    onReplaceQueryChange: (String) -> Unit,
    onToggleCaseSensitive: () -> Unit,
    onToggleWholeWord: () -> Unit,
    onToggleRegex: () -> Unit,
    onFindNext: () -> Unit,
    onFindPrevious: () -> Unit,
    onReplaceCurrent: () -> Unit,
    onReplaceAll: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = theme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // Find Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Find Input Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(theme.background)
                        .border(1.dp, theme.gutterText.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Find...",
                            color = theme.gutterText.copy(alpha = 0.6f),
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            color = theme.text,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        cursorBrush = SolidColor(theme.bookmarkColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("find_input_field")
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Match counter badge
                val matchInfo = if (searchQuery.isEmpty()) {
                    ""
                } else if (matchCount == 0) {
                    "0/0"
                } else {
                    "${activeMatchIndex + 1}/$matchCount"
                }
                if (matchInfo.isNotEmpty()) {
                    Text(
                        text = matchInfo,
                        color = if (matchCount > 0) theme.bookmarkColor else Color(0xFFFF5252),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                // Prev / Next Arrows
                IconButton(
                    onClick = onFindPrevious,
                    enabled = matchCount > 0,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("find_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Previous Match",
                        tint = if (matchCount > 0) theme.text else theme.gutterText.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onFindNext,
                    enabled = matchCount > 0,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("find_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Next Match",
                        tint = if (matchCount > 0) theme.text else theme.gutterText.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Options: Case Sensitive [Aa], Whole Word [W], Regex [.*]
                OptionToggleButton(
                    label = "Aa",
                    isActive = searchCaseSensitive,
                    theme = theme,
                    tooltip = "Match Case",
                    onClick = onToggleCaseSensitive
                )

                OptionToggleButton(
                    label = "W",
                    isActive = searchWholeWord,
                    theme = theme,
                    tooltip = "Match Whole Word",
                    onClick = onToggleWholeWord
                )

                OptionToggleButton(
                    label = ".*",
                    isActive = searchRegex,
                    theme = theme,
                    tooltip = "Regular Expression",
                    onClick = onToggleRegex
                )

                // Close search bar
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("close_find_bar_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Find Bar",
                        tint = theme.text.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Replace Row (If Replace Mode is active)
            if (isReplaceMode) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(theme.background)
                            .border(1.dp, theme.gutterText.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (replaceQuery.isEmpty()) {
                            Text(
                                text = "Replace with...",
                                color = theme.gutterText.copy(alpha = 0.6f),
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        BasicTextField(
                            value = replaceQuery,
                            onValueChange = onReplaceQueryChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                color = theme.text,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            cursorBrush = SolidColor(theme.bookmarkColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("replace_input_field")
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onReplaceCurrent,
                        enabled = matchCount > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = theme.bookmarkColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("replace_button")
                    ) {
                        Text("Replace", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    OutlinedButton(
                        onClick = onReplaceAll,
                        enabled = matchCount > 0,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("replace_all_button")
                    ) {
                        Text("Replace All", fontSize = 12.sp, color = theme.text)
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionToggleButton(
    label: String,
    isActive: Boolean,
    theme: EditorThemeColors,
    tooltip: String,
    onClick: () -> Unit
) {
    val bg = if (isActive) theme.bookmarkColor else Color.Transparent
    val textColor = if (isActive) Color.White else theme.text.copy(alpha = 0.7f)

    Box(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .size(width = 26.dp, height = 26.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(bg)
            .border(
                1.dp,
                if (isActive) theme.bookmarkColor else theme.gutterText.copy(alpha = 0.3f),
                RoundedCornerShape(3.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = textColor
        )
    }
}
