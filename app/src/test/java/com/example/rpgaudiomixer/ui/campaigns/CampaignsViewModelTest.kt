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

import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.repository.CampaignRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import app.cash.turbine.test

@OptIn(ExperimentalCoroutinesApi::class)
class CampaignsViewModelTest {
    private val campaignRepository: CampaignRepository = mockk(relaxed = true)

    @Test
    fun `emits loading then success with campaigns`() = runTest {
        // Arrange
        val campaigns = listOf(
            Campaign(1, "C1", null, 123L),
            Campaign(2, "C2", null, 456L)
        )
        coEvery { campaignRepository.observeAll() } returns flowOf(campaigns)

        // Act
        val viewModel = CampaignsViewModel(campaignRepository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(CampaignsUiState.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(CampaignsUiState.Success::class.java)
            assertThat((success as CampaignsUiState.Success).campaigns).isEqualTo(campaigns)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits loading then success with no campaigns`() = runTest {
        // Arrange
        coEvery { campaignRepository.observeAll() } returns flowOf(emptyList())

        // Act
        val viewModel = CampaignsViewModel(campaignRepository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(CampaignsUiState.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(CampaignsUiState.Success::class.java)
            assertThat((success as CampaignsUiState.Success).campaigns).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits error when repository throws`() = runTest {
        // Arrange
        val errorMsg = "fail"
        coEvery { campaignRepository.observeAll() } returns kotlinx.coroutines.flow.flow { throw RuntimeException(errorMsg) }

        // Act
        val viewModel = CampaignsViewModel(campaignRepository)

        // Assert
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(CampaignsUiState.Loading::class.java)
            val error = awaitItem()
            assertThat(error).isInstanceOf(CampaignsUiState.Error::class.java)
            assertThat((error as CampaignsUiState.Error).message).contains(errorMsg)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
