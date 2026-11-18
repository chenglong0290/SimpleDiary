package com.simplediary.app.ui.diary

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simplediary.app.data.MediaType
import com.simplediary.app.ui.components.MediaAttachmentRow
import com.simplediary.app.ui.components.MoodColorPicker
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryEditorScreen(
    viewModel: DiaryEditorViewModel,
    onBack: () -> Unit
) {
    val body by viewModel.body.collectAsStateWithLifecycle()
    val moodColor by viewModel.moodColor.collectAsStateWithLifecycle()
    val media by viewModel.media.collectAsStateWithLifecycle()
    val ready by viewModel.ready.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    var showPalette by remember { mutableStateOf(false) }
    var pendingCapture by remember { mutableStateOf<Pair<File, MediaType>?>(null) }

    fun exitEditor() {
        scope.launch {
            viewModel.finalizeAndExit()
            onBack()
        }
    }

    BackHandler { exitEditor() }

    LaunchedEffect(ready) {
        if (ready) {
            focusRequester.requestFocus()
        }
    }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.addMedia(it, MediaType.IMAGE) }
    }
    val pickVideo = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.addMedia(it, MediaType.VIDEO) }
    }
    val pickAudio = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.addMedia(it, MediaType.AUDIO) }
    }

    val takePicture = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val pending = pendingCapture
        if (success && pending != null && pending.second == MediaType.IMAGE) {
            viewModel.addCapturedFile(pending.first, MediaType.IMAGE)
        }
        pendingCapture = null
    }

    fun uriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("写日记") },
                navigationIcon = {
                    IconButton(onClick = { exitEditor() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TextField(
                    value = body,
                    onValueChange = viewModel::onBodyChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .focusRequester(focusRequester),
                    placeholder = {
                        Text(
                            "写下此刻…",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                MediaAttachmentRow(
                    media = media,
                    onRemove = viewModel::removeMedia,
                    modifier = Modifier.padding(bottom = 72.dp)
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { pickImage.launch("image/*") }) {
                    Icon(Icons.Outlined.Image, contentDescription = "添加图片")
                }
                IconButton(
                    onClick = {
                        val file = viewModel.createCaptureFile(MediaType.IMAGE)
                        pendingCapture = file to MediaType.IMAGE
                        takePicture.launch(uriForFile(file))
                    }
                ) {
                    Icon(Icons.Outlined.PhotoCamera, contentDescription = "拍照")
                }
                IconButton(onClick = { pickVideo.launch("video/*") }) {
                    Icon(Icons.Outlined.Videocam, contentDescription = "添加视频")
                }
                IconButton(onClick = { pickAudio.launch("audio/*") }) {
                    Icon(Icons.Outlined.AudioFile, contentDescription = "添加音频")
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = { showPalette = true },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(moodColor))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Palette,
                        contentDescription = "心情颜色",
                        tint = Color.White
                    )
                }
                DropdownMenu(
                    expanded = showPalette,
                    onDismissRequest = { showPalette = false }
                ) {
                    MoodColorPicker(
                        selected = moodColor,
                        onSelect = {
                            viewModel.onMoodChange(it)
                            showPalette = false
                        }
                    )
                }
            }
        }
    }
}
