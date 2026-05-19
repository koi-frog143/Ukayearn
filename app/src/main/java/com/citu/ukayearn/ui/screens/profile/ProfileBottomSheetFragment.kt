package com.citu.ukayearn.ui.screens.profile

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileBottomSheetFragment : BottomSheetDialogFragment() {

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
    ): View = inflater.inflate(R.layout.fragment_profile_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindProfileHeader(view)
        bindComingSoon(view, R.id.rowEditProfile, getString(R.string.edit_profile), R.drawable.ic_edit_profile_24)
        bindComingSoon(view, R.id.rowPaymentMethods, getString(R.string.payment_methods), R.drawable.ic_payment_24)
        bindComingSoon(view, R.id.rowDeliveryAddresses, getString(R.string.delivery_addresses), R.drawable.ic_location_24)

        val logoutRow = configureRow(
            view,
            R.id.rowLogout,
            getString(R.string.logout),
            R.drawable.ic_logout_24,
            R.color.error
        )
        logoutRow.setOnClickListener {
            Database.currentUsername = ""
            dismiss()
            val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.nav_home, true)
                .build()
            navController.navigate(R.id.nav_login, null, navOptions)
        }
    }

    private fun bindProfileHeader(view: View) {
        val isSeller = Database.isCurrentUserSeller()
        view.findViewById<TextView>(R.id.tvProfileName).text = Database.currentDisplayName()
        view.findViewById<TextView>(R.id.tvProfileEmail).text = Database.currentEmail()
        view.findViewById<TextView>(R.id.tvProfileBadge).text = Database.currentProfileBadge()

        view.findViewById<TextView>(R.id.tvProfileStatOneValue).text = if (isSeller) {
            Database.products.count { it.seller == Database.currentSellerName() }.toString()
        } else {
            "2"
        }
        view.findViewById<TextView>(R.id.tvProfileStatOneLabel).text = if (isSeller) {
            getString(R.string.profile_listings)
        } else {
            getString(R.string.to_ship)
        }
        view.findViewById<TextView>(R.id.tvProfileStatTwoValue).text = if (isSeller) {
            Database.haggleOffersForCurrentSeller().count {
                it.status == Database.HaggleStatus.PENDING
            }.toString()
        } else {
            "1"
        }
        view.findViewById<TextView>(R.id.tvProfileStatTwoLabel).text = if (isSeller) {
            getString(R.string.profile_pending)
        } else {
            getString(R.string.to_receive)
        }
        view.findViewById<TextView>(R.id.tvProfileStatThreeValue).text = if (isSeller) {
            Database.haggleOffersForCurrentSeller().count {
                it.status == Database.HaggleStatus.APPROVED
            }.toString()
        } else {
            "8"
        }
        view.findViewById<TextView>(R.id.tvProfileStatThreeLabel).text = if (isSeller) {
            getString(R.string.profile_approved)
        } else {
            getString(R.string.completed_orders)
        }
    }

    private fun bindComingSoon(view: View, rowId: Int, label: String, iconRes: Int) {
        configureRow(view, rowId, label, iconRes).setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.profile_action_coming_soon, label),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun configureRow(
        view: View,
        rowId: Int,
        label: String,
        iconRes: Int,
        tintRes: Int = R.color.secondary_blue
    ): View {
        return view.findViewById<View>(rowId).apply {
            findViewById<TextView>(R.id.tvProfileSettingTitle).text = label
            findViewById<ImageView>(R.id.ivProfileSettingIcon).apply {
                setImageResource(iconRes)
                imageTintList = ColorStateList.valueOf(requireContext().getColor(tintRes))
            }
        }
    }

    companion object {
        const val TAG = "ProfileBottomSheetFragment"
    }
}
