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
