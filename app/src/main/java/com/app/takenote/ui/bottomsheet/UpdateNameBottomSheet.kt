package com.app.takenote.ui.bottomsheet

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import com.app.takenote.R
import com.app.takenote.utility.CURRENT_USER_NAME
import com.app.takenote.utility.keyBoardVisibility
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.update_username_bottom_sheet.view.*

class UpdateNameBottomSheet : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.update_username_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUsername = arguments?.getString(CURRENT_USER_NAME, "")
        view.apply {
            updatedName.text = Editable.Factory.getInstance().newEditable(currentUsername)
            context.keyBoardVisibility(InputMethodManager.SHOW_FORCED)
            cancel.setOnClickListener {
                context.keyBoardVisibility(InputMethodManager.RESULT_HIDDEN)
                dismiss()
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        view?.context?.keyBoardVisibility(InputMethodManager.RESULT_HIDDEN)
        super.onCancel(dialog)
    }
}