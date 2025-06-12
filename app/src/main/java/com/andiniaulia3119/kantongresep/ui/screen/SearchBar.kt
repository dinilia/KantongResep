package com.andiniaulia3119.kantongresep.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.andiniaulia3119.kantongresep.R

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    value: String,
    onSearchAction: (String) -> Unit,
    filterList: List<String>,
    selectedFilter: String?,
    onFilterSelected: (String?) -> Unit
) {
    var isShowingOptions by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = value,
                onValueChange = {
                    onSearchAction(it)
                    isShowingOptions = false // otomatis tutup filter saat mengetik
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.TwoTone.Search,
                        contentDescription = "Cari resep"
                    )
                },
                placeholder = {
                    Text(text = "Cari resep..")
                },
                shape = RoundedCornerShape(8.dp)
            )
            IconButton(
                onClick = { isShowingOptions = !isShowingOptions }
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_filter_alt_24),
                    contentDescription = "Filter"
                )
            }
        }

        AnimatedVisibility(visible = isShowingOptions) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(filterList) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = {
                            if (selectedFilter == filter) {
                                onFilterSelected(null) // unselect
                            } else {
                                onFilterSelected(filter) // apply filter
                            }
                        },
                        label = { Text(text = filter) },
                        shape = CircleShape
                    )
                }
            }
        }
    }
}
