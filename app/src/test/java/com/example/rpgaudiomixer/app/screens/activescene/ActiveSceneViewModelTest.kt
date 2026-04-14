package com.example.rpgaudiomixer.app.screens.activescene

import androidx.lifecycle.SavedStateHandle
import com.example.rpgaudiomixer.app.domain.model.Scene
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActiveSceneViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val sceneId = 99L
    private val testScene = Scene(
        id = sceneId,
        name = "Haunted Forest",
        description = "A dark and eerie forest",
        tags = "horror,ambient",
    )

    private val mockRepository: SceneRepository = mockk {
        coEvery { getById(sceneId) } returns testScene
        every { observeAll() } returns emptyFlow()
        every { observeDeleted() } returns emptyFlow()
    }

    private val savedStateHandle = SavedStateHandle(mapOf("sceneId" to sceneId))

    private lateinit var viewModel: ActiveSceneViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        repository: SceneRepository = mockRepository,
    ): ActiveSceneViewModel {
        viewModel = ActiveSceneViewModel(repository, savedStateHandle)
        return viewModel
    }

    @Test
    fun `loads scene data and emits Ready state`() = runTest(testDispatcher) {
        // Arrange & Act
        val vm = createViewModel()

        // Assert
        val state = vm.uiState.value
        assertThat(state).isInstanceOf(ActiveSceneUiState.Ready::class.java)
        val ready = state as ActiveSceneUiState.Ready
        assertThat(ready.sceneName).isEqualTo("Haunted Forest")
        assertThat(ready.sceneDescription).isEqualTo("A dark and eerie forest")
    }

    @Test
    fun `emits Error when scene is not found`() = runTest(testDispatcher) {
        // Arrange
        val repository: SceneRepository = mockk {
            coEvery { getById(sceneId) } returns null
        }

        // Act
        val vm = createViewModel(repository)

        // Assert
        val state = vm.uiState.value
        assertThat(state).isInstanceOf(ActiveSceneUiState.Error::class.java)
        assertThat((state as ActiveSceneUiState.Error).message).isEqualTo("Scene not found")
    }

    @Test
    fun `emits Error when repository throws`() = runTest(testDispatcher) {
        // Arrange
        val repository: SceneRepository = mockk {
            coEvery { getById(sceneId) } throws RuntimeException("DB failure")
        }

        // Act
        val vm = createViewModel(repository)

        // Assert
        val state = vm.uiState.value
        assertThat(state).isInstanceOf(ActiveSceneUiState.Error::class.java)
        assertThat((state as ActiveSceneUiState.Error).message).isEqualTo("DB failure")
    }

    @Test
    fun `Ready state has default master volumes at 1_0`() = runTest(testDispatcher) {
        // Arrange & Act
        val vm = createViewModel()

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.masterAtmosphereVolume).isEqualTo(1.0f)
        assertThat(state.masterFxVolume).isEqualTo(1.0f)
    }

    @Test
    fun `Ready state has session lock disabled by default`() = runTest(testDispatcher) {
        // Arrange & Act
        val vm = createViewModel()

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.isSessionLocked).isFalse()
    }

    @Test
    fun `playCategory sets category isPlaying to true`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()
        setReadyStateWithCategories(vm)

        // Act
        vm.playCategory(1L)

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.categories.first { it.id == 1L }.isPlaying).isTrue()
    }

    @Test
    fun `pauseCategory sets category isPlaying to false`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()
        setReadyStateWithCategories(vm, isPlaying = true)

        // Act
        vm.pauseCategory(1L)

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.categories.first { it.id == 1L }.isPlaying).isFalse()
    }

    @Test
    fun `setCategoryIntensity changes intensity level when available`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()
        setReadyStateWithCategories(vm)

        // Act
        vm.setCategoryIntensity(1L, 2)

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.categories.first { it.id == 1L }.intensityLevel).isEqualTo(2)
    }

    @Test
    fun `setCategoryIntensity does not change when level is unavailable`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()
        setReadyStateWithCategories(vm, availableIntensities = setOf(1, 2))

        // Act
        vm.setCategoryIntensity(1L, 3)

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.categories.first { it.id == 1L }.intensityLevel).isEqualTo(1)
    }

    @Test
    fun `setCategoryMix changes mix volume`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()
        setReadyStateWithCategories(vm)

        // Act
        vm.setCategoryMix(1L, 0.5f)

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.categories.first { it.id == 1L }.mixVolume).isEqualTo(0.5f)
    }

    @Test
    fun `setCategoryMix clamps volume to valid range`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()
        setReadyStateWithCategories(vm)

        // Act
        vm.setCategoryMix(1L, 1.5f)

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.categories.first { it.id == 1L }.mixVolume).isEqualTo(1.0f)
    }

    @Test
    fun `setMasterAtmosphereVolume updates master volume`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()

        // Act
        vm.setMasterAtmosphereVolume(0.7f)

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.masterAtmosphereVolume).isEqualTo(0.7f)
    }

    @Test
    fun `setMasterAtmosphereVolume clamps to valid range`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()

        // Act
        vm.setMasterAtmosphereVolume(-0.5f)

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.masterAtmosphereVolume).isEqualTo(0.0f)
    }

    @Test
    fun `setMasterFxVolume updates master fx volume`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()

        // Act
        vm.setMasterFxVolume(0.3f)

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.masterFxVolume).isEqualTo(0.3f)
    }

    @Test
    fun `setMasterFxVolume clamps to valid range`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()

        // Act
        vm.setMasterFxVolume(2.0f)

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.masterFxVolume).isEqualTo(1.0f)
    }

    @Test
    fun `playFx sets fx button isPlaying to true`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()
        setReadyStateWithFx(vm)

        // Act
        vm.playFx(10L)

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.fxButtons.first { it.trackId == 10L }.isPlaying).isTrue()
    }

    @Test
    fun `stopAll stops all categories and fx`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()
        setReadyStateWithCategories(vm, isPlaying = true)
        setReadyStateWithFx(vm, isPlaying = true)

        // Act
        vm.stopAll()

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.categories.all { !it.isPlaying }).isTrue()
        assertThat(state.fxButtons.all { !it.isPlaying }).isTrue()
    }

    @Test
    fun `toggleSessionLock flips lock state`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()

        // Act
        vm.toggleSessionLock()

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.isSessionLocked).isTrue()
    }

    @Test
    fun `toggleSessionLock twice returns to unlocked`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()

        // Act
        vm.toggleSessionLock()
        vm.toggleSessionLock()

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.isSessionLocked).isFalse()
    }

    @Test
    fun `actions on non-Ready state are no-ops`() = runTest(testDispatcher) {
        // Arrange
        val repository: SceneRepository = mockk {
            coEvery { getById(sceneId) } returns null
        }
        val vm = createViewModel(repository)

        // Act — calling actions while in Error state
        vm.playCategory(1L)
        vm.setMasterAtmosphereVolume(0.5f)
        vm.toggleSessionLock()

        // Assert — state remains Error
        assertThat(vm.uiState.value).isInstanceOf(ActiveSceneUiState.Error::class.java)
    }

    @Test
    fun `playCategory does not affect other categories`() = runTest(testDispatcher) {
        // Arrange
        val vm = createViewModel()
        setReadyStateWithMultipleCategories(vm)

        // Act
        vm.playCategory(1L)

        // Assert
        val state = vm.uiState.value as ActiveSceneUiState.Ready
        assertThat(state.categories.first { it.id == 1L }.isPlaying).isTrue()
        assertThat(state.categories.first { it.id == 2L }.isPlaying).isFalse()
    }

    // --- Helpers ---

    private fun setReadyStateWithCategories(
        vm: ActiveSceneViewModel,
        isPlaying: Boolean = false,
        availableIntensities: Set<Int> = setOf(1, 2, 3),
    ) {
        val currentState = vm.uiState.value as ActiveSceneUiState.Ready
        val field = ActiveSceneViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val flow = field.get(vm) as kotlinx.coroutines.flow.MutableStateFlow<ActiveSceneUiState>
        flow.value = currentState.copy(
            categories = listOf(
                CategoryUiModel(
                    id = 1L,
                    name = "Forest Ambience",
                    isPlaying = isPlaying,
                    currentTrackName = "forest_01.mp3",
                    mixVolume = 0.75f,
                    intensityLevel = 1,
                    availableIntensities = availableIntensities,
                ),
            ),
        )
    }

    private fun setReadyStateWithMultipleCategories(vm: ActiveSceneViewModel) {
        val currentState = vm.uiState.value as ActiveSceneUiState.Ready
        val field = ActiveSceneViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val flow = field.get(vm) as kotlinx.coroutines.flow.MutableStateFlow<ActiveSceneUiState>
        flow.value = currentState.copy(
            categories = listOf(
                CategoryUiModel(
                    id = 1L,
                    name = "Forest Ambience",
                    isPlaying = false,
                    currentTrackName = null,
                    mixVolume = 0.75f,
                    intensityLevel = 1,
                    availableIntensities = setOf(1, 2, 3),
                ),
                CategoryUiModel(
                    id = 2L,
                    name = "Rain",
                    isPlaying = false,
                    currentTrackName = null,
                    mixVolume = 0.5f,
                    intensityLevel = 1,
                    availableIntensities = setOf(1, 2),
                ),
            ),
        )
    }

    private fun setReadyStateWithFx(
        vm: ActiveSceneViewModel,
        isPlaying: Boolean = false,
    ) {
        val currentState = vm.uiState.value as ActiveSceneUiState.Ready
        val field = ActiveSceneViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val flow = field.get(vm) as kotlinx.coroutines.flow.MutableStateFlow<ActiveSceneUiState>
        flow.value = currentState.copy(
            fxButtons = listOf(
                FxButtonUiModel(trackId = 10L, name = "Thunder", isPlaying = isPlaying),
                FxButtonUiModel(trackId = 11L, name = "Sword Clash", isPlaying = isPlaying),
            ),
        )
    }
}
