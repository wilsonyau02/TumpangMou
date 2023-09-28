package my.edu.tarc.tumpangmou

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.tumpangmou.ui.home.DriverPost

class PostAdapter () : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    //Cached copy of contacts
    private var driverPostList = emptyList<DriverPost>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView){
        val travelDate : TextView = itemView.findViewById(R.id.textViewDate)
        val travelTime : TextView = itemView.findViewById(R.id.textViewTime)
        val departurePlace : TextView = itemView.findViewById(R.id.textViewDeparturePlace)
        val travellPlace : TextView = itemView.findViewById(R.id.textViewTravellingPlace)
        val points : TextView = itemView.findViewById(R.id.textViewDriverPoints)
        val passNum : TextView = itemView.findViewById(R.id.textViewPassNum)
    }

    internal fun setDriverPost(drivePost: List<DriverPost>){
        this.driverPostList = drivePost
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Create a new view, which define the UI of the list item
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.history_record, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Get element from the dataset at this position and replace the contents of the view with that element
        val currentItem = driverPostList[position]
        holder.travelDate.text = currentItem.travelDate
        holder.travelTime.text = currentItem.travelTime
        holder.departurePlace.text = currentItem.departurePlace
        holder.travellPlace.text = currentItem.travellingPlace
        holder.points.text = currentItem.driverPoints.toString()
        holder.passNum.text = currentItem.passNum.toString()
        holder.itemView.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return driverPostList.size
    }
}