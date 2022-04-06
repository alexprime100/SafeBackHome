/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.example.safebackhome.service

import android.app.Activity
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.example.safebackhome.R

fun Activity.requestActivityTransitionUpdates() {
  val request = ActivityTransitionRequest(getActivitiesToTrack())
  val task = ActivityRecognitionClient(this).requestActivityTransitionUpdates(request,
      TransitionsReceiver.getPendingIntent(this))

  task.run {
    addOnSuccessListener {
      //Log.d("TransitionUpdate", getString(R.string.transition_update_request_success))
    }
    addOnFailureListener {
      //Log.d("TransitionUpdate", getString(R.string.transition_update_request_failed))
    }
  }
}

fun Activity.removeActivityTransitionUpdates() {
  val task = ActivityRecognitionClient(this).removeActivityTransitionUpdates(
      TransitionsReceiver.getPendingIntent(this))

  task.run {
    addOnSuccessListener {
      //Log.d("TransitionUpdate", getString(R.string.transition_update_remove_success))
    }
    addOnFailureListener {
      //Log.d("TransitionUpdate", getString(R.string.transition_update_remove_failed))
    }
  }
}

private fun getActivitiesToTrack(): List<ActivityTransition> =
    mutableListOf<ActivityTransition>()
        .apply {
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.STILL)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.STILL)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.WALKING)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.WALKING)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.RUNNING)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.RUNNING)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
              .build())
        }