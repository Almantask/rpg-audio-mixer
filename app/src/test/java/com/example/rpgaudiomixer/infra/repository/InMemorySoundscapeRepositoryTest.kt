package com.example.rpgaudiomixer.infra.repository

import app.cash.turbine.test
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InMemorySoundscapeRepositoryTest {

    private lateinit var repository: InMemorySoundscapeRepository

    @BeforeEach
    fun setUp() {
        repository = InMemorySoundscapeRepository()
    }

    @Test
    fun getAllCategories_returns_empty_list_initially() = runTest {
        // Act
        val categories = repository.getAllCategories()

        // Assert
        assertThat(categories).isEmpty()
    }

    @Test
    fun createCategory_creates_and_returns_new_category() = runTest {
        // Act
        val category = repository.createCategory("Forest", null, "Nature")

        // Assert
        assertThat(category.name).isEqualTo("Forest")
        assertThat(category.themeLabel).isEqualTo("Nature")
        assertThat(category.id).isNotEmpty()
    }

    @Test
    fun createCategory_adds_category_to_list() = runTest {
        // Act
        repository.createCategory("Forest")
        val categories = repository.getAllCategories()

        // Assert
        assertThat(categories).hasSize(1)
        assertThat(categories[0].name).isEqualTo("Forest")
    }

    @Test
    fun getCategoryById_returns_category_when_exists() = runTest {
        // Arrange
        val created = repository.createCategory("Combat")

        // Act
        val found = repository.getCategoryById(created.id)

        // Assert
        assertThat(found).isNotNull
        assertThat(found?.id).isEqualTo(created.id)
        assertThat(found?.name).isEqualTo("Combat")
    }

    @Test
    fun getCategoryById_returns_null_when_not_exists() = runTest {
        // Act
        val found = repository.getCategoryById("non-existent-id")

        // Assert
        assertThat(found).isNull()
    }

    @Test
    fun updateCategory_updates_existing_category() = runTest {
        // Arrange
        val category = repository.createCategory("Mystery")
        val updated = category.copy(name = "Mystery Updated", themeLabel = "Enigma")

        // Act
        repository.updateCategory(updated)
        val found = repository.getCategoryById(category.id)

        // Assert
        assertThat(found?.name).isEqualTo("Mystery Updated")
        assertThat(found?.themeLabel).isEqualTo("Enigma")
    }

    @Test
    fun deleteCategory_removes_category() = runTest {
        // Arrange
        val category = repository.createCategory("Boss")

        // Act
        repository.deleteCategory(category.id)
        val categories = repository.getAllCategories()

        // Assert
        assertThat(categories).isEmpty()
    }

    @Test
    fun observeAllCategories_emits_current_categories() = runTest {
        // Arrange
        repository.createCategory("Forest")
        repository.createCategory("Combat")

        // Act & Assert
        repository.observeAllCategories().test {
            val categories = awaitItem()
            assertThat(categories).hasSize(2)
            assertThat(categories.map { it.name }).containsExactlyInAnyOrder("Forest", "Combat")
        }
    }

    @Test
    fun createTrack_creates_and_returns_new_track() = runTest {
        // Arrange
        val category = repository.createCategory("Forest")

        // Act
        val track = repository.createTrack(
            categoryId = category.id,
            name = "Moonroots",
            filePath = "/forest/moonroots.mp3",
            intensityLevel = IntensityLevel.I,
            mixVolume = 0.8f
        )

        // Assert
        assertThat(track.name).isEqualTo("Moonroots")
        assertThat(track.categoryId).isEqualTo(category.id)
        assertThat(track.intensityLevel).isEqualTo(IntensityLevel.I)
        assertThat(track.mixVolume).isEqualTo(0.8f)
        assertThat(track.id).isNotEmpty()
    }

    @Test
    fun getTracksByCategory_returns_tracks_for_category() = runTest {
        // Arrange
        val category = repository.createCategory("Combat")
        repository.createTrack(category.id, "Steel Cathedral", "/combat/steel.mp3", IntensityLevel.III)
        repository.createTrack(category.id, "Storm Crown", "/combat/storm.mp3", IntensityLevel.III)

        // Act
        val tracks = repository.getTracksByCategory(category.id)

        // Assert
        assertThat(tracks).hasSize(2)
        assertThat(tracks.map { it.name }).containsExactlyInAnyOrder("Steel Cathedral", "Storm Crown")
    }

    @Test
    fun getTracksByCategoryAndIntensity_filters_by_intensity() = runTest {
        // Arrange
        val category = repository.createCategory("Mystery")
        repository.createTrack(category.id, "Track I", "/path.mp3", IntensityLevel.I)
        repository.createTrack(category.id, "Track II", "/path.mp3", IntensityLevel.II)
        repository.createTrack(category.id, "Track III", "/path.mp3", IntensityLevel.III)

        // Act
        val tracksLevel2 = repository.getTracksByCategoryAndIntensity(category.id, IntensityLevel.II)

        // Assert
        assertThat(tracksLevel2).hasSize(1)
        assertThat(tracksLevel2[0].name).isEqualTo("Track II")
    }

    @Test
    fun updateTrack_updates_existing_track() = runTest {
        // Arrange
        val category = repository.createCategory("Boss")
        val track = repository.createTrack(category.id, "Storm Ritual", "/path.mp3", IntensityLevel.I)
        val updated = track.copy(name = "Storm Ritual Updated", intensityLevel = IntensityLevel.II, mixVolume = 0.5f)

        // Act
        repository.updateTrack(updated)
        val tracks = repository.getTracksByCategory(category.id)

        // Assert
        assertThat(tracks).hasSize(1)
        assertThat(tracks[0].name).isEqualTo("Storm Ritual Updated")
        assertThat(tracks[0].intensityLevel).isEqualTo(IntensityLevel.II)
        assertThat(tracks[0].mixVolume).isEqualTo(0.5f)
    }

    @Test
    fun deleteTrack_removes_track() = runTest {
        // Arrange
        val category = repository.createCategory("Forest")
        val track = repository.createTrack(category.id, "Feywood", "/path.mp3", IntensityLevel.I)

        // Act
        repository.deleteTrack(track.id)
        val tracks = repository.getTracksByCategory(category.id)

        // Assert
        assertThat(tracks).isEmpty()
    }

    @Test
    fun deleteCategory_also_deletes_all_tracks() = runTest {
        // Arrange
        val category = repository.createCategory("Combat")
        val track = repository.createTrack(category.id, "Battle", "/path.mp3", IntensityLevel.II)

        // Act
        repository.deleteCategory(category.id)
        val tracks = repository.getTracksByCategory(category.id)

        // Assert
        assertThat(tracks).isEmpty()
    }

    @Test
    fun observeTracksByCategory_emits_current_tracks() = runTest {
        // Arrange
        val category = repository.createCategory("Mystery")
        repository.createTrack(category.id, "Echoes", "/path.mp3", IntensityLevel.I)
        repository.createTrack(category.id, "Lanterns", "/path.mp3", IntensityLevel.III)

        // Act & Assert
        repository.observeTracksByCategory(category.id).test {
            val tracks = awaitItem()
            assertThat(tracks).hasSize(2)
            assertThat(tracks.map { it.name }).containsExactlyInAnyOrder("Echoes", "Lanterns")
        }
    }

    @Test
    fun getAllCategories_includes_track_counts_by_level() = runTest {
        // Arrange
        val category = repository.createCategory("Forest")
        repository.createTrack(category.id, "Track1", "/path.mp3", IntensityLevel.I)
        repository.createTrack(category.id, "Track2", "/path.mp3", IntensityLevel.I)
        repository.createTrack(category.id, "Track3", "/path.mp3", IntensityLevel.II)

        // Act
        val categories = repository.getAllCategories()

        // Assert
        assertThat(categories).hasSize(1)
        val forestCategory = categories[0]
        assertThat(forestCategory.trackCountByLevel[IntensityLevel.I]).isEqualTo(2)
        assertThat(forestCategory.trackCountByLevel[IntensityLevel.II]).isEqualTo(1)
        assertThat(forestCategory.trackCountByLevel[IntensityLevel.III]).isEqualTo(0)
    }
}
