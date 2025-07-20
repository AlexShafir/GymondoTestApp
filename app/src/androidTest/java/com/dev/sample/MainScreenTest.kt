package com.dev.sample

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dev.sample.app.ui.screens.MainScreen
import com.dev.sample.app.ui.theme.SampleTheme
import com.dev.sample.app.viewmodel.MainScreenVM
import com.dev.sample.data.remote.PicsumItem
import com.dev.sample.data.repository.IPicsumRepository
import com.dev.sample.data.repository.ServerUnavailable
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun testMainScreen_success() {
        val canonicalList = listOf(
            PicsumItem("1", "Author A", "https://picsum.photos/id/1/5000/3333"),
            PicsumItem("2", "Author B", "https://picsum.photos/id/2/5000/3333"),
        )

        val repo = object : IPicsumRepository {
            override suspend fun getDefaultImages() = canonicalList
        }

        val vm = MainScreenVM(repo)

        composeTestRule.setContent {
            FullMainScreen(vm)
        }

        composeTestRule.onNodeWithText("Fetch").performClick()

        composeTestRule.onNodeWithText("by Author A").assertIsDisplayed()
        composeTestRule.onNodeWithText("by Author B").assertIsDisplayed()
    }

    @Test
    fun testMainScreen_fail() {
        val repo = object : IPicsumRepository {
            override suspend fun getDefaultImages() = throw ServerUnavailable()
        }

        val vm = MainScreenVM(repo)

        composeTestRule.setContent {
            FullMainScreen(vm)
        }

        composeTestRule.onNodeWithText("Fetch").performClick()

        composeTestRule.onNodeWithTag("snackbar").assertIsDisplayed()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FullMainScreen(vm: MainScreenVM) {
        SampleTheme {

            val snackbarHostState = remember { SnackbarHostState() }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.testTag("snackbar")
                    )
                },
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        title = {
                            Text(stringResource(R.string.app_name))
                        }
                    )
                }
            ) { innerPadding ->
                MainScreen(
                    vm = vm,
                    modifier = Modifier.padding(innerPadding),
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}