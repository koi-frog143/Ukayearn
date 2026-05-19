package com.citu.ukayearn.ui.screens.profile

import android.app.Dialog
import android.net.Uri
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import android.widget.EditText
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileBottomSheetFragment : BottomSheetDialogFragment() {
    private var profileSheetView: View? = null
    private var editProfileImageView: ImageView? = null
    private var editProfileImageUri: String? = null
    private val profileImagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        editProfileImageUri = uri.toString()
        editProfileImageView?.apply {
            imageTintList = null
            setPadding(0, 0, 0, 0)
            setImageURI(uri)
        }
    }

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
        profileSheetView = view

        bindProfileHeader(view)
        configureRow(view, R.id.rowEditProfile, getString(R.string.edit_profile), R.drawable.ic_edit_profile_24)
            .setOnClickListener {
                showEditProfileDialog()
            }
        configureRow(view, R.id.rowDeliveryAddresses, getString(R.string.delivery_addresses), R.drawable.ic_location_24)
            .setOnClickListener {
                showDeliveryAddressDialog()
            }

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
        bindProfilePhoto(view.findViewById(R.id.ivProfilePhoto), Database.currentProfileImageUri())

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

    private fun bindProfilePhoto(imageView: ImageView, imageUri: String?) {
        if (imageUri.isNullOrBlank()) {
            imageView.setImageResource(R.drawable.ic_profile_24)
            imageView.setPadding(0, 0, 0, 0)
            imageView.imageTintList = ColorStateList.valueOf(requireContext().getColor(R.color.secondary_blue))
        } else {
            imageView.imageTintList = null
            imageView.setPadding(0, 0, 0, 0)
            imageView.setImageURI(Uri.parse(imageUri))
        }
    }

    private fun showEditProfileDialog() {
        val user = Database.currentUser() ?: return
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val nameInput = dialogView.findViewById<EditText>(R.id.etEditName)
        val usernameInput = dialogView.findViewById<EditText>(R.id.etEditUsername)
        val passwordInput = dialogView.findViewById<EditText>(R.id.etEditPassword)
        val photo = dialogView.findViewById<ImageView>(R.id.ivEditProfilePhoto)

        editProfileImageUri = user.profileImageUri
        editProfileImageView = photo
        nameInput.setText(user.name)
        usernameInput.setText(user.username)
        passwordInput.setText(user.pass)
        bindProfilePhoto(photo, editProfileImageUri)

        dialogView.findViewById<View>(R.id.cardEditProfilePhoto).setOnClickListener {
            profileImagePicker.launch("image/*")
        }
        dialogView.findViewById<AppCompatButton>(R.id.btnEditCancel).setOnClickListener {
            editProfileImageView = null
            dialog.dismiss()
        }
        dialogView.findViewById<AppCompatButton>(R.id.btnEditSave).setOnClickListener {
            val name = nameInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (name.isBlank() || username.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), R.string.all_fields_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updated = Database.updateCurrentProfile(name, username, password, editProfileImageUri)
            if (!updated) {
                Toast.makeText(requireContext(), R.string.profile_username_taken, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            profileSheetView?.let(::bindProfileHeader)
            Toast.makeText(requireContext(), R.string.profile_updated, Toast.LENGTH_SHORT).show()
            editProfileImageView = null
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            editProfileImageView = null
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun showDeliveryAddressDialog() {
        val user = Database.currentUser() ?: return
        val dialogView = layoutInflater.inflate(R.layout.dialog_delivery_address, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val addressInput = dialogView.findViewById<EditText>(R.id.etDialogDeliveryAddress)
        val phoneInput = dialogView.findViewById<EditText>(R.id.etDialogPhoneNumber)
        val landmarkInput = dialogView.findViewById<EditText>(R.id.etDialogLandmark)

        addressInput.setText(user.deliveryAddress)
        phoneInput.setText(user.phoneNumber)
        landmarkInput.setText(user.landmark)

        dialogView.findViewById<View>(R.id.btnDeliveryCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.btnDeliverySave).setOnClickListener {
            val address = addressInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val landmark = landmarkInput.text.toString().trim()
            if (address.isBlank() || phone.isBlank() || landmark.isBlank()) {
                Toast.makeText(requireContext(), R.string.delivery_details_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Database.saveCurrentDeliveryDetails(address, phone, landmark)
            Toast.makeText(requireContext(), R.string.delivery_address_saved, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
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
