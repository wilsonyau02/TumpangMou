package my.edu.tarc.tumpangmou.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.launch

class userViewModel (application: Application) : AndroidViewModel(application) {
    var userInfoList: LiveData<List<User>> //all the driver post list


    private val repository: UserRepository

    init {
        val userInfoDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userInfoDao)
        userInfoList = repository.allUserInformation
    }

    //Call a asyn task
    fun insertUserInfo(userInfo: User) = viewModelScope.launch{
        repository.insert(userInfo)
    }

    fun updateDriverPost(userInfo: User) = viewModelScope.launch {
        repository.update(userInfo)
    }

    fun getUserInfo(id: String) = viewModelScope.launch {
        repository.getUser(id)
    }
}