package com.example.rickandmortyapp

import android.util.Log
import app.cash.turbine.test
import com.example.rickandmortyapp.core.UiState
import com.example.rickandmortyapp.data.model.CharacterRaM
import com.example.rickandmortyapp.data.network.ConnectivityObserver
import com.example.rickandmortyapp.fake.FakeFavouriteRepository
import com.example.rickandmortyapp.fake.FakeRaMRepository
import com.example.rickandmortyapp.ui.home.HomeViewModel
import com.example.rickandmortycore.coreUi.model.CharacterDisplayModel
import com.example.rickandmortycore.coreUi.model.CharacterFilter
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val coroutineRule = StandardTestDispatcherRule()

    private lateinit var fakeRepository: FakeRaMRepository
    private lateinit var fakeFavouriteRepository: FakeFavouriteRepository
    private lateinit var connectivityObserver: ConnectivityObserver
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0

        fakeRepository = FakeRaMRepository()
        fakeFavouriteRepository = FakeFavouriteRepository()
        connectivityObserver = mockk()
        every { connectivityObserver.isConnected } returns flowOf(true)

        viewModel = HomeViewModel(
            repository = fakeRepository,
            favouriteRepository = fakeFavouriteRepository,
            connectivityObserver = connectivityObserver
        )
    }

    private fun UiState<List<CharacterDisplayModel>>.data(): List<CharacterDisplayModel> =
        (this as UiState.Success).data

    @Test
    fun `ao iniciar carrega a primeira pagina`() = runTest {
        viewModel.uiState.test {
            val result = awaitItem()
            assertTrue(result is UiState.Success)
            assertEquals(4, result.data().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `busca por nome filtra corretamente`() = runTest {
        viewModel.uiState.test {
            skipItems(1)
            viewModel.searchCharacter("Rick")
            advanceUntilIdle()
            skipItems(1)
            val result = awaitItem()
            assertTrue(result is UiState.Success)
            assertTrue(result.data().all { it.name.contains("Rick", ignoreCase = true) })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `busca vazia restaura lista completa`() = runTest {
        viewModel.uiState.test {
            skipItems(1)

            viewModel.searchCharacter("Rick")
            advanceUntilIdle()

            skipItems(2)

            viewModel.searchCharacter("")
            advanceUntilIdle()

            awaitItem()
            val result = awaitItem()
            assertTrue(result is UiState.Success)
            assertEquals(4, result.data().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filtro por gender filtra corretamente`() = runTest {
        viewModel.uiState.test {
            skipItems(1)
            viewModel.updateFilter(CharacterFilter(gender = "Female"))
            awaitItem()
            val result = awaitItem()
            assertTrue(result is UiState.Success)
            assertTrue(result.data().all { it.gender.equals("Female", ignoreCase = true) })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filtro por status filtra corretamente`() = runTest {
        viewModel.uiState.test {
            skipItems(1)
            viewModel.updateFilter(CharacterFilter(status = "Alive"))
            awaitItem()
            val result = awaitItem()
            assertTrue(result is UiState.Success)
            assertTrue(result.data().all { it.status.equals("Alive", ignoreCase = true) })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filtro por favoritos exibe lista local`() = runTest {
        fakeFavouriteRepository.add(
            CharacterRaM(id = 1, name = "Rick Sanchez", status = "Alive", species = "Human")
        )
        viewModel.uiState.test {
            skipItems(1)
            viewModel.updateFilter(CharacterFilter(showFavourites = true))
            val result = awaitItem()
            assertTrue(result is UiState.Success)
            assertEquals(1, result.data().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sem internet ativa favoritos automaticamente`() = runTest {
        every { connectivityObserver.isConnected } returns flowOf(false)
        viewModel = HomeViewModel(
            repository = fakeRepository,
            favouriteRepository = fakeFavouriteRepository,
            connectivityObserver = connectivityObserver
        )
        viewModel.uiState.test {
            advanceUntilIdle()
            assertTrue(viewModel.filter.value.showFavourites)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sem internet nao e possivel desmarcar favoritos`() = runTest {
        every { connectivityObserver.isConnected } returns flowOf(false)
        viewModel = HomeViewModel(
            repository = fakeRepository,
            favouriteRepository = fakeFavouriteRepository,
            connectivityObserver = connectivityObserver
        )
        viewModel.uiState.test {
            advanceUntilIdle()
            viewModel.updateFilter(CharacterFilter(showFavourites = false))
            advanceUntilIdle()
            assertTrue(viewModel.filter.value.showFavourites)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `quando nao ha mais paginas nao faz nova chamada`() = runTest {
        viewModel.uiState.test {
            skipItems(1)
            val callsBefore = fakeRepository.callCount
            viewModel.loadNextPage()
            advanceUntilIdle()
            assertEquals(callsBefore, fakeRepository.callCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `erro na API resulta em estado de erro`() = runTest {
        fakeRepository.shouldThrowError = true
        viewModel = HomeViewModel(
            repository = fakeRepository,
            favouriteRepository = fakeFavouriteRepository,
            connectivityObserver = connectivityObserver
        )
        viewModel.uiState.test {
            skipItems(1)
            assertTrue(awaitItem() is UiState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}