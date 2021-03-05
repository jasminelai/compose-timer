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
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ButtonDefaults.elevation
import androidx.compose.material.ButtonDefaults.textButtonColors
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.darkBrown
import com.example.androiddevchallenge.ui.theme.lightBrown
import com.example.androiddevchallenge.ui.theme.typography

private const val MINUTE_IN_SECONDS = 60L
private const val SECOND = 1L

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val model: TimerViewModel by viewModels()

        setContent {
            MyTheme {
                TimerView(model)
            }
        }
    }
}

@Composable
fun TimerView(model: TimerViewModel) {
    val time: Long by model.time.observeAsState(0L)
    val timerState: TimerState by model.timerState.observeAsState(TimerState.Cleared)

    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (timerState != TimerState.Running) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    TimeController(
                        "Minutes",
                        increment = { model.modifyTime(true, MINUTE_IN_SECONDS) },
                        decrement = { model.modifyTime(false, MINUTE_IN_SECONDS) }
                    )
                    TimeController(
                        "Seconds",
                        increment = { model.modifyTime(true, SECOND) },
                        decrement = { model.modifyTime(false, SECOND) }
                    )
                }
            }
            Clock(time, timerState == TimerState.Running)
            ButtonRow(
                timerState,
                startTimer = { model.startTimer() },
                pauseTimer = { model.pauseTimer() },
                resetTimer = { model.clearTimer() }
            )
        }
    }
}

@Composable
fun Clock(
    time: Long,
    isRunning: Boolean
) {
    Text(
        formatTime(time),
        style = typography.h1,
        color = if (isRunning) darkBrown else lightBrown
    )
}

private fun formatTime(timeInSeconds: Long): String {
    val minutes = timeInSeconds / 60
    val seconds = timeInSeconds - minutes * 60
    var formattedTime = ""
    if (minutes < 10) formattedTime += "0"
    formattedTime += "$minutes:"
    if (seconds < 10) formattedTime += "0"
    formattedTime += seconds
    return formattedTime
}

@Composable
fun TimeController(
    name: String,
    increment: () -> Unit,
    decrement: () -> Unit
) {
    Card(
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(onClick = increment) {
                Icon(
                    Icons.Filled.KeyboardArrowUp,
                    contentDescription = "increment",
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.size(4.dp))
            Text(name, modifier = Modifier.padding(horizontal = 8.dp), style = typography.h3)
            Spacer(Modifier.size(4.dp))
            IconButton(onClick = decrement) {
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = "decrement",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun ButtonRow(
    timerState: TimerState,
    startTimer: () -> Unit,
    pauseTimer: () -> Unit,
    resetTimer: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        when (timerState) {
            TimerState.Cleared -> TimerButton(ButtonType.Start, startTimer)
            TimerState.Running -> TimerButton(ButtonType.Pause, pauseTimer)
            TimerState.Paused -> {
                TimerButton(ButtonType.Resume, startTimer)
                TimerButton(ButtonType.Reset, resetTimer)
            }
        }
    }
}

@Composable
fun TimerButton(
    type: ButtonType = ButtonType.Start,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = textButtonColors(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary
        ),
        elevation = elevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Icon(
            buttonIcon(type),
            contentDescription = type.name,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            type.name,
            style = typography.button
        )
    }
}

private fun buttonIcon(type: ButtonType) = when (type) {
    ButtonType.Start, ButtonType.Resume -> Icons.Filled.PlayArrow
    ButtonType.Pause -> Icons.Filled.Pause
    ButtonType.Reset -> Icons.Filled.Replay
}

@Preview
@Composable
fun ClearedButtonPreview() {
    MyTheme {
        ButtonRow(timerState = TimerState.Cleared, {}, {}, {})
    }
}

@Preview
@Composable
fun RunningButtonPreview() {
    MyTheme {
        ButtonRow(timerState = TimerState.Running, {}, {}, {})
    }
}

@Preview
@Composable
fun PausedButtonPreview() {
    MyTheme {
        ButtonRow(timerState = TimerState.Paused, {}, {}, {})
    }
}

@Preview
@Composable
fun MinutesPreview() {
    MyTheme {
        TimeController("Minutes", {}, {})
    }
}

@Preview
@Composable
fun SecondsPreview() {
    MyTheme {
        TimeController("Seconds", {}, {})
    }
}

// @Preview("Light Theme", widthDp = 360, heightDp = 640)
// @Composable
// fun LightPreview() {
//    MyTheme {
//        TimerView()
//    }
// }
//
// @Preview("Dark Theme", widthDp = 360, heightDp = 640)
// @Composable
// fun DarkPreview() {
//    MyTheme(darkTheme = true) {
//        TimerView()
//    }
// }
