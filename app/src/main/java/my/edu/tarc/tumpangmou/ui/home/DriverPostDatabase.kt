package my.edu.tarc.tumpangmou.ui.home

import android.content.Context
import androidx.room.*
import java.sql.Date
import java.sql.Time


@Database (entities = arrayOf(DriverPost::class), version = 1, exportSchema = false)
@TypeConverters(MyTypeConverters::class, StringListTypeConverter::class)
abstract class DriverPostDatabase: RoomDatabase() {

    abstract fun driverPostDao(): DriverPostDao

    companion object{
        //Singleton prevents multiple instances of database opening at the same time
        //Singleton = the class only have one instance
        @Volatile //Volatile memory - RAM
        private var INSTANCE: DriverPostDatabase? = null


        //Cotnext = context level parameter -> Application
        fun getDatabase(context: Context) : DriverPostDatabase{
            //context.deleteDatabase("driverPost_db")
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }

            //Run at the same time at the application (I will wait for you)
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DriverPostDatabase::class.java,
                    "driverPost_db"
                ).build()

                //Store in local variable
                INSTANCE = instance
                return instance
            }
        }
    }
}

class MyTypeConverters {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return if (timestamp == null) null else Date(timestamp)
    }

    @TypeConverter
    fun fromTime(time: Time?): Long? {
        return time?.time
    }

    @TypeConverter
    fun toTime(timestamp: Long?): Time? {
        return if (timestamp == null) null else Time(timestamp)
    }
}

class StringListTypeConverter {
    @TypeConverter
    fun fromString(value: String?): MutableList<String>? {
        return if (value == null) null else value.split(",").toMutableList()
    }

    @TypeConverter
    fun toString(value: MutableList<String>?): String? {
        return value?.joinToString(",")
    }
}

