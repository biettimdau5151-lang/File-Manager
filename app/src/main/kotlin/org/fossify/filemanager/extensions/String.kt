package org.fossify.filemanager.extensions

fun String.isZipFile() = endsWith(".zip", true)

fun String.isTextFile(): Boolean {
    val textExtensions = listOf(
        ".txt", ".text", ".log", ".md", ".markdown", ".csv", ".tsv", ".json", ".xml",
        ".html", ".htm", ".css", ".js", ".ts", ".kt", ".java", ".py", ".rb", ".php",
        ".sh", ".bat", ".cmd", ".ps1", ".yaml", ".yml", ".toml", ".ini", ".cfg",
        ".conf", ".properties", ".gradle", ".gitignore", ".dockerfile", ".sql",
        ".c", ".cpp", ".h", ".hpp", ".cs", ".go", ".rs", ".swift", ".r", ".m"
    )
    return textExtensions.any { endsWith(it, true) }
}

fun String.isVideoFile(): Boolean {
    val videoExtensions = listOf(
        ".mp4", ".mkv", ".avi", ".mov", ".wmv", ".flv", ".webm", ".m4v",
        ".3gp", ".3g2", ".mpg", ".mpeg", ".ts", ".vob", ".ogv", ".rm",
        ".rmvb", ".asf", ".divx", ".f4v"
    )
    return videoExtensions.any { endsWith(it, true) }
}

fun String.isAudioFile(): Boolean {
    val audioExtensions = listOf(
        ".mp3", ".wav", ".flac", ".aac", ".ogg", ".wma", ".m4a",
        ".opus", ".amr", ".mid", ".midi", ".aiff"
    )
    return audioExtensions.any { endsWith(it, true) }
}

fun String.isPathInHiddenFolder(): Boolean {
    val parts = split("/")
    for (i in 1 until parts.size - 1) {
        val part = parts[i]
        val isHidden = part.startsWith(".") && part != "." && part != ".." && part.isNotEmpty()
        if (isHidden) {
            return true
        }
    }
    return false
}
