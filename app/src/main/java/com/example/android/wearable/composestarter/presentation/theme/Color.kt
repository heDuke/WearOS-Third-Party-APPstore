/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.wearable.composestarter.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors

internal val wearColorPalette: Colors = Colors(
    primary = Color(0xFF87CEEB), // 天蓝色
    primaryVariant = Color(0xFF5DADE2), // 稍深的天蓝色
    secondary = Color(0xFF87CEEB), // 可以保持和 primary 一致或选择其他颜色
    secondaryVariant = Color(0xFF5DADE2), // 可以保持和 primaryVariant 一致
    error = Color(0xFFF28B82),
    onPrimary = Color.Black, // 天蓝色背景上的文字颜色
    onSecondary = Color.Black, // Secondary 颜色背景上的文字颜色
    onError = Color.Black, // Error 颜色背景上的文字颜色
    surface = Color.Black, // 表面颜色，通常是背景色
    onSurface = Color.White, // 表面颜色上的文字颜色
    onBackground = Color.White, // 背景颜色上的文字颜色
    background = Color.Black, // 背景颜色
)
