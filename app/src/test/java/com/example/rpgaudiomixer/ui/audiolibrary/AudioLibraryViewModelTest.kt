import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.FX
import com.example.rpgaudiomixer.domain.repository.SoundscapeCategoryRepository
import com.example.rpgaudiomixer.domain.repository.FXRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class AudioLibraryViewModelTest {
    private val soundscapeCategoryRepository: SoundscapeCategoryRepository = mockk(relaxed = true)
    private val fxRepository: FXRepository = mockk(relaxed = true)

    @Test
    fun `emits loading then success with data`() = runTest {
        // Arrange
        val soundscapes = listOf(SoundscapeCategory(1, "Cat1", emptyList()))
        val fx = listOf(FX(1, "FX1", "uri1", listOf("tag1")))
        coEvery { soundscapeCategoryRepository.observeAll() } returns flowOf(soundscapes)
        coEvery { fxRepository.observeAll() } returns flowOf(fx)

        // Act
        val viewModel = AudioLibraryViewModel(soundscapeCategoryRepository, fxRepository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(AudioLibraryUiState.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(AudioLibraryUiState.Success::class.java)
            val s = success as AudioLibraryUiState.Success
            assertThat(s.soundscapes).isEqualTo(soundscapes)
            assertThat(s.fx).isEqualTo(fx)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits loading then success with empty data`() = runTest {
        // Arrange
        coEvery { soundscapeCategoryRepository.observeAll() } returns flowOf(emptyList())
        coEvery { fxRepository.observeAll() } returns flowOf(emptyList())

        // Act
        val viewModel = AudioLibraryViewModel(soundscapeCategoryRepository, fxRepository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(AudioLibraryUiState.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(AudioLibraryUiState.Success::class.java)
            val s = success as AudioLibraryUiState.Success
            assertThat(s.soundscapes).isEmpty()
            assertThat(s.fx).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits error when repository throws`() = runTest {
        // Arrange
        val errorMsg = "fail"
        coEvery { soundscapeCategoryRepository.observeAll() } returns kotlinx.coroutines.flow.flow { throw RuntimeException(errorMsg) }
        coEvery { fxRepository.observeAll() } returns flowOf(emptyList())

        // Act
        val viewModel = AudioLibraryViewModel(soundscapeCategoryRepository, fxRepository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(AudioLibraryUiState.Loading::class.java)
            val error = awaitItem()
            assertThat(error).isInstanceOf(AudioLibraryUiState.Error::class.java)
            assertThat((error as AudioLibraryUiState.Error).message).contains(errorMsg)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
