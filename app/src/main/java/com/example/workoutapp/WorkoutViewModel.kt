package com.example.workoutapp

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.workoutapp.Exercise
import com.example.workoutapp.R

class WorkoutViewModel : ViewModel() {

    // Beispiel-Liste von Übungen
    val exercises = listOf(
        Exercise("Jumping Jacks", "Springe mit gespreizten Beinen und Händen über dem Kopf.", R.drawable.jumping_jacks, 10),
        Exercise("Push Ups", "Mache Liegestütze mit geradem Rücken.", R.drawable.push_ups, 10),
        Exercise("Squats", "Beuge die Knie, halte den Rücken gerade.", R.drawable.squats, 10)
    )

    private val pauseDurationSec = 5

    private var currentIndex = 0
    private var isInExercisePhase = true

    private var timer: CountDownTimer? = null

    private val _currentExercise = MutableLiveData<Exercise>(exercises[0])
    val currentExercise: LiveData<Exercise> = _currentExercise

    private val _timeLeft = MutableLiveData<Int>(exercises[0].durationSec)
    val timeLeft: LiveData<Int> = _timeLeft

    private val _isExercisePhase = MutableLiveData<Boolean>(true)
    val isExercisePhase: LiveData<Boolean> = _isExercisePhase

    private val _progress = MutableLiveData<Int>(0)
    val progress: LiveData<Int> = _progress

    fun startWorkout() {
        startTimer()
    }

    private fun startTimer() {
        timer?.cancel()

        val duration = if (isInExercisePhase)
            exercises[currentIndex].durationSec * 1000L
        else
            pauseDurationSec * 1000L

        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                if (isInExercisePhase) {
                    // Übung beendet, Pause starten
                    isInExercisePhase = false
                    _isExercisePhase.value = false
                    _timeLeft.value = pauseDurationSec
                    startTimer()
                } else {
                    // Pause beendet, nächste Übung oder Ende
                    isInExercisePhase = true
                    _isExercisePhase.value = true
                    currentIndex++

                    if (currentIndex >= exercises.size) {
                        // Workout beendet
                        _progress.value = exercises.size
                        timer?.cancel()
                    } else {
                        _currentExercise.value = exercises[currentIndex]
                        _progress.value = currentIndex
                        _timeLeft.value = exercises[currentIndex].durationSec
                        startTimer()
                    }
                }
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}
