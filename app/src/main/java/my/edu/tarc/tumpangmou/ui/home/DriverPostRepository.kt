package my.edu.tarc.tumpangmou.ui.home

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class DriverPostRepository (private val driverPostDao: DriverPostDao) {
    //Room execute all queries on a separate thread
    //Get all the contract from database without communicate to repository
    val allDriverPost: LiveData<List<DriverPost>> = driverPostDao.getAllDriverPost()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    //If DAO is suspend, the function that call it also need suspend
    suspend fun insert(driverPost: DriverPost){
        driverPostDao.insert(driverPost)
    }

    @WorkerThread
    suspend fun update(driverPost:DriverPost){
        driverPostDao.update(driverPost)
    }

//    suspend fun updateStatus(postId: String, newStatus: String) {
//        driverPostDao.updateStatus(postId, newStatus)
//    }

    @WorkerThread
    suspend fun delete(driverPost: DriverPost){
        driverPostDao.delete(driverPost)
    }

    @WorkerThread
    suspend fun deleteAll(){
        driverPostDao.deleteAll()
    }

    @WorkerThread
    suspend fun getDriverPost(id: String){
        driverPostDao.getDriverPost(id)
    }
}