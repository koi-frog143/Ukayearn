package com.citu.ukayearn.ui.screens.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.citu.ukayearn.R
import com.citu.ukayearn.data.Database

class ChatFragment : Fragment() {
    private lateinit var inboxContainer: View
    private lateinit var conversationContainer: View
    private lateinit var composerBar: View
    private lateinit var activeInitial: TextView
    private lateinit var activeSeller: TextView
    private lateinit var sellerStatus: TextView
    private lateinit var sellerMessage: TextView
    private lateinit var buyerMessage: TextView
    private lateinit var buyerMessageTime: TextView
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
        activeSeller = view.findViewById(R.id.tvActiveSeller)
        sellerStatus = view.findViewById(R.id.tvSellerStatus)
        sellerMessage = view.findViewById(R.id.tvSellerChatMessage)
        buyerMessage = view.findViewById(R.id.tvBuyerChatMessage)
        buyerMessageTime = view.findViewById(R.id.tvBuyerChatTime)
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

        view.findViewById<View>(R.id.btnBackToInbox).setOnClickListener {
            showInbox()
        }
        view.findViewById<View>(R.id.btnChatInfo).setOnClickListener {
            ChatInfoBottomSheetFragment.newInstance(currentSeller)
                .show(parentFragmentManager, ChatInfoBottomSheetFragment.TAG)
        }
        view.findViewById<View>(R.id.btnAttachImage).setOnClickListener {
            Toast.makeText(requireContext(), R.string.photo_picker_coming_soon, Toast.LENGTH_SHORT).show()
        }
        view.findViewById<View>(R.id.btnApproveHaggle).setOnClickListener {
            currentHaggleOffer?.let { offer ->
                offer.status = Database.HaggleStatus.APPROVED
                Database.approvedHaggleVouchers[offer.product.id] = offer.offerPrice
                Toast.makeText(requireContext(), R.string.haggle_approved_added_to_cart, Toast.LENGTH_SHORT).show()
                bindHaggleWidget(currentSeller)
                updateInboxPreviews(view)
            }
        }
        view.findViewById<View>(R.id.btnDeclineHaggle).setOnClickListener {
            currentHaggleOffer?.let { offer ->
                offer.status = Database.HaggleStatus.DECLINED
                Toast.makeText(requireContext(), R.string.haggle_declined_message, Toast.LENGTH_SHORT).show()
                bindHaggleWidget(currentSeller)
                updateInboxPreviews(view)
            }
        }
        view.findViewById<View>(R.id.btnSendMessage).setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                buyerMessage.text = message
                buyerMessage.visibility = View.VISIBLE
                buyerMessageTime.visibility = View.VISIBLE
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
        bindHaggleWidget(seller)
        bindPresetConversationVisibility()
        buyerMessage.visibility = View.GONE
        buyerMessageTime.visibility = View.GONE
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
        bindPreview(view, R.id.tvPreviewCebuFinds, getString(R.string.cebu_finds), getString(R.string.chat_preview_cebu_finds))
        bindPreview(view, R.id.tvPreviewUkayBoss, getString(R.string.ukay_boss), getString(R.string.chat_preview_ukay_boss))
    }

    private fun bindPreview(view: View, previewId: Int, seller: String, fallback: String) {
        val offer = Database.latestHaggleForSeller(seller)
        view.findViewById<TextView>(previewId).text = if (offer != null) {
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
        }
    }

    private fun bindSellerInbox(view: View) {
        val seller = Database.currentSellerName()
        val latestOffer = seller?.let(Database::latestHaggleForSeller)
        val rowMap = mapOf(
            getString(R.string.thriftkada) to Triple(R.id.rowThriftKada, R.id.tvThreadThriftKada, R.id.tvPreviewThriftKada),
            getString(R.string.cebu_finds) to Triple(R.id.rowCebuFinds, R.id.tvThreadCebuFinds, R.id.tvPreviewCebuFinds),
            getString(R.string.ukay_boss) to Triple(R.id.rowUkayBoss, R.id.tvThreadUkayBoss, R.id.tvPreviewUkayBoss)
        )

        view.findViewById<View>(R.id.rowRetroLane).visibility = View.GONE
        rowMap.forEach { (rowSeller, ids) ->
            val row = view.findViewById<View>(ids.first)
            row.visibility = if (rowSeller == seller && latestOffer != null) View.VISIBLE else View.GONE
            if (row.visibility == View.VISIBLE) {
                val buyerName = Database.displayNameFor(latestOffer?.buyerUsername.orEmpty())
                view.findViewById<TextView>(ids.second).text = buyerName
                view.findViewById<TextView>(ids.third).text =
                    getString(R.string.haggle_inbox_preview_format, haggleStatusText(latestOffer!!.status))
                row.setOnClickListener {
                    openConversation(
                        seller = rowSeller,
                        displayName = buyerName,
                        message = getString(R.string.incoming_haggle_message)
                    )
                }
            }
        }
        emptyMessages.visibility = if (latestOffer == null) View.VISIBLE else View.GONE
    }

    private fun bindPresetConversationVisibility() {
        val showPresetMessages = !Database.isCurrentUserSeller()
        sellerPresetMessageRow.visibility = if (showPresetMessages) View.VISIBLE else View.GONE
        buyerPresetMessageRow.visibility = if (showPresetMessages) View.VISIBLE else View.GONE
        sellerImageMessageRow.visibility = if (showPresetMessages) View.VISIBLE else View.GONE
        sellerImageTime.visibility = if (showPresetMessages) View.VISIBLE else View.GONE
        composerBar.visibility = if (showPresetMessages) View.VISIBLE else View.GONE
    }
}
