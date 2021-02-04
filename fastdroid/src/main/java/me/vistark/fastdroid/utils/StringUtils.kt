package me.vistark.fastdroid.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

object StringUtils {
    fun String.like(regexStr: String, groupId: Int = 0): String {
        val pattern: Pattern = Pattern.compile(regexStr)
        val matcher: Matcher = pattern.matcher(this)
        if (matcher.find()) {
            return matcher.group(groupId) ?: ""
        }
        return ""
    }
}