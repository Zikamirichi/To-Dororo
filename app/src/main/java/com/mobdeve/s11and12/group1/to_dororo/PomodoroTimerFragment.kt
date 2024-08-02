package com.mobdeve.s11and12.group1.to_dororo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
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
    private lateinit var heartCountTextView: TextView

    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning: Boolean = false
    private var timeLeftInMillis: Long = START_TIME_IN_MILLIS
    private var pomodoroCount: Int = 0
    private var noteId: String? = null
    private var noteTitle: String? = null

    private var isShortButtonPressed: Boolean = false
    private var isLongButtonPressed: Boolean = false
    private var isPomodoroButtonPressed: Boolean = false

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
        heartCountTextView = view.findViewById(R.id.heart_count)

        noteId = arguments?.getString("NOTE_ID")
        noteTitle = arguments?.getString("NOTE_TITLE")

        fetchNoteTitle()
        if (noteId != null) {
            fetchNoteTitle()
        } else if (noteTitle != null) {
            activityButton.text = noteTitle
        }
        activityButton.setOnClickListener { showTaskSelectionDialog() }

            startButton.setOnClickListener {
                val selectedTitle = activityButton.text.toString()
                if (selectedTitle == "Select Task") {
                    Toast.makeText(requireContext(), "Please select a task first", Toast.LENGTH_SHORT).show()
                } else if (selectedTitle != "Select Task"){
                    startPauseTimer()
                }
            }
            restartButton.setOnClickListener {
                val selectedTitle = activityButton.text.toString()
                if (selectedTitle == "Select Task") {
                Toast.makeText(requireContext(), "Please select a task first", Toast.LENGTH_SHORT).show()
                } else if (selectedTitle != "Select Task") {
                resetTimer()
                }
            }


            markDoneButton.setOnClickListener { checkAndMarkAsDone() }
            shortButton.setOnClickListener {
                val selectedTitle = activityButton.text.toString()
                if (selectedTitle == "Select Task") {
                    Toast.makeText(requireContext(), "Please select a task first", Toast.LENGTH_SHORT).show()
                } else if (selectedTitle != "Select Task") {
                    isShortButtonPressed = true
                    isLongButtonPressed = false
                    isPomodoroButtonPressed = false
                    startShortBreak()
                }
            }
            longButton.setOnClickListener {
                val selectedTitle = activityButton.text.toString()
                if (selectedTitle == "Select Task") {
                    Toast.makeText(requireContext(), "Please select a task first", Toast.LENGTH_SHORT).show()
                } else if (selectedTitle != "Select Task") {
                    isShortButtonPressed = false
                    isLongButtonPressed = true
                    isPomodoroButtonPressed = false
                    startLongBreak()
                }
            }
            pomodoroButton.setOnClickListener {
                val selectedTitle = activityButton.text.toString()
                if (selectedTitle == "Select Task") {
                    Toast.makeText(requireContext(), "Please select a task first", Toast.LENGTH_SHORT).show()
                } else if (selectedTitle != "Select Task") {
                    isShortButtonPressed = false
                    isLongButtonPressed = false
                    isPomodoroButtonPressed = true
                    startPomodoro()
                }
            }

        fetchHeartCount()
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

        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .whereEqualTo("isSelectedForPomodoro", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.firstOrNull()
                    document?.let {
                        val title = it.getString("title") ?: "No Title"
                        activityButton.text = title

                        // Reset the isSelectedForPomodoro field to false
                        it.reference.update("isSelectedForPomodoro", false)
                            .addOnSuccessListener {
                                Log.d("PomodoroTimerFragment", "isSelectedForPomodoro reset successfully.")
                            }
                            .addOnFailureListener { e ->
                                Log.e("PomodoroTimerFragment", "Error resetting isSelectedForPomodoro: ${e.message}")
                            }
                    } ?: run {
                        activityButton.text = "Select Task"
                    }
                } else {
                    activityButton.text = "Select Task"
                }
            }
            .addOnFailureListener { e ->
                activityButton.text = "Error fetching title"
            }
    }

    private fun showTaskSelectionDialog() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .whereEqualTo("isCompleted", false)
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
                Toast.makeText(requireContext(), "Error fetching Title", Toast.LENGTH_SHORT).show()
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

        if (selectedTitle == "Select Task") {
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

        val newTotalTimeMillis: Long

        // Check which button was pressed and set newTotalTimeMillis accordingly
        newTotalTimeMillis = when {
            isPomodoroButtonPressed -> START_TIME_IN_MILLIS - timeLeftInMillis
            isShortButtonPressed -> SHORT_BREAK_TIME_IN_MILLIS - timeLeftInMillis
            isLongButtonPressed -> LONG_BREAK_TIME_IN_MILLIS - timeLeftInMillis
            else -> 0 // Default case if no button was pressed
        }


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
                markNoteAsCompleted() // Pass noteId and title
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error updating total time", Toast.LENGTH_SHORT).show()
            }
    }


    private fun markNoteAsCompleted() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Log.e("PomodoroTimerFragment", "User is not logged in.")
            return
        }
        val selectedTitle = activityButton.text.toString()

        if (selectedTitle == "No Title") {
            Toast.makeText(requireContext(), "Please select a task first", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid

        firestore.collection("users")
            .document(userId)
            .collection("notes")
            .whereEqualTo("title", selectedTitle)
            .get()
            .addOnSuccessListener { result ->
                if (result != null && !result.isEmpty) {
                    val document = result.documents[0]
                    document.reference.update("isCompleted", true)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Task marked as completed!", Toast.LENGTH_SHORT).show()
                            addHeartsToUser(userId, 100)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("PomodoroTimerFragment", "Error marking task as completed: ${exception.message}")
                            Toast.makeText(requireContext(), "Error marking task as completed. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.e("PomodoroTimerFragment", "No matching document found.")
                    Toast.makeText(requireContext(), "Error: No matching document found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("PomodoroTimerFragment", "Error fetching document: ${exception.message}")
                Toast.makeText(requireContext(), "Error fetching document. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addHeartsToUser(userId: String, heartsToAdd: Int) {
        val userRef = firestore.collection("users").document(userId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentHearts = snapshot.getLong("hearts") ?: 0
            val newHearts = currentHearts + heartsToAdd
            transaction.update(userRef, "hearts", newHearts)
        }.addOnSuccessListener {
            Toast.makeText(requireContext(), "100 hearts added to your account!", Toast.LENGTH_SHORT).show()
            // Navigate back to the main menu
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity?.finish()
        }.addOnFailureListener { exception ->
            Log.e("PomodoroTimerFragment", "Error adding hearts: ${exception.message}")
            Toast.makeText(requireContext(), "Error adding hearts. Please try again.", Toast.LENGTH_SHORT).show()
        }
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

    private fun fetchHeartCount() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            return
        }

        val userId = currentUser.uid

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val heartCount = document.getLong("hearts") ?: 0
                heartCountTextView.text = heartCount.toString()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                exception.printStackTrace()
            }
    }
}






