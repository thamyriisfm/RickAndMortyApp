package com.example.rickandmortyapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.rickandmortyapp.data.model.CharacterRaM
import com.example.rickandmortyapp.data.model.CharacterType
import com.example.rickandmortyapp.extensions.toCharacterRaM
import com.example.rickandmortyapp.ui.details.DetailsScreen
import com.example.rickandmortyapp.ui.details.DetailsViewModel
import com.example.rickandmortyapp.ui.favourites.FavouritesScreen
import com.example.rickandmortyapp.ui.favourites.FavouritesViewModel
import com.example.rickandmortyapp.ui.home.HomeListScreen
import com.example.rickandmortyapp.ui.home.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.reflect.typeOf

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val homeViewModel: HomeViewModel = koinViewModel()
    val favouritesViewModel: FavouritesViewModel = koinViewModel()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Home,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable<AppRoutes.Home> {
                HomeListScreen(
                    onCharacterClicked = { displayModel ->
                        navController.navigate(
                            AppRoutes.Details(displayModel.toCharacterRaM())
                        )
                    },
                    viewModel = homeViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable<AppRoutes.Favourites> {
                FavouritesScreen(
                    onCharacterClicked = { displayModel ->
                        navController.navigate(
                            AppRoutes.Details(displayModel.toCharacterRaM())
                        )
                    },
                    viewModel = favouritesViewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                )
            }
            composable<AppRoutes.Details>(
                typeMap = mapOf(typeOf<CharacterRaM>() to CharacterType)
            ) {
                val args = it.toRoute<AppRoutes.Details>()
                val detailsViewModel: DetailsViewModel = koinViewModel(
                    parameters = { parametersOf(args.characterRaM.id ?: 0L) }
                )
                DetailsScreen(
                    characterRaMSelected = args.characterRaM,
                    onBack = { navController.popBackStack() },
                    viewModel = detailsViewModel,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}