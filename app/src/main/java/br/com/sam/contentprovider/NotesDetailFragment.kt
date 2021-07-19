package br.com.sam.contentprovider

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import br.com.sam.contentprovider.database.NotesDatabaseHelper.Companion.DESCRIPTION_NOTES
import br.com.sam.contentprovider.database.NotesDatabaseHelper.Companion.TITLE_NOTES
import br.com.sam.contentprovider.database.NotesProvider.Companion.URI_NOTES

class NotesDetailFragment: DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var noteEditTitle: EditText
    private lateinit var noteEditDescription: EditText
    private var id: Long = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity?.layoutInflater?.inflate(R.layout.note_detail, null)

        initVariables(view)

        return AlertDialog.Builder(activity as Activity)
            .setTitle(if (newNote()) "Nova mensagem" else "Editar mensagem")
            .setView(view)
            .setPositiveButton("Salvar", this)
            .setNegativeButton("Cancelar", this)
            .create()
    }

    private fun newNote(): Boolean {
        var note = true

        if (arguments != null && arguments?.getLong(EXTRA_ID) != 0L) {
            id = arguments?.getLong(EXTRA_ID) as Long
            val uri = Uri.withAppendedPath(URI_NOTES, id.toString())
            val cursor = activity?.contentResolver?.query(
                uri,
                null,
                null,
                null,
                null
            )

            if (cursor?.moveToNext() as Boolean) {
                note = false
                noteEditTitle.setText(cursor.getString(cursor.getColumnIndex(TITLE_NOTES)))
                noteEditDescription.setText(cursor.getString(cursor.getColumnIndex(DESCRIPTION_NOTES)))
            }
            cursor.close()
        }

        return note
    }

    private fun initVariables(view: View?) {
        noteEditTitle = view?.findViewById(R.id.ed_note_title) as EditText
        noteEditDescription = view.findViewById(R.id.ed_note_description) as EditText
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
        val values = ContentValues()
        values.put(TITLE_NOTES, noteEditTitle.text.toString())
        values.put(DESCRIPTION_NOTES, noteEditDescription.text.toString())

        if (id != 0L) {
            val uri = Uri.withAppendedPath(URI_NOTES, id.toString())
            context?.contentResolver?.update(uri, values, null, null)
        } else {
            context?.contentResolver?.insert(URI_NOTES, values)
        }
    }

    companion object {
        private const val EXTRA_ID = "id"
        fun newInstance(id: Long): NotesDetailFragment {
            val bundle = Bundle()
            val notesFragment = NotesDetailFragment()

            bundle.putLong(EXTRA_ID, id)
            notesFragment.arguments = bundle

            return  notesFragment
        }
    }
}