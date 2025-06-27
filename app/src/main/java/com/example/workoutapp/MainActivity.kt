package com.example.workoutapp

import androidx.compose.runtime.livedata.observeAsState
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.workoutapp.ui.theme.WorkoutAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: WorkoutViewModel by viewModels()

    private lateinit var startSound: MediaPlayer
    private lateinit var endSound: MediaPlayer
    private lateinit var pauseStartSound: MediaPlayer
    private lateinit var pauseEndSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sounds laden
        startSound = MediaPlayer.create(this, R.raw.start_signal)
        endSound = MediaPlayer.create(this, R.raw.end_signal)
        pauseStartSound = MediaPlayer.create(this, R.raw.pause_start)
        pauseEndSound = MediaPlayer.create(this, R.raw.pause_end)

        setContent {
            WorkoutAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WorkoutScreen(viewModel, onStartSound = { startSound.start() }, onPauseSound = { pauseStartSound.start() })
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        startSound.release()
        endSound.release()
        pauseStartSound.release()
        pauseEndSound.release()
    }
}

@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel,
    onStartSound: () -> Unit,
    onPauseSound: () -> Unit,
) {
    val exercise by viewModel.currentExercise.observeAsState()
    val timeLeft by viewModel.timeLeft.observeAsState()
    val isExercisePhase by viewModel.isExercisePhase.observeAsState()
    val progress by viewModel.progress.observeAsState()

    LaunchedEffect(isExercisePhase) {
        if (isExercisePhase == true) {
            onStartSound()
        } else {
            onPauseSound()
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        exercise?.let {
            Text(text = it.name, style = MaterialTheme.typography.headlineMedium)
            Text(text = it.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Image(painter = painterResource(id = it.imageResId), contentDescription = it.name)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Time left: ${timeLeft ?: 0} seconds", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(24.dp))

        LinearProgressIndicator(
            progress = { (progress ?: 0) / viewModel.exercises.size.toFloat() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
