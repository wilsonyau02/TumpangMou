package my.edu.tarc.tumpangmou.ui.home

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
//import com.google.firebase.firestore.auth.User
import my.edu.tarc.tumpangmou.UserDao

class UserRepository (private val userDao: UserDao) {
    //Room execute all queries on a separate thread
    //Get all the contract from database without communicate to repository
    val allUserInformation: LiveData<List<User>> = userDao.getAllUsers()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    //If DAO is suspend, the function that call it also need suspend
    suspend fun insert(userInfo: User){
        userDao.insert(userInfo)
    }

    @WorkerThread
    suspend fun update(userInfo: User){
        userDao.update(userInfo)
    }

    @WorkerThread
    suspend fun delete(userInfo: User){
        userDao.delete(userInfo)
    }

    @WorkerThread
    suspend fun deleteAll(){
        userDao.deleteAll()
    }

    @WorkerThread
    suspend fun getUser(id: String){
        userDao.getUser(id)
    }
}