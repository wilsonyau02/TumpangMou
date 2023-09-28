package my.edu.tarc.tumpangmou.ui.home

import android.content.Context
import androidx.room.*
import androidx.room.RoomDatabase
import my.edu.tarc.tumpangmou.UserDao
import java.sql.Date
import java.sql.Time


@Database (entities = arrayOf(User :: class), version = 1, exportSchema = false)
abstract class UserDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object{
        //Singleton prevents multiple instances of database opening at the same time
        //Singleton = the class only have one instance
        @Volatile //Volatile memory - RAM
        private var INSTANCE: UserDatabase? = null


        //Cotnext = context level parameter -> Application
        fun getDatabase(context: Context) : UserDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }

            //Run at the same time at the application (I will wait for you)
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "contact_db"
                ).build()

                //Store in local variable
                INSTANCE = instance
                return instance
            }
        }
    }
}