package com.lenecoproekt.minesweeper.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.lenecoproekt.minesweeper.R
import com.lenecoproekt.minesweeper.databinding.FragmentRecorddialogBinding


class RecordDialogFragment : DialogFragment() {
    private lateinit var recordName: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recorddialog, null)
        recordName = view.findViewById(R.id.recordName)
        view.findViewById<Button>(R.id.saveRecordButton).setOnClickListener(listener)
        view.findViewById<Button>(R.id.cancelButton).setOnClickListener { dismiss()}
        return view
    }

    private val listener = View.OnClickListener {
        dismiss()
        val name = recordName.text.toString()
        (activity as GameActivity).onDialogResult(name)
    }

}