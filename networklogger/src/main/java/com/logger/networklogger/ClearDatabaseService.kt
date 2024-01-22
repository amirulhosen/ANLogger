package com.logger.networklogger

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.preference.PreferenceManager
import com.logger.networklogger.data.RoomDbRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import java.util.Calendar
import java.util.concurrent.TimeUnit

internal class ClearDatabaseService : JobIntentService() {
    private val scope = MainScope()
    private val roomDbRepository: RoomDbRepository by KoinJavaComponent.inject(RoomDbRepository::class.java)
    override fun onHandleWork(intent: Intent) {
        scope.launch {
            val latestId = roomDbRepository.getLastInsertedTime()
//            checkAndClearDb(this@ClearDatabaseService, latestId)
        }
    }

    fun enqueueWork(context: Context, work: Intent) {
        enqueueWork(context, ClearDatabaseService::class.java,
            CLEAN_DATABASE_JOB_ID, work)
    }

    companion object {
        private const val CLEAN_DATABASE_JOB_ID = 123321
    }

    private suspend fun checkAndClearDb(context: Context, latestTime: Long) {
        val createTime = latestTime
        val timeDiff = Calendar.getInstance().timeInMillis - createTime
        val days = TimeUnit.MILLISECONDS.toDays(timeDiff)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        when {
            prefs.getString("clean_period_list", "")
                ?.equals("three_day") == true && days >= 3 -> {
                roomDbRepository.deleteTable()
            }

            prefs.getString("clean_period_list", "")
                ?.equals("two_day") == true && days >= 3 -> {
                roomDbRepository.deleteTable()
            }

            prefs.getString("clean_period_list", "")
                ?.equals("one_day") == true && days >= 3 -> {
                roomDbRepository.deleteTable()
            }

            prefs.getString("clean_period_list", "")
                ?.equals("") == true -> {
                roomDbRepository.deleteTable()
            }

            else -> {
                Log.i("", "Ignore")
            }
        }
    }
}
