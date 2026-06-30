package com.app.instagramclone.presentation.auth

import app.cash.turbine.test
import com.app.instagramclone.domain.repository.AuthRepository
import com.app.instagramclone.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class AuthViewModelTest {

    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository = mockk<AuthRepository>()
    private lateinit var viewModel: AuthViewModel

    @BeforeEach
    fun setup() {
        viewModel = AuthViewModel(authRepository)
    }

    @Test
    fun `estado inicial e Idle`() {
        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `login com sucesso emite Loading depois Success`() = runTest {
        coEvery { authRepository.login(any()) } returns Result.success(Unit)

        viewModel.uiState.test {
            assertEquals(AuthUiState.Idle, awaitItem())

            viewModel.login("usuario")

            assertEquals(AuthUiState.Loading, awaitItem())
            assertEquals(AuthUiState.Success, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login com falha emite Loading depois Error`() = runTest {
        coEvery { authRepository.login(any()) } returns
            Result.failure(Exception("Credenciais inválidas"))

        viewModel.uiState.test {
            assertEquals(AuthUiState.Idle, awaitItem())

            viewModel.login("usuario")

            assertEquals(AuthUiState.Loading, awaitItem())
            val error = awaitItem()
            assertTrue(error is AuthUiState.Error)
            assertEquals("Credenciais inválidas", (error as AuthUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login com username vazio emite Error sem chamar repositorio`() = runTest {
        viewModel.uiState.test {
            assertEquals(AuthUiState.Idle, awaitItem())

            viewModel.login("")

            val error = awaitItem()
            assertTrue(error is AuthUiState.Error)
            coVerify(exactly = 0) { authRepository.login(any()) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `resetState volta para Idle`() = runTest {
        coEvery { authRepository.login(any()) } returns Result.success(Unit)
        viewModel.login("usuario")

        viewModel.resetState()

        assertEquals(AuthUiState.Idle, viewModel.uiState.value)
    }
}
