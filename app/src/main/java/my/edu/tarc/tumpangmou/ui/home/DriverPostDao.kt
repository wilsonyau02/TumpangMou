package my.edu.tarc.tumpangmou.ui.home

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DriverPostDao {
    @Query("SELECT * FROM driverPost ORDER BY id ASC")
    fun getAllDriverPost(): LiveData<List<DriverPost>>

    @Query("SELECT * FROM driverPost WHERE id LIKE :inputID AND STATUS = 'Incomplete' ")
    fun getDriverPost(inputID : String): LiveData<List<DriverPost>>

    //Suspend - execute the function in a separate thread
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(driverPost: DriverPost)

    @Update
    suspend fun update(driverPost: DriverPost)

//    @Query("UPDATE driverPost SET status = :newStatus WHERE id = :postId")
//    suspend fun updateStatus(postId: String, newStatus: String)

    @Delete
    suspend fun delete(driverPost: DriverPost)

    @Query("DELETE FROM driverPost")
    suspend fun deleteAll()
}