package com.example.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DocumentStatsDialog
import com.example.ui.components.EditorCanvas
import com.example.ui.components.EditorFindReplaceBar
import com.example.ui.components.EditorStatusBar
import com.example.ui.components.EditorTabBar
import com.example.ui.components.EditorToolbar
import com.example.ui.components.GoToLineDialog
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.components.LineToolsDialog
import com.example.ui.components.PreviewDialog
import com.example.ui.components.RenameDialog
import com.example.ui.components.SupportTipJarDialog
import com.example.ui.components.SymbolQuickBar
import com.example.ui.components.ThemeSelectorDialog
import com.example.ui.viewmodel.EditorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotepadScreen(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val theme = uiState.theme
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Active document title
    val activeDoc = uiState.documents.firstOrNull { it.id == uiState.activeDocumentId }
    val activeTitle = activeDoc?.title ?: "NoteCode++"

    // Storage Access Framework: Open File Launcher
    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    val content = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            BufferedReader(InputStreamReader(inputStream)).readText()
                        } ?: ""
                    }
                    val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "document.txt"
                    viewModel.openFileFromSystem(fileName, content)
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Error opening file: ${e.localizedMessage}")
                }
            }
        }
    }

    // Storage Access Framework: Export / Save As Launcher
    val exportFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/*")
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            OutputStreamWriter(outputStream).use { writer ->
                                writer.write(uiState.editorValue.text)
                            }
                        }
                    }
                    val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: activeTitle
                    snackbarHostState.showSnackbar("Exported successfully as $fileName")
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Export failed: ${e.localizedMessage}")
                }
            }
        }
    }

    // Calculate line & col
    val currentLine = remember(uiState.editorValue.text, uiState.editorValue.selection) {
        viewModel.getCurrentLineNumber()
    }
    val currentCol = remember(uiState.editorValue.text, uiState.editorValue.selection) {
        viewModel.getCurrentColNumber()
    }
    val totalLines = remember(uiState.editorValue.text) {
        val count = uiState.editorValue.text.count { it == '\n' } + 1
        if (uiState.editorValue.text.isEmpty()) 1 else count
    }
    val selectionLength = remember(uiState.editorValue.selection) {
        (uiState.editorValue.selection.max - uiState.editorValue.selection.min).coerceAtLeast(0)
    }

    // Show info messages in snackbar
    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = theme.gutterBackground,
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App Logo Badge
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF388E3C)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "++",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NoteCode++",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.text,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Active doc badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(theme.surface)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = activeTitle,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (uiState.isModified) Color(0xFFFF5252) else theme.bookmarkColor,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Quick Header actions
                    IconButton(
                        onClick = { viewModel.createNewDocument() },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("header_new_file_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New File",
                            tint = theme.text.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.saveActiveDocument() },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("header_save_file_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save File",
                            tint = if (uiState.isModified) Color(0xFF4CAF50) else theme.text.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleFindBar(replaceMode = false) },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("header_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = theme.text.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                EditorStatusBar(
                    language = uiState.activeLanguage,
                    currentLine = currentLine,
                    currentCol = currentCol,
                    selectionLength = selectionLength,
                    totalLength = uiState.editorValue.text.length,
                    totalLines = totalLines,
                    lineEnding = uiState.activeLineEnding,
                    encoding = uiState.activeEncoding,
                    isReadOnly = uiState.isReadOnly,
                    theme = theme,
                    onLanguageClick = { viewModel.setShowLanguageDialog(true) },
                    onLineEndingClick = { viewModel.toggleLineEnding() },
                    onEncodingClick = { viewModel.toggleEncoding() },
                    onReadOnlyClick = { viewModel.toggleReadOnly() }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Tab Bar
            EditorTabBar(
                documents = uiState.documents,
                activeDocumentId = uiState.activeDocumentId,
                isCurrentModified = uiState.isModified,
                theme = theme,
                onTabSelected = { viewModel.selectDocument(it) },
                onTabClosed = { viewModel.closeDocument(it) },
                onNewTab = { viewModel.createNewDocument() },
                onTabLongClick = { viewModel.setShowRenameDialog(true) }
            )

            // 2. Toolbar
            EditorToolbar(
                isModified = uiState.isModified,
                wordWrap = uiState.wordWrap,
                showWhitespace = uiState.showWhitespace,
                hasBookmarks = uiState.bookmarks.isNotEmpty(),
                theme = theme,
                onSave = { viewModel.saveActiveDocument() },
                onExportFile = { exportFileLauncher.launch(activeTitle) },
                onOpenFile = { openFileLauncher.launch(arrayOf("*/*", "text/*")) },
                onUndo = { viewModel.undo() },
                onRedo = { viewModel.redo() },
                onToggleFind = { viewModel.toggleFindBar(replaceMode = false) },
                onToggleReplace = { viewModel.toggleFindBar(replaceMode = true) },
                onToggleBookmark = { viewModel.toggleBookmark(currentLine) },
                onNextBookmark = { viewModel.nextBookmark() },
                onPrevBookmark = { viewModel.prevBookmark() },
                onClearBookmarks = { viewModel.clearAllBookmarks() },
                onIndent = { viewModel.indent() },
                onUnindent = { viewModel.unindent() },
                onDuplicateLine = { viewModel.duplicateLine() },
                onDeleteLine = { viewModel.deleteLine() },
                onToggleComment = { viewModel.toggleComment() },
                onInsertTimestamp = { viewModel.insertTimestamp() },
                onChangeCase = { viewModel.changeCase(it) },
                onToggleWordWrap = { viewModel.toggleWordWrap() },
                onToggleWhitespace = { viewModel.toggleWhitespace() },
                onShowLineTools = { viewModel.setShowLineToolsDialog(true) },
                onShowSupport = { viewModel.setShowSupportDialog(true) },
                onZoomIn = { viewModel.increaseFontSize() },
                onZoomOut = { viewModel.decreaseFontSize() },
                onPreview = { viewModel.setShowPreviewDialog(true) },
                onShowStats = { viewModel.setShowStatsDialog(true) },
                onSelectTheme = { viewModel.setShowThemeDialog(true) },
                onGoToLine = { viewModel.setShowGoToLineDialog(true) },
                onRename = { viewModel.setShowRenameDialog(true) }
            )

            // 3. Quick Symbol & Snippet Accessory Bar
            SymbolQuickBar(
                theme = theme,
                onInsertSymbol = { viewModel.insertSymbol(it) }
            )

            // 4. Find and Replace Bar (Collapsible)
            AnimatedVisibility(
                visible = uiState.isFindBarVisible,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                EditorFindReplaceBar(
                    isReplaceMode = uiState.isReplaceMode,
                    searchQuery = uiState.searchQuery,
                    replaceQuery = uiState.replaceQuery,
                    searchCaseSensitive = uiState.searchCaseSensitive,
                    searchWholeWord = uiState.searchWholeWord,
                    searchRegex = uiState.searchRegex,
                    matchCount = uiState.searchMatches.size,
                    activeMatchIndex = uiState.activeSearchIndex,
                    theme = theme,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onReplaceQueryChange = { viewModel.setReplaceQuery(it) },
                    onToggleCaseSensitive = { viewModel.toggleSearchCaseSensitive() },
                    onToggleWholeWord = { viewModel.toggleSearchWholeWord() },
                    onToggleRegex = { viewModel.toggleSearchRegex() },
                    onFindNext = { viewModel.findNext() },
                    onFindPrevious = { viewModel.findPrevious() },
                    onReplaceCurrent = { viewModel.replaceCurrent() },
                    onReplaceAll = { viewModel.replaceAll() },
                    onClose = { viewModel.closeFindBar() }
                )
            }

            // 5. Editor Canvas (Gutter + Text Field)
            EditorCanvas(
                editorValue = uiState.editorValue,
                language = uiState.activeLanguage,
                theme = theme,
                bookmarks = uiState.bookmarks,
                currentLineNumber = currentLine,
                wordWrap = uiState.wordWrap,
                showWhitespace = uiState.showWhitespace,
                fontSizeSp = uiState.fontSizeSp,
                isReadOnly = uiState.isReadOnly,
                searchQuery = uiState.searchQuery,
                activeSearchIndex = uiState.activeSearchIndex,
                searchCaseSensitive = uiState.searchCaseSensitive,
                searchWholeWord = uiState.searchWholeWord,
                searchRegex = uiState.searchRegex,
                onValueChange = { viewModel.onEditorValueChanged(it) },
                onToggleBookmark = { viewModel.toggleBookmark(it) },
                modifier = Modifier.weight(1f)
            )
        }
    }

    // --- Dialogs ---

    if (uiState.showLineToolsDialog) {
        LineToolsDialog(
            theme = theme,
            onSortAscending = { viewModel.sortLines(true) },
            onSortDescending = { viewModel.sortLines(false) },
            onRemoveDuplicates = { viewModel.removeDuplicateLines() },
            onTrimWhitespace = { viewModel.trimLeadingTrailingWhitespace() },
            onJoinLines = { viewModel.joinLines() },
            onReverseLines = { viewModel.reverseLines() },
            onDismiss = { viewModel.setShowLineToolsDialog(false) }
        )
    }

    if (uiState.showSupportDialog) {
        SupportTipJarDialog(
            theme = theme,
            onTipSelected = { amount ->
                viewModel.setShowSupportDialog(false)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Thank you for choosing to support NoteCode++ ($amount)! You are keeping developer tools free.")
                }
            },
            onDismiss = { viewModel.setShowSupportDialog(false) }
        )
    }

    if (uiState.showLanguageDialog) {
        LanguageSelectorDialog(
            currentLanguage = uiState.activeLanguage,
            theme = theme,
            onLanguageSelected = { viewModel.setLanguage(it) },
            onDismiss = { viewModel.setShowLanguageDialog(false) }
        )
    }

    if (uiState.showStatsDialog) {
        DocumentStatsDialog(
            title = activeTitle,
            content = uiState.editorValue.text,
            language = uiState.activeLanguage,
            encoding = uiState.activeEncoding,
            lineEnding = uiState.activeLineEnding,
            theme = theme,
            onDismiss = { viewModel.setShowStatsDialog(false) }
        )
    }

    if (uiState.showPreviewDialog) {
        PreviewDialog(
            title = activeTitle,
            content = uiState.editorValue.text,
            language = uiState.activeLanguage,
            theme = theme,
            onDismiss = { viewModel.setShowPreviewDialog(false) }
        )
    }

    if (uiState.showRenameDialog) {
        RenameDialog(
            currentTitle = activeTitle,
            onConfirm = { viewModel.renameActiveDocument(it) },
            onDismiss = { viewModel.setShowRenameDialog(false) }
        )
    }

    if (uiState.showGoToLineDialog) {
        GoToLineDialog(
            currentLine = currentLine,
            totalLines = totalLines,
            onGoToLine = { viewModel.goToLine(it) },
            onDismiss = { viewModel.setShowGoToLineDialog(false) }
        )
    }

    if (uiState.showThemeDialog) {
        ThemeSelectorDialog(
            currentTheme = theme,
            onSelectTheme = { viewModel.setTheme(it) },
            onDismiss = { viewModel.setShowThemeDialog(false) }
        )
    }
}
