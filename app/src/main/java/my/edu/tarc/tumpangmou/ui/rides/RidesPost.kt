package my.edu.tarc.tumpangmou.ui.rides

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "driverPost")
@Serializable
data class RidesPost(
    @PrimaryKey val id: String = "",
    val departurePlace: String = "",
    val travellingPlace: String = "",
    val travelDate: String = "",
    val travelTime: String = "",
    val carPlateNum: String = "",
    val carModel: String = "",
    val carColor: String = "",
    val passNum: Int = 0,
    val passCount: Int = 0,
    val status: String = "",
    val driverPoints: Int = 0,
    val passengerPoints: Int = 0,
    val driverID: String = "",
    var requester: MutableList<String>? = mutableListOf()

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() !!,
        parcel.readString() !!,
        parcel.readString() !!,
        parcel.readString() !!,
        parcel.readString() !!,
        parcel.readString() !!,
        parcel.readString() !!,
        parcel.readString() !!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() !!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() !!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(departurePlace)
        parcel.writeString(travellingPlace)
        parcel.writeString(travelDate)
        parcel.writeString(travelTime)
        parcel.writeString(carPlateNum)
        parcel.writeString(carModel)
        parcel.writeString(carColor)
        parcel.writeInt(passNum)
        parcel.writeInt(passCount)
        parcel.writeString(status)
        parcel.writeInt(driverPoints)
        parcel.writeInt(passengerPoints)
        parcel.writeString(driverID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RidesPost> {
        override fun createFromParcel(parcel: Parcel): RidesPost {
            return RidesPost(parcel)
        }

        override fun newArray(size: Int): Array<RidesPost?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "$id : $departurePlace"
    }
}
