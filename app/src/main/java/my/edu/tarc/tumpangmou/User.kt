package my.edu.tarc.tumpangmou.ui.home

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.sql.Date
import java.sql.Time
import java.time.LocalTime

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val dob: String = "",
    val gender: String = "",
    val password: String = "",

): Serializable {
    constructor() : this("", "", "", "", "", "", "", "")

    override fun toString(): String {
        return "$id"
    }
}
