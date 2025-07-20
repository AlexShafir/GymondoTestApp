package com.dev.sample

import com.dev.sample.app.viewmodel.MainScreenVM
import com.dev.sample.app.viewmodel.MainScreenVM.State
import com.dev.sample.data.remote.PicsumItem
import com.dev.sample.data.repository.IPicsumRepository
import com.dev.sample.data.repository.ServerUnavailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenVMTest {

    /**
     * Test viewmodel for Success
     */
    @Test
    fun testVm_Success() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        val canonicalList = listOf(
            PicsumItem("1", "Alejandro Escamilla", "https://picsum.photos/id/1/5000/3333"),
            PicsumItem("2", "Alejandro Escamilla", "https://picsum.photos/id/2/5000/3333"),
        )

        val repo = object : IPicsumRepository {
            override suspend fun getDefaultImages() = canonicalList
        }

        val vm = MainScreenVM(repo)

        vm.getImages()
        advanceUntilIdle()

        val state = vm.state.value
        assert(state is State.Success)
        val data = (state as State.Success<List<PicsumItem>>).data
        assertEquals(data, canonicalList)

        Dispatchers.resetMain()
    }

    /**
     * Test viewmodel for Error
     */
    @Test
    fun testVm_Error() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        val repo = object : IPicsumRepository {
            override suspend fun getDefaultImages() = throw ServerUnavailable()
        }

        val vm = MainScreenVM(repo)

        vm.getImages()
        advanceUntilIdle()
        val state = vm.state.value
        assert(state is State.Error)

        Dispatchers.resetMain()
    }
}