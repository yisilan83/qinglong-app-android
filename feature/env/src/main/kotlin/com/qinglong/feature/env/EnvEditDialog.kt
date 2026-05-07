                OutlinedTextField(
                    value = value, onValueChange = { value = it },
                    label = { Text("值") },
                    minLines = 3,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth()
                )