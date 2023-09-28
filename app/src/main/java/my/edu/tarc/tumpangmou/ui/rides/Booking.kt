package my.edu.tarc.tumpangmou.ui.rides

data class Booking(
    val postId: String,
    val driverId: String,
    val passengerId: String,
    val driverPoints: Int,
    val passengerPoints: Int,
    val carPlateNum: String,
    val carModel: String,
    val carColor: String,
    val source: String,
    val destination: String,
    val date: String,
    val time: String
) {
    constructor() : this("", "", "", 0, 0, "", "", "", "", "", "", "")
}
