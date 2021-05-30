package com.network.ramp.cceventize.ui.event_list

import EventAdapter
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.network.ramp.cceventize.R
import com.network.ramp.cceventize.data.model.Event
import com.network.ramp.cceventize.data.model.Ticket
import com.network.ramp.cceventize.databinding.FragmentEventListBinding
import com.network.ramp.cceventize.ui.event_crud.model.Asset
import com.network.ramp.cceventize.ui.event_crud.model.AssetService
import com.network.ramp.cceventize.ui.event_crud.model.AssetsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.math.pow

class EventListFragment : Fragment() {

    private var _binding: FragmentEventListBinding? = null

    private lateinit var auth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val authority = "buy.ramp.network" //"ri-widget-staging.firebaseapp.com"
    private val finalUrl = "ramp-example://ramp.purchase.complete"
    private val hostApiKey = "TEST"
    private val hostAppName = "TEST"

    private lateinit var assets: ArrayList<Asset>

    // How much tickets user buy
    private var buyTicketNumber = 0
    // Event ID for clicked event
    private var buyTicketEventId = ""

    // Fragment type (ALL - all events, MY - my events, TICKET - tickets)
    private var fragmentType = "all"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = Firebase.auth

        assets = arrayListOf()

        if (arguments != null) {
            fragmentType = arguments?.getString("fragment_type").toString()
        }

        val lv = binding.events

        if (fragmentType == "my" || fragmentType == "all") {
            // If screen for all events or my events
            Firebase.database.getReference("event")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val eventList = ArrayList<EventListItemModel>()
                            for (e in snapshot.children) {
                                val event = e.getValue(Event::class.java)
                                if (event != null) {
                                    if (fragmentType == "my") {
                                        if (event.user == auth.uid) {
                                            eventList.add(parseEventForUI(event, e.key))
                                        }
                                    } else if (fragmentType == "all") {
                                        if (event.user != auth.uid) {
                                            eventList.add(parseEventForUI(event, e.key))
                                        }
                                    }
                                }
                            }

                            val attractionsAdapter =
                                EventAdapter(eventList, this@EventListFragment.requireContext())
                            lv.adapter = attractionsAdapter
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }
        else if (fragmentType == "tickets") {
            // If screen for tickets
            Firebase.database.getReference("ticket")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val eventList = ArrayList<EventListItemModel>()
                            for (e in snapshot.children) {
                                val ticket = e.getValue(Ticket::class.java)
                                if (ticket != null) {
                                    ticket.event?.let {
                                        Firebase.database.getReference("event").child(
                                            it
                                        ).addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                val event = snapshot.getValue(Event::class.java)
                                                if (event != null) {
                                                    eventList.add(parseEventForUI(event, snapshot.key))
                                                }

                                                val attractionsAdapter =
                                                    EventAdapter(eventList, this@EventListFragment.requireContext())
                                                lv.adapter = attractionsAdapter
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }

                                        })
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }

        lv.dividerHeight = 10

        // Listener for on event click
        lv.onItemClickListener = AdapterView.OnItemClickListener {
                adapterView, view, i, l ->
                if (fragmentType == "all") {
                    val clickedEvent = adapterView.getItemAtPosition(i) as EventListItemModel
                    val dialogView = layoutInflater.inflate(R.layout.dialog_buy_ticket, null)

                    val tvEventName = dialogView.findViewById<TextView>(R.id.event_name)
                    tvEventName.setText(clickedEvent.name)
                    val tvEventPrice = dialogView.findViewById<TextView>(R.id.event_price)
                    // Set ticket price in format "PRICE CURRENCY"
                    tvEventPrice.setText(java.lang.String.format("%s %s", clickedEvent.price.toString() , clickedEvent.currency))
                    val spNumberOfTickets = dialogView.findViewById<Spinner>(R.id.event_ticket_number)

                    // Fill spinner for how much tickets user can buy
                    val ticketNumberArray = arrayOf("1", "2", "3", "4", "5")
                    val eventTypeArrayAdapter = ArrayAdapter(this@EventListFragment.requireContext(), android.R.layout.simple_spinner_item, ticketNumberArray)
                    eventTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    with(spNumberOfTickets)
                    {
                        adapter = eventTypeArrayAdapter
                        setSelection(0, false)
                    }

                    AlertDialog.Builder(this@EventListFragment.requireContext())
                        .setView(dialogView)
                        .setPositiveButton(R.string.dialog_buy_ticket_event_ok_button) { dialog, which ->
                            buyTicketNumber = (spNumberOfTickets.selectedItem as String).toInt()
                            buyTicketEventId = clickedEvent.key!!
                            // Formula for currency amount - format for currency for that event * ticket number * ticket price
                            showBrowser(
                                composeUrl(
                                    clickedEvent.currency, clickedEvent.address,
                                    getSelectedAssetDecimals(clickedEvent.currency) * buyTicketNumber * clickedEvent.price!!
                                )
                            )
                        }
                        .setNegativeButton(R.string.dialog_buy_ticket_event_cancel_button) { dialog, which ->
                            dialog.dismiss()
                        }
                        .show()
                }
        }

        activity?.intent?.let { processIntent(it) }

        val service = Retrofit.Builder()
            .baseUrl("https://api-instant.ramp.network/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AssetService::class.java)

        // Get assets from RAMP API
        service.getAssets().enqueue(object : Callback<AssetsResponse> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<AssetsResponse>, t: Throwable) {
                t.printStackTrace()
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(
                call: Call<AssetsResponse>,
                response: Response<AssetsResponse>
            ) {
                val listOfAssets = response.body()
                val symbolList = ArrayList<String>()
                if (listOfAssets != null) {
                    for (i in 0 until listOfAssets.assets.size) {
                        symbolList.add(listOfAssets.assets.get(i).symbol)
                        assets.add(
                            Asset(
                                symbol = listOfAssets.assets.get(i).symbol,
                                name = listOfAssets.assets.get(i).name,
                                minPurchaseAmountEur = listOfAssets.assets.get(i).minPurchaseAmountEur,
                                priceEur = listOfAssets.assets.get(i).priceEur,
                                decimals = listOfAssets.assets.get(i).decimals
                            )
                        )
                    }
                }
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // From event to layout list item
    private fun parseEventForUI(event: Event, eventKey: String?) : EventListItemModel {
        val eventListItemModel = EventListItemModel()
        eventListItemModel.key = eventKey
        if (event.type == "Concert") {
            eventListItemModel.image = R.drawable.event_type_concert
        } else if (event.type == "Conference") {
            eventListItemModel.image = R.drawable.event_type_conference
        } else if (event.type == "Festival") {
            eventListItemModel.image = R.drawable.event_type_festival
        } else if (event.type == "Seminar") {
            eventListItemModel.image = R.drawable.event_type_seminar
        } else if (event.type == "Sport") {
            eventListItemModel.image = R.drawable.event_type_sport
        } else if (event.type == "Theater") {
            eventListItemModel.image = R.drawable.event_type_theater
        } else if (event.type == "Trade show") {
            eventListItemModel.image = R.drawable.event_type_trade_show
        } else if (event.type == "Workshop") {
            eventListItemModel.image = R.drawable.event_type_workshop
        } else {
            eventListItemModel.image = R.drawable.event_type_concert
        }
        eventListItemModel.name = event.name
        eventListItemModel.description = event.description
        eventListItemModel.start_time = event.start_time
        eventListItemModel.end_time = event.end_time
        eventListItemModel.currency = event.currency
        eventListItemModel.price = event.price
        eventListItemModel.address = event.address

        return eventListItemModel
    }

    // Make link for rump network
    private fun composeUrl(swapAsset: String?, userAddress: String?, swapAmount: Double): String {
        return Uri.Builder()
            .scheme("https")
            .authority(authority)
            .appendQueryParameter("swapAsset", swapAsset)
            .appendQueryParameter("swapAmount", swapAmount.toBigDecimal().toPlainString())
            .appendQueryParameter("userAddress", userAddress)
            .appendQueryParameter("userEmailAddress", auth.currentUser?.email)
            .appendQueryParameter("finalUrl", finalUrl)
            //.appendQueryParameter("hostAppName", hostAppName)
            //.appendQueryParameter("hostApiKey", hostApiKey)
            .build()
            .toString()
    }

    private fun showBrowser(link: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(link) }
        startActivity(intent)
    }

    // After purchase on RAMP network
    private fun processIntent(intent: Intent) {
        val uri = intent.data?.toString()
        if (uri == finalUrl) {
            Toast.makeText(this.requireContext(), "Purchase complete.", Toast.LENGTH_LONG).show()

            // For every ticket create one new row on database
            for (i in 1..buyTicketNumber) {
                val ticketKey = Firebase.database.getReference().child("ticket").push().key
                val user = auth.currentUser?.uid

                val ticket = Ticket(buyTicketEventId, user)
                val ticketValues = ticket.toMap()

                val childUpdates = hashMapOf<String, Any>(
                    "/ticket/$ticketKey" to ticketValues
                )
                Firebase.database.getReference().updateChildren(childUpdates)
            }
        }
    }

    private fun getSelectedAssetDecimals(assetName: String?): Double {
        for (i in 0 until assets.size) {
            if (assets[i].symbol.equals(assetName))
            {
                return 10.toDouble().pow(assets[i].decimals);
            }
        }
        return 1.0
    }
}