package com.example.rpgaudiomixer.app.motion

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class MotionTransitionType {
    CONTAINER_TRANSFORM,
    SHARED_X_AXIS,
    SHARED_Z_AXIS,
    SHARED_Y_AXIS_ENTER,
    SHARED_Y_AXIS_EXIT,
}

data class MotionTransitionState(
    val type: MotionTransitionType? = null,
    val source: String? = null,
    val target: String? = null,
)

@Singleton
class MotionSystemStateRepository @Inject constructor() {
    private val _lastTransition = MutableStateFlow(MotionTransitionState())
    val lastTransition: StateFlow<MotionTransitionState> = _lastTransition.asStateFlow()

    fun record(type: MotionTransitionType, source: String, target: String) {
        _lastTransition.value = MotionTransitionState(type = type, source = source, target = target)
    }
}
