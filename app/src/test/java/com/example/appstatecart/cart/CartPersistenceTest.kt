package com.example.appstatecart.cart

import androidx.appstate.statestore.StateStore
import androidx.compose.runtime.snapshots.Snapshot
import com.example.appstatecart.data.stateStoreDataStore
import com.example.appstatecart.data.syncToDataStoreOrReset
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

// Real dispatchers + a real file: syncToDataStore runs its own listener {} on Dispatchers.Default.
class CartPersistenceTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val file by lazy { File(tempFolder.root, "statestore.json") }

    @Test
    fun `cart survives an app restart`() = runBlocking {
        val firstLaunch = launchApp()
        firstLaunch.cartManager.add("apple")
        firstLaunch.cartManager.add("apple")
        awaitCondition { file.exists() && file.readText().contains("apple") }
        firstLaunch.job.cancelAndJoin()

        val secondLaunch = launchApp()
        awaitCondition { secondLaunch.cartManager.cart.value.lines.isNotEmpty() }

        assertThat(secondLaunch.cartManager.cart.value.lines).containsExactly("apple", 2)
        secondLaunch.job.cancelAndJoin()
    }

    @Test
    fun `a saved cart that no longer decodes is reset instead of crashing`() = runBlocking {
        // What an older app version could have saved if Cart's shape had changed since.
        file.writeText("""{"com.example.appstatecart.cart.CartKey":"{\"lines\":\"oops\"}"}""")

        val app = launchApp()
        app.cartManager.add("banana")
        awaitCondition { file.readText().contains("banana") }

        assertThat(app.job.isActive).isTrue()
        assertThat(app.cartManager.cart.value.lines).containsExactly("banana", 1)
        app.job.cancelAndJoin()
    }

    @Test
    fun `a file that is not JSON at all starts with an empty cart`() = runBlocking {
        file.writeText("not json")

        val app = launchApp()
        app.cartManager.add("banana")
        awaitCondition { file.readText().contains("banana") }

        assertThat(app.job.isActive).isTrue()
        assertThat(app.cartManager.cart.value.lines).containsExactly("banana", 1)
        app.job.cancelAndJoin()
    }

    private class LaunchedApp(val cartManager: CartManager, val job: Job)

    // Mirrors CartApp.onCreate. A failed sync would cancel this scope, which the tests detect.
    private fun launchApp(): LaunchedApp {
        val job = Job()
        val scope = CoroutineScope(job + Dispatchers.IO)
        val stateStore = StateStore()
        val cartManager = CartManager(stateStore) // registers CartKey before syncing
        val dataStore = stateStoreDataStore(file.absolutePath, CoroutineScope(SupervisorJob(job) + Dispatchers.IO))
        scope.launch { stateStore.syncToDataStoreOrReset(dataStore) }
        return LaunchedApp(cartManager, job)
    }

    // If a transformTest ran earlier in this JVM, the library's process-wide snapshot manager is
    // bound to that test's (now dead) dispatcher, so apply notifications are sent by hand.
    private suspend fun awaitCondition(condition: () -> Boolean) = withTimeout(5.seconds) {
        while (!condition()) {
            Snapshot.sendApplyNotifications()
            delay(20.milliseconds)
        }
    }
}
