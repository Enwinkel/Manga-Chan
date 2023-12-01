package com.stasst.fetchapp.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.stasst.fetchapp.R
import com.stasst.fetchapp.util.FrameAnimation
import com.stasst.fetchapp.util.LoadNetworkImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

@Composable
fun ReadScreen(navController: NavController, url: String?) {
    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var pageIndex by remember { mutableStateOf(0) }
    var loading by remember { mutableStateOf(true) }
    val pressed = remember { mutableStateOf(false) }


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


    BackHandler {
        if (pageIndex > 0) {
            pageIndex--
        } else {
            navController.popBackStack()
        }
    }

    LaunchedEffect(url) {
        loading = true
        val docFirst = withContext(Dispatchers.IO) {
            Jsoup.connect("https://manga-chan.me" + url).timeout(400 * 1000).get()
        }
        val trueLinkElements = docFirst.select("a[href^=/online/]")
        val trueLink = trueLinkElements.get(0).attr("href")

        val doc = withContext(Dispatchers.IO) {
            Jsoup.connect("https://manga-chan.me" + trueLink).timeout(400 * 1000).get()
        }

        val scriptTags = doc.select("script")

        var fullimgData = ""
        for (script in scriptTags) {
            val scriptContent = script.html()
            if (scriptContent.contains("var fullimg")) {
                fullimgData = scriptContent
                break
            }
        }

        imageUrls = extractFullImgUrls(fullimgData)
        imageUrls = imageUrls.map { url ->
            url.replace("manganew_thumbs_retina", "manga")
                .replace(".jpg", ".png").replace("im4", "img2")
        }

        Log.d("painterUrlStart", imageUrls.get(pageIndex))


        loading = false
    }


    val updatePageIndex: () -> Unit = {
        if (pageIndex < imageUrls.size - 1) {
            pageIndex++
        }
    }

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
        Column {
            Row(
                modifier = Modifier.padding(8.dp).clickable {
                    navController.popBackStack()
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                //Log.d("painterUrlColumt", imageUrls.get(pageIndex))
                Column(
                    modifier = Modifier
                        .background(
                            if (pressed.value) Color.Transparent else Color.White
                        ),
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (imageUrls.size != 0) {
                        val urls = imageUrls
                        Log.d("painterUrl", urls.get(pageIndex))
                        LoadNetworkImage(imageUrl = urls.get(pageIndex), updatePageIndex)
                    }
                }
            }
        }
    }
}

fun extractFullImgUrls(scriptContent: String): List<String> {
    val fullimgList = mutableListOf<String>()

    val startIndex = scriptContent.indexOf('[')
    val endIndex = scriptContent.indexOf(']')

    if (startIndex != -1 && endIndex != -1) {
        val imgData = scriptContent.substring(startIndex + 1, endIndex)
        val imgUrls = imgData.split(",\"")

        for (url in imgUrls) {
            val cleanUrl = url.replace("\"", "").trim()
            fullimgList.add(cleanUrl)
        }
    }

    return fullimgList
}




