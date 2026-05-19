package com.citu.ukayearn.ui.screens.chat

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.citu.ukayearn.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChatInfoBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            setOnShowListener { dialog ->
                val bottomSheet = (dialog as BottomSheetDialog)
                    .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.background = ColorDrawable(Color.TRANSPARENT)
                bottomSheet?.let {
                    BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_chat_info_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storeName = requireArguments().getString(ARG_STORE_NAME).orEmpty()
        view.findViewById<TextView>(R.id.tvInfoInitial).text = storeName.firstOrNull()?.toString().orEmpty()
        view.findViewById<TextView>(R.id.tvInfoStoreName).text = storeName

        view.findViewById<View>(R.id.rowMuteChat).setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.chat_muted, storeName),
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }

        view.findViewById<View>(R.id.rowReportStore).setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.report_submitted, storeName),
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }
    }

    companion object {
        const val TAG = "ChatInfoBottomSheetFragment"
        private const val ARG_STORE_NAME = "storeName"

        fun newInstance(storeName: String): ChatInfoBottomSheetFragment {
            return ChatInfoBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_STORE_NAME, storeName)
                }
            }
        }
    }
}
