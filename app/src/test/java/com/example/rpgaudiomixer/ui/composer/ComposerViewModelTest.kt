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
import com.example.rpgaudiomixer.domain.repository.SoundscapeCategoryRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class ComposerViewModelTest {
    private val repository: SoundscapeCategoryRepository = mockk(relaxed = true)

    @Test
    fun `emits loading then success with category`() = runTest {
        // Arrange
        val category = SoundscapeCategory(1, "Cat1", emptyList())
        coEvery { repository.observeAll() } returns flowOf(listOf(category))

        // Act
        val viewModel = ComposerViewModel(repository)
        viewModel.loadCategory(1)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(ComposerUiState.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(ComposerUiState.Success::class.java)
            assertThat((success as ComposerUiState.Success).category).isEqualTo(category)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits error when category not found`() = runTest {
        // Arrange
        coEvery { repository.observeAll() } returns flowOf(emptyList())

        // Act
        val viewModel = ComposerViewModel(repository)
        viewModel.loadCategory(1)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(ComposerUiState.Loading::class.java)
            val error = awaitItem()
            assertThat(error).isInstanceOf(ComposerUiState.Error::class.java)
            assertThat((error as ComposerUiState.Error).message).contains("not found")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits error when repository throws`() = runTest {
        // Arrange
        val errorMsg = "fail"
        coEvery { repository.observeAll() } returns kotlinx.coroutines.flow.flow { throw RuntimeException(errorMsg) }

        // Act
        val viewModel = ComposerViewModel(repository)
        viewModel.loadCategory(1)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(ComposerUiState.Loading::class.java)
            val error = awaitItem()
            assertThat(error).isInstanceOf(ComposerUiState.Error::class.java)
            assertThat((error as ComposerUiState.Error).message).contains(errorMsg)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
