            AnimatedVisibility(
                visible = uiState is LoginUiState.NeedTwoFactor,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {