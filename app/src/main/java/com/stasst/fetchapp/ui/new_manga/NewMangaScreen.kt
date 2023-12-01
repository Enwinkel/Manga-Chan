package com.stasst.fetchapp.ui.new_manga

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.stasst.fetchapp.R
import com.stasst.fetchapp.util.FrameAnimation
import kotlinx.coroutines.launch

@Composable
fun NewMangaScreen(
    navController: NavController,
    viewModel: NewMangaViewModel = hiltViewModel()
) {
    val mangaData by viewModel.mangaData.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val title by viewModel.title.collectAsState()

    val frame0 = painterResource(R.drawable.eromanga_frame_0)
    val frame1 = painterResource(R.drawable.eromanga_frame_1)
    val frame2 = painterResource(R.drawable.eromanga_frame_2)
    val frame3 = painterResource(R.drawable.eromanga_frame_3)
    val frame4 = painterResource(R.drawable.eromanga_frame_4)
    val frame5 = painterResource(R.drawable.eromanga_frame_5)
    val frame6 = painterResource(R.drawable.eromanga_frame_6)
    val frame7 = painterResource(R.drawable.eromanga_frame_7)
    val frame8 = painterResource(R.drawable.eromanga_frame_8)
    val frames = listOf(frame0, frame1, frame2, frame3, frame4, frame5, frame6, frame7, frame8)

    if (loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FrameAnimation(
                modifier = Modifier.fillMaxWidth().height(500.dp),
                frames = frames,
                frameDurationMillis = 75
            )
            Spacer(modifier = Modifier.padding(1.dp))
            Text(
                modifier = Modifier.padding(bottom = 85.dp),
                text = "Загрузка...",
                style = TextStyle(fontSize = 20.sp),
                fontFamily = FontFamily.SansSerif
            )
        }

    } else {

        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var selectedItem by remember { mutableStateOf<String?>("") }
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet(modifier = Modifier.width(300.dp)) {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                            .background(color = colorResource(R.color.secondary))
                    ) {
                        var isSelected: Boolean

                        item {
                            isSelected = "Вся манга" == selectedItem
                            var color: Color
                            if (isSelected) color = colorResource(R.color.primary)
                            else color = Color.White
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = {
                                        selectedItem = "Вся манга"
                                        viewModel.onEvent(NewMangaEvent.TagClickEvent("Вся манга"))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = color)
                                ) {
                                    Text(
                                        text = "Вся манга",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                        items(tags.size) { index ->
                            isSelected = tags.get(index) == selectedItem
                            val color: Color
                            if (isSelected) color = colorResource(R.color.primary)
                            else color = Color.White
                            Row(
                                modifier = Modifier
                                    .padding(top = 1.dp, bottom = 2.dp)
                                    .background(color = color)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    modifier = Modifier.padding(start = 5.dp, end = 5.dp)
                                        .height(43.dp),
                                    onClick = {
                                        viewModel.onEvent(NewMangaEvent.TagAddedEvent(tags.get(index)))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "add"
                                    )
                                }
                                Text(
                                    text = tags.get(index),
                                    modifier = Modifier
                                        .padding(top = 0.dp, bottom = 0.dp)
                                        .clickable {
                                            selectedItem = tags.getOrNull(index)
                                            viewModel.onEvent(
                                                NewMangaEvent.TagClickEvent(
                                                    tags.get(
                                                        index
                                                    )
                                                )
                                            )
                                        },
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                }
            },
            drawerState = drawerState
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize().background(Color(0xFFE4E7EE)),
                    columns = GridCells.Fixed(1)
                ) {
                    item {
                        pagination(
                            viewModel = viewModel,
                            currentPage,
                            Modifier.fillMaxSize().padding(bottom = 3.dp, top = 58.dp)
                        )
                    }

                    val urlList = mangaData!!.imageUrls
                    Log.d("mangaTags", mangaData!!.tags.toString())
                    items(urlList.size) { index ->
                        TwoImagesWithTextLayout(
                            navController,
                            urlList[index],
                            mangaData!!.titles!![index],
                            mangaData!!.links[index],
                            mangaData!!.tags[index],
                            viewModel
                        )
                    }

                    item {
                        pagination(
                            viewModel = viewModel,
                            currentPage,
                            Modifier.fillMaxSize().padding(bottom = 10.dp, top = 3.dp)
                        )
                    }
                }
            }
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(color = Color(0xFF669922).copy(alpha = 0.73f)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.padding(
                            start = 5.dp,
                            top = 0.dp,
                            bottom = 0.dp,
                            end = 11.dp
                        ),
                        onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        colors = IconButtonColors(
                            containerColor = Color(0xFFc9e0ab),
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0xFFc9e0ab),
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                    Text(text = title, color = Color.White, fontSize = 20.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().height(1.dp)
                        .background(color = Color(0xFF669922))
                ) {}
                Row(
                    modifier = Modifier.fillMaxWidth().height(1.dp)
                        .background(color = Color.Black.copy(alpha = 0.1f))
                ) {}
            }
        }
    }
}

@Composable
private fun pagination(viewModel: NewMangaViewModel, page: Int, rowModifier: Modifier) {

    Row(
        modifier = rowModifier,
        horizontalArrangement = Arrangement.spacedBy(
            6.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val colors = ButtonDefaults.buttonColors(
            contentColor = Color.Black, containerColor = Color.White
        )
        val border = BorderStroke(1.dp, Color(0xFFc9e0ab))
        val modifier = Modifier.height(36.dp)

        if (page > 5) {
            OutlinedButton(
                modifier = modifier,
                border = border,
                colors = colors,
                onClick = {
                    viewModel.onEvent(NewMangaEvent.PageChangedEvent(page - 5))
                }
            ) {
                Text(text = (page - 5).toString())
            }
        } else if (page in 3..5) {
            OutlinedButton(
                modifier = modifier,
                border = border,
                colors = colors,
                onClick = {
                    viewModel.onEvent(NewMangaEvent.PageChangedEvent(1))
                }
            ) {
                Text(text = "1")
            }
        }

        if (page > 3) {
            Text(
                text = "_",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF669922)
            )
        }

        if (page > 1) {
            OutlinedButton(
                modifier = modifier,
                border = border,
                colors = colors,
                onClick = {
                    viewModel.onEvent(NewMangaEvent.PageChangedEvent(page - 1))
                }
            ) {
                Text(text = (page - 1).toString())
            }
        }

        OutlinedButton(
            modifier = modifier,
            border = border,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFc9e0ab)
            ),
            onClick = {
                viewModel.onEvent(NewMangaEvent.PageChangedEvent(page))
            }
        ) {
            Text(text = (page).toString(), color = Color.Black)
        }

        OutlinedButton(
            modifier = modifier,
            border = border,
            colors = colors,
            onClick = {
                viewModel.onEvent(NewMangaEvent.PageChangedEvent(page + 1))
            }
        ) {
            Text(text = (page + 1).toString())
        }

        Text(
            text = "_",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF669922)
        )

        OutlinedButton(
            modifier = modifier,
            border = border,
            colors = colors,
            onClick = {
                viewModel.onEvent(NewMangaEvent.PageChangedEvent(page + 5))
            }
        ) {
            Text(text = (page + 5).toString())
        }

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TwoImagesWithTextLayout(
    navController: NavController,
    image: String,
    title: String,
    link: String,
    tagsList: List<String>,
    viewModel: NewMangaViewModel
) {
    var shortTagsList = tagsList.take(8).toMutableList()
    var extended by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.padding(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        onClick = {
            val encodedLink = Uri.encode(link)
            navController.navigate("read/$encodedLink")
        }
    ) {
        Row(Modifier.fillMaxWidth()) {
            Image(
                painter = rememberImagePainter(image),
                contentDescription = null,
                modifier = Modifier
                    .weight(55f)
                    .padding(end = 2.dp)
                    .height(130.dp)
                    .align(Alignment.CenterVertically)
            )
            Column(
                modifier = Modifier.weight(100f)
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .padding(7.dp),
                    fontFamily = FontFamily.SansSerif,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold
                    )
                )
                FlowRow {
                    if (tagsList.size > 9 && !extended) {
                        shortTagsList.forEachIndexed { index, tag ->
                            AssistChip(
                                modifier = Modifier.height(17.dp).widthIn(max = 124.dp)
                                    .padding(bottom = 1.dp, end = 1.dp),
                                onClick = {
                                    viewModel.onEvent(NewMangaEvent.TagClickEvent(tag))
                                },
                                label = {
                                    Text(
                                        modifier = Modifier.wrapContentWidth(),
                                        text = tag,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF008000)
                                        )
                                    )
                                }
                            )
                        }
                        AssistChip(
                            modifier = Modifier.height(17.dp).widthIn(max = 124.dp),
                            onClick = {
                                shortTagsList = tagsList.toMutableList()
                                extended = true
                            },
                            label = {
                                Text(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(bottom = 1.dp),
                                    text = "+" + (tagsList.size - 8),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF397AFF)
                                    )
                                )
                            }
                        )
                    } else {
                        tagsList.forEachIndexed { index, tag ->
                            AssistChip(
                                modifier = Modifier.height(17.dp).widthIn(max = 124.dp)
                                    .padding(bottom = 1.dp, end = 1.dp),
                                onClick = {
                                    viewModel.onEvent(NewMangaEvent.TagClickEvent(tag))
                                },
                                label = {
                                    Text(
                                        modifier = Modifier.wrapContentWidth(),
                                        text = tag,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF008000)
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}