private fun sortScripts(list: List<ScriptFile>): List<ScriptFile> {
        return list.sortedWith(compareByDescending<ScriptFile> { it.isDirectory }.thenBy { it.title })
            .map { file ->
                val children = file.children
                if (children != null) {
                    file.copy(children = sortScripts(children))
                } else file
            }
    }