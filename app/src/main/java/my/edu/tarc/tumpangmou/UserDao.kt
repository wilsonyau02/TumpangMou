package my.edu.tarc.tumpangmou

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import my.edu.tarc.tumpangmou.ui.home.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user ORDER BY id ASC")
    fun getAllUsers(): LiveData<List<User>>

 @Query("SELECT * FROM user WHERE id LIKE :inputID")
    fun getUser(inputID : String): LiveData<List<User>>

    //Suspend - execute the function in a separate thread
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userInformation: User)

    @Update
    suspend fun update(userInformation: User)

    @Delete
    suspend fun delete(userInformation: User)

    @Query("DELETE FROM user")
    suspend fun deleteAll()
}