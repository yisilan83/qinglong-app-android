        val children = file.children
        if (isDir && !children.isNullOrEmpty()) {
            AnimatedVisibility(expanded) {
                Column {
                    val sorted = children.sortedWith(
                        compareByDescending<ScriptFile> { it.isDirectory }.thenBy { it.title }
                    )
                    sorted.forEach { child ->
                        ScriptTreeItem(child, depth + 1, onClick, onLongClick)
                    }
                }
            }
        }