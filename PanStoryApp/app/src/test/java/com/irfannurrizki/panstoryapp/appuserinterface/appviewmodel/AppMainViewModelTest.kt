package com.irfannurrizki.panstoryapp.appuserinterface.appviewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.irfannurrizki.panstoryapp.DataDummy
import com.irfannurrizki.panstoryapp.MainCoroutineRule
import com.irfannurrizki.panstoryapp.MainDispatcherRule
import com.irfannurrizki.panstoryapp.appdata.apprepo.AppStoryRepository
import com.irfannurrizki.panstoryapp.appdata.serverdata.apiresponse.ListStoryItem
import com.irfannurrizki.panstoryapp.appuserinterface.appadapter.AppListStoryAdapter
import com.irfannurrizki.panstoryapp.appuserinterface.apppaging.AppStoryPagingSource
import com.irfannurrizki.panstoryapp.appuserinterface.apppaging.AppStoryPagingSourceTest
import com.irfannurrizki.panstoryapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
//import org.junit.jupiter.api.Assertions.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AppMainViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()
    val mainCoroutineRule=MainCoroutineRule()
    @Mock
    private lateinit var appStoryRepository: AppStoryRepository
    private val dummyToken = "rUJkbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWlZbHdrZ21DRndYM0tVVXMiLCJpYXQiOjE2NjYzNjA2ODN9.dNTSWDgq0awV8SjaL4b5582nikxN6-tcjpnSYDcgUEw"

    @Test
    fun `When get list story should not null`()= mainCoroutineRule.runBlockingTest{
        val dataDummyStories = DataDummy.generateDummyStoriesList()
        val data : PagingData<ListStoryItem> = AppStoryPagingSource.snapshot(dataDummyStories)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data
        Mockito.`when`(appStoryRepository.getStoryPaging(dummyToken)).thenReturn(expectedStories)

        val mainViewModel = AppMainViewModel(appStoryRepository)
        val actualStories = mainViewModel.getStories(dummyToken).getOrAwaitValue()

        val storyDiffer = AsyncPagingDataDiffer(
            diffCallback = AppListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        storyDiffer.submitData(actualStories)

        Mockito.verify(appStoryRepository).getStoryPaging(dummyToken)
        Assert.assertNotNull(actualStories)
        Assert.assertEquals(dataDummyStories.size, storyDiffer.snapshot().size)
        Assert.assertNotNull(storyDiffer.snapshot())
        Assert.assertEquals(dataDummyStories.size, storyDiffer.snapshot().size)
        Assert.assertEquals(dataDummyStories[0], storyDiffer.snapshot()[0])
    }

    @Test
    fun `Stories length should be the same`() = runTest {
        val dataDummyStories = DataDummy.generateDummyStoriesList()
        val data = AppStoryPagingSourceTest.snapshot(dataDummyStories)

        val dataStories = MutableLiveData<PagingData<ListStoryItem>>()
        dataStories.value = data

        `when`(appStoryRepository.getStoryPaging(dummyToken)).thenReturn(dataStories)

        val mainViewModel = AppMainViewModel(appStoryRepository)
        val actualStories = mainViewModel.getStories(dummyToken).getOrAwaitValue()
        val storyDiffer = AsyncPagingDataDiffer(
            diffCallback = AppListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        storyDiffer.submitData(actualStories)

        Mockito.verify(appStoryRepository).getStoryPaging(dummyToken)
        Assert.assertNotNull(actualStories)

        Assert.assertEquals(dataDummyStories.size, storyDiffer.snapshot().size)
    }

    @Test
    fun `First Story item should be the same`() = runTest {
        val dataDummyStories = DataDummy.generateDummyStoriesList()
        val data = AppStoryPagingSourceTest.snapshot(dataDummyStories)

        val dataStories = MutableLiveData<PagingData<ListStoryItem>>()
        dataStories.value = data

        `when`(appStoryRepository.getStoryPaging(dummyToken)).thenReturn(dataStories)

        val mainViewModel = AppMainViewModel(appStoryRepository)
        val actualStories = mainViewModel.getStories(dummyToken).getOrAwaitValue()
        val storyDiffer = AsyncPagingDataDiffer(
            diffCallback = AppListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        storyDiffer.submitData(actualStories)

        Mockito.verify(appStoryRepository).getStoryPaging(dummyToken)
        Assert.assertEquals(dataDummyStories[0], storyDiffer.snapshot()[0])
    }

    @Test
    fun `If Story empty should return true`() = runTest {
        val dataDummyStories = DataDummy.emptyListStoryDummy()
        val data = AppStoryPagingSourceTest.snapshot(dataDummyStories)

        val dataStories = MutableLiveData<PagingData<ListStoryItem>>()
        dataStories.value = data

        `when`(appStoryRepository.getStoryPaging(dummyToken)).thenReturn(dataStories)

        val mainViewModel = AppMainViewModel(appStoryRepository)
        val actualStories = mainViewModel.getStories(dummyToken).getOrAwaitValue()
        val storyDiffer = AsyncPagingDataDiffer(
            diffCallback = AppListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        storyDiffer.submitData(actualStories)

        Mockito.verify(appStoryRepository).getStoryPaging(dummyToken)
        Assert.assertEquals(storyDiffer.snapshot().isEmpty(), true)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}