package com.example.appstatecart

import android.app.Application
import com.example.appstatecart.data.stateStoreDataStore
import com.example.appstatecart.data.syncToDataStoreOrReset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CartApp : Application() {

    // DataStore does its file I/O on this scope.
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
        val dataStore = stateStoreDataStore(
            path = filesDir.resolve("statestore.json").absolutePath,
            scope = applicationScope,
        )
        applicationScope.launch {
            container.stateStore.syncToDataStoreOrReset(dataStore)
        }
    }
}
