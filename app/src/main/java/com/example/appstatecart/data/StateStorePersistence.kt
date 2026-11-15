package com.example.appstatecart.data

import android.util.Log
import androidx.appstate.datastore.StateStorePreferences
import androidx.appstate.datastore.StateStoreSerializer
import androidx.appstate.datastore.syncToDataStore
import androidx.appstate.statestore.StateStore
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioStorage
import kotlinx.coroutines.CoroutineScope
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * The same DataStore that `syncToDataStore(path, scope)` builds internally, plus a corruption
 * handler: a file that is not valid JSON at all starts empty instead of failing.
 */
fun stateStoreDataStore(path: String, scope: CoroutineScope): DataStore<StateStorePreferences> =
    DataStoreFactory.create(
        storage = OkioStorage(FileSystem.SYSTEM, StateStoreSerializer) { path.toPath() },
        corruptionHandler = ReplaceFileCorruptionHandler { StateStoreSerializer.defaultValue },
        scope = scope,
    )

/**
 * [syncToDataStore], but a saved value that no longer decodes (for example after `Cart` gains a
 * field) resets the saved state instead of crashing the app on every launch.
 *
 * The library decodes each value itself and throws [CorruptionException] out of the sync, so the
 * DataStore corruption handler above never sees it. Never returns, like [syncToDataStore].
 */
suspend fun StateStore.syncToDataStoreOrReset(dataStore: DataStore<StateStorePreferences>) {
    try {
        syncToDataStore(dataStore)
    } catch (e: CorruptionException) {
        Log.w("StateStorePersistence", "Saved state could not be restored, starting empty", e)
        dataStore.updateData { StateStoreSerializer.defaultValue }
        syncToDataStore(dataStore)
    }
}
