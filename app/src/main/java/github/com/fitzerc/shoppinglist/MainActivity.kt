package github.com.fitzerc.shoppinglist

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
        Column(modifier = Modifier.fillMaxHeight()) {
        //Column {
            TopAppBar(
                title = { Text(text = "MagniList") },
                backgroundColor = MaterialTheme.colors.primary
            )

            ListLazyColumn(entries = entries, onEntryDeleteClick = {
                scope.launch {
                    viewModel.value.deleteEntry(it)
                }
            })

            BottomActionBar(
                curListId = viewModel.value.currentList.id,
                scope = scope,
                onAddClicked = {
                    scope.launch {
                        var tmpListId = it.listId

                        if (lists.isEmpty()) {
                            Log.d("DataAccess", "No lists found, creating default.")
                            val defaultList = ListDto(
                                id = UUID.randomUUID(),
                                name = "Default List",
                                description = "Default list automatically created by app.",
                            )

                            viewModel.value.addList(defaultList)
                            viewModel.value.currentList = defaultList

                            tmpListId = defaultList.id
                        }

                        viewModel.value.addEntry(EntryDto(
                            id = it.id,
                            listId = tmpListId,
                            name = it.name,
                            description = it.description,
                            sortOrder = it.sortOrder,
                            entryDate = it.entryDate
                        ))
                    }
                },
                onDelAllClicked = {
                    scope.launch {
                        viewModel.value.deleteAllEntries(it)
                    }
                }
            )
        }
    }
}

@Composable
fun Greeting(listName: String) {
    Text(text = "Current List: $listName")
}

@Composable
fun ListLazyColumn(entries: List<EntryDto>, onEntryDeleteClick: (EntryDto) -> Unit) {
    if (entries.isEmpty()) {
        Text(text = "Uh Oh! No lists found.")
    } else {
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(entries.count()) { index ->
                ListEntryRow(entries[index], onDeleteClick = {
                    onEntryDeleteClick(entries[index])
                })
            }
        }
    }
}

@Composable
fun ListEntryRow(entry: EntryDto, onDeleteClick: (EntryDto) -> Unit) {
    Row {
        Text(entry.name)
        Button(onClick = { onDeleteClick(entry) }) {
            Text(text = "X")
        }
    }
}

@Composable
fun BottomActionBar(
    curListId: UUID,
    scope: CoroutineScope,
    onAddClicked: (EntryDto) -> Unit,
    onDelAllClicked: (UUID) -> Unit
) {
    Row (modifier = Modifier
        .fillMaxSize()
        .border(2.dp, Color.Cyan)) {
        //Add Button
        Button(modifier = Modifier.background(MaterialTheme.colors.primary), onClick = {
            onAddClicked(
                EntryDto(
                    UUID.randomUUID(),
                    curListId,
                    "test entry",
                    "test description",
                )
            )
        }) {
            Text(text = "Add")
        }

        //Delete All Button
        Button(
            modifier = Modifier.background(MaterialTheme.colors.primary),
            onClick = { scope.launch { onDelAllClicked(curListId) } }) {
            Text(text = "Delete All")
        }
    }
}