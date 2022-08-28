package github.com.fitzerc.shoppinglist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import github.com.fitzerc.shoppinglist.data.EntryDto
import github.com.fitzerc.shoppinglist.data.ListDto
import github.com.fitzerc.shoppinglist.data.access.room.ShoppingListDatabase
import github.com.fitzerc.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val scope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var db: ShoppingListDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = viewModels<MainActivityViewModel>()

        setContent {
            ShoppingListTheme {
                MainContent(viewModel)
            }
        }
    }

}

@Composable
fun MainContent(viewModel: Lazy<MainActivityViewModel>) {
    val lists = viewModel.value.lists.collectAsState().value
    val scope = CoroutineScope(Dispatchers.IO)

    val entries = viewModel.value.currentEntries.collectAsState().value

    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column {
            Greeting(if (lists.isEmpty()) "No Lists Found!" else lists.first().name)
            if (lists.isEmpty()) {
                Text(text = "Create a list!")
            } else {
                LazyColumn() {
                    items(entries.count()) { index ->
                        Row {
                            Text(text = if (entries.isEmpty()) "Nothing here" else entries[index].name)
                            Button(onClick = {
                                scope.launch {
                                    viewModel.value.deleteEntry(entries[index])
                                }
                            }) {
                                Text(text = "X")
                            }
                        }
                    }
                }
            }
            Row {
                Button(onClick = {
                    scope.launch {
                        val list: ListDto
                        if (lists.isEmpty()) {
                            list = ListDto(
                                UUID.randomUUID(),
                                "test",
                                "test desc"
                            )

                            viewModel.value.addList(list)
                            viewModel.value.currentList = list
                        }

                        viewModel.value.addEntry(
                            EntryDto(
                                UUID.randomUUID(),
                                viewModel.value.currentList.id,
                                "test entry",
                                "test description",
                            )
                        )
                    }
                }) {
                    Text(text = "Add")
                }
                Button(onClick = {
                    scope.launch {
                        viewModel.value.deleteAllEntries(viewModel.value.currentList.id)
                    }
                }) {
                    Text(text = "Delete All")
                }
            }
        }
    }
}

@Composable
fun Greeting(listName: String) {
    Text(text = "Current List: $listName")
}