package com.mobdeve.s11and12.group1.to_dororo

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.Locale
import java.util.concurrent.TimeUnit

class PomodoroTimerFragment : Fragment() {

    private lateinit var timerTextView: TextView
    private lateinit var startButton: ImageButton
    private lateinit var restartButton: ImageButton
    private lateinit var markDoneButton: Button
    private lateinit var shortButton: Button
    private lateinit var longButton: Button
    private lateinit var pomodoroButton: Button

    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning: Boolean = false
    private var timeLeftInMillis: Long = START_TIME_IN_MILLIS
    private var pomodoroCount: Int = 0

    companion object {
        const val START_TIME_IN_MILLIS: Long = 1500000 // 25 minutes
        const val SHORT_BREAK_TIME_IN_MILLIS: Long = 300000 // 5 minutes
        const val LONG_BREAK_TIME_IN_MILLIS: Long = 900000 // 15 minutes
        const val POMODOROS_BEFORE_LONG_BREAK: Int = 4
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pomodoro_timer, container, false)

        timerTextView = view.findViewById(R.id.timer_text)
        startButton = view.findViewById(R.id.start_button)
        restartButton = view.findViewById(R.id.restart_button)
        markDoneButton = view.findViewById(R.id.mark_done_button)
        shortButton = view.findViewById(R.id.short_button)
        longButton = view.findViewById(R.id.long_button)
        pomodoroButton = view.findViewById(R.id.pomodoro_button)

        startButton.setOnClickListener { startPauseTimer() }
        restartButton.setOnClickListener { resetTimer() }
        markDoneButton.setOnClickListener { markAsDone() }
        shortButton.setOnClickListener { startShortBreak() }
        longButton.setOnClickListener { startLongBreak() }
        pomodoroButton.setOnClickListener { startPomodoro() }

        updateCountDownText()

        // For History Button
        val historyButton = view.findViewById<ImageButton>(R.id.history_icon)
        historyButton.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }

        val helpView = view.findViewById<ImageButton>(R.id.help_icon)
        helpView.setOnClickListener {
            startActivity(Intent(requireContext(), HelpActivity::class.java))
        }

        return view
    }

    private fun startPauseTimer() {
        if (isTimerRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                isTimerRunning = false
                startButton.setImageResource(R.drawable.start)
                handleTimerFinish()
            }
        }.start()

        isTimerRunning = true
        startButton.setImageResource(R.drawable.pause)
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        startButton.setImageResource(R.drawable.start)
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        timeLeftInMillis = START_TIME_IN_MILLIS
        updateCountDownText()
        startButton.setImageResource(R.drawable.start)
        isTimerRunning = false
    }

    private fun startPomodoro() {
        resetTimer()
        timeLeftInMillis = START_TIME_IN_MILLIS
        updateCountDownText()
        startTimer()
    }

    private fun startShortBreak() {
        resetTimer()
        timeLeftInMillis = SHORT_BREAK_TIME_IN_MILLIS
        updateCountDownText()
        startTimer()
    }

    private fun startLongBreak() {
        resetTimer()
        timeLeftInMillis = LONG_BREAK_TIME_IN_MILLIS
        updateCountDownText()
        startTimer()
    }

    private fun markAsDone() {
      
    }

    private fun handleTimerFinish() {
        if (timeLeftInMillis == START_TIME_IN_MILLIS) {
            pomodoroCount++
            if (pomodoroCount % POMODOROS_BEFORE_LONG_BREAK == 0) {
                startLongBreak()
            } else {
                startShortBreak()
            }
        } else {
            startPomodoro()
        }
    }

    private fun updateCountDownText() {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis) - TimeUnit.MINUTES.toSeconds(minutes)

        val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        timerTextView.text = timeFormatted
    }
}


