import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.network.ramp.cceventize.R
import com.network.ramp.cceventize.ui.event_list.EventListItemModel
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(items: ArrayList<EventListItemModel>, ctx: Context) :
    ArrayAdapter<EventListItemModel>(ctx, R.layout.fragment_event_list_item, items) {

    //view holder is used to prevent findViewById calls
    private class AttractionItemViewHolder {
        var image: ImageView? = null
        var title: TextView? = null
        var description: TextView? = null
        var from: TextView? = null
        var to: TextView? = null
        var price: TextView? = null
    }

    var startDateTimeCalendar = Calendar.getInstance()
    var endDateTimeCalendar = Calendar.getInstance()

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var view = view

        val viewHolder: AttractionItemViewHolder

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.fragment_event_list_item, viewGroup, false)

            viewHolder = AttractionItemViewHolder()
            viewHolder.title = view!!.findViewById<View>(R.id.title) as TextView
            viewHolder.description = view.findViewById<View>(R.id.description) as TextView
            viewHolder.from = view.findViewById<View>(R.id.from) as TextView
            viewHolder.to = view.findViewById<View>(R.id.to) as TextView
            viewHolder.price = view.findViewById<View>(R.id.price) as TextView
            //shows how to apply styles to views of item for specific items
            viewHolder.image = view.findViewById<View>(R.id.image) as ImageView
        } else {
            //no need to call findViewById, can use existing ones from saved view holder
            viewHolder = view.tag as AttractionItemViewHolder
        }

        val event = getItem(i)
        viewHolder.title!!.text = event!!.name
        viewHolder.description!!.text = event.description
        if (event.start_time != null && event.start_time != 0.toLong()) {
            val myFormat = "MM/dd/yyyy hh:mm a" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            startDateTimeCalendar.timeInMillis = event.start_time!!
            viewHolder.from!!.setText(sdf.format(startDateTimeCalendar.getTime()))
        }
        else {
            viewHolder.from!!.text = ""
        }
        if (event.end_time != null && event.end_time != 0.toLong()) {
            val myFormat = "MM/dd/yyyy hh:mm a" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            endDateTimeCalendar.timeInMillis = event.end_time!!
            viewHolder.to!!.setText(sdf.format(endDateTimeCalendar.getTime()))
        }
        else {
            viewHolder.to!!.text = ""
        }
        viewHolder.price!!.setText(event.price.toString() + " " + event.currency)
        viewHolder.image!!.setImageResource(event.image)

        view.tag = viewHolder

        return view
    }
}