package my.edu.tar.mobileassignment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.tumpangmou.ui.rides.RidesPost
import my.edu.tarc.tumpangmou.R

class LoadPostAdapter(private var loadPostInfo: List<RidesPost>) : RecyclerView.Adapter<LoadPostAdapter.myViewHolder>() {
    var onBookButtonClickListener : ((RidesPost) -> Unit)? = null

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder (itemView) {
        val loadPostDriverName: TextView = itemView.findViewById(R.id.loadPostDriverName)
        val loadPostCarName: TextView = itemView.findViewById(R.id.loadPostCarName)
        val loadPostCarColour: TextView = itemView.findViewById(R.id.loadPostCarColour)
        val loadPostCarPlate: TextView = itemView.findViewById(R.id.loadPostCarPlate)
        val loadPostCarSource: TextView = itemView.findViewById(R.id.loadPostCarSource)
        val loadPostCarDestination: TextView = itemView.findViewById(R.id.loadPostCarDestination)
        val loadPostDate: TextView = itemView.findViewById(R.id.loadPostDate)
        val loadPostTime: TextView = itemView.findViewById(R.id.loadPostTime)
        private val loadPostBookingButton: Button = itemView.findViewById(R.id.buttonBookPost)

        init {
            loadPostBookingButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onBookButtonClickListener?.invoke(loadPostInfo[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_recycler_load_post,
            parent,false)
        return myViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return loadPostInfo.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = loadPostInfo[position]
        holder.loadPostDriverName.text = currentItem.id
        holder.loadPostCarName.text = currentItem.carModel
        holder.loadPostCarColour.text = currentItem.carColor
        holder.loadPostCarPlate.text = currentItem.carPlateNum
        holder.loadPostCarSource.text = currentItem.departurePlace
        holder.loadPostCarDestination.text = currentItem.travellingPlace
        holder.loadPostDate.text = currentItem.travelDate
        holder.loadPostTime.text = currentItem.travelTime
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<RidesPost>) {
        loadPostInfo = newData
        notifyDataSetChanged()
    }
}
