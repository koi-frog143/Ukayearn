package com.citu.ukayearn.ui.screens.chat

import android.os.Bundle
import android.net.Uri
import android.graphics.Typeface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.citu.ukayearn.MainActivity
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database
import com.citu.ukayearn.ui.util.AssetImageLoader
import java.io.File

class ChatFragment : Fragment() {
    private lateinit var inboxContainer: View
    private lateinit var conversationContainer: View
    private lateinit var composerBar: View
    private lateinit var activeInitial: TextView
    private lateinit var activeAvatar: ImageView
    private lateinit var activeSeller: TextView
    private lateinit var sellerStatus: TextView
    private lateinit var sellerMessage: TextView
    private lateinit var buyerPresetMessage: TextView
    private lateinit var buyerMessage: TextView
    private lateinit var buyerMessageTime: TextView
    private lateinit var sentMessageRow: View
    private lateinit var sentImageMessageRow: LinearLayout
    private lateinit var searchInput: EditText
    private lateinit var messageInput: EditText
    private lateinit var backCallback: OnBackPressedCallback
    private lateinit var haggleOfferCard: View
    private lateinit var sellerHaggleActions: View
    private lateinit var haggleWidgetTitle: TextView
    private lateinit var haggleWidgetBody: TextView
    private lateinit var emptyMessages: View
    private lateinit var sellerPresetMessageRow: View
    private lateinit var buyerPresetMessageRow: View
    private lateinit var sellerImageMessageRow: View
    private lateinit var sellerImageTime: View
    private var currentHaggleOffer: Database.HaggleOffer? = null
    private var currentSeller: String = ""
    private var searchQuery: String = ""
    private var pendingCameraImageUri: Uri? = null
    private val galleryPicker = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris.forEach(::sendImageMessage)
    }
    private val cameraCapture = registerForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
        val uri = pendingCameraImageUri
        if (captured && uri != null) {
            sendImageMessage(uri)
        } else {
            Toast.makeText(requireContext(), R.string.camera_unavailable, Toast.LENGTH_SHORT).show()
        }
        pendingCameraImageUri = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_chat, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inboxContainer = view.findViewById(R.id.inboxContainer)
        conversationContainer = view.findViewById(R.id.conversationContainer)
        composerBar = view.findViewById(R.id.chatComposerBar)
        activeInitial = view.findViewById(R.id.tvActiveInitial)
        activeAvatar = view.findViewById(R.id.ivActiveAvatar)
        activeSeller = view.findViewById(R.id.tvActiveSeller)
        sellerStatus = view.findViewById(R.id.tvSellerStatus)
        sellerMessage = view.findViewById(R.id.tvSellerChatMessage)
        buyerPresetMessage = view.findViewById(R.id.tvBuyerPresetMessage)
        buyerMessage = view.findViewById(R.id.tvBuyerChatMessage)
        buyerMessageTime = view.findViewById(R.id.tvBuyerChatTime)
        sentMessageRow = view.findViewById(R.id.sentMessageRow)
        sentImageMessageRow = view.findViewById(R.id.sentImageMessageRow)
        searchInput = view.findViewById(R.id.etChatSearch)
        messageInput = view.findViewById(R.id.etMessage)
        haggleOfferCard = view.findViewById(R.id.haggleOfferCard)
        sellerHaggleActions = view.findViewById(R.id.sellerHaggleActions)
        haggleWidgetTitle = view.findViewById(R.id.tvHaggleWidgetTitle)
        haggleWidgetBody = view.findViewById(R.id.tvHaggleWidgetBody)
        emptyMessages = view.findViewById(R.id.tvEmptyMessages)
        sellerPresetMessageRow = view.findViewById(R.id.sellerPresetMessageRow)
        buyerPresetMessageRow = view.findViewById(R.id.buyerPresetMessageRow)
        sellerImageMessageRow = view.findViewById(R.id.sellerImageMessageRow)
        sellerImageTime = view.findViewById(R.id.tvSellerImageTime)
        backCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                showInbox()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

        bindStoreAvatars(view)
        bindSearch(view)
        bindMessageDeletion()

        view.findViewById<View>(R.id.rowThriftKada).setOnClickListener {
            openConversation(
                seller = getString(R.string.thriftkada),
                displayName = getString(R.string.thriftkada),
                message = getString(R.string.chat_seller_message)
            )
        }
        view.findViewById<View>(R.id.rowRetroLane).setOnClickListener {
            openConversation(
                seller = getString(R.string.retro_lane),
                displayName = getString(R.string.retro_lane),
                message = getString(R.string.chat_preview_retro_lane)
            )
        }
        view.findViewById<View>(R.id.rowCebuFinds).setOnClickListener {
            openConversation(
                seller = getString(R.string.cebu_finds),
                displayName = getString(R.string.cebu_finds),
                message = getString(R.string.chat_preview_cebu_finds)
            )
        }
        view.findViewById<View>(R.id.rowUkayBoss).setOnClickListener {
            openConversation(
                seller = getString(R.string.ukay_boss),
                displayName = getString(R.string.ukay_boss),
                message = getString(R.string.chat_preview_ukay_boss)
            )
        }
        bindThreadDeletion(view, R.id.rowThriftKada, getString(R.string.thriftkada))
        bindThreadDeletion(view, R.id.rowRetroLane, getString(R.string.retro_lane))
        bindThreadDeletion(view, R.id.rowCebuFinds, getString(R.string.cebu_finds))
        bindThreadDeletion(view, R.id.rowUkayBoss, getString(R.string.ukay_boss))

        view.findViewById<View>(R.id.btnBackToInbox).setOnClickListener {
            showInbox()
        }
        view.findViewById<View>(R.id.btnChatInfo).setOnClickListener {
            ChatInfoBottomSheetFragment.newInstance(currentSeller)
                .show(parentFragmentManager, ChatInfoBottomSheetFragment.TAG)
        }
        view.findViewById<View>(R.id.btnAttachImage).setOnClickListener {
            showPictureSourceDialog()
        }
        view.findViewById<View>(R.id.btnApproveHaggle).setOnClickListener {
            currentHaggleOffer?.let { offer ->
                offer.status = Database.HaggleStatus.APPROVED
                Database.approvedHaggleVouchers[offer.product.id] = offer.offerPrice
                Database.addTextChatMessage(
                    seller = currentSeller,
                    senderUsername = Database.currentUsername,
                    body = getString(R.string.haggle_approved)
                )
                Database.markConversationUnreadForUser(offer.buyerUsername, currentSeller)
                Toast.makeText(requireContext(), R.string.haggle_approved_added_to_cart, Toast.LENGTH_SHORT).show()
                bindHaggleWidget(currentSeller)
                updateInboxPreviews(view)
            }
        }
        view.findViewById<View>(R.id.btnDeclineHaggle).setOnClickListener {
            currentHaggleOffer?.let { offer ->
                offer.status = Database.HaggleStatus.DECLINED
                Database.addTextChatMessage(
                    seller = currentSeller,
                    senderUsername = Database.currentUsername,
                    body = getString(R.string.haggle_declined)
                )
                Database.markConversationUnreadForUser(offer.buyerUsername, currentSeller)
                Toast.makeText(requireContext(), R.string.haggle_declined_message, Toast.LENGTH_SHORT).show()
                bindHaggleWidget(currentSeller)
                updateInboxPreviews(view)
            }
        }
        view.findViewById<View>(R.id.btnSendMessage).setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                addSentTextMessage(message)
                Database.markSellerConversationUnread(currentSeller)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.message_sent, activeSeller.text),
                    Toast.LENGTH_SHORT
                ).show()
                messageInput.text.clear()
            }
        }

        showInbox()
        bindInboxForCurrentAccount(view)
        updateInboxPreviews(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            bindInboxForCurrentAccount(it)
            updateInboxPreviews(it)
        }
    }

    private fun openConversation(seller: String, displayName: String, message: String) {
        Database.markConversationReadForCurrentUser(seller)
        (activity as? MainActivity)?.refreshChatBadge()
        inboxContainer.visibility = View.GONE
        conversationContainer.visibility = View.VISIBLE
        composerBar.visibility = View.VISIBLE
        setBottomNavVisible(false)
        backCallback.isEnabled = true
        currentSeller = seller
        activeInitial.text = displayName.first().toString()
        activeSeller.text = displayName
        sellerStatus.text = getString(R.string.seller_online)
        sellerMessage.text = message
        sellerMessage.visibility = View.GONE
        buyerPresetMessage.visibility = View.GONE
        sentMessageRow.visibility = View.GONE
        buyerMessage.visibility = View.GONE
        buyerMessageTime.visibility = View.GONE
        sentImageMessageRow.removeAllViews()
        sentImageMessageRow.visibility = View.GONE
        bindActiveAvatar(seller)
        bindHaggleWidget(seller)
        bindPresetConversationVisibility()
        renderConversationHistory()
        view?.let { updateInboxPreviews(it) }
        messageInput.text.clear()
    }

    private fun showInbox() {
        inboxContainer.visibility = View.VISIBLE
        conversationContainer.visibility = View.GONE
        composerBar.visibility = View.GONE
        setBottomNavVisible(true)
        if (::backCallback.isInitialized) {
            backCallback.isEnabled = false
        }
    }

    override fun onDestroyView() {
        setBottomNavVisible(true)
        super.onDestroyView()
    }

    private fun setBottomNavVisible(isVisible: Boolean) {
        activity?.findViewById<View>(R.id.bottom_nav_container)?.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }

    private fun bindHaggleWidget(seller: String) {
        val offer = Database.latestHaggleForSeller(seller)
        currentHaggleOffer = offer

        if (offer == null) {
            haggleOfferCard.visibility = View.GONE
            return
        }

        haggleOfferCard.visibility = View.VISIBLE
        haggleWidgetTitle.text = when (offer.status) {
            Database.HaggleStatus.PENDING -> getString(R.string.haggle_sent)
            Database.HaggleStatus.APPROVED -> getString(R.string.haggle_approved)
            Database.HaggleStatus.DECLINED -> getString(R.string.haggle_declined)
        }
        haggleWidgetBody.text = getString(
            R.string.haggle_widget_body_format,
            offer.product.name,
            getString(R.string.price_format, offer.offerPrice),
            haggleStatusText(offer.status)
        )
        sellerHaggleActions.visibility =
            if (offer.status == Database.HaggleStatus.PENDING && Database.isCurrentUserSellerFor(seller)) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun haggleStatusText(status: Database.HaggleStatus): String {
        return when (status) {
            Database.HaggleStatus.PENDING -> getString(R.string.waiting_for_seller_response)
            Database.HaggleStatus.APPROVED -> getString(R.string.haggle_voucher_ready)
            Database.HaggleStatus.DECLINED -> getString(R.string.haggle_declined_try_again)
        }
    }

    private fun updateInboxPreviews(view: View) {
        if (Database.isCurrentUserSeller()) {
            bindSellerInbox(view)
            return
        }
        bindPreview(view, R.id.tvPreviewThriftKada, getString(R.string.thriftkada), getString(R.string.chat_preview_thriftkada))
        bindPreview(view, R.id.tvPreviewRetroLane, getString(R.string.retro_lane), getString(R.string.chat_preview_retro_lane))
        bindPreview(view, R.id.tvPreviewCebuFinds, getString(R.string.cebu_finds), getString(R.string.chat_preview_cebu_finds))
        bindPreview(view, R.id.tvPreviewUkayBoss, getString(R.string.ukay_boss), getString(R.string.chat_preview_ukay_boss))
        applySearchFilter(view)
    }

    private fun bindPreview(view: View, previewId: Int, seller: String, fallback: String) {
        val latestMessage = Database.latestChatMessageForSeller(seller)
        val offer = Database.latestHaggleForSeller(seller)
        val receivedPrompt = Database.latestReceivedPromptForSeller(seller)
        view.findViewById<TextView>(previewId).text = if (latestMessage != null) {
            if (latestMessage.type == Database.ChatMessageType.IMAGE) {
                getString(R.string.chat_image_label)
            } else {
                latestMessage.body.orEmpty()
            }
        } else if (receivedPrompt != null) {
            receivedPrompt
        } else if (offer != null) {
            getString(R.string.haggle_inbox_preview_format, haggleStatusText(offer.status))
        } else {
            fallback
        }
    }

    private fun bindInboxForCurrentAccount(view: View) {
        if (Database.isCurrentUserSeller()) {
            bindSellerInbox(view)
        } else {
            view.findViewById<View>(R.id.rowThriftKada).visibility = View.VISIBLE
            view.findViewById<View>(R.id.rowRetroLane).visibility = View.VISIBLE
            view.findViewById<View>(R.id.rowCebuFinds).visibility = View.VISIBLE
            view.findViewById<View>(R.id.rowUkayBoss).visibility = View.VISIBLE
            emptyMessages.visibility = View.GONE
            applySearchFilter(view)
        }
    }

    private fun bindSellerInbox(view: View) {
        val seller = Database.currentSellerName()
        val latestOffer = seller?.let(Database::latestHaggleForSeller)
        val latestReceivedPrompt = seller?.let(Database::latestReceivedPromptForSeller)
        val latestChatMessage = seller?.let(Database::latestChatMessageForSeller)
        val rowMap = mapOf(
            getString(R.string.thriftkada) to Triple(R.id.rowThriftKada, R.id.tvThreadThriftKada, R.id.tvPreviewThriftKada),
            getString(R.string.cebu_finds) to Triple(R.id.rowCebuFinds, R.id.tvThreadCebuFinds, R.id.tvPreviewCebuFinds),
            getString(R.string.ukay_boss) to Triple(R.id.rowUkayBoss, R.id.tvThreadUkayBoss, R.id.tvPreviewUkayBoss)
        )

        view.findViewById<View>(R.id.rowRetroLane).visibility = View.GONE
        rowMap.forEach { (rowSeller, ids) ->
            val row = view.findViewById<View>(ids.first)
            row.visibility =
                if (rowSeller == seller && (latestOffer != null || latestReceivedPrompt != null || latestChatMessage != null)) {
                    View.VISIBLE
                } else {
                    View.GONE
            }
            if (row.visibility == View.VISIBLE) {
                val buyerName = latestOffer?.buyerUsername
                    ?.let(Database::displayNameFor)
                    ?: getString(R.string.buyer)
                view.findViewById<TextView>(ids.second).text = buyerName
                view.findViewById<TextView>(ids.third).text = latestChatMessage?.let {
                    if (it.type == Database.ChatMessageType.IMAGE) getString(R.string.chat_image_label) else it.body.orEmpty()
                } ?: latestReceivedPrompt
                    ?: getString(R.string.haggle_inbox_preview_format, haggleStatusText(latestOffer!!.status))
                row.setOnClickListener {
                    openConversation(
                        seller = rowSeller,
                        displayName = buyerName,
                        message = latestChatMessage?.body ?: latestReceivedPrompt ?: getString(R.string.incoming_haggle_message)
                    )
                }
            }
        }
        emptyMessages.visibility =
            if (latestOffer == null && latestReceivedPrompt == null && latestChatMessage == null) View.VISIBLE else View.GONE
        applySearchFilter(view)
    }

    private fun bindPresetConversationVisibility() {
        val showPresetMessages = !Database.isCurrentUserSeller()
        sellerPresetMessageRow.visibility = View.GONE
        buyerPresetMessageRow.visibility = View.GONE
        sellerImageMessageRow.visibility = View.GONE
        sellerImageTime.visibility = View.GONE
        composerBar.visibility = if (showPresetMessages) View.VISIBLE else View.GONE
    }

    private fun bindStoreAvatars(view: View) {
        bindThreadAvatar(view, R.id.ivAvatarThriftKada, getString(R.string.thriftkada))
        bindThreadAvatar(view, R.id.ivAvatarCebuFinds, getString(R.string.cebu_finds))
        bindThreadAvatar(view, R.id.ivAvatarUkayBoss, getString(R.string.ukay_boss))
    }

    private fun bindThreadAvatar(view: View, imageViewId: Int, seller: String) {
        val store = Database.stores.firstOrNull { it.name == seller } ?: return
        view.findViewById<ImageView>(imageViewId).apply {
            visibility = View.VISIBLE
            AssetImageLoader.load(this, store.imageUrl)
        }
    }

    private fun bindActiveAvatar(seller: String) {
        val store = Database.stores.firstOrNull { it.name == seller }
        activeAvatar.visibility = if (store == null) View.GONE else View.VISIBLE
        if (store != null) {
            AssetImageLoader.load(activeAvatar, store.imageUrl)
        }
    }

    private fun bindSearch(view: View) {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString().orEmpty().trim()
                applySearchFilter(view)
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun applySearchFilter(view: View) {
        if (!::searchInput.isInitialized) return

        val rows = listOf(
            ThreadRow(
                getString(R.string.thriftkada),
                R.id.rowThriftKada,
                R.id.tvThreadThriftKada,
                R.id.tvPreviewThriftKada,
                R.id.tvUnreadThriftKada
            ),
            ThreadRow(
                getString(R.string.retro_lane),
                R.id.rowRetroLane,
                R.id.tvThreadRetroLane,
                R.id.tvPreviewRetroLane,
                R.id.tvUnreadRetroLane
            ),
            ThreadRow(
                getString(R.string.cebu_finds),
                R.id.rowCebuFinds,
                R.id.tvThreadCebuFinds,
                R.id.tvPreviewCebuFinds,
                R.id.tvUnreadCebuFinds
            ),
            ThreadRow(
                getString(R.string.ukay_boss),
                R.id.rowUkayBoss,
                R.id.tvThreadUkayBoss,
                R.id.tvPreviewUkayBoss,
                R.id.tvUnreadUkayBoss
            )
        )
        var visibleRows = 0

        rows.forEach { row ->
            val rowView = view.findViewById<View>(row.rowId)
            val isDeleted = Database.isConversationDeletedForCurrentUser(row.seller)
            val rowCanShow = if (Database.isCurrentUserSeller()) {
                !isDeleted &&
                    Database.currentSellerName() == row.seller &&
                    (Database.latestHaggleForSeller(row.seller) != null ||
                        Database.latestReceivedPromptForSeller(row.seller) != null ||
                        Database.latestChatMessageForSeller(row.seller) != null)
            } else {
                !isDeleted
            }

            val nameView = view.findViewById<TextView>(row.nameId)
            val previewView = view.findViewById<TextView>(row.previewId)
            val name = nameView.text.toString()
            val preview = previewView.text.toString()
            val matches = searchQuery.isBlank() ||
                name.contains(searchQuery, ignoreCase = true) ||
                preview.contains(searchQuery, ignoreCase = true)

            rowView.visibility = if (rowCanShow && matches) View.VISIBLE else View.GONE
            nameView.text = highlightSearch(name)
            previewView.text = highlightSearch(preview)
            view.findViewById<TextView>(row.unreadBadgeId).visibility =
                if (rowCanShow && matches && Database.isConversationUnreadForCurrentUser(row.seller)) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            if (rowCanShow && matches) visibleRows++
        }

        if (Database.isCurrentUserSeller()) {
            val hasSellerOffer = Database.currentSellerName()?.let(Database::latestHaggleForSeller) != null
            emptyMessages.visibility = if (hasSellerOffer && visibleRows == 0) View.VISIBLE else emptyMessages.visibility
        } else {
            emptyMessages.visibility = if (visibleRows == 0) View.VISIBLE else View.GONE
        }
    }

    private fun highlightSearch(text: String): CharSequence {
        if (searchQuery.isBlank()) return text

        val spannable = SpannableString(text)
        val start = text.lowercase().indexOf(searchQuery.lowercase())
        if (start >= 0) {
            val end = start + searchQuery.length
            spannable.setSpan(
                BackgroundColorSpan(ContextCompat.getColor(requireContext(), R.color.leaf_mist)),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannable
    }

    private fun bindMessageDeletion() {
        bindDeleteOnLongPress(sellerPresetMessageRow, sellerMessage)
        bindDeleteOnLongPress(buyerPresetMessageRow, buyerPresetMessage)
        bindDeleteOnLongPress(sentMessageRow, buyerMessage, buyerMessageTime)
    }

    private fun bindDeleteOnLongPress(row: View, message: View, time: View? = null) {
        message.setOnLongClickListener {
            showDeleteMessageDialog {
                row.visibility = View.GONE
                message.visibility = View.GONE
                time?.visibility = View.GONE
                Toast.makeText(requireContext(), R.string.message_deleted, Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun bindThreadDeletion(view: View, rowId: Int, seller: String) {
        view.findViewById<View>(rowId).setOnLongClickListener {
            showDeleteConversationDialog(seller)
            true
        }
    }

    private fun showPictureSourceDialog() {
        val options = arrayOf(getString(R.string.use_camera), getString(R.string.open_gallery))
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.send_picture)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> galleryPicker.launch("image/*")
                }
            }
            .show()
    }

    private fun openCamera() {
        val imageDir = File(requireContext().cacheDir, "chat_images").apply { mkdirs() }
        val imageFile = File.createTempFile("chat_", ".jpg", imageDir)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            imageFile
        )
        pendingCameraImageUri = uri
        cameraCapture.launch(uri)
    }

    private fun sendImageMessage(uri: Uri) {
        Database.addImageChatMessage(
            seller = currentSeller,
            senderUsername = Database.currentUsername.ifBlank { "admin" },
            imageUri = uri.toString()
        )
        renderConversationHistory()
        Database.markSellerConversationUnread(currentSeller)
        Toast.makeText(requireContext(), getString(R.string.picture_sent, activeSeller.text), Toast.LENGTH_SHORT).show()
    }

    private fun addSentTextMessage(message: String) {
        Database.addTextChatMessage(
            seller = currentSeller,
            senderUsername = Database.currentUsername.ifBlank { "admin" },
            body = message
        )
        renderConversationHistory()
    }

    private fun renderConversationHistory() {
        sentImageMessageRow.removeAllViews()
        Database.conversationMessagesForSeller(currentSeller).forEach { message ->
            val isMine = message.senderUsername == Database.currentUsername.ifBlank { "admin" }
            val row = when (message.type) {
                Database.ChatMessageType.TEXT -> createSentTextBubble(message, isMine)
                Database.ChatMessageType.IMAGE -> createSentImageBubble(message, isMine)
            }
            sentImageMessageRow.addView(row)
        }
        sentImageMessageRow.visibility =
            if (sentImageMessageRow.childCount == 0) View.GONE else View.VISIBLE
    }

    private fun createSentTextBubble(message: Database.ChatMessage, isMine: Boolean = true): View {
        val row = layoutInflater.inflate(R.layout.item_chat_sent_text, sentImageMessageRow, false) as LinearLayout
        val messageView = row.findViewById<TextView>(R.id.tvSentText)
        row.gravity = if (isMine) android.view.Gravity.END else android.view.Gravity.START
        messageView.text = message.body.orEmpty()
        messageView.setTextColor(ContextCompat.getColor(requireContext(), if (isMine) R.color.white else R.color.black))
        messageView.setBackgroundResource(if (isMine) R.drawable.chat_buyer_bubble_bg else R.drawable.chat_seller_bubble_bg)
        messageView.setOnLongClickListener {
            showDeleteMessageDialog {
                Database.deleteChatMessage(message.id)
                renderConversationHistory()
                view?.let { updateInboxPreviews(it) }
                Toast.makeText(requireContext(), R.string.message_deleted, Toast.LENGTH_SHORT).show()
            }
            true
        }
        return row
    }

    private fun createSentImageBubble(message: Database.ChatMessage, isMine: Boolean = true): View {
        val row = layoutInflater.inflate(R.layout.item_chat_sent_image, sentImageMessageRow, false) as LinearLayout
        val image = row.findViewById<ImageView>(R.id.ivSentImage)
        val card = row.findViewById<View>(R.id.cardSentImage)
        row.gravity = if (isMine) android.view.Gravity.END else android.view.Gravity.START
        val imageUri = Uri.parse(message.imageUri.orEmpty())
        image.setImageURI(imageUri)
        card.setOnClickListener { showImagePreview(imageUri) }
        card.setOnLongClickListener {
            showDeleteMessageDialog {
                Database.deleteChatMessage(message.id)
                renderConversationHistory()
                view?.let { updateInboxPreviews(it) }
                Toast.makeText(requireContext(), R.string.message_deleted, Toast.LENGTH_SHORT).show()
            }
            true
        }
        return row
    }

    private fun showImagePreview(uri: Uri) {
        val image = ImageView(requireContext()).apply {
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            setPadding(10.dp(), 10.dp(), 10.dp(), 10.dp())
            setImageURI(uri)
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setView(image)
            .create()
        image.setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun Int.dp(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun showDeleteMessageDialog(onConfirm: () -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_delete, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.tvDeleteTitle).text = getString(R.string.delete_message_title)
        dialogView.findViewById<TextView>(R.id.tvDeleteMessage).text = getString(R.string.delete_message_message)
        dialogView.findViewById<View>(R.id.btnDeleteCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.btnDeleteConfirm).setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun showDeleteConversationDialog(seller: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_delete, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.tvDeleteTitle).text = getString(R.string.delete_chat_title)
        dialogView.findViewById<TextView>(R.id.tvDeleteMessage).text = getString(R.string.delete_chat_message, seller)
        dialogView.findViewById<View>(R.id.btnDeleteCancel).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.btnDeleteConfirm).setOnClickListener {
            Database.deleteConversationForCurrentUser(seller)
            if (currentSeller == seller && conversationContainer.visibility == View.VISIBLE) {
                showInbox()
            }
            view?.let {
                bindInboxForCurrentAccount(it)
                updateInboxPreviews(it)
            }
            (activity as? MainActivity)?.refreshChatBadge()
            Toast.makeText(requireContext(), R.string.chat_deleted, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private data class ThreadRow(
        val seller: String,
        val rowId: Int,
        val nameId: Int,
        val previewId: Int,
        val unreadBadgeId: Int
    )
}
