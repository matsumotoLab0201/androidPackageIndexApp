package com.app.androidpackageindexapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.coil.rememberCoilPainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import android.util.Log
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val apiService = createApiService()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val searchStringState = remember { mutableStateOf(TextFieldValue()) }
    var apiItems by remember {
        mutableStateOf<List<ApiItem>>(emptyList())
    }
    var searchTextState by remember { mutableStateOf(TextFieldValue()) }
    var searchResult by remember { mutableStateOf("") }



    LaunchedEffect(Unit) {
        val items = withContext(Dispatchers.IO) {
            apiService.getApiItems()
        }
        apiItems = items
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                title = { Text(
                    text = "Android Package Name 一覧",
                    fontSize = 20.sp, // フォントサイズを調整
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 17.dp)
                ) },
                actions = {

//                    BasicTextField(
//                        value = searchStringState.value,
//                        onValueChange = { searchStringState.value = it },
//                        keyboardOptions = KeyboardOptions.Default.copy(
//                            imeAction = ImeAction.Search
//                        ),
//                        keyboardActions = KeyboardActions(
//                            onSearch = {
//                                coroutineScope.launch {
//                                    scaffoldState.snackbarHostState.showSnackbar(
//                                        "Searching: ${searchStringState.value.text}"
//                                    )
//                                    searchResult = searchStringState.value.text
//                                }
//                            }
//                        ),
//                        singleLine = true,
//                        textStyle = LocalTextStyle.current.copy(
//                            color = Color.White
//                        ),
//                        modifier = Modifier
//                            .padding(end = 16.dp)
//                            .fillMaxWidth(0.5f)
//                    )

                }
            )
        },
        content = {
            Column(
                modifier = Modifier.padding(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                /*
                OutlinedTextField(
                    value = searchTextState,
                    onValueChange = { searchTextState = it },
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val searchResult = searchTextState.text
                        Log.d("テスト1", "onclick" + searchResult)

                    }
                ) {
                    Text("Search")
                }
                Spacer(modifier = Modifier.height(12.dp))

                Log.d("テスト1", "searchResult" + searchResult)
                var filteredItems = if (searchResult.isNotEmpty()) {
                    apiItems.filter { item ->
                        item.pacage.contains(searchResult, ignoreCase = true)
                    }
                } else {
                    // searchResultが空の場合、全てのアイテムを表示
                    apiItems
                }

                 */

                    ApiItemList(apiItems)


            }

        }
    )
}

data class ApiItem(
    val no: Int,
    val pacage: String,
    val explanation: String,
    val type: String,
    val URL: String,
    val icon: String
)

interface ApiService {
    @GET("android-package-index.json")
    suspend fun getApiItems(): List<ApiItem>
}

@Composable
fun createApiService(): ApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/matumotokohei/rentMock/master/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(ApiService::class.java)
}

@Composable
fun ApiItemList(apiItems: List<ApiItem>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        items(apiItems) { item ->
            ApiItemCard(item = item)
        }
    }
}

@Composable
fun ApiItemCard(item: ApiItem) {
    val context = LocalContext.current
    val urlToOpen = remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                urlToOpen.value = item.URL
            },
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberCoilPainter(
                        request = item.icon,
                        previewPlaceholder = R.drawable.ic_launcher_foreground
                    ),
                    contentDescription = "API Icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp)
                )
                Column {
                    Text(
                        text = item.pacage,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = item.explanation,
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = item.type,
                        style = MaterialTheme.typography.caption
                    )
                }
            }
            if (urlToOpen.value.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "URL: ${urlToOpen.value}",
                    style = MaterialTheme.typography.caption,
                    color = Color.Blue,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen.value))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}


