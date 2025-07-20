package com.dev.sample.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.dev.sample.R
import com.dev.sample.app.ui.theme.SampleTheme
import com.dev.sample.app.viewmodel.MainScreenVM
import com.dev.sample.app.viewmodel.MainScreenVM.State
import com.dev.sample.data.remote.PicsumItem
import com.dev.sample.data.repository.ServerError
import com.dev.sample.data.repository.ServerUnavailable

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    vm: MainScreenVM = viewModel(),
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        // Fetch button
        Button(onClick = {
            vm.getImages()
        }) { Text(text = stringResource(R.string.fetch)) }


        // List
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            when (state) {
                is State.Loading -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }

                is State.Success -> {
                    val data = (state as State.Success<List<PicsumItem>>).data
                    if (data.isEmpty()) {
                        Text(text = stringResource(R.string.no_items_fetched))
                    } else {
                        LazyColumn {
                            items(data.size) { index ->
                                val item = data[index]

                                ListItem(item)
                                HorizontalDivider()
                            }
                        }
                    }
                }

                is State.Error, State.None -> {
                    Text(text = stringResource(R.string.no_items_fetched))
                }
            }

        }

        // Snackbar
        LaunchedEffect(
            state
        ) {
            if (state is State.Error) {
                val ex = (state as State.Error).exception

                val text = when (ex) {
                    is ServerError -> context.getString(R.string.server_error, ex.code)
                    is ServerUnavailable -> context.getString(R.string.server_unavailable)
                }

                snackbarHostState.showSnackbar(text)

            }
        }

    }
}

@Composable
fun ListItem(item: PicsumItem) {
    val contentDescription =
        stringResource(R.string.content_description_image, item.id, item.author)
    val title = stringResource(R.string.image_placeholder, item.id)
    val subtitle = stringResource(R.string.by_placeholder, item.author)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(72.dp)
            .padding(horizontal = 16.dp)
    ) {

        AsyncImage(
            modifier = Modifier.size(40.dp),
            model = item.download_url,
            placeholder = ColorPainter(Color.Gray),
            error = ColorPainter(Color.Red),
            contentDescription = contentDescription
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ListItemPreview() {
    val item = remember { PicsumItem("12", "Paul Jarvis", "https://picsum.photos/id/11/2500/1667") }

    ListItem(item)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val snackbarHostState = remember { SnackbarHostState() }

    SampleTheme {
        MainScreen(
            snackbarHostState = snackbarHostState
        )
    }
}