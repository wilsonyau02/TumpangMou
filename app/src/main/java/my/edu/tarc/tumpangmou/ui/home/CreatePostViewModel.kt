package my.edu.tarc.tumpangmou.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CreatePostViewModel (application: Application) : AndroidViewModel(application) {
    var driverPostList: LiveData<List<DriverPost>> //all the driver post list
    var pendingDriverPost: DriverPost? = null //save the pending driver post
    val postIdList: MutableList<String> = mutableListOf() //all the post id for the user
    var pendingPost: String = ""//pending post for the logged in user
    var postLenth: Int = 0 //length of the post for the logged in user


    private val repository: DriverPostRepository

    init {
        val driverPostDao = DriverPostDatabase.getDatabase(application).driverPostDao()
        repository = DriverPostRepository(driverPostDao)
        driverPostList = repository.allDriverPost
    }

    //Call a asyn task
    fun insertDriverPost(driverPost: DriverPost) = viewModelScope.launch{
        repository.insert(driverPost)
    }

    fun updateDriverPost(driverPost: DriverPost) = viewModelScope.launch {
        repository.update(driverPost)
    }
//    fun updateStatus(postId: String, newStatus: String) = viewModelScope.launch {
//        repository.updateStatus(postId, newStatus)
//    }

    fun deleteDriverPost(driverPost: DriverPost) = viewModelScope.launch {
        repository.delete(driverPost)
    }

    fun deleteAllDriverPost() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun getDriverPost(id: String) = viewModelScope.launch {
        repository.getDriverPost(id)
    }

    fun getAllDriverPost() = viewModelScope.launch {
        repository.allDriverPost
    }
}