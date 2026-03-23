package com.example.rickandmortyapp.di

import com.example.rickandmortyapp.data.local.FavouriteDatabase
import com.example.rickandmortyapp.data.network.ConnectivityObserver
import com.example.rickandmortyapp.data.network.HttpClientProvider
import com.example.rickandmortyapp.data.network.RaMApiClient
import com.example.rickandmortyapp.data.repository.FavouriteRepository
import com.example.rickandmortyapp.data.repository.FavouriteRepositoryInterface
import com.example.rickandmortyapp.data.repository.RaMRepository
import com.example.rickandmortyapp.data.repository.RaMRepositoryInterface
import com.example.rickandmortyapp.ui.details.DetailsViewModel
import com.example.rickandmortyapp.ui.favourites.FavouritesViewModel
import com.example.rickandmortyapp.ui.home.HomeViewModel
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Network
    single<HttpClient> { HttpClientProvider.client }
    single { RaMApiClient(get()) }

    // Database
    single { FavouriteDatabase.getInstance(androidContext()) }
    single { get<FavouriteDatabase>().favouriteDao() }

    // Connectivity
    single { ConnectivityObserver(androidContext()) }

    // Repositories
    single<RaMRepositoryInterface> { RaMRepository(apiClient = get()) }
    single<FavouriteRepositoryInterface> { FavouriteRepository(dao = get()) }

    // ViewModels
    viewModel {
        HomeViewModel(
            repository = get(),
            favouriteRepository = get(),
            connectivityObserver = get()
        )
    }
    viewModel { FavouritesViewModel(repository = get()) }
    viewModel { params -> DetailsViewModel(repository = get(), characterId = params.get()) }
}