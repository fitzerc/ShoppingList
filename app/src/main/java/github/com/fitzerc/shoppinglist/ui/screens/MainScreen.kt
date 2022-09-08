package github.com.fitzerc.shoppinglist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import github.com.fitzerc.shoppinglist.data.EntryDto
import github.com.fitzerc.shoppinglist.data.ListDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

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
fun ListLazyColumn(
    entries: List<EntryDto>,
    curList: ListDto,
    onRowSelected: (EntryDto) -> Unit,
    onEntryDeleteClick: (EntryDto) -> Unit
) {
    Column {
        ListHeader(curList = curList)
        if (entries.isEmpty()) {
            Text(text = "Uh Oh! No entries found.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                items(entries.count()) { index ->
                    ListEntryRow(entries[index],
                        index < entries.lastIndex,
                        onRowSelected = { onRowSelected(entries[index]) },
                        onDeleteClick = {
                            onEntryDeleteClick(entries[index]) },
                    )
                }
            }
        }
    }
}

@Composable
fun ListHeader(curList: ListDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row (modifier = Modifier.height(40.dp).fillMaxWidth()) {
                Text(
                    text = curList.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically)
                        .padding(start = 15.dp)
                )
            }

            Row {
                Divider(
                    color = MaterialTheme.colors.primary,
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                )
            }
        }
    }
}

@Composable
fun ListEntryRow(
    entry: EntryDto,
    divider: Boolean,
                 onRowSelected: (EntryDto) -> Unit,
                 onDeleteClick: (EntryDto) -> Unit) {
    Row(modifier = Modifier.clickable { onRowSelected(entry) }) {
        Column(
            modifier = Modifier
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(entry.name)
        }

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Button(onClick = { onDeleteClick(entry) },
            modifier = Modifier.padding(end = 5.dp)) {
                Text(text = "X")
            }
        }
    }

    if (divider) {
        Divider(color = MaterialTheme.colors.primary, thickness = .5.dp)
    }
}

@Composable
fun BottomActionBar(
    curListId: UUID,
    scope: CoroutineScope,
    onDelAllClicked: (UUID) -> Unit,
    onListsClicked: () -> Unit,
) {
    val delAllBtnText = "Delete All"
    val listsBtnText = "Lists"

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        //Delete All Button
        Button(
            modifier = Modifier.background(MaterialTheme.colors.primary),
            onClick = { scope.launch { onDelAllClicked(curListId) } }) {
            Text(text = delAllBtnText)
        }

        Button(
            modifier = Modifier.background(MaterialTheme.colors.primary),
            onClick = { scope.launch { onDelAllClicked(curListId) } }) {
            Text(text = listsBtnText)
        }
    }
}

@Composable
fun ListsBottomSheet(entry: EntryDto) {
    Text(text = "Name: ${entry.name}",
        modifier = Modifier.fillMaxSize())
}
