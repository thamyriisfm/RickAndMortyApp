package com.example.rickandmortyapp

import app.cash.turbine.test
import com.example.rickandmortyapp.core.UiState
import com.example.rickandmortyapp.data.model.CharacterRaM
import com.example.rickandmortyapp.fake.FakeFavouriteRepository
import com.example.rickandmortyapp.ui.favourites.FavouritesViewModel
import com.example.rickandmortycore.coreUi.model.CharacterDisplayModel
import com.example.rickandmortycore.coreUi.model.CharacterFilter
import com.example.rickandmortycore.coreUi.model.SortOption
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavouritesViewModelTest {

    @get:Rule
    val coroutineRule = StandardTestDispatcherRule()

    private lateinit var fakeRepository: FakeFavouriteRepository
    private lateinit var viewModel: FavouritesViewModel

    private val rick = CharacterRaM(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        gender = "Male"
    )
    private val morty = CharacterRaM(
        id = 2,
        name = "Morty Smith",
        status = "Alive",
        species = "Human",
        gender = "Male"
    )
    private val beth = CharacterRaM(
        id = 3,
        name = "Beth Smith",
        status = "Alive",
        species = "Human",
        gender = "Female"
    )
    private val birdperson = CharacterRaM(
        id = 4,
        name = "Birdperson",
        status = "Dead",
        species = "Bird-Person",
        gender = "Male"
    )

    @Before
    fun setup() {
        fakeRepository = FakeFavouriteRepository()
        viewModel = FavouritesViewModel(fakeRepository)
    }

    private fun UiState<List<CharacterDisplayModel>>.data(): List<CharacterDisplayModel> =
        (this as UiState.Success).data

    @Test
    fun `estado inicial e loading`() = runTest {
        viewModel.uiState.test {
            assertTrue(awaitItem() is UiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ao adicionar favorito aparece na lista`() = runTest {
        viewModel.uiState.test {
            skipItems(1)
            fakeRepository.add(rick)
            assertEquals(1, awaitItem().data().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `lista com um unico item nao esta vazia`() = runTest {
        fakeRepository.add(rick)
        viewModel.uiState.test {
            skipItems(1)
            val result = awaitItem()
            assertTrue(result is UiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `remover favorito remove da lista`() = runTest {
        fakeRepository.add(rick)
        viewModel.uiState.test {
            skipItems(2)
            viewModel.removeFavourite(rick)
            assertTrue(awaitItem() is UiState.Empty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filtro por nome funciona corretamente`() = runTest {
        fakeRepository.add(rick)
        fakeRepository.add(morty)
        viewModel.uiState.test {
            skipItems(2)
            viewModel.searchCharacter("Rick")
            val result = awaitItem().data()
            assertEquals(1, result.size)
            assertEquals("Rick Sanchez", result.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `busca por nome vazia restaura lista completa`() = runTest {
        fakeRepository.add(rick)
        fakeRepository.add(morty)
        viewModel.uiState.test {
            skipItems(2)
            viewModel.searchCharacter("Rick")
            skipItems(1)
            viewModel.searchCharacter("")
            val result = awaitItem().data()
            assertEquals(2, result.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filtro por gender funciona corretamente`() = runTest {
        fakeRepository.add(rick)
        fakeRepository.add(beth)
        viewModel.uiState.test {
            skipItems(2)
            viewModel.updateFilter(CharacterFilter(gender = "Female"))
            val result = awaitItem().data()
            assertEquals(1, result.size)
            assertEquals("Beth Smith", result.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filtro por status funciona corretamente`() = runTest {
        fakeRepository.add(rick)
        fakeRepository.add(birdperson)
        viewModel.uiState.test {
            skipItems(2)
            viewModel.updateFilter(CharacterFilter(status = "Dead"))
            val result = awaitItem().data()
            assertEquals(1, result.size)
            assertEquals("Birdperson", result.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `combinacao de filtros funciona corretamente`() = runTest {
        fakeRepository.add(rick)
        fakeRepository.add(beth)
        fakeRepository.add(birdperson)
        viewModel.uiState.test {
            skipItems(2)
            viewModel.updateFilter(CharacterFilter(gender = "Female", status = "Alive"))
            val result = awaitItem().data()
            assertEquals(1, result.size)
            assertEquals("Beth Smith", result.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `limpar filtros restaura lista completa`() = runTest {
        fakeRepository.add(rick)
        fakeRepository.add(beth)
        viewModel.uiState.test {
            skipItems(2)
            viewModel.updateFilter(CharacterFilter(gender = "Female"))
            assertEquals(1, awaitItem().data().size)
            viewModel.updateFilter(CharacterFilter())
            assertEquals(2, awaitItem().data().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ordenacao por nome funciona corretamente`() = runTest {
        fakeRepository.add(morty)
        fakeRepository.add(beth)
        fakeRepository.add(rick)
        viewModel.uiState.test {
            skipItems(1)
            viewModel.updateFilter(CharacterFilter(sortOption = SortOption.NAME))
            val result = awaitItem().data()
            assertEquals(
                listOf("Beth Smith", "Morty Smith", "Rick Sanchez"),
                result.map { it.name }
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ordenacao por status funciona corretamente`() = runTest {
        fakeRepository.add(rick)
        fakeRepository.add(birdperson)
        viewModel.uiState.test {
            skipItems(2)
            viewModel.updateFilter(CharacterFilter(sortOption = SortOption.STATUS))
            val result = awaitItem().data()
            assertEquals(listOf("Alive", "Dead"), result.map { it.status })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ordenacao por species funciona corretamente`() = runTest {
        fakeRepository.add(rick)
        fakeRepository.add(birdperson)
        viewModel.uiState.test {
            skipItems(1)
            viewModel.updateFilter(CharacterFilter(sortOption = SortOption.SPECIES))
            val result = awaitItem().data()
            assertEquals(listOf("Bird-Person", "Human"), result.map { it.species })
            cancelAndIgnoreRemainingEvents()
        }
    }
}