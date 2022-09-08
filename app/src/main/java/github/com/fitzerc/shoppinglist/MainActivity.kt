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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import github.com.fitzerc.shoppinglist.data.EntryDto
import github.com.fitzerc.shoppinglist.data.ListDto
import github.com.fitzerc.shoppinglist.data.access.room.ShoppingListDatabase
import github.com.fitzerc.shoppinglist.ui.screens.BottomActionBar
import github.com.fitzerc.shoppinglist.ui.screens.ListLazyColumn
import github.com.fitzerc.shoppinglist.ui.screens.ListsBottomSheet
import github.com.fitzerc.shoppinglist.ui.screens.TopActionBar
import github.com.fitzerc.shoppinglist.ui.theme.ShoppingListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //val scope = CoroutineScope(Dispatchers.IO)

    @Inject
    lateinit var db: ShoppingListDatabase

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = viewModels<MainActivityViewModel>()

        setContent {
            ShoppingListTheme {

                val lists = viewModel.value.lists.collectAsState().value
                //val scope = CoroutineScope(Dispatchers.IO)
                val scope = rememberCoroutineScope()

                val entries = viewModel.value.currentEntries.collectAsState().value

                val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
                val modalBottomSheetState =
                    rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

                var selectedEntry by remember { mutableStateOf(EntryDto(
                    UUID.randomUUID(),
                    name =  "",
                    description = "",
                    entryDate = Date(),
                    listId = UUID.randomUUID()
                )) }

                ModalBottomSheetLayout(
                    sheetContent = {
                        ListsBottomSheet(selectedEntry)
                    },
                    sheetState = modalBottomSheetState,
                ) {
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = {
                            TopAppBar(
                                modifier = Modifier.height(100.dp),
                                title = {
                                    TopActionBar(
                                        currentList = viewModel.value.currentList,
                                        onAddClicked = {
                                            scope.launch {
                                                var tmpListId = it.listId

                                                if (lists.isEmpty()) {
                                                    Log.d(
                                                        "DataAccess",
                                                        "No lists found, creating default."
                                                    )
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
                            ListLazyColumn(entries = entries,
                                curList = viewModel.value.currentList,
                                onRowSelected = {
                                    scope.launch {
                                        selectedEntry = it
                                        modalBottomSheetState.show()
                                    }
                                },
                                onEntryDeleteClick = {
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
                                    },
                                    onListsClicked = {
                                        scope.launch {
                                            modalBottomSheetState.show()
                                        }
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}



