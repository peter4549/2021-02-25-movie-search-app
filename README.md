# 2021-02-25-movie-search-app

# Note
# @Volatile
This helps us make sure the value of INSTANCE is always up to date and the same to all excution threats.  
  
The value of a volatile variable will never be cached, and all writes and reads will be done to and from the main memory, it means that changes made by one thread to INSTANCE are visible to all other threads immediately.

# synchronized
Multiple threads can potentially ask for a database instance at the same time, leaving us with two instead of one.  
  
Wrapping our code into synchronized means only one thread of execution at a time can enter this block of code, which makes sure the database only gets initalized once.

## synchronized(this)
We need to pass ourselves into synchronized so that we have access to the context.
```
companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        synchronized(this) {
            var instance = INSTANCE

            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
            }
            
            return instance
        }
    }
}
```
