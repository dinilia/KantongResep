package com.andiniaulia3119.kantongresep.ui.screen

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.datastore.core.IOException
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andiniaulia3119.kantongresep.BuildConfig
import com.andiniaulia3119.kantongresep.R
import com.andiniaulia3119.kantongresep.model.Resep
import com.andiniaulia3119.kantongresep.model.User
import com.andiniaulia3119.kantongresep.network.Api
import com.andiniaulia3119.kantongresep.network.UserDataStore
import com.andiniaulia3119.kantongresep.ui.viewmodel.ResepViewModel
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ResepViewModel = viewModel()
) {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(initial = User())

    var showProfileDialog by remember { mutableStateOf(false) }
    var showImageDialog by remember { mutableStateOf(false) }
    var showHapusDialog by remember { mutableStateOf(false) }

    var selectedBitmap: Bitmap? by remember { mutableStateOf(null) }
    var selectedResep by remember { mutableStateOf<Resep?>(null) }

    val resepList by viewModel.resepList.collectAsState()
    val status by viewModel.status.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    val filteredList = resepList.filter {
        (searchQuery.isBlank() || it.nama.contains(searchQuery, ignoreCase = true)) &&
                (selectedFilter == null || it.kategori.equals(selectedFilter, ignoreCase = true))
    }

    val cropLauncher = rememberLauncherForActivityResult(CropImageContract()) {
        val uri = it.uriContent
        selectedBitmap = getCroppedImage(context.contentResolver, uri)

        if (selectedBitmap != null) showImageDialog = true
    }

    val message by viewModel.message.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var isEditMode by remember { mutableStateOf(false) }

    LaunchedEffect(user.email) {
        if (user.email.isNotEmpty()) {
            viewModel.fetchResep(user.email)
        }
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("DapurKu!", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        if (user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                signIn(context, dataStore)
                            }
                        } else {
                            showProfileDialog = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.account_circle),
                            contentDescription = "Profil",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (user.email.isNotEmpty()) {
                    val options = CropImageContractOptions(
                        null,
                        CropImageOptions(
                            imageSourceIncludeCamera = true,
                            imageSourceIncludeGallery = true,
                            fixAspectRatio = true
                        )
                    )
                    cropLauncher.launch(options)
                } else {
                    Toast.makeText(context, "Anda belum login :(", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Resep")
            }
        }
    ) { padding ->

        if (showProfileDialog) {
            ProfilDialog(user = user, onDismissRequest = { showProfileDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch {
                    signOut(context, dataStore)
                }
                showProfileDialog = false
            }
        }

        if (showImageDialog && (isEditMode || selectedBitmap != null)) {
            ImageDialog(
                bitmap = selectedBitmap ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
                onDismissRequest = {
                    showImageDialog = false
                    isEditMode = false
                    selectedResep = null
                },
                onConfirmation = { nama, deskripsi, kategori ->
                    if (isEditMode && selectedResep != null) {
                        viewModel.updateResep(
                            id = selectedResep!!.recipeId,
                            nama = nama,
                            deskripsi = deskripsi,
                            kategori = kategori,
                            bitmap = selectedBitmap ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
                            userEmail = user.email
                        )
                    } else {
                        viewModel.uploadAndCreateResep(
                            nama = nama,
                            deskripsi = deskripsi,
                            kategori = kategori,
                            bitmap = selectedBitmap ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888),
                            userEmail = user.email
                        )
                    }
                    showImageDialog = false
                    isEditMode = false
                    selectedResep = null
                },
                isEdit = isEditMode,
                initialData = selectedResep
            )
        }

        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 8.dp
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Masak apa hari ini,", fontSize = 14.sp)
                    Text(
                        if (user.email.isNotEmpty()) user.name else "Yuk login dulu~",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                SearchBar(
                    value = searchQuery,
                    onSearchAction = { searchQuery = it },
                    filterList = listOf("Sup", "Gorengan", "Minuman Kopi"),
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                Text(
                    "Resep Makanan Indonesia",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            when (status) {
                Api.ApiStatus.SUCCESS -> {
                    items(filteredList) { resep ->
                        RecipeCard(
                            name = resep.nama,
                            imageId = resep.imageId,
                            deskripsi = resep.deskripsi,
                            modifier = Modifier.padding(8.dp),
                            onCardClick = {
                                selectedResep = resep
                                showHapusDialog = true
                            },
                            onDeleteClick = {
                                selectedResep = resep
                                showHapusDialog = true
                            },
                            onEditClick = {
                                selectedResep = resep
                                selectedBitmap = null // Diambil ulang jika perlu
                                isEditMode = true
                                showImageDialog = true
                            }
                        )
                    }

                    if (showHapusDialog && selectedResep != null) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            HapusDialog(
                                data = selectedResep!!,
                                onDismissRequest = { showHapusDialog = false }
                            ) {
                                viewModel.deleteResep(selectedResep!!.recipeId, user.email)
                                showHapusDialog = false
                            }
                        }
                    }
                }

                Api.ApiStatus.LOADING -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                        }
                    }
                }

                Api.ApiStatus.FAILED -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val message = if (user.email.isEmpty()) "Login dulu yuk untuk melihat data!" else "Terjadi kesalahan atau data kosong."
                            Text(message)
                            Button(
                                onClick = { viewModel.fetchResep(user.email) },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
            }
        }
    }
}


// Google Sign-In & Sign-Out Logic
private suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private suspend fun handleSignIn(result: GetCredentialResponse, dataStore: UserDataStore) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(User(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error parsing ID: ${e.message}")
        }
    } else {
        Log.e("SIGN-IN", "Unrecognized custom credential type")
    }
}

private suspend fun signOut(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-OUT", "Error: ${e.errorMessage}")
    }
}

fun getCroppedImage(contentResolver: ContentResolver, uri: Uri?): Bitmap? {
    return try {
        uri?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(contentResolver, it)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

