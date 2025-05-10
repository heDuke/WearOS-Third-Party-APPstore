/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:OptIn(ExperimentalHorologistApi::class, ExperimentalWearFoundationApi::class)

package com.example.android.wearable.composestarter.presentation

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.material.AppCard
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.android.wearable.composestarter.R
import com.example.android.wearable.composestarter.presentation.theme.WearAppTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.AlertContent
import com.google.android.horologist.compose.material.Chip
import com.google.android.horologist.compose.material.ListHeaderDefaults.firstItemPadding
import com.google.android.horologist.compose.material.ResponsiveListHeader

/**
 * Simple "Hello, World" app meant as a starting point for a new project using Compose for Wear OS.
 *
 * Displays a centered [Text] composable and a list built with
 * (https://github.com/google/horologist).
 *
 * Use the Wear version of Compose Navigation. You can carry
 * over your knowledge from mobile and it supports the swipe-to-dismiss gesture (Wear OS's
 * back action). For more information, go here:
 * https://developer.android.com/reference/kotlin/androidx/wear/compose/navigation/package-summary
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val navController = rememberSwipeDismissableNavController()

    WearAppTheme {
        AppScaffold {
            SwipeDismissableNavHost(navController = navController, startDestination = "menu") {
                composable("menu") {
                    GreetingScreen(
                        "Android",
                        onShowList = { navController.navigate("list") }
                    )
                }
                composable("list") {
                    ListScreen(navController = navController) // Pass navController to ListScreen
                }
                // Define the destination for the AppDetailScreen
                // Arguments for appName and fileUrl
                composable(
                    route = "appDetail/{appName}/{fileUrl}",
                    arguments = listOf(
                        androidx.navigation.navArgument("appName") { type = androidx.navigation.NavType.StringType },
                        androidx.navigation.navArgument("fileUrl") { type = androidx.navigation.NavType.StringType }
                    )
                ) { backStackEntry ->
                    val appName = backStackEntry.arguments?.getString("appName") ?: "Unknown App"
                    val fileUrl = backStackEntry.arguments?.getString("fileUrl") ?: "" // Default to empty string if null
                    AppDetailScreen(appName = appName, fileUrl = fileUrl)
                }
            }
        }
    }
}

@Composable
fun GreetingScreen(greetingName: String, onShowList: () -> Unit) {
    // 移除了 scrollState 和相关的滚动修饰符，简化为非滚动屏幕
    // val scrollState = rememberScrollState()

    /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
     * version of LazyColumn for wear devices with some added features. For more information,
     * see d.android.com/wear/compose.
     *
     * 修正意见 1 和 3: 当前使用 Column，适用于内容较少的情况。如果内容会很多，
     * 应该切换到 ScalingLazyColumn 以获得更好的性能和 Wear OS 体验。
     * 同时，Greeting Composable 的实现也需要调整以适应 Column。
     */
    // ScreenScaffold 现在不需要 scrollState 参数，因为它内部的内容不再滚动
    ScreenScaffold {
        // 修正意见 2: ScalingLazyColumnDefaults.padding 是为 ScalingLazyColumn 设计的，
        // 不适用于标准的 Column。移除此行，并在 Column 的 Modifier 中使用标准的 padding。
        // val padding = ScalingLazyColumnDefaults.padding(
        //     first = ItemType.Text,
        //     second = ItemType.TitleCard,
        //     last =  ItemType.Chip,
        // )()

        Column(
            modifier = Modifier
                .fillMaxSize()
                // 移除了 verticalScroll 和 rotaryScrollable 修饰符
                // .verticalScroll(scrollState)
                // .rotaryScrollable(
                //     behavior(scrollableState = scrollState),
                //     focusRequester = rememberActiveFocusRequester()
                // )
                // 修正意见 2: 使用标准的 Modifier.padding
                .padding(horizontal = 16.dp, vertical = 8.dp), // 示例：添加一些内边距
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically), // 示例：添加垂直间距并垂直居中
            horizontalAlignment = Alignment.CenterHorizontally // 让内容水平居中
        ) {
            // 修正意见 3: Greeting Composable 的实现已修改，不再使用 ResponsiveListHeader。
            // ResponsiveListHeader 通常用于 ScalingLazyColumn 中的列表头。
            Greeting(greetingName = greetingName)

            // 移除了 TitleCard 以进一步简化屏幕
            // TitleCard(title = { Text("Example Title") }, onClick = { }) {
            //     Text("Example Content")
            // }

            // 修正意见 4: Chip 的 label 参数需要一个 Composable lambda，而不是字符串。
            Chip(
                label = {
                    Text(
                        text = "Show List",
                        textAlign = TextAlign.Center // 示例：文本居中
                    )
                },
                onClick = onShowList, // 修正：传递正确的 lambda
                modifier = Modifier.fillMaxWidth() // Chip 通常填充宽度
            )

            // 注意：你之前的代码中有一个 OutlinedButton 导致了参数错误，
            // 但在这个版本中它被移除了。如果需要 OutlinedButton，请确保
            // onClick 参数是 () -> Unit 类型，并且提供了内容 lambda { ... }。
            // 例如：
            // OutlinedButton(onClick = { /* Action */ }) {
            //     Text("Click Me")
            // }
        }
    }
}

@Composable
fun ListScreen(navController: NavHostController) { // Accept navController
    var showDialog by remember { mutableStateOf(false) }

    /*
     * Specifying the types of items that appear at the start and end of the list ensures that the
     * appropriate padding is used.
     */
    val columnState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.Text, // Adjust padding types as needed for the list content
            last = ItemType.SingleButton
        )
    )

    ScreenScaffold(scrollState = columnState) {
        /*
         * The Horologist [ScalingLazyColumn] takes care of the horizontal and vertical
         * padding for the list, so there is no need to specify it, as in the [GreetingScreen]
         * composable.
         */
        ScalingLazyColumn(
            columnState = columnState
        ) {
            // Add a header item
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(text = "App Cards")
                }
            }

            // Generate and display at least 20 AppCards
            items(25) { index -> // Displaying 25 AppCards
                val appName = "App ${index + 1}"
                // Placeholder URL - replace with actual file URLs
                val fileUrl = "https://example.com/files/app${index + 1}.apk" // Example dummy URL

                AppCard(
                    onClick = {
                        // Navigate to AppDetailScreen, encoding the URL
                        val encodedUrl = Uri.encode(fileUrl)
                        navController.navigate("appDetail/${appName}/${encodedUrl}")
                    },
                    appName = { Text(appName) }, // App name text
                    title = { Text("Card Title ${index + 1}") }, // Card title text
                    time = { Text("Now") }, // Optional time text
                    appImage = { // App icon or image
                        // Using a simple icon for demonstration
                        Icon(
                            imageVector = Icons.Default.Face, // Using a built-in icon
                            contentDescription = "App Icon ${index + 1}",
                            modifier = Modifier.size(24.dp) // Adjust icon size as needed
                        )
                    }
                ) {
                    // Content area of the AppCard
                    Text("This is the content for App ${index + 1}.")
                }
            }

            // The original items are replaced by the AppCards, but you can add more items here if needed
            // item {
            //     Chip(label = { Text("Example Chip") }, onClick = { })
            // }
            // item {
            //     Button(
            //         imageVector = Icons.Default.Build,
            //         contentDescription = "Example Button",
            //         onClick = { showDialog = true }
            //     )
            // }
        }
    }

    SampleDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onCancel = {},
        onOk = {}
    )
}

@SuppressLint("UseKtx")
@Composable
fun AppDetailScreen(appName: String, fileUrl: String) {
    val columnState = rememberResponsiveColumnState()
    val context = LocalContext.current // Get the current context

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(columnState = columnState) {
            // App Name Header
            item {
                ResponsiveListHeader(contentPadding = firstItemPadding()) {
                    Text(text = appName)
                }
            }

            // Detailed Description
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    text = "This is a detailed description for $appName. It explains what the app does, its features, and why you should download it. This section can be quite long to demonstrate scrolling." +
                        " More details here to make the list scrollable and test the crown input." +
                        " Add even more text to ensure it overflows and requires scrolling." +
                        " This helps in verifying the scrolling and rotary input behavior on Wear OS devices." +
                        " Final line of description for $appName."
                )
            }

            // Download Button
            item {
                com.google.android.horologist.compose.material.Button( // Using Horologist Button
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Download $appName",
                    onClick = {
                        // Trigger download using DownloadManager
                        if (fileUrl.isNotEmpty()) {
                            try {
                                val request = DownloadManager.Request(fileUrl.toUri())
                                    .setTitle("Downloading $appName")
                                    .setDescription("Downloading file for $appName")
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                        "${appName.replace(" ", "_")}.apk") // Save to Downloads folder

                                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                downloadManager.enqueue(request)
                                Toast.makeText(context, "Download started for $appName", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed to start download: ${e.message}", Toast.LENGTH_LONG).show()
                                e.printStackTrace()
                            }
                        } else {
                            Toast.makeText(context, "Download URL is not available for $appName", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // Add a spacer at the end for better scrolling experience
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


// 修正意见 3: Greeting Composable 的实现已修改，不再使用 ResponsiveListHeader。
// ResponsiveListHeader 通常用于 ScalingLazyColumn 中的列表头。
@Composable
fun Greeting(greetingName: String) {
    // 使用标准的 Text Composable
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp), // 示例：添加底部内边距
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = ("这是一个使用Gemini对ComposeStarter项目进行修改，使其成为一个采用MaterialDesign设计的WearOS第三方应用商店（该软件仅作娱乐，你可以对此进行修改发布你自己运营的WearOS第三方应用商店）*不保证可用" )
    )
}

@Composable
fun SampleDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    onOk: () -> Unit
) {
    val state = rememberResponsiveColumnState()

    Dialog(
        showDialog = showDialog,
        onDismissRequest = onDismiss,
        scrollState = state.state
    ) {
        SampleDialogContent(onCancel, onDismiss, onOk)
    }
}

@Composable
fun SampleDialogContent(
    onCancel: () -> Unit,
    onDismiss: () -> Unit,
    onOk: () -> Unit
) {
    AlertContent(
        icon = {},
        title = "Title",
        onCancel = {
            onCancel()
            onDismiss()
        },
        onOk = {
            onOk()
            onDismiss()
        }
    ) {
        item {
            Text(text = "An unknown error occurred during the request.")
        }
    }
}


@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun GreetingScreenPreview() {
    WearAppTheme { // 预览时也应该包裹主题
        GreetingScreen("Preview Android", onShowList = {})
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun ListScreenPreview() {
    WearAppTheme { // 预览时也应该包裹主题
        ListScreen(navController = rememberSwipeDismissableNavController()) // Provide a dummy navController for preview
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun AppDetailScreenPreview() {
    WearAppTheme { // 预览时也应该包裹主题
        AppDetailScreen(appName = "Preview App", fileUrl = "https://example.com/files/sample.apk") // Provide dummy data for preview
    }
}


