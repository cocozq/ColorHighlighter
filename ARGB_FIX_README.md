# ARGB Color Support Fix

## 问题描述

原始的Color Highlighter插件在处理ARGB颜色格式时存在问题，特别是对于以`0x`前缀开头的8位十六进制颜色值。例如：

```kotlin
const val RIGHT_BG_COLOR = (0xFFAB9EF1).toInt()
const val CANDI_BG_COLOR = (0xFFC5C7D9).toInt()
const val ARROW_COLOR = (0xff757575).toInt()
```

以及JSON中的颜色值：
```json
{
  "FontColor": "0x00FFFFFF",
  "RingBgColor": "0xFFAB9EF1",
  "CandiBgColor": "0xFFC5C7D9"
}
```

## 问题根源

1. **颜色通道顺序错误**: 原始的`getArgb`方法将Alpha通道作为第四个参数传递给Color构造函数，但实际上应该正确处理ARGB格式中Alpha在前的顺序。

2. **颜色值范围问题**: Color构造函数需要0.0-1.0范围的浮点数，而不是0-255的整数值。

3. **格式识别不准确**: 对于`0x`前缀的8位十六进制数，应该优先按照ARGB格式处理，而不是依赖全局配置。

## 解决方案

### 1. 修复ColorUtils.kt中的getArgb方法

**修改前:**
```kotlin
private fun getArgb(rgb: String): Color {
  val a = rgb.substring(0, 2).toInt(16)
  val r = rgb.substring(2, 4).toInt(16)
  val g = rgb.substring(4, 6).toInt(16)
  val b = rgb.substring(6, 8).toInt(16)

  return try {
    Color(r, g, b, a)  // 错误：Alpha位置不对，且值未标准化
  } catch (e: Exception) {
    Color(a, r, g)     // 错误：异常处理逻辑不正确
  }
}
```

**修改后:**
```kotlin
private fun getArgb(rgb: String): Color {
  val a = rgb.substring(0, 2).toInt(16)
  val r = rgb.substring(2, 4).toInt(16)
  val g = rgb.substring(4, 6).toInt(16)
  val b = rgb.substring(6, 8).toInt(16)

  return try {
    // For ARGB format, alpha should be normalized to 0.0-1.0 range
    val normalizedAlpha = a / 255.0f
    Color(r / 255.0f, g / 255.0f, b / 255.0f, normalizedAlpha)
  } catch (e: Exception) {
    Color(r, g, b)
  }
}
```

### 2. 修复getRgba方法

同样修复了RGBA格式的颜色值标准化问题。

### 3. 添加专用的getARGB方法

```kotlin
/** Parse ARGB color format specifically (Alpha-Red-Green-Blue) */
fun getARGB(hex: String): Color {
  val rgb = normalizeRGB(hex, 8)
  return getArgb(rgb)
}
```

### 4. 改进HexColorParser的格式识别

**修改前:**
```kotlin
8 + offset -> ColorUtils.getRGBA(text.substring(offset)) // RRGGBBAA
```

**修改后:**
```kotlin
8 + offset -> {
  // For 0x prefix, treat as ARGB format (common in Android/Kotlin)
  // For # prefix or no prefix, use the configurable RGBA/ARGB logic
  if (text.startsWith("0x") || text.startsWith("0X")) {
    ColorUtils.getARGB(text.substring(offset)) // ARGB format
  } else {
    ColorUtils.getRGBA(text.substring(offset)) // RRGGBBAA or AARRGGBB based on config
  }
}
```

## 测试用例

创建了以下测试文件来验证修复效果：

1. `test_argb_colors.kt` - Kotlin ARGB颜色测试
2. `test_argb_colors.json` - JSON ARGB颜色测试

## 修改的文件

1. `src/main/java/com/mallowigi/utils/ColorUtils.kt`
   - 修复了`getArgb`方法的颜色通道处理
   - 修复了`getRgba`方法的颜色值标准化
   - 添加了专用的`getARGB`方法

2. `src/main/java/com/mallowigi/search/parsers/HexColorParser.kt`
   - 改进了8位十六进制颜色的格式识别逻辑
   - 对`0x`前缀的颜色优先使用ARGB格式

## 预期效果

修复后，以下颜色应该能够正确显示：

- `0xFFAB9EF1` - 显示为紫色，完全不透明
- `0xFFC5C7D9` - 显示为浅灰色，完全不透明
- `0xff757575` - 显示为灰色，完全不透明
- `0x80FF0000` - 显示为红色，50%透明度
- `0x00FFFFFF` - 显示为白色，完全透明

## 兼容性

这些修改保持了向后兼容性：
- 原有的RGB和RGBA格式仍然正常工作
- 配置选项`isRgbaEnabled`仍然有效
- 只是改进了ARGB格式的处理逻辑
