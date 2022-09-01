package github.com.fitzerc.shoppinglist

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.compiler.plugins.kotlin.ComposeFqNames.remember
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
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

    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    TopActionBar(currentList = viewModel.value.currentList, onAddClicked = {
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

                            viewModel.value.addEntry(
                                EntryDto(
                                    id = it.id,
                                    listId = tmpListId,
                                    name = it.name,
                                    description = it.description,
                                    sortOrder = it.sortOrder,
                                    entryDate = it.entryDate
                                )
                            )
                        }

                    })
                },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        /*
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "fab icon")
            }
        },
        drawerContent = { Text(text = "Drawer Menu 1") },
         */
        content = {
            val p = it
            ListLazyColumn(entries = entries, onEntryDeleteClick = {
                scope.launch {
                    viewModel.value.deleteEntry(it)
                }
            })
        },
        bottomBar = {
            BottomAppBar(backgroundColor = MaterialTheme.colors.primary) {
                BottomActionBar(
                    curListId = viewModel.value.currentList.id,
                    scope = scope,
                    onDelAllClicked = {
                        scope.launch {
                            viewModel.value.deleteAllEntries(it)
                        }
                    }
                )
            }
        }
    )
}

@Composable
fun Greeting(listName: String) {
    Text(text = "Current List: $listName")
}

@Composable
fun TopActionBar(
    currentList: ListDto,
    onAddClicked: (EntryDto) -> Unit
) {
    var newEntryText by remember { mutableStateOf(TextFieldValue("")) }

    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(0.dp)) {
        //TODO:
        //add icon?
        TextField(
            modifier = Modifier
                .fillMaxWidth(.75f)
                .padding(top = 1.dp),
            value = newEntryText,
            onValueChange = { newEntryText = it },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            trailingIcon = {
                if (newEntryText.text.isNotEmpty()) {
                    IconButton(onClick = { newEntryText = TextFieldValue("") }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            },
            //TODO: use theme instead of DarkGray
            placeholder = {
                Text(
                    color = Color.DarkGray,
                    text = "New Entry",
                )
            })

        Button(
            onClick = {
                onAddClicked(
                    EntryDto(
                        UUID.randomUUID(),
                        currentList.id,
                        newEntryText.text,
                        "",
                    )
                )

                newEntryText = TextFieldValue("")
            }) {
            Text(text = "+")
        }
    }
}

@Composable
fun ListLazyColumn(entries: List<EntryDto>, onEntryDeleteClick: (EntryDto) -> Unit) {
    if (entries.isEmpty()) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Text(text = "Uh Oh! No lists found.")
        }
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
    onDelAllClicked: (UUID) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //Delete All Button
        Button(
            modifier = Modifier.background(MaterialTheme.colors.primary),
            onClick = { scope.launch { onDelAllClicked(curListId) } }) {
            Text(text = "Delete All")
        }
    }
}