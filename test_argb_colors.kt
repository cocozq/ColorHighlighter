// Test file for ARGB color parsing
// This file contains various ARGB color formats that should be correctly highlighted

class TestARGBColors {
    companion object {
        // Kotlin ARGB colors - these should show correct colors now
        const val RIGHT_BG_COLOR = (0xFFAB9EF1).toInt()  // Should show purple with full opacity
        const val CANDI_BG_COLOR = (0xFFC5C7D9).toInt()  // Should show light gray with full opacity
        const val ARROW_COLOR = (0xff757575).toInt()     // Should show gray with full opacity
        const val COMPOSE_FONT_COLOR = (0xFFFF4081).toInt() // Should show pink with full opacity
        const val COMPOSE_BG_COLOR = (0xe6eceff1).toInt()   // Should show light blue with some transparency
        
        // Additional test cases
        const val TRANSPARENT_RED = (0x80FF0000).toInt()    // Should show red with 50% transparency
        const val SEMI_TRANSPARENT_BLUE = (0xCC0000FF).toInt() // Should show blue with ~80% opacity
        const val FULLY_TRANSPARENT = (0x00FFFFFF).toInt()     // Should show white with full transparency
        
        // Hex string format
        const val HEX_ARGB_1 = 0xFFAB9EF1  // Purple
        const val HEX_ARGB_2 = 0x80FF0000  // Semi-transparent red
        const val HEX_ARGB_3 = 0xCC00FF00  // Semi-transparent green
    }
}

// JSON format test (as mentioned in the user's example)
/*
{
  "ThemeName": "默认",
  "FontColor": "0x00FFFFFF",
  "RingBgColor": "0xFFAB9EF1",
  "CandiBgColor": "0xFFC5C7D9",
  "ArrowColor": "0xFF757575",
  "ComposeFontColor": "0xFFFF4081",
  "ComposeBgColor": "0xE6ECEFF1",
  "StatusBarStyle": true,
  "IconHidden": false,
  "CandiHeight": 72
}
*/