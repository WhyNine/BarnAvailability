/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.barnavailability

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.barnavailability.databinding.FragmentOverviewBinding
import java.util.*

/**
 * This fragment shows the calendar for the week.
 */
class OverviewFragment : Fragment() {
    lateinit var dateEdt: EditText

    private val viewModel: OverviewViewModel by viewModels()

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOverviewBinding.inflate(inflater)

        binding.buttonPrev.setOnClickListener{updateToPrevious() }
        binding.buttonNext.setOnClickListener{updateToNext() }

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

//        Log.d("fragment", "onCreate called")

        dateEdt = binding.wbdate
        dateEdt.setOnClickListener {

            // on below line we are getting
            // the instance of our calendar.
            val c = Calendar.getInstance()

            // on below line we are getting
            // our day, month and year.
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // on below line we are creating a
            // variable for date picker dialog.
            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                requireContext(),
                { _, yearOfAD, monthOfYear, dayOfMonth ->           // First parameter should be view
                    // on below line we are setting date to our edit text.
//                    val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
//                    Log.d("datepicker", dat)
                    viewModel.fetchEventsForDate(dayOfMonth, monthOfYear, yearOfAD)
                },
                // on below line we are passing year, month
                // and day for the selected date in our date picker.
                year, month, day
            )
            // at last we are calling show to display our date picker dialog.
            datePickerDialog.show()
        }

        return binding.root
    }

    private fun updateToPrevious() {
        Log.d("click", "clicked previous")
        viewModel.fetchEventsForPreviousWeek()
    }

    private fun updateToNext() {
        Log.d("click", "clicked next")
        viewModel.fetchEventsForNextWeek()
    }
}
