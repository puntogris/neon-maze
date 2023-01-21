package com.puntogris.neonmaze.utils

sealed class PlayerStates{
    object HasNewMoves : PlayerStates()
    object NotNewMoves : PlayerStates()
}
