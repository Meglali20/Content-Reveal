package com.oussamameg.contentreveal

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.unit.IntSize

/**
 * Manages the state of a particle system, maintaining a collection of particles and handling their initialization.
 * This class ensures particles persist positioning of particles across recompositions.
 */
class ParticleState {
    private var _particles = mutableStateListOf<Particle>()
    val particles: List<Particle> = _particles

    /**
     * @param size The size of the container where particles will be rendered (so that the particles don't go out of bounds)
     * @param count The number of particles to create
     */
    fun initializeIfNeeded(size: IntSize, count: Int) {
        if (_particles.isEmpty()) {
            _particles.addAll(List(count) {
                Particle(
                    currentPosition = generatePosition(size),
                    targetPosition = generatePosition(size),
                    initialPosition = generatePosition(size)
                )
            })
        }
    }
}