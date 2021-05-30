package com.network.ramp.cceventize.ui.event_crud

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.network.ramp.cceventize.R
import com.network.ramp.cceventize.data.model.Event
import com.network.ramp.cceventize.databinding.ActivityEventBinding
import com.network.ramp.cceventize.ui.event_crud.model.Asset
import com.network.ramp.cceventize.ui.event_crud.model.AssetService
import com.network.ramp.cceventize.ui.event_crud.model.AssetsResponse
import com.network.ramp.cceventize.ui.login.afterTextChanged
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Activity class for the event form.
 */
class EventActivity : AppCompatActivity() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var binding: ActivityEventBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var eventType: Spinner
    private lateinit var eventName: EditText
    private lateinit var eventDescription: EditText
    private lateinit var eventStartDate: EditText
    private lateinit var eventStartTime: EditText
    private lateinit var eventEndDate: EditText
    private lateinit var eventEndTime: EditText
    private lateinit var eventCurrency: Spinner
    private lateinit var eventTicketPrice: EditText
    private lateinit var eventCoinAddress: EditText
    private lateinit var tvEventTicketPrice: TextView
    private lateinit var tvEventCoinAddress: TextView

    // Assets list from RAMP network
    private lateinit var assets: ArrayList<Asset>
    var startDateTimeCalendar = Calendar.getInstance()
    var endDateTimeCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference
        assets = arrayListOf()

        eventType = binding.spEventType
        eventName = binding.etEventName
        eventDescription = binding.etEventDescription
        eventStartDate = binding.etStartDate
        eventStartTime = binding.etStartTime
        eventEndDate = binding.etEndDate
        eventEndTime = binding.etEndTime
        eventCurrency = binding.spCurrency
        eventTicketPrice = binding.etEventPrice
        eventCoinAddress = binding.etCoinAddress
        tvEventTicketPrice = binding.tvEventPrice
        tvEventCoinAddress = binding.tvCoinAddress
        val createEvent = binding.btnCreateEvent

        eventViewModel = ViewModelProvider(this, EventViewModelFactory())
            .get(EventViewModel::class.java)

        //Check is correct data input
        eventViewModel.eventFormState.observe(this@EventActivity, Observer {
            val eventState = it ?: return@Observer

            // Disable create event button unless all fields are not valid
            createEvent.isEnabled = eventState.isDataValid

            if (eventState.nameError != null) {
                eventName.error = getString(eventState.nameError)
            }
            if (eventState.descriptionError != null) {
                eventDescription.error = getString(eventState.descriptionError)
            }
            if (eventState.ticketPriceError != null) {
                eventTicketPrice.error = getString(eventState.ticketPriceError)
            }
            if (eventState.coinAddressError != null) {
                eventCoinAddress.error = getString(eventState.coinAddressError)
            }
        })

        eventName.afterTextChanged {
            valueChanged()
        }

        eventDescription.afterTextChanged {
            valueChanged()
        }

        eventTicketPrice.afterTextChanged {
            valueChanged()
        }

        eventCoinAddress.afterTextChanged {
            valueChanged()
        }

        // Listener for start date field
        val startDateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                startDateTimeCalendar.set(Calendar.YEAR, year)
                startDateTimeCalendar.set(Calendar.MONTH, monthOfYear)
                startDateTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "MM/dd/yyyy" // Display format
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                eventStartDate.setText(sdf.format(startDateTimeCalendar.getTime()))
            }
        }
        eventStartDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@EventActivity,
                    startDateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    startDateTimeCalendar.get(Calendar.YEAR),
                    startDateTimeCalendar.get(Calendar.MONTH),
                    startDateTimeCalendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        // Listener for start time field
        val startTimeSetListener = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
                startDateTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                startDateTimeCalendar.set(Calendar.MINUTE, minute)

                val myFormat = "hh:mm a" // Display format
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                eventStartTime.setText(sdf.format(startDateTimeCalendar.getTime()))
            }
        }
        eventStartTime.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                TimePickerDialog(this@EventActivity,
                    startTimeSetListener,
                    startDateTimeCalendar.get(Calendar.HOUR_OF_DAY),
                    startDateTimeCalendar.get(Calendar.MINUTE),
                    false).show()
            }
        })

        // Listener for end date field
        val endDateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                endDateTimeCalendar.set(Calendar.YEAR, year)
                endDateTimeCalendar.set(Calendar.MONTH, monthOfYear)
                endDateTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "MM/dd/yyyy" // Display format
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                eventEndDate.setText(sdf.format(endDateTimeCalendar.getTime()))
            }
        }
        eventEndDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@EventActivity,
                    endDateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    endDateTimeCalendar.get(Calendar.YEAR),
                    endDateTimeCalendar.get(Calendar.MONTH),
                    endDateTimeCalendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        // Listener for end time field
        val endTimeSetListener = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
                endDateTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                endDateTimeCalendar.set(Calendar.MINUTE, minute)

                val myFormat = "hh:mm a" // Display format
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                eventEndTime.setText(sdf.format(endDateTimeCalendar.getTime()))
            }
        }
        eventEndTime.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                TimePickerDialog(this@EventActivity,
                    endTimeSetListener,
                    endDateTimeCalendar.get(Calendar.HOUR_OF_DAY),
                    endDateTimeCalendar.get(Calendar.MINUTE),
                    false).show()
            }
        })

        // Event when user change currency
        eventCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                valueChanged()
                // Change hint for ticket price editText and text for event ticket price label (textView)
                var text = java.lang.String.format("%s %s", getString(R.string.event_price), eventCurrency.selectedItem as String)
                eventTicketPrice.setHint(text)
                tvEventTicketPrice.setText(text)

                // Change hint for coin address editText and text for coin address label (textView)
                text = java.lang.String.format("%s %s", eventCurrency.selectedItem as String, getString(R.string.event_coin_address))
                eventCoinAddress.setHint(text)
                tvEventCoinAddress.setText(text)
            }
        }

        // On create event click
        createEvent.setOnClickListener {
            // Get key for new event
            val eventKey = database.child("event").push().key
            val user = auth.currentUser?.uid
            var price : Double
            try {
                price = eventTicketPrice.text.toString().toDouble()
            } catch (e: NumberFormatException) {
                price = 0.0 // your default value
            }

            val event = Event(eventType.selectedItem as String, eventName.text.toString(), eventDescription.text.toString(),
                startDateTimeCalendar.timeInMillis, endDateTimeCalendar.timeInMillis, eventCurrency.selectedItem as String,
                price, eventCoinAddress.text.toString(), user)
            val eventValues = event.toMap()

            // Create new event in Firebase Database
            val childUpdates = hashMapOf<String, Any>(
                "/event/$eventKey" to eventValues
            )
            database.updateChildren(childUpdates)

            finish()
        }

        // Service for get information about assets on RAMP network
        val service = Retrofit.Builder()
            .baseUrl("https://api-instant.ramp.network/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AssetService::class.java)


        service.getAssets().enqueue(object : Callback<AssetsResponse> {

            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<AssetsResponse>, t: Throwable) {
                t.printStackTrace()
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<AssetsResponse>, response: Response<AssetsResponse>) {
                val listOfAssets = response.body()
                val symbolList = ArrayList<String>()
                if (listOfAssets != null) {
                    for (i in 0 until listOfAssets.assets.size) {
                        symbolList.add(listOfAssets.assets.get(i).symbol)
                        assets.add(Asset(symbol = listOfAssets.assets.get(i).symbol,
                            name = listOfAssets.assets.get(i).name,
                            minPurchaseAmountEur = listOfAssets.assets.get(i).minPurchaseAmountEur,
                            priceEur = listOfAssets.assets.get(i).priceEur,
                            decimals = listOfAssets.assets.get(i).decimals))
                    }
                }

                // Fill list (spinner) for currency field
                val eventCurrencyArrayAdapter = ArrayAdapter(this@EventActivity, android.R.layout.simple_spinner_item, symbolList.distinct().sorted())
                eventCurrencyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                with(eventCurrency)
                {
                    adapter = eventCurrencyArrayAdapter
                    setSelection(0, false)
                }
            }
        })

        // Fill list (spinner) for event types field
        val eventTypesArray = arrayOf("Concert", "Conference", "Festival", "Seminar", "Sport", "Theater", "Trade show", "Workshop")
        val eventTypeArrayAdapter = ArrayAdapter(this@EventActivity, android.R.layout.simple_spinner_item, eventTypesArray)
        eventTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        with(eventType)
        {
            adapter = eventTypeArrayAdapter
            setSelection(0, false)
        }
    }

    private fun getSelectedAsset(assetName: String): Asset? {
        for (i in 0 until assets.size) {
            if (assets[i].symbol.equals(assetName))
            {
                return assets[i]
            }
        }
        return null
    }

    /***
     * Function that is called when user change some field on screen
     */
    private fun valueChanged() {
        var price : Double
        try {
            price = eventTicketPrice.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            price = 0.0 // your default value
        }
        val selectedAsset = getSelectedAsset(eventCurrency.selectedItem as String)
        eventViewModel.eventDataChanged(
            eventName.text.toString(),
            eventDescription.text.toString(),
            price,
            eventCoinAddress.text.toString(),
            selectedAsset?.priceEur,
            selectedAsset?.minPurchaseAmountEur
        )
    }
}