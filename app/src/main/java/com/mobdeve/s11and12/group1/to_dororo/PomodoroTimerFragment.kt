package com.mobdeve.s11and12.group1.to_dororo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import java.util.concurrent.TimeUnit

class PomodoroTimerFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var timerTextView: TextView
    private lateinit var startButton: ImageButton
    private lateinit var restartButton: ImageButton
    private lateinit var markDoneButton: Button
    private lateinit var shortButton: Button
    private lateinit var longButton: Button
    private lateinit var pomodoroButton: Button
    private lateinit var activityButton: Button

    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning: Boolean = false
    private var timeLeftInMillis: Long = START_TIME_IN_MILLIS
    private var pomodoroCount: Int = 0
    private var noteId: String? = null
    private var noteTitle: String? = null

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

        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        timerTextView = view.findViewById(R.id.timer_text)
        startButton = view.findViewById(R.id.start_button)
        restartButton = view.findViewById(R.id.restart_button)
        markDoneButton = view.findViewById(R.id.mark_done_button)
        shortButton = view.findViewById(R.id.short_button)
        longButton = view.findViewById(R.id.long_button)
        pomodoroButton = view.findViewById(R.id.pomodoro_button)
        activityButton = view.findViewById(R.id.activity_button)

        noteId = arguments?.getString("NOTE_ID")
        noteTitle = arguments?.getString("NOTE_TITLE")

        if (noteId != null) {
            fetchNoteTitle()
        } else if (noteTitle != null) {
            activityButton.text = noteTitle
        }
        activityButton.setOnClickListener { showTaskSelectionDialog() }

        startButton.setOnClickListener { startPauseTimer() }
        restartButton.setOnClickListener { resetTimer() }
        markDoneButton.setOnClickListener { checkAndMarkAsDone() }
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

    private fun fetchNoteTitle() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        noteId?.let { id ->
            firestore.collection("users")
                .document(userId)
                .collection("notes")
                .document(id)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val title = document.getString("title") ?: "No Title"
                        activityButton.text = title
                    } else {
                        activityButton.text = "No Title"
                    }
                }
                .addOnFailureListener { e ->
                    activityButton.text = "Error fetching title"
                }
        }
    }

    private fun showTaskSelectionDialog() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .get()
            .addOnSuccessListener { result ->
                val titles = result.map { it.getString("title") ?: "No Title" }

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Select a Task")
                builder.setItems(titles.toTypedArray()) { dialog, which ->
                    val selectedTitle = titles[which]
                    activityButton.text = selectedTitle
                }
                builder.show()
            }
            .addOnFailureListener { e ->
                // Handle failure, maybe show a toast or log the error
            }
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

    private fun checkAndMarkAsDone() {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val selectedTitle = activityButton.text.toString()

        if (selectedTitle == "No Title") {
            Toast.makeText(requireContext(), "Please select a task first", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .whereEqualTo("title", selectedTitle)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val isCompleted = document.getBoolean("isCompleted") ?: false
                    if (!isCompleted) {
                        val noteId = document.id
                        val totalTimeStr = document.getString("totalTime") ?: "0"
                        val totalTime = totalTimeStr.toLongOrNull() ?: 0L
                        updateNoteWithTotalTime(noteId, totalTime)
                    } else {
                        Toast.makeText(requireContext(), "This task is already completed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching task", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateNoteWithTotalTime(noteId: String, currentTotalTimeMillis: Long) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val pomodoroDurationMillis = START_TIME_IN_MILLIS - timeLeftInMillis
        val newTotalTimeMillis =  pomodoroDurationMillis

        // Convert milliseconds to minutes and seconds
        val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(newTotalTimeMillis)
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(newTotalTimeMillis) - TimeUnit.MINUTES.toSeconds(totalMinutes)

        // Format the total time as "Xm Ys"
        val formattedTotalTime = String.format(Locale.getDefault(), "%dm %ds", totalMinutes, totalSeconds)

        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .document(noteId)
            .update("totalTime", formattedTotalTime)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Total time updated", Toast.LENGTH_SHORT).show()
                markNoteAsCompleted(noteId, activityButton.text.toString()) // Pass noteId and title
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error updating total time", Toast.LENGTH_SHORT).show()
            }
    }

    private fun markNoteAsCompleted(noteId: String, noteTitle: String) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra("NOTE_ID", noteId)
            putExtra("taskTitle", noteTitle) // Pass the note title
        }
        startActivity(intent)
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






