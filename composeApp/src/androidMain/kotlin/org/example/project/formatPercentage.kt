package org.example.project

actual fun formatPercentage(value: Float): String {
    return "%.1f%%".format(value)
}
