package my.edu.tarc.tumpangmou.ui.home

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.sql.Date
import java.sql.Time
import java.time.LocalTime

@Entity(tableName = "driverPost")
data class DriverPost(
    @PrimaryKey var id: String = "",
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

): Serializable {
    constructor() : this("", "", "", "", "", "", "", "", 0, 0, "", 0, 0, "")

    override fun toString(): String {
        return "$id : $departurePlace"
    }
}
