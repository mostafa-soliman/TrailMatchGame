package com.example.matchingactiviy

import androidx.annotation.StringDef


class LetterFactory {
//    val letterAssets: String
//        get() = "letter/" + letter + "_bg.png"
//    val tracingAssets: String
//        get() = "trace/" + letter + "_tracing.png"
//    val strokeAssets: String
//        get() = "strokes/" + letter + "_PointsInfo.json"

    @StringDef(*[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z])
    @Retention(
        AnnotationRetention.SOURCE
    )
    annotation class Letter

    private var letter = A
    fun setLetter(@Letter letterChar: String) {
        letter = letterChar
    }

    companion object {
        const val A = "A"
        const val B = "B"
        const val C = "C"
        const val D = "D"
        const val E = "E"
        const val F = "F"
        const val G = "G"
        const val H = "H"
        const val I = "I"
        const val J = "J"
        const val K = "K"
        const val L = "L"
        const val M = "M"
        const val N = "N"
        const val O = "O"
        const val P = "P"
        const val Q = "Q"
        const val R = "R"
        const val S = "S"
        const val T = "T"
        const val U = "U"
        const val V = "V"
        const val W = "W"
        const val X = "X"
        const val Y = "Y"
        const val Z = "Z"
    }
}

