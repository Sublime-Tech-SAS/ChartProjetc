package org.example.project

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

actual fun formatPercentage(value: Float): String {
    return NSString.stringWithFormat("%.1f%%", value)
}
