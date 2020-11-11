package com.app.takenote.ui.bottomsheet


import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.app.takenote.R
import com.app.takenote.extensions.isEmptyOrIsBlank
import com.app.takenote.utility.*
import com.app.takenote.viewmodels.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.update_username_bottom_sheet.*
import kotlinx.android.synthetic.main.update_username_bottom_sheet.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class UpdateNameBottomSheet : BottomSheetDialogFragment() {
    private val profileViewModel: ProfileViewModel by sharedViewModel()
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
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        dialog?.window?.statusBarColor = Color.TRANSPARENT
        var currentUsername = ""
        var primaryId = ""
        arguments?.apply {
            currentUsername = getString(CURRENT_USER_NAME, "")
            primaryId = getString(PRIMARY_ID, "")
        }
        view.apply {
            updatedName.text = Editable.Factory.getInstance().newEditable(currentUsername)
            cancel.setOnClickListener {
                activity?.hideKeyboard(view.windowToken)
                dismiss()
            }
            save.setOnClickListener {
                val updatedName = updatedName.text.toString()
                progress.visibility = View.VISIBLE
                hideViews()
                if (currentUsername != updatedName && !updatedName.isEmptyOrIsBlank()) {
                    profileViewModel.updateName(primaryId, updatedName)
                } else {
                    dismiss()
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updatedName.requestFocus()
        updatedName.openKeyboard()
    }

//    private fun openKeyboard() {
//        val inputMethodManager: InputMethodManager =
//            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.showSoftInput(updatedName, InputMethodManager.SHOW_IMPLICIT)
//    }


    private fun hideViews() {
        cancel.visibility = View.GONE
        save.visibility = View.GONE
    }

    override fun onCancel(dialog: DialogInterface) {
        activity?.hideKeyboard(view?.windowToken)
        super.onCancel(dialog)
    }

}