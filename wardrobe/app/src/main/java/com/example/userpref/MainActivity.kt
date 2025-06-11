package com.example.userpref

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import android.media.MediaPlayer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.compose.material3.Text
// Add missing imports
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.VisualTransformation
import coil.compose.AsyncImage
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.itemsIndexed


import com.example.userpref.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Fashion App Themes - Updated with fashion-focused colors
sealed class FashionTheme(
    val name: String,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val accent: Color
) {
    object Chic: FashionTheme(
        "Chic Black",
        Color(0xFF000000),
        Color(0xFFFFD700),
        Color(0xFFF8F8F8),
        Color(0xFFFFFFFF),
        Color(0xFF000000),
        Color(0xFFFFD700)
    )

    object Rose: FashionTheme(
        "Rose Gold",
        Color(0xFFE91E63),
        Color(0xFFFF69B4),
        Color(0xFFFFF0F5),
        Color(0xFFFFFFFF),
        Color(0xFF2D2D2D),
        Color(0xFFFF1744)
    )

    object Elegant: FashionTheme(
        "Elegant Navy",
        Color(0xFF1A237E),
        Color(0xFF7986CB),
        Color(0xFFF3F4F6),
        Color(0xFFFFFFFF),
        Color(0xFF1A237E),
        Color(0xFF3F51B5)
    )

    object Luxury: FashionTheme(
        "Luxury Purple",
        Color(0xFF6A1B9A),
        Color(0xFFBA68C8),
        Color(0xFFF3E5F5),
        Color(0xFFFFFFFF),
        Color(0xFF4A148C),
        Color(0xFF9C27B0)
    )
}

// Clothing item data class
data class ClothingItem(
    val id: String,
    val name: String,
    val category: ClothingCategory,
    val imageUrl: String,
    val brand: String = "",
    val color: String = "",
    val season: String = "",
    val isFavorite: Boolean = false
)

// Outfit data class
data class Outfit(
    val id: String,
    val name: String,
    val items: List<ClothingItem>,
    val occasion: String = "",
    val date: String = "",
    val isFavorite: Boolean = false,
    val imageUrl: String = ""
)

// Clothing categories
enum class ClothingCategory(val displayName: String, val icon: ImageVector) {
    TOPS("Tops", Icons.Filled.Face),
    BOTTOMS("Bottoms", Icons.Filled.Person),
    DRESSES("Dresses", Icons.Filled.Favorite),
    OUTERWEAR("Outerwear", Icons.Filled.Star),
    SHOES("Shoes", Icons.Filled.ShoppingCart),
    ACCESSORIES("Accessories", Icons.Filled.Build)
}

// Sample fashion images (using placeholder URLs - in real app, these would be from a fashion API)
object FashionImages {
    val sampleTops = listOf(
        "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=300&h=400&fit=crop",
        "https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=300&h=400&fit=crop",
        "https://images.unsplash.com/photo-1571945153237-4929e783af4a?w=300&h=400&fit=crop",
        "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=300&h=400&fit=crop"
    )

    val sampleBottoms = listOf(
        "https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=300&h=400&fit=crop",
        "https://images.unsplash.com/photo-1506629905607-92902ab3b674?w=300&h=400&fit=crop",
        "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=300&h=400&fit=crop",
        "https://images.unsplash.com/photo-1473691955023-da1c49c95c78?w=300&h=400&fit=crop"
    )

    val sampleDresses = listOf(
        "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=300&h=400&fit=crop",
        "https://images.unsplash.com/photo-1566479050817-d4a0f687a421?w=300&h=400&fit=crop",
        "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=300&h=400&fit=crop",
        "https://images.unsplash.com/photo-1515372039744-b8f02a3ae446?w=300&h=400&fit=crop"
    )

    val sampleShoes = listOf(
        "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=300&h=300&fit=crop",
        "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=300&h=300&fit=crop",
        "https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=300&h=300&fit=crop",
        "https://images.unsplash.com/photo-1584464491033-06628f3a6b7b?w=300&h=300&fit=crop"
    )

    val sampleOutfits = listOf(
        "https://images.unsplash.com/photo-1469334031218-e382a71b716b?w=300&h=500&fit=crop",
        "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=300&h=500&fit=crop",
        "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=300&h=500&fit=crop",
        "https://images.unsplash.com/photo-1493666438817-866a91353ca9?w=300&h=500&fit=crop"
    )
}

// Sample data
object SampleData {
    val sampleClothingItems = listOf(
        ClothingItem("1", "White Cotton Blouse", ClothingCategory.TOPS, FashionImages.sampleTops[0], "Zara", "White", "All Season"),
        ClothingItem("2", "Black Blazer", ClothingCategory.OUTERWEAR, FashionImages.sampleTops[1], "H&M", "Black", "Fall/Winter"),
        ClothingItem("3", "Blue Jeans", ClothingCategory.BOTTOMS, FashionImages.sampleBottoms[0], "Levi's", "Blue", "All Season"),
        ClothingItem("4", "Little Black Dress", ClothingCategory.DRESSES, FashionImages.sampleDresses[0], "ASOS", "Black", "All Season"),
        ClothingItem("5", "White Sneakers", ClothingCategory.SHOES, FashionImages.sampleShoes[0], "Nike", "White", "All Season"),
        ClothingItem("6", "Heeled Boots", ClothingCategory.SHOES, FashionImages.sampleShoes[1], "Zara", "Brown", "Fall/Winter")
    )

    val sampleOutfits = listOf(
        Outfit("1", "Casual Friday", sampleClothingItems.take(3), "Work", "Today", false, FashionImages.sampleOutfits[0]),
        Outfit("2", "Date Night", listOf(sampleClothingItems[3], sampleClothingItems[5]), "Date", "Saturday", true, FashionImages.sampleOutfits[1]),
        Outfit("3", "Weekend Brunch", listOf(sampleClothingItems[0], sampleClothingItems[2], sampleClothingItems[4]), "Casual", "Sunday", false, FashionImages.sampleOutfits[2])
    )
}

// Language data class
data class Language(val name: String, val code: String)

// Available languages
val availableLanguages = listOf(
    Language("English", "en"),
    Language("اردو", "ur"),
    Language("हिंदी", "hi"),
    Language("中文", "zh"),
    Language("Español", "es")
)

// Navigation destinations
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Wardrobe : Screen("wardrobe", "Wardrobe", Icons.Filled.Face)
    object Outfits : Screen("outfits", "Outfits", Icons.Filled.Favorite)
    object Planner : Screen("planner", "Planner", Icons.Filled.DateRange)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
}

// Define DataStore for preferences
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fashion_preferences")

// Define preference keys
object PreferencesKeys {
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val SELECTED_THEME = stringPreferencesKey("selected_theme")
    val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    val PROFILE_PICTURE_PATH = stringPreferencesKey("profile_picture_path")
    val IS_AUTHENTICATED = booleanPreferencesKey("is_authenticated")
    val USER_ID = stringPreferencesKey("user_id")
    val STORED_PASSWORD = stringPreferencesKey("stored_password")
}

// Custom composables for consistent styling
@Composable
fun FashionText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    androidx.compose.material3.Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        onTextLayout = {},
        style = style
    )
}

@Composable
fun FashionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

// User Preferences repository
class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_EMAIL] = email
        }
    }

    suspend fun saveSelectedTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_THEME] = theme
        }
    }

    suspend fun saveSelectedLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_LANGUAGE] = language
        }
    }

    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun saveDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE_ENABLED] = enabled
        }
    }

    suspend fun saveProfilePicturePath(path: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PROFILE_PICTURE_PATH] = path
        }
    }
    suspend fun saveAuthState(isAuthenticated: Boolean, userId: String = "", password: String = "") {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_AUTHENTICATED] = isAuthenticated
            preferences[PreferencesKeys.USER_ID] = userId
            preferences[PreferencesKeys.STORED_PASSWORD] = password
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_AUTHENTICATED] = false
            preferences[PreferencesKeys.USER_ID] = ""
            preferences[PreferencesKeys.STORED_PASSWORD] = ""
        }
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.map { preferences ->
        UserPreferences(
            userName = preferences[PreferencesKeys.USER_NAME] ?: "",
            userEmail = preferences[PreferencesKeys.USER_EMAIL] ?: "",
            selectedTheme = preferences[PreferencesKeys.SELECTED_THEME] ?: "Chic Black",
            selectedLanguage = preferences[PreferencesKeys.SELECTED_LANGUAGE] ?: "en",
            notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            darkModeEnabled = preferences[PreferencesKeys.DARK_MODE_ENABLED] ?: false,
            profilePicturePath = preferences[PreferencesKeys.PROFILE_PICTURE_PATH] ?: "",
            isAuthenticated = preferences[PreferencesKeys.IS_AUTHENTICATED] ?: false,
            userId = preferences[PreferencesKeys.USER_ID] ?: "",
            storedPassword = preferences[PreferencesKeys.STORED_PASSWORD] ?: ""
        )
    }
}

// User preferences data class
data class UserPreferences(
    val userName: String,
    val userEmail: String,
    val selectedTheme: String,
    val selectedLanguage: String,
    val notificationsEnabled: Boolean,
    val darkModeEnabled: Boolean,
    val profilePicturePath: String,
    val isAuthenticated: Boolean = false,
    val userId: String = "",
    val storedPassword: String = ""
)
data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String = "" // Don't store in real app
)
sealed class AuthScreen(val route: String) {
    object Splash : AuthScreen("splash")
    object Login : AuthScreen("login")
    object Signup : AuthScreen("signup")
    object Main : AuthScreen("main")
}

data class AuthState(
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// View Model
class UserPreferencesViewModel(private val repository: UserPreferencesRepository) : ViewModel() {
    val userPreferences = repository.userPreferencesFlow

    suspend fun saveUserName(name: String) {
        repository.saveUserName(name)
    }

    suspend fun saveUserEmail(email: String) {
        repository.saveUserEmail(email)
    }

    suspend fun saveSelectedTheme(theme: String) {
        repository.saveSelectedTheme(theme)
    }

    suspend fun saveSelectedLanguage(language: String) {
        repository.saveSelectedLanguage(language)
    }

    suspend fun saveNotificationsEnabled(enabled: Boolean) {
        repository.saveNotificationsEnabled(enabled)
    }

    suspend fun saveDarkModeEnabled(enabled: Boolean) {
        repository.saveDarkModeEnabled(enabled)
    }

    suspend fun saveProfilePicturePath(path: String) {
        repository.saveProfilePicturePath(path)
    }

    suspend fun login(email: String, password: String): Boolean {
        // Simple validation - in real app, use proper authentication
        if (email.isNotEmpty() && password.length >= 6) {
            val userId = System.currentTimeMillis().toString()
            repository.saveAuthState(true, userId, password)
            repository.saveUserEmail(email)
            return true
        }
        return false
    }

    suspend fun signup(name: String, email: String, password: String): Boolean {
        // Simple validation - in real app, use proper authentication
        if (name.isNotEmpty() && email.isNotEmpty() && password.length >= 6) {
            val userId = System.currentTimeMillis().toString()
            repository.saveAuthState(true, userId, password)
            repository.saveUserName(name)
            repository.saveUserEmail(email)
            return true
        }
        return false
    }

    suspend fun logout() {
        repository.logout()
    }

}

// View Model factory
class UserPreferencesViewModelFactory(private val repository: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserPreferencesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserPreferencesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SplashScreen_Custom)
        super.onCreate(savedInstanceState)
        initializeBackgroundMusic()
        Handler(Looper.getMainLooper()).postDelayed({
            setTheme(R.style.Theme_ObjectComboGenerator)
            initializeContent()
        }, 2500)
    }
    private fun initializeBackgroundMusic() {
        try {
            // Initialize MediaPlayer with the music file from res/raw
            mediaPlayer = MediaPlayer.create(this, R.raw.your_background_music)

            mediaPlayer?.apply {
                // Set the music to loop continuously
                isLooping = true

                // Set volume (0.0f to 1.0f) - adjust as needed
                setVolume(0.3f, 0.3f) // 30% volume for background music

                // Prepare and start playing
                prepareAsync()
                setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.start()
                }

                // Handle any errors
                setOnErrorListener { _, what, extra ->

                    false
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun initializeContent() {
        setContent {
            val context = LocalContext.current
            val repository = UserPreferencesRepository(context.dataStore)
            val viewModel: UserPreferencesViewModel = viewModel(
                factory = UserPreferencesViewModelFactory(repository)
            )

            val userPreferences by viewModel.userPreferences.collectAsState(
                initial = UserPreferences(
                    userName = "",
                    userEmail = "",
                    selectedTheme = "Chic Black",
                    selectedLanguage = "en",
                    notificationsEnabled = true,
                    darkModeEnabled = false,
                    profilePicturePath = "",
                    isAuthenticated = false,
                    userId = "",
                    storedPassword = ""
                )
            )

            val currentTheme = when (userPreferences.selectedTheme) {
                "Rose Gold" -> FashionTheme.Rose
                "Elegant Navy" -> FashionTheme.Elegant
                "Luxury Purple" -> FashionTheme.Luxury
                else -> FashionTheme.Chic
            }

            val darkMode = userPreferences.darkModeEnabled

            val colorScheme = when {
                darkMode -> darkColorScheme(
                    primary = currentTheme.primary,
                    secondary = currentTheme.secondary,
                    background = Color(0xFF121212),
                    surface = Color(0xFF1E1E1E),
                    onSurface = Color.White
                )
                else -> lightColorScheme(
                    primary = currentTheme.primary,
                    secondary = currentTheme.secondary,
                    background = currentTheme.background,
                    surface = currentTheme.surface,
                    onSurface = currentTheme.onSurface
                )
            }

            MaterialTheme(
                colorScheme = colorScheme
            ) {
//                FashionLookbookApp(viewModel, userPreferences, currentTheme)
                AuthenticationApp(viewModel, userPreferences, currentTheme)
            }
        }
    }
}
@Composable
fun AuthenticationApp(
    viewModel: UserPreferencesViewModel,
    userPreferences: UserPreferences,
    currentTheme: FashionTheme
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (userPreferences.isAuthenticated) AuthScreen.Main.route else AuthScreen.Login.route
    ) {
        composable(AuthScreen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AuthScreen.Main.route) {
                        popUpTo(AuthScreen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(AuthScreen.Signup.route)
                },
                viewModel = viewModel,
                currentTheme = currentTheme
            )
        }

        composable(AuthScreen.Signup.route) {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate(AuthScreen.Main.route) {
                        popUpTo(AuthScreen.Signup.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                viewModel = viewModel,
                currentTheme = currentTheme
            )
        }

        composable(AuthScreen.Main.route) {
            FashionLookbookApp(
                viewModel = viewModel,
                userPreferences = userPreferences,
                currentTheme = currentTheme,
                onLogout = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(AuthScreen.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit,
    viewModel: UserPreferencesViewModel,
    currentTheme: FashionTheme
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        currentTheme.primary.copy(alpha = 0.1f),
                        currentTheme.accent.copy(alpha = 0.05f)
                    )
                )
            )
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo/Title Section
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(currentTheme.primary, currentTheme.accent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FashionText(
                text = "Fashion Lookbook",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = currentTheme.primary
            )

            FashionText(
                text = "Sign in to continue",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Login Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    FashionText(
                        text = "Welcome Back",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = ""
                        },
                        label = { FashionText("Email") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = null,
                                tint = currentTheme.accent
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = ""
                        },
                        label = { FashionText("Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = currentTheme.accent
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.KeyboardArrowLeft else Icons.Filled.KeyboardArrowRight,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        FashionText(
                            text = errorMessage,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login Button
                    FashionButton(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please fill in all fields"
                                return@FashionButton
                            }

                            isLoading = true
                            coroutineScope.launch {
                                val success = viewModel.login(email, password)
                                isLoading = false
                                if (success) {
                                    onLoginSuccess()
                                } else {
                                    errorMessage = "Invalid email or password"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentTheme.accent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            FashionText(
                                "Sign In",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sign Up Navigation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FashionText(
                            text = "Don't have an account? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        FashionText(
                            text = "Sign Up",
                            style = MaterialTheme.typography.bodyMedium,
                            color = currentTheme.accent,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onNavigateToSignup() }
                        )
                    }
                }
            }
        }
    }
}

// 11. ADD SIGNUP SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: UserPreferencesViewModel,
    currentTheme: FashionTheme
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        currentTheme.primary.copy(alpha = 0.1f),
                        currentTheme.accent.copy(alpha = 0.05f)
                    )
                )
            )
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo/Title Section
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(currentTheme.primary, currentTheme.accent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FashionText(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = currentTheme.primary
            )

            FashionText(
                text = "Join the fashion community",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Signup Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    FashionText(
                        text = "Getting Started",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Name Field
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            errorMessage = ""
                        },
                        label = { FashionText("Full Name") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = currentTheme.accent
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent
                        ),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = ""
                        },
                        label = { FashionText("Email") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = null,
                                tint = currentTheme.accent
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = ""
                        },
                        label = { FashionText("Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = currentTheme.accent
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.KeyboardArrowLeft else Icons.Filled.KeyboardArrowRight,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password Field
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorMessage = ""
                        },
                        label = { FashionText("Confirm Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = currentTheme.accent
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Filled.KeyboardArrowLeft else Icons.Filled.KeyboardArrowRight,
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        singleLine = true,
                        enabled = !isLoading
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        FashionText(
                            text = errorMessage,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Signup Button
                    FashionButton(
                        onClick = {
                            when {
                                name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                                    errorMessage = "Please fill in all fields"
                                }
                                password != confirmPassword -> {
                                    errorMessage = "Passwords do not match"
                                }
                                password.length < 6 -> {
                                    errorMessage = "Password must be at least 6 characters"
                                }
                                else -> {
                                    isLoading = true
                                    coroutineScope.launch {
                                        val success = viewModel.signup(name, email, password)
                                        isLoading = false
                                        if (success) {
                                            onSignupSuccess()
                                        } else {
                                            errorMessage = "Failed to create account"
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentTheme.accent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            FashionText(
                                "Create Account",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login Navigation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FashionText(
                            text = "Already have an account? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        FashionText(
                            text = "Sign In",
                            style = MaterialTheme.typography.bodyMedium,
                            color = currentTheme.accent,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onNavigateToLogin() }
                        )
                    }
                }
            }
        }
    }
}
// Main App Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FashionLookbookApp(
    viewModel: UserPreferencesViewModel,
    userPreferences: UserPreferences,
    currentTheme: FashionTheme,
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val screens = listOf(
        Screen.Wardrobe,
        Screen.Outfits,
        Screen.Planner,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    val interactionSource = remember { MutableInteractionSource() }

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            FashionText(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 12.sp
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = currentTheme.accent,
                            selectedTextColor = currentTheme.accent,
                            indicatorColor = currentTheme.accent.copy(alpha = 0.2f)
                        ),
                        interactionSource = interactionSource
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Wardrobe.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Wardrobe.route) {
                WardrobeScreen(viewModel, userPreferences, currentTheme)
            }
            composable(Screen.Outfits.route) {
                OutfitsScreen(viewModel, userPreferences, currentTheme)
            }
            composable(Screen.Planner.route) {
                PlannerScreen(viewModel, userPreferences, currentTheme)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(viewModel, userPreferences, currentTheme, onLogout)
            }
        }
    }
}

// Wardrobe Screen
// View mode enum - must be outside composable
enum class ViewMode { GRID, LIST }

// Enhanced Wardrobe Screen with Advanced UI and Functionality
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(
    viewModel: UserPreferencesViewModel,
    userPreferences: UserPreferences,
    currentTheme: FashionTheme
) {
    var selectedCategory by remember { mutableStateOf<ClothingCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    var viewMode by remember { mutableStateOf(ViewMode.GRID) }
    var selectedItem by remember { mutableStateOf<ClothingItem?>(null) }
    var editingItem by remember { mutableStateOf<ClothingItem?>(null) }
    var favoriteItems by remember { mutableStateOf(setOf<String>()) }

    // Enhanced sample data with more realistic items
    val enhancedClothingItems = remember {
        mutableStateListOf<ClothingItem>().apply {
            addAll(listOf(
                ClothingItem("1", "Silk Blouse", ClothingCategory.TOPS, FashionImages.sampleTops[0], "Zara", "Ivory", "All Season", true),
                ClothingItem("2", "Leather Jacket", ClothingCategory.OUTERWEAR, FashionImages.sampleTops[1], "AllSaints", "Black", "Fall/Winter"),
                ClothingItem("3", "High-Waist Jeans", ClothingCategory.BOTTOMS, FashionImages.sampleBottoms[0], "Levi's", "Dark Blue", "All Season"),
                ClothingItem("4", "Midi Dress", ClothingCategory.DRESSES, FashionImages.sampleDresses[0], "ASOS", "Navy", "Spring/Summer", true),
                ClothingItem("5", "Designer Sneakers", ClothingCategory.SHOES, FashionImages.sampleShoes[0], "Balenciaga", "White", "All Season"),
                ClothingItem("6", "Ankle Boots", ClothingCategory.SHOES, FashionImages.sampleShoes[1], "Dr. Martens", "Brown", "Fall/Winter"),
                ClothingItem("7", "Cashmere Sweater", ClothingCategory.TOPS, FashionImages.sampleTops[2], "Uniqlo", "Beige", "Fall/Winter"),
                ClothingItem("8", "Maxi Dress", ClothingCategory.DRESSES, FashionImages.sampleDresses[1], "H&M", "Floral", "Spring/Summer"),
                ClothingItem("9", "Blazer", ClothingCategory.OUTERWEAR, FashionImages.sampleTops[3], "Mango", "Grey", "All Season", true),
                ClothingItem("10", "Palazzo Pants", ClothingCategory.BOTTOMS, FashionImages.sampleBottoms[1], "Zara", "Black", "Spring/Summer")
            ))
        }
    }

    // Filter items based on search and category
    val filteredItems = enhancedClothingItems.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                item.brand.contains(searchQuery, ignoreCase = true) ||
                item.color.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || item.category == selectedCategory
        val matchesFilter = when (selectedFilter) {
            "Favorites" -> item.isFavorite || favoriteItems.contains(item.id)
            "Recent" -> true // Would implement based on last added/modified
            "All" -> true
            else -> item.season == selectedFilter
        }
        matchesSearch && matchesCategory && matchesFilter
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // Enhanced Header with Search
                Column {
                    // Top Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            FashionText(
                                text = "My Wardrobe",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            FashionText(
                                text = "${filteredItems.size} items in your closet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }

                        Row {
                            // View mode toggle
                            IconButton(
                                onClick = { viewMode = if (viewMode == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(currentTheme.accent.copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    imageVector = if (viewMode == ViewMode.GRID) Icons.Filled.List else Icons.Filled.Face,
                                    contentDescription = "Toggle View",
                                    tint = currentTheme.accent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Add item button
                            IconButton(
                                onClick = { showAddItemDialog = true },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(currentTheme.accent)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Add Item",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { FashionText("Search items, brands, colors...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = currentTheme.accent
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Stats
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        item {
                            QuickStatCard(
                                title = "Total Items",
                                value = "${enhancedClothingItems.size}",
                                icon = Icons.Filled.Face,
                                color = currentTheme.primary
                            )
                        }
                        item {
                            QuickStatCard(
                                title = "Favorites",
                                value = "${enhancedClothingItems.count { it.isFavorite || favoriteItems.contains(it.id) }}",
                                icon = Icons.Filled.Favorite,
                                color = Color.Red
                            )
                        }
                        item {
                            QuickStatCard(
                                title = "Categories",
                                value = "${ClothingCategory.values().size}",
                                icon = Icons.Filled.Star,
                                color = currentTheme.accent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Filter Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            item {
                                FilterChip(
                                    onClick = { selectedCategory = null },
                                    label = { FashionText("All", fontSize = 12.sp) },
                                    selected = selectedCategory == null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = currentTheme.accent.copy(alpha = 0.2f),
                                        selectedLabelColor = currentTheme.accent
                                    )
                                )
                            }

                            items(ClothingCategory.values()) { category ->
                                FilterChip(
                                    onClick = { selectedCategory = category },
                                    label = { FashionText(category.displayName, fontSize = 12.sp) },
                                    selected = selectedCategory == category,
                                    leadingIcon = if (selectedCategory == category) {
                                        { Icon(category.icon, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = currentTheme.accent.copy(alpha = 0.2f),
                                        selectedLabelColor = currentTheme.accent
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = { showFilterDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(currentTheme.accent.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Build,
                                contentDescription = "More Filters",
                                tint = currentTheme.accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            item {
                // Items Display
                if (filteredItems.isEmpty()) {
                    EmptyWardrobeState(
                        onAddClick = { showAddItemDialog = true },
                        currentTheme = currentTheme,
                        isFiltered = searchQuery.isNotEmpty() || selectedCategory != null
                    )
                } else {
                    if (viewMode == ViewMode.GRID) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .height(((filteredItems.size + 1) / 2 * 280).dp)
                                .padding(horizontal = 16.dp),
                            userScrollEnabled = false
                        ) {
                            items(filteredItems) { item ->
                                EnhancedClothingItemCard(
                                    item = item,
                                    currentTheme = currentTheme,
                                    onFavoriteClick = {
                                        favoriteItems = if (favoriteItems.contains(item.id)) {
                                            favoriteItems - item.id
                                        } else {
                                            favoriteItems + item.id
                                        }
                                    },
                                    onClick = { selectedItem = item },
                                    isFavorite = item.isFavorite || favoriteItems.contains(item.id)
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            filteredItems.forEach { item ->
                                ClothingItemListCard(
                                    item = item,
                                    currentTheme = currentTheme,
                                    onFavoriteClick = {
                                        favoriteItems = if (favoriteItems.contains(item.id)) {
                                            favoriteItems - item.id
                                        } else {
                                            favoriteItems + item.id
                                        }
                                    },
                                    onClick = { selectedItem = item },
                                    isFavorite = item.isFavorite || favoriteItems.contains(item.id)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Dialogs
        if (showAddItemDialog) {
            AddItemDialog(
                showDialog = showAddItemDialog,
                onDismiss = { showAddItemDialog = false },
                onAddItem = { newItem ->
                    enhancedClothingItems.add(newItem)
                    showAddItemDialog = false
                },
                currentTheme = currentTheme
            )
        }

        if (showFilterDialog) {
            FilterDialog(
                showDialog = showFilterDialog,
                onDismiss = { showFilterDialog = false },
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                currentTheme = currentTheme
            )
        }

        if (selectedItem != null) {
            ItemDetailsDialog(
                item = selectedItem!!,
                onDismiss = { selectedItem = null },
                currentTheme = currentTheme,
                onEdit = {
                    editingItem = selectedItem
                    selectedItem = null
                },
                onDelete = {
                    enhancedClothingItems.remove(selectedItem)
                    selectedItem = null
                }
            )
        }

        if (editingItem != null) {
            EditItemDialog(
                item = editingItem!!,
                onDismiss = { editingItem = null },
                onSave = { updatedItem ->
                    val index = enhancedClothingItems.indexOfFirst { it.id == updatedItem.id }
                    if (index != -1) {
                        enhancedClothingItems[index] = updatedItem
                    }
                    editingItem = null
                },
                currentTheme = currentTheme
            )
        }
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.width(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            FashionText(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            FashionText(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EnhancedClothingItemCard(
    item: ClothingItem,
    currentTheme: FashionTheme,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    isFavorite: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box {
            Column {
                // Image section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Category badge
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = currentTheme.accent.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        FashionText(
                            text = item.category.displayName,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Favorite button
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Info section
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    FashionText(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FashionText(
                            text = item.brand,
                            style = MaterialTheme.typography.bodySmall,
                            color = currentTheme.accent,
                            fontWeight = FontWeight.SemiBold
                        )

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            FashionText(
                                text = item.color,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FashionText(
                            text = item.season,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClothingItemListCard(
    item: ClothingItem,
    currentTheme: FashionTheme,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    isFavorite: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                FashionText(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FashionText(
                    text = "${item.brand} • ${item.color}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = currentTheme.accent
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = currentTheme.accent.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        FashionText(
                            text = item.category.displayName,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = currentTheme.accent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    FashionText(
                        text = item.season,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isFavorite) Color.Red.copy(alpha = 0.1f) else Color.Transparent)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyWardrobeState(
    onAddClick: () -> Unit,
    currentTheme: FashionTheme,
    isFiltered: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isFiltered) Icons.Filled.Search else Icons.Filled.Face,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        FashionText(
            text = if (isFiltered) "No items found" else "Your wardrobe is empty",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        FashionText(
            text = if (isFiltered) "Try adjusting your search or filters" else "Start building your digital closet by adding your first item",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!isFiltered) {
            FashionButton(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentTheme.accent
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FashionText("Add First Item", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// Edit Item Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemDialog(
    item: ClothingItem,
    onDismiss: () -> Unit,
    onSave: (ClothingItem) -> Unit,
    currentTheme: FashionTheme
) {
    var itemName by remember { mutableStateOf(item.name) }
    var itemBrand by remember { mutableStateOf(item.brand) }
    var itemColor by remember { mutableStateOf(item.color) }
    var selectedCategory by remember { mutableStateOf(item.category) }
    var selectedSeason by remember { mutableStateOf(item.season) }

    val seasons = listOf("All Season", "Spring/Summer", "Fall/Winter", "Spring", "Summer", "Fall", "Winter")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FashionText(
                        text = "Edit Item",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        tint = currentTheme.accent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { FashionText("Item Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = currentTheme.accent,
                        focusedLabelColor = currentTheme.accent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = itemBrand,
                    onValueChange = { itemBrand = it },
                    label = { FashionText("Brand") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = currentTheme.accent,
                        focusedLabelColor = currentTheme.accent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = itemColor,
                    onValueChange = { itemColor = it },
                    label = { FashionText("Color") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = currentTheme.accent,
                        focusedLabelColor = currentTheme.accent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category selection
                FashionText(
                    text = "Category",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ClothingCategory.values()) { category ->
                        FilterChip(
                            onClick = { selectedCategory = category },
                            label = { FashionText(category.displayName, fontSize = 12.sp) },
                            selected = selectedCategory == category,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = currentTheme.accent.copy(alpha = 0.2f),
                                selectedLabelColor = currentTheme.accent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Season selection
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedSeason,
                        onValueChange = {},
                        readOnly = true,
                        label = { FashionText("Season") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        seasons.forEach { season ->
                            DropdownMenuItem(
                                text = { FashionText(season) },
                                onClick = {
                                    selectedSeason = season
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FashionButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        FashionText("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    FashionButton(
                        onClick = {
                            if (itemName.isNotBlank()) {
                                val updatedItem = item.copy(
                                    name = itemName,
                                    brand = itemBrand,
                                    color = itemColor,
                                    category = selectedCategory,
                                    season = selectedSeason
                                )
                                onSave(updatedItem)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentTheme.accent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = itemName.isNotBlank()
                    ) {
                        FashionText("Save Changes", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// Add Item Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAddItem: (ClothingItem) -> Unit,
    currentTheme: FashionTheme
) {
    var itemName by remember { mutableStateOf("") }
    var itemBrand by remember { mutableStateOf("") }
    var itemColor by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ClothingCategory.TOPS) }
    var selectedSeason by remember { mutableStateOf("All Season") }

    val seasons = listOf("All Season", "Spring/Summer", "Fall/Winter", "Spring", "Summer", "Fall", "Winter")

    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    FashionText(
                        text = "Add New Item",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { FashionText("Item Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = itemBrand,
                        onValueChange = { itemBrand = it },
                        label = { FashionText("Brand") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = itemColor,
                        onValueChange = { itemColor = it },
                        label = { FashionText("Color") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Category selection
                    FashionText(
                        text = "Category",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(ClothingCategory.values()) { category ->
                            FilterChip(
                                onClick = { selectedCategory = category },
                                label = { FashionText(category.displayName, fontSize = 12.sp) },
                                selected = selectedCategory == category,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = currentTheme.accent.copy(alpha = 0.2f),
                                    selectedLabelColor = currentTheme.accent
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Season selection
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSeason,
                            onValueChange = {},
                            readOnly = true,
                            label = { FashionText("Season") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = currentTheme.accent,
                                focusedLabelColor = currentTheme.accent
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            seasons.forEach { season ->
                                DropdownMenuItem(
                                    text = { FashionText(season) },
                                    onClick = {
                                        selectedSeason = season
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FashionButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            FashionText("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        FashionButton(
                            onClick = {
                                if (itemName.isNotBlank()) {
                                    val newItem = ClothingItem(
                                        id = System.currentTimeMillis().toString(),
                                        name = itemName,
                                        category = selectedCategory,
                                        imageUrl = when (selectedCategory) {
                                            ClothingCategory.TOPS -> FashionImages.sampleTops.random()
                                            ClothingCategory.BOTTOMS -> FashionImages.sampleBottoms.random()
                                            ClothingCategory.DRESSES -> FashionImages.sampleDresses.random()
                                            ClothingCategory.SHOES -> FashionImages.sampleShoes.random()
                                            else -> FashionImages.sampleTops.random()
                                        },
                                        brand = itemBrand,
                                        color = itemColor,
                                        season = selectedSeason
                                    )
                                    onAddItem(newItem)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = currentTheme.accent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = itemName.isNotBlank()
                        ) {
                            FashionText("Add Item", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

// Filter Dialog
@Composable
fun FilterDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    currentTheme: FashionTheme
) {
    val filters = listOf("All", "Favorites", "Recent", "Spring/Summer", "Fall/Winter", "All Season")

    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    FashionText(
                        text = "Filter Items",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    filters.forEach { filter ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onFilterSelected(filter)
                                    onDismiss()
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedFilter == filter,
                                onClick = {
                                    onFilterSelected(filter)
                                    onDismiss()
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = currentTheme.accent
                                )
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            FashionText(
                                text = filter,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FashionButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentTheme.accent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        FashionText("Apply", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// Item Details Dialog
@Composable
fun ItemDetailsDialog(
    item: ClothingItem,
    onDismiss: () -> Unit,
    currentTheme: FashionTheme,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                // Image section
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Title and category
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            FashionText(
                                text = item.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            FashionText(
                                text = item.brand,
                                style = MaterialTheme.typography.titleMedium,
                                color = currentTheme.accent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = currentTheme.accent.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            FashionText(
                                text = item.category.displayName,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = currentTheme.accent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Details
                    ItemDetailRow("Color", item.color, Icons.Filled.Face)
                    ItemDetailRow("Season", item.season, Icons.Filled.DateRange)
                    ItemDetailRow("Category", item.category.displayName, item.category.icon)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FashionButton(
                            onClick = {
                                showDeleteConfirmation = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FashionText("Delete", color = Color.Red, fontWeight = FontWeight.SemiBold)
                        }

                        FashionButton(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = currentTheme.accent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FashionText("Edit", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FashionButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        FashionText("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { FashionText("Delete Item", fontWeight = FontWeight.Bold) },
            text = { FashionText("Are you sure you want to delete '${item.name}'? This action cannot be undone.") },
            confirmButton = {
                FashionButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    FashionText("Delete", color = Color.White)
                }
            },
            dismissButton = {
                FashionButton(
                    onClick = { showDeleteConfirmation = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    FashionText("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

@Composable
fun ItemDetailRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            FashionText(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            FashionText(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Enhanced Outfit Builder
@Composable
fun OutfitBuilderScreen(
    viewModel: UserPreferencesViewModel,
    userPreferences: UserPreferences,
    currentTheme: FashionTheme
) {
    var selectedItems by remember { mutableStateOf(listOf<ClothingItem>()) }
    var outfitName by remember { mutableStateOf("") }
    var outfitOccasion by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                FashionText(
                    text = "Create Outfit",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                FashionText(
                    text = "${selectedItems.size} items selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            FashionButton(
                onClick = { showSaveDialog = true },
                enabled = selectedItems.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedItems.isNotEmpty()) currentTheme.accent else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FashionText(
                    "Save Outfit",
                    color = if (selectedItems.isNotEmpty()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Selected items preview
        if (selectedItems.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = currentTheme.accent.copy(alpha = 0.1f)
                )
            ) {
                LazyRow(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(selectedItems) { item ->
                        Box {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.name,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            IconButton(
                                onClick = {
                                    selectedItems = selectedItems - item
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(20.dp)
                                    .offset(x = 6.dp, y = (-6).dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Remove",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Available items by category
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(ClothingCategory.values()) { category ->
                val categoryItems = SampleData.sampleClothingItems.filter { it.category == category }

                if (categoryItems.isNotEmpty()) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        FashionText(
                            text = category.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(categoryItems) { item ->
                                SelectableItemCard(
                                    item = item,
                                    isSelected = selectedItems.contains(item),
                                    onSelectionChange = {
                                        selectedItems = if (selectedItems.contains(item)) {
                                            selectedItems - item
                                        } else {
                                            selectedItems + item
                                        }
                                    },
                                    currentTheme = currentTheme
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Save outfit dialog
    if (showSaveDialog) {
        SaveOutfitDialog(
            showDialog = showSaveDialog,
            onDismiss = { showSaveDialog = false },
            onSave = { name, occasion ->
                // Save outfit logic here
                showSaveDialog = false
                selectedItems = emptyList()
            },
            currentTheme = currentTheme
        )
    }
}

@Composable
fun SelectableItemCard(
    item: ClothingItem,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    currentTheme: FashionTheme
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { onSelectionChange(!isSelected) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) currentTheme.accent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(2.dp, currentTheme.accent) else null,
        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(currentTheme.accent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                FashionText(
                    text = item.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                FashionText(
                    text = item.brand,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) currentTheme.accent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveOutfitDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    currentTheme: FashionTheme
) {
    var outfitName by remember { mutableStateOf("") }
    var outfitOccasion by remember { mutableStateOf("") }

    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    FashionText(
                        text = "Save Outfit",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = outfitName,
                        onValueChange = { outfitName = it },
                        label = { FashionText("Outfit Name") },
                        placeholder = { FashionText("e.g., Sunday Brunch") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = outfitOccasion,
                        onValueChange = { outfitOccasion = it },
                        label = { FashionText("Occasion") },
                        placeholder = { FashionText("e.g., Casual, Work, Date") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FashionButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            FashionText("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        FashionButton(
                            onClick = {
                                if (outfitName.isNotBlank()) {
                                    onSave(outfitName, outfitOccasion)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = currentTheme.accent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = outfitName.isNotBlank()
                        ) {
                            FashionText("Save", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClothingItemCard(item: ClothingItem, currentTheme: FashionTheme) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable { /* View item details */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            // Favorite icon
            IconButton(
                onClick = { /* Toggle favorite */ },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (item.isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Item info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
                    .padding(12.dp)
            ) {
                FashionText(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 2
                )

                if (item.brand.isNotEmpty()) {
                    FashionText(
                        text = item.brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// Outfits Screen
// Replace your existing OutfitsScreen with this enhanced version

// Outfits Screen - Enhanced with full functionality
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitsScreen(
    viewModel: UserPreferencesViewModel,
    userPreferences: UserPreferences,
    currentTheme: FashionTheme
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedOutfit by remember { mutableStateOf<Outfit?>(null) }
    var editingOutfit by remember { mutableStateOf<Outfit?>(null) }
    var favoriteOutfits by remember { mutableStateOf(setOf<String>()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    // Enhanced sample outfits with more variety
    val enhancedOutfits = remember {
        mutableStateListOf<Outfit>().apply {
            addAll(listOf(
                Outfit("1", "Casual Friday", SampleData.sampleClothingItems.take(3), "Work", "Today", false, FashionImages.sampleOutfits[0]),
                Outfit("2", "Date Night", listOf(SampleData.sampleClothingItems[3], SampleData.sampleClothingItems[5]), "Date", "Saturday", true, FashionImages.sampleOutfits[1]),
                Outfit("3", "Weekend Brunch", listOf(SampleData.sampleClothingItems[0], SampleData.sampleClothingItems[2], SampleData.sampleClothingItems[4]), "Casual", "Sunday", false, FashionImages.sampleOutfits[2]),
                Outfit("4", "Business Meeting", listOf(SampleData.sampleClothingItems[1], SampleData.sampleClothingItems[2]), "Work", "Monday", false, FashionImages.sampleOutfits[3]),
                Outfit("5", "Girls Night Out", listOf(SampleData.sampleClothingItems[3], SampleData.sampleClothingItems[5]), "Party", "Friday", true, FashionImages.sampleOutfits[0])
            ))
        }
    }

    // Filter outfits based on search and filter
    val filteredOutfits = enhancedOutfits.filter { outfit ->
        val matchesSearch = outfit.name.contains(searchQuery, ignoreCase = true) ||
                outfit.occasion.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Favorites" -> outfit.isFavorite || favoriteOutfits.contains(outfit.id)
            "Work" -> outfit.occasion.equals("Work", ignoreCase = true)
            "Casual" -> outfit.occasion.equals("Casual", ignoreCase = true)
            "Date" -> outfit.occasion.equals("Date", ignoreCase = true)
            "Party" -> outfit.occasion.equals("Party", ignoreCase = true)
            "All" -> true
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                // Enhanced Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        FashionText(
                            text = "My Outfits",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        FashionText(
                            text = "${filteredOutfits.size} outfits in your collection",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    FashionButton(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentTheme.accent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FashionText("Create", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { FashionText("Search outfits, occasions...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = currentTheme.accent
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = currentTheme.accent,
                        focusedLabelColor = currentTheme.accent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf("All", "Favorites", "Work", "Casual", "Date", "Party")
                    items(filters) { filter ->
                        FilterChip(
                            onClick = { selectedFilter = filter },
                            label = { FashionText(filter, fontSize = 12.sp) },
                            selected = selectedFilter == filter,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = currentTheme.accent.copy(alpha = 0.2f),
                                selectedLabelColor = currentTheme.accent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick stats
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = currentTheme.accent.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutfitStat("${enhancedOutfits.size}", "Total Outfits", Icons.Filled.Favorite)
                        OutfitStat("${enhancedOutfits.count { it.isFavorite || favoriteOutfits.contains(it.id) }}", "Favorites", Icons.Filled.Star)
                        OutfitStat("${enhancedOutfits.count { it.occasion == "Work" }}", "Work Outfits", Icons.Filled.DateRange)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (filteredOutfits.isEmpty()) {
                item {
                    EmptyOutfitsState(
                        onCreateClick = { showCreateDialog = true },
                        currentTheme = currentTheme,
                        isFiltered = searchQuery.isNotEmpty() || selectedFilter != "All"
                    )
                }
            } else {
                items(filteredOutfits) { outfit ->
                    EnhancedOutfitCard(
                        outfit = outfit,
                        currentTheme = currentTheme,
                        onFavoriteClick = {
                            favoriteOutfits = if (favoriteOutfits.contains(outfit.id)) {
                                favoriteOutfits - outfit.id
                            } else {
                                favoriteOutfits + outfit.id
                            }
                        },
                        onEditClick = { editingOutfit = outfit },
                        onDeleteClick = { enhancedOutfits.remove(outfit) },
                        onViewClick = { selectedOutfit = outfit },
                        isFavorite = outfit.isFavorite || favoriteOutfits.contains(outfit.id)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Dialogs
        if (showCreateDialog) {
            CreateOutfitDialog(
                showDialog = showCreateDialog,
                onDismiss = { showCreateDialog = false },
                onCreateOutfit = { newOutfit ->
                    enhancedOutfits.add(newOutfit)
                    showCreateDialog = false
                },
                currentTheme = currentTheme
            )
        }

        if (selectedOutfit != null) {
            OutfitDetailsDialog(
                outfit = selectedOutfit!!,
                onDismiss = { selectedOutfit = null },
                currentTheme = currentTheme,
                onEdit = {
                    editingOutfit = selectedOutfit
                    selectedOutfit = null
                },
                onDelete = {
                    enhancedOutfits.remove(selectedOutfit)
                    selectedOutfit = null
                }
            )
        }

        if (editingOutfit != null) {
            EditOutfitDialog(
                outfit = editingOutfit!!,
                onDismiss = { editingOutfit = null },
                onSave = { updatedOutfit ->
                    val index = enhancedOutfits.indexOfFirst { it.id == updatedOutfit.id }
                    if (index != -1) {
                        enhancedOutfits[index] = updatedOutfit
                    }
                    editingOutfit = null
                },
                currentTheme = currentTheme
            )
        }
    }
}

// Enhanced Outfit Card with full functionality
@Composable
fun EnhancedOutfitCard(
    outfit: Outfit,
    currentTheme: FashionTheme,
    onFavoriteClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onViewClick: () -> Unit,
    isFavorite: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = outfit.imageUrl,
                    contentDescription = outfit.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                // Favorite button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Occasion tag
                if (outfit.occasion.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = currentTheme.accent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        FashionText(
                            text = outfit.occasion,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        FashionText(
                            text = outfit.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (outfit.date.isNotEmpty()) {
                            FashionText(
                                text = outfit.date,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(currentTheme.accent.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit",
                                tint = currentTheme.accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Red.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Items in outfit
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(outfit.items.take(4)) { item ->
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (outfit.items.size > 4) {
                        item {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                FashionText(
                                    text = "+${outfit.items.size - 4}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Empty Outfits State
@Composable
fun EmptyOutfitsState(
    onCreateClick: () -> Unit,
    currentTheme: FashionTheme,
    isFiltered: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isFiltered) Icons.Filled.Search else Icons.Filled.Favorite,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        FashionText(
            text = if (isFiltered) "No outfits found" else "No outfits yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        FashionText(
            text = if (isFiltered) "Try adjusting your search or filters" else "Create your first outfit by combining your favorite pieces",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!isFiltered) {
            FashionButton(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentTheme.accent
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FashionText("Create First Outfit", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// Create Outfit Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOutfitDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCreateOutfit: (Outfit) -> Unit,
    currentTheme: FashionTheme
) {
    var outfitName by remember { mutableStateOf("") }
    var outfitOccasion by remember { mutableStateOf("") }
    var selectedItems by remember { mutableStateOf(listOf<ClothingItem>()) }

    val occasions = listOf("Work", "Casual", "Date", "Party", "Travel", "Sport", "Formal")

    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                LazyColumn(
                    modifier = Modifier.padding(24.dp)
                ) {
                    item {
                        FashionText(
                            text = "Create New Outfit",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = outfitName,
                            onValueChange = { outfitName = it },
                            label = { FashionText("Outfit Name") },
                            placeholder = { FashionText("e.g., Sunday Brunch") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = currentTheme.accent,
                                focusedLabelColor = currentTheme.accent
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Occasion selection
                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = outfitOccasion,
                                onValueChange = {},
                                readOnly = true,
                                label = { FashionText("Occasion") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = currentTheme.accent,
                                    focusedLabelColor = currentTheme.accent
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                occasions.forEach { occasion ->
                                    DropdownMenuItem(
                                        text = { FashionText(occasion) },
                                        onClick = {
                                            outfitOccasion = occasion
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        FashionText(
                            text = "Select Items",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Available items to select
                    items(SampleData.sampleClothingItems.chunked(2)) { itemPair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemPair.forEach { item ->
                                SelectableItemCard(
                                    item = item,
                                    isSelected = selectedItems.contains(item),
                                    onSelectionChange = {
                                        selectedItems = if (selectedItems.contains(item)) {
                                            selectedItems - item
                                        } else {
                                            selectedItems + item
                                        }
                                    },
                                    currentTheme = currentTheme,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Fill remaining space if odd number of items
                            if (itemPair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FashionButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                FashionText("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            FashionButton(
                                onClick = {
                                    if (outfitName.isNotBlank() && selectedItems.isNotEmpty()) {
                                        val newOutfit = Outfit(
                                            id = System.currentTimeMillis().toString(),
                                            name = outfitName,
                                            items = selectedItems,
                                            occasion = outfitOccasion,
                                            date = "Today",
                                            imageUrl = FashionImages.sampleOutfits.random()
                                        )
                                        onCreateOutfit(newOutfit)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = currentTheme.accent
                                ),
                                shape = RoundedCornerShape(12.dp),
                                enabled = outfitName.isNotBlank() && selectedItems.isNotEmpty()
                            ) {
                                FashionText("Create", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Outfit Details Dialog
@Composable
fun OutfitDetailsDialog(
    outfit: Outfit,
    onDismiss: () -> Unit,
    currentTheme: FashionTheme,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                // Image section
                AsyncImage(
                    model = outfit.imageUrl,
                    contentDescription = outfit.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Title and occasion
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            FashionText(
                                text = outfit.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (outfit.occasion.isNotEmpty()) {
                                FashionText(
                                    text = outfit.occasion,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = currentTheme.accent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = currentTheme.accent.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            FashionText(
                                text = "${outfit.items.size} items",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = currentTheme.accent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Outfit items
                    FashionText(
                        text = "Items in this outfit:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(outfit.items) { item ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.name,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                FashionText(
                                    text = item.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FashionButton(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = currentTheme.accent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FashionText("Edit", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FashionButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        FashionText("Close", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { FashionText("Delete Outfit", fontWeight = FontWeight.Bold) },
            text = { FashionText("Are you sure you want to delete '${outfit.name}'? This action cannot be undone.") },
            confirmButton = {
                FashionButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    FashionText("Delete", color = Color.White)
                }
            },
            dismissButton = {
                FashionButton(
                    onClick = { showDeleteConfirmation = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    FashionText("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

// Edit Outfit Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOutfitDialog(
    outfit: Outfit,
    onDismiss: () -> Unit,
    onSave: (Outfit) -> Unit,
    currentTheme: FashionTheme
) {
    var outfitName by remember { mutableStateOf(outfit.name) }
    var outfitOccasion by remember { mutableStateOf(outfit.occasion) }
    var selectedItems by remember { mutableStateOf(outfit.items) }

    val occasions = listOf("Work", "Casual", "Date", "Party", "Travel", "Sport", "Formal")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FashionText(
                            text = "Edit Outfit",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            tint = currentTheme.accent,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = outfitName,
                        onValueChange = { outfitName = it },
                        label = { FashionText("Outfit Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = currentTheme.accent,
                            focusedLabelColor = currentTheme.accent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Occasion selection
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = outfitOccasion,
                            onValueChange = {},
                            readOnly = true,
                            label = { FashionText("Occasion") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = currentTheme.accent,
                                focusedLabelColor = currentTheme.accent
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            occasions.forEach { occasion ->
                                DropdownMenuItem(
                                    text = { FashionText(occasion) },
                                    onClick = {
                                        outfitOccasion = occasion
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FashionText(
                        text = "Current Items",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Show current items
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedItems) { item ->
                            Box {
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.name,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                IconButton(
                                    onClick = {
                                        selectedItems = selectedItems - item
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .offset(x = 6.dp, y = (-6).dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Remove",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    FashionText(
                        text = "Add More Items",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Available items to add
                items(SampleData.sampleClothingItems.chunked(2)) { itemPair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemPair.forEach { item ->
                            SelectableItemCard(
                                item = item,
                                isSelected = selectedItems.contains(item),
                                onSelectionChange = {
                                    selectedItems = if (selectedItems.contains(item)) {
                                        selectedItems - item
                                    } else {
                                        selectedItems + item
                                    }
                                },
                                currentTheme = currentTheme,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Fill remaining space if odd number of items
                        if (itemPair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FashionButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            FashionText("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        FashionButton(
                            onClick = {
                                if (outfitName.isNotBlank() && selectedItems.isNotEmpty()) {
                                    val updatedOutfit = outfit.copy(
                                        name = outfitName,
                                        occasion = outfitOccasion,
                                        items = selectedItems
                                    )
                                    onSave(updatedOutfit)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = currentTheme.accent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = outfitName.isNotBlank() && selectedItems.isNotEmpty()
                        ) {
                            FashionText("Save Changes", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

// Updated SelectableItemCard with modifier parameter
@Composable
fun SelectableItemCard(
    item: ClothingItem,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    currentTheme: FashionTheme,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onSelectionChange(!isSelected) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) currentTheme.accent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(2.dp, currentTheme.accent) else null,
        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(currentTheme.accent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                FashionText(
                    text = item.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                FashionText(
                    text = item.brand,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) currentTheme.accent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun OutfitStat(value: String, label: String, icon: ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        FashionText(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        FashionText(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun OutfitCard(outfit: Outfit, currentTheme: FashionTheme) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* View outfit details */ },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = outfit.imageUrl,
                    contentDescription = outfit.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                // Favorite button
                IconButton(
                    onClick = { /* Toggle favorite */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                ) {
                    Icon(
                        imageVector = if (outfit.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (outfit.isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Occasion tag
                if (outfit.occasion.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = currentTheme.accent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        FashionText(
                            text = outfit.occasion,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        FashionText(
                            text = outfit.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (outfit.date.isNotEmpty()) {
                            FashionText(
                                text = outfit.date,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = { /* Share outfit */ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { /* Edit outfit */ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Items in outfit
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    outfit.items.take(4).forEach { item ->
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (outfit.items.size > 4) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            FashionText(
                                text = "+${outfit.items.size - 4}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    viewModel: UserPreferencesViewModel,
    userPreferences: UserPreferences,
    currentTheme: FashionTheme
) {
    var selectedWeekOffset by remember { mutableStateOf(0) }
    var selectedDay by remember { mutableStateOf(2) } // Wednesday
    var showOutfitDialog by remember { mutableStateOf(false) }
    var showWeatherDialog by remember { mutableStateOf(false) }
    var plannedOutfits by remember { mutableStateOf(mapOf<Int, PlannedOutfit>()) }
    var showSuggestions by remember { mutableStateOf(false) }
    var selectedSuggestion by remember { mutableStateOf<OutfitSuggestion?>(null) }

    // Sample weather data
    val weatherData = remember {
        mapOf(
            0 to WeatherInfo("Sunny", 24, "☀️", "Perfect for light layers"),
            1 to WeatherInfo("Cloudy", 18, "☁️", "Bring a light jacket"),
            2 to WeatherInfo("Rainy", 16, "🌧️", "Don't forget an umbrella"),
            3 to WeatherInfo("Partly Cloudy", 22, "⛅", "Comfortable weather"),
            4 to WeatherInfo("Sunny", 26, "☀️", "Great for summer pieces"),
            5 to WeatherInfo("Windy", 20, "💨", "Secure loose clothing"),
            6 to WeatherInfo("Clear", 23, "🌤️", "Perfect day for anything")
        )
    }

    // Sample outfit suggestions
    val outfitSuggestions = remember {
        listOf(
            OutfitSuggestion(
                "1", "Business Casual", "Perfect for office meetings",
                FashionImages.sampleOutfits[0], listOf("Blazer", "Trousers", "Shirt")
            ),
            OutfitSuggestion(
                "2", "Weekend Brunch", "Relaxed yet stylish",
                FashionImages.sampleOutfits[1], listOf("Dress", "Sneakers", "Cardigan")
            ),
            OutfitSuggestion(
                "3", "Date Night", "Elegant evening look",
                FashionImages.sampleOutfits[2], listOf("Dress", "Heels", "Jewelry")
            ),
            OutfitSuggestion(
                "4", "Gym & Errands", "Active and comfortable",
                FashionImages.sampleOutfits[3], listOf("Leggings", "Top", "Sneakers")
            )
        )
    }

    // Generate week dates
    val currentWeek = remember(selectedWeekOffset) {
        generateWeekDates(selectedWeekOffset)
    }

    val calendar = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                // Enhanced Header with gradient background
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                    colors = listOf(
                                        currentTheme.primary.copy(alpha = 0.9f),
                                        currentTheme.accent.copy(alpha = 0.8f)
                                    )
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    FashionText(
                                        text = "Style Planner",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    FashionText(
                                        text = "Plan your perfect week",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }

                                IconButton(
                                    onClick = { showSuggestions = !showSuggestions },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Suggestions",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Week navigation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { selectedWeekOffset-- },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Previous week",
                                        tint = Color.White
                                    )
                                }

                                FashionText(
                                    text = when (selectedWeekOffset) {
                                        0 -> "This Week"
                                        1 -> "Next Week"
                                        -1 -> "Last Week"
                                        else -> "${if (selectedWeekOffset > 0) "+" else ""}$selectedWeekOffset weeks"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )

                                IconButton(
                                    onClick = { selectedWeekOffset++ },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = "Next week",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Enhanced Calendar with weather and outfits
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FashionText(
                                text = currentWeek.first,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            FashionText(
                                text = "${plannedOutfits.size} outfits planned",
                                style = MaterialTheme.typography.bodyMedium,
                                color = currentTheme.accent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Enhanced calendar days
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(calendar) { index, day ->
                                val dayDate = currentWeek.second[index]
                                val weather = weatherData[index]
                                val hasOutfit = plannedOutfits.containsKey(index)
                                val isSelected = selectedDay == index

                                EnhancedDayCard(
                                    day = day,
                                    date = dayDate,
                                    weather = weather,
                                    hasOutfit = hasOutfit,
                                    isSelected = isSelected,
                                    onClick = { selectedDay = index },
                                    currentTheme = currentTheme
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Weather info for selected day
                weatherData[selectedDay]?.let { selectedWeather ->
                    WeatherCard(
                        weather = selectedWeather,
                        dayName = calendar[selectedDay],
                        currentTheme = currentTheme,
                        onClick = { showWeatherDialog = true }
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Outfit section for selected day
                val selectedWeather = weatherData[selectedDay]
                if (plannedOutfits.containsKey(selectedDay)) {
                    PlannedOutfitCard(
                        outfit = plannedOutfits[selectedDay]!!,
                        dayName = calendar[selectedDay],
                        currentTheme = currentTheme,
                        onEdit = { showOutfitDialog = true },
                        onRemove = {
                            plannedOutfits = plannedOutfits.toMutableMap().apply {
                                remove(selectedDay)
                            }
                        }
                    )
                } else {
                    EmptyOutfitCard(
                        dayName = calendar[selectedDay],
                        weather = selectedWeather,
                        currentTheme = currentTheme,
                        onPlanOutfit = { showOutfitDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // AI Suggestions section (expandable)
                if (showSuggestions) {
                    AISuggestionsSection(
                        suggestions = outfitSuggestions,
                        weather = selectedWeather,
                        currentTheme = currentTheme,
                        onSuggestionClick = { suggestion ->
                            selectedSuggestion = suggestion
                        },
                        onApplySuggestion = { suggestion ->
                            plannedOutfits = plannedOutfits.toMutableMap().apply {
                                put(selectedDay, PlannedOutfit(
                                    id = System.currentTimeMillis().toString(),
                                    name = suggestion.name,
                                    items = suggestion.items,
                                    imageUrl = suggestion.imageUrl,
                                    weather = selectedWeather?.condition ?: "",
                                    notes = suggestion.description
                                ))
                            }
                            selectedSuggestion = null
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Week overview stats
                WeekOverviewStats(
                    plannedOutfits = plannedOutfits,
                    currentTheme = currentTheme
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Floating action buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Quick add outfit FAB
            FloatingActionButton(
                onClick = { showOutfitDialog = true },
                containerColor = currentTheme.accent,
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Plan outfit",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Weather details FAB
            weatherData[selectedDay]?.let { selectedWeather ->
                FloatingActionButton(
                    onClick = { showWeatherDialog = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    FashionText(
                        text = selectedWeather.emoji,
                        fontSize = 20.sp
                    )
                }
            }
        }

        // Dialogs
        if (showOutfitDialog) {
            PlanOutfitDialog(
                showDialog = showOutfitDialog,
                onDismiss = { showOutfitDialog = false },
                dayName = calendar[selectedDay],
                weather = weatherData[selectedDay],
                currentOutfit = plannedOutfits[selectedDay],
                onSave = { outfit ->
                    plannedOutfits = plannedOutfits.toMutableMap().apply {
                        put(selectedDay, outfit)
                    }
                    showOutfitDialog = false
                },
                currentTheme = currentTheme
            )
        }

        if (showWeatherDialog) {
            weatherData[selectedDay]?.let { selectedWeather ->
                WeatherDetailsDialog(
                    weather = selectedWeather,
                    dayName = calendar[selectedDay],
                    onDismiss = { showWeatherDialog = false },
                    currentTheme = currentTheme
                )
            }
        }
    }
}

// Data classes
data class WeatherInfo(
    val condition: String,
    val temperature: Int,
    val emoji: String,
    val suggestion: String
)

data class PlannedOutfit(
    val id: String,
    val name: String,
    val items: List<String>,
    val imageUrl: String,
    val weather: String,
    val notes: String = ""
)

data class OutfitSuggestion(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val items: List<String>
)

// Enhanced Day Card
@Composable
fun EnhancedDayCard(
    day: String,
    date: String,
    weather: WeatherInfo?,
    hasOutfit: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    currentTheme: FashionTheme
) {
    Card(
        modifier = Modifier
            .width(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) currentTheme.accent else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 2.dp),
        border = if (isSelected) BorderStroke(2.dp, currentTheme.accent) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FashionText(
                text = day,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            FashionText(
                text = date,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Weather emoji
            if (weather != null) {
                FashionText(
                    text = weather.emoji,
                    fontSize = 20.sp
                )

                FashionText(
                    text = "${weather.temperature}°",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Outfit indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            hasOutfit && isSelected -> Color.White
                            hasOutfit -> currentTheme.accent
                            else -> Color.Transparent
                        }
                    )
                    .border(
                        1.dp,
                        if (hasOutfit) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        CircleShape
                    )
            )
        }
    }
}

// Weather Card
@Composable
fun WeatherCard(
    weather: WeatherInfo,
    dayName: String,
    currentTheme: FashionTheme,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(currentTheme.accent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                FashionText(
                    text = weather.emoji,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FashionText(
                        text = "${weather.temperature}°C",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FashionText(
                        text = weather.condition,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                FashionText(
                    text = weather.suggestion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Weather details",
                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
            )
        }
    }
}

// Planned Outfit Card
@Composable
fun PlannedOutfitCard(
    outfit: PlannedOutfit,
    dayName: String,
    currentTheme: FashionTheme,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = outfit.imageUrl,
                    contentDescription = outfit.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                )
                            )
                        )
                )

                // Action buttons
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = currentTheme.accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Remove",
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Outfit title overlay
                FashionText(
                    text = outfit.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = currentTheme.accent.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        FashionText(
                            text = "$dayName's Look",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = currentTheme.accent,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (outfit.weather.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Weather",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            FashionText(
                                text = outfit.weather,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                if (outfit.items.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    FashionText(
                        text = "Items: ${outfit.items.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }

                if (outfit.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    FashionText(
                        text = outfit.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

// Empty Outfit Card
@Composable
fun EmptyOutfitCard(
    dayName: String,
    weather: WeatherInfo?,
    currentTheme: FashionTheme,
    onPlanOutfit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlanOutfit() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(
            1.dp,
            currentTheme.accent.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(currentTheme.accent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Plan outfit",
                    tint = currentTheme.accent,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            FashionText(
                text = "Plan $dayName's Outfit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            FashionText(
                text = weather?.let { "Perfect for ${it.condition.lowercase()} weather (${it.temperature}°C)" }
                    ?: "Create a perfect look for this day",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            FashionButton(
                onClick = onPlanOutfit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentTheme.accent
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FashionText(
                    "Plan Outfit",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// AI Suggestions Section
@Composable
fun AISuggestionsSection(
    suggestions: List<OutfitSuggestion>,
    weather: WeatherInfo?,
    currentTheme: FashionTheme,
    onSuggestionClick: (OutfitSuggestion) -> Unit,
    onApplySuggestion: (OutfitSuggestion) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = currentTheme.accent.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = currentTheme.accent,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                FashionText(
                    text = "AI Style Suggestions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            weather?.let {
                FashionText(
                    text = "Based on ${it.condition.lowercase()} weather",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(suggestions) { suggestion ->
                    SuggestionCard(
                        suggestion = suggestion,
                        currentTheme = currentTheme,
                        onClick = { onSuggestionClick(suggestion) },
                        onApply = { onApplySuggestion(suggestion) }
                    )
                }
            }
        }
    }
}

// Enhanced Suggestion Card
@Composable
fun SuggestionCard(
    suggestion: OutfitSuggestion,
    currentTheme: FashionTheme,
    onClick: () -> Unit,
    onApply: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = suggestion.imageUrl,
                    contentDescription = suggestion.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Apply button overlay
                FashionButton(
                    onClick = onApply,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentTheme.accent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    FashionText(
                        "Apply",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                FashionText(
                    text = suggestion.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                FashionText(
                    text = suggestion.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(suggestion.items.take(3)) { item ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = currentTheme.accent.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            FashionText(
                                text = item,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = currentTheme.accent,
                                fontSize = 10.sp
                            )
                        }
                    }

                    if (suggestion.items.size > 3) {
                        item {
                            FashionText(
                                text = "+${suggestion.items.size - 3}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Week Overview Stats
@Composable
fun WeekOverviewStats(
    plannedOutfits: Map<Int, PlannedOutfit>,
    currentTheme: FashionTheme
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            FashionText(
                text = "Week Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = "${plannedOutfits.size}",
                    label = "Planned",
                    icon = Icons.Filled.Check,
                    color = currentTheme.accent
                )

                StatItem(
                    value = "${7 - plannedOutfits.size}",
                    label = "Remaining",
                    icon = Icons.Filled.Add,
                    color = MaterialTheme.colorScheme.outline
                )

                StatItem(
                    value = "${(plannedOutfits.size * 100 / 7)}%",
                    label = "Complete",
                    icon = Icons.Filled.Star,
                    color = if (plannedOutfits.size >= 5) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                )
            }

            if (plannedOutfits.size < 7) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = currentTheme.accent.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = currentTheme.accent,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FashionText(
                            text = "Complete your week for the perfect wardrobe planning!",
                            style = MaterialTheme.typography.bodySmall,
                            color = currentTheme.accent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        FashionText(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )

        FashionText(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

// Plan Outfit Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanOutfitDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    dayName: String,
    weather: WeatherInfo?,
    currentOutfit: PlannedOutfit?,
    onSave: (PlannedOutfit) -> Unit,
    currentTheme: FashionTheme
) {
    var outfitName by remember { mutableStateOf(currentOutfit?.name ?: "") }
    var selectedItems by remember { mutableStateOf(currentOutfit?.items ?: emptyList()) }
    var notes by remember { mutableStateOf(currentOutfit?.notes ?: "") }

    val availableItems = listOf(
        "White Shirt", "Black Blazer", "Blue Jeans", "Little Black Dress",
        "Sneakers", "Heels", "Cardigan", "Trench Coat", "Scarf", "Handbag"
    )

    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                LazyColumn(
                    modifier = Modifier.padding(24.dp)
                ) {
                    item {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                FashionText(
                                    text = if (currentOutfit != null) "Edit Outfit" else "Plan Outfit",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                FashionText(
                                    text = "for $dayName",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = currentTheme.accent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = null,
                                tint = currentTheme.accent,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Weather info
                        if (weather != null) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    FashionText(
                                        text = weather.emoji,
                                        fontSize = 20.sp
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column {
                                        FashionText(
                                            text = "${weather.condition}, ${weather.temperature}°C",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        FashionText(
                                            text = weather.suggestion,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Outfit name
                        OutlinedTextField(
                            value = outfitName,
                            onValueChange = { outfitName = it },
                            label = { FashionText("Outfit Name") },
                            placeholder = { FashionText("e.g., Business Casual") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = currentTheme.accent,
                                focusedLabelColor = currentTheme.accent
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = currentTheme.accent
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Items selection
                        FashionText(
                            text = "Select Items",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        FashionText(
                            text = "${selectedItems.size} items selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Items grid
                    items(availableItems.chunked(2)) { itemPair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemPair.forEach { item ->
                                val isSelected = selectedItems.contains(item)

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedItems = if (isSelected) {
                                                selectedItems - item
                                            } else {
                                                selectedItems + item
                                            }
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected)
                                            currentTheme.accent.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    border = if (isSelected) BorderStroke(2.dp, currentTheme.accent) else null,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Filled.Check else Icons.Filled.Add,
                                            contentDescription = null,
                                            tint = if (isSelected) currentTheme.accent else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        FashionText(
                                            text = item,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isSelected) currentTheme.accent else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                }
                            }

                            // Fill remaining space if odd number
                            if (itemPair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Notes
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { FashionText("Notes (Optional)") },
                            placeholder = { FashionText("Add any special notes...") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = currentTheme.accent,
                                focusedLabelColor = currentTheme.accent
                            ),
                            minLines = 2,
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FashionButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                FashionText("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            FashionButton(
                                onClick = {
                                    if (outfitName.isNotBlank() && selectedItems.isNotEmpty()) {
                                        val outfit = PlannedOutfit(
                                            id = currentOutfit?.id ?: System.currentTimeMillis().toString(),
                                            name = outfitName,
                                            items = selectedItems,
                                            imageUrl = currentOutfit?.imageUrl ?: FashionImages.sampleOutfits.random(),
                                            weather = weather?.condition ?: "",
                                            notes = notes
                                        )
                                        onSave(outfit)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = currentTheme.accent
                                ),
                                shape = RoundedCornerShape(12.dp),
                                enabled = outfitName.isNotBlank() && selectedItems.isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                FashionText("Save", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Weather Details Dialog
@Composable
fun WeatherDetailsDialog(
    weather: WeatherInfo,
    dayName: String,
    onDismiss: () -> Unit,
    currentTheme: FashionTheme
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FashionText(
                    text = "$dayName Weather",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Weather icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(currentTheme.accent.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    FashionText(
                        text = weather.emoji,
                        fontSize = 48.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                FashionText(
                    text = "${weather.temperature}°C",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FashionText(
                    text = weather.condition,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = currentTheme.accent.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = currentTheme.accent,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        FashionText(
                            text = weather.suggestion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = currentTheme.accent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                FashionButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentTheme.accent
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    FashionText("Got it", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// Helper function to generate week dates
fun generateWeekDates(weekOffset: Int): Pair<String, List<String>> {
    val calendar = Calendar.getInstance()

    // Add the week offset
    calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)

    // Set to Monday (first day of week)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    val dates = (0..6).map { dayOffset ->
        val dayCalendar = calendar.clone() as Calendar
        dayCalendar.add(Calendar.DAY_OF_WEEK, dayOffset)
        dayCalendar.get(Calendar.DAY_OF_MONTH).toString()
    }

    val monthYearFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    val monthYear = monthYearFormat.format(calendar.time)

    return Pair(monthYear, dates)
}


@Composable
fun FashionAnalytics(
    plannedOutfits: Map<Int, PlannedOutfit>,
    currentTheme: FashionTheme
) {
    val totalPlanned = plannedOutfits.size
    val completionRate = (totalPlanned * 100 / 7)
    val favoriteItems = plannedOutfits.values.flatMap { it.items }.groupBy { it }.maxByOrNull { it.value.size }?.key

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = currentTheme.accent.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = currentTheme.accent,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                FashionText(
                    text = "Style Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Completion progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FashionText(
                        text = "Week Progress",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FashionText(
                        text = "$completionRate%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = currentTheme.accent
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = completionRate / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = currentTheme.accent,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            if (favoriteItems != null && totalPlanned > 1) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FashionText(
                            text = "Most used item: $favoriteItems",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// Smart Recommendations based on weather and preferences
@Composable
fun SmartRecommendations(
    weather: WeatherInfo?,
    plannedOutfits: Map<Int, PlannedOutfit>,
    currentTheme: FashionTheme
) {
    val recommendations = remember(weather, plannedOutfits) {
        generateSmartRecommendations(weather, plannedOutfits)
    }

    if (recommendations.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Build,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FashionText(
                        text = "Smart Tips",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                recommendations.forEach { recommendation ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary)
                                .offset(y = 6.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        FashionText(
                            text = recommendation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// Generate smart recommendations based on context
fun generateSmartRecommendations(
    weather: WeatherInfo?,
    plannedOutfits: Map<Int, PlannedOutfit>
): List<String> {
    val recommendations = mutableListOf<String>()

    // Weather-based recommendations
    weather?.let { w ->
        when {
            w.temperature < 15 -> recommendations.add("Consider layering with a warm coat or cardigan")
            w.temperature > 25 -> recommendations.add("Light fabrics and breathable materials work best")
            w.condition.contains("Rain", true) -> recommendations.add("Don't forget waterproof shoes and an umbrella")
            w.condition.contains("Windy", true) -> recommendations.add("Avoid loose scarves and choose fitted silhouettes")
            else -> {

                recommendations.add("Perfect weather for your favorite outfit!")
            }
        }
    }

    // Planning recommendations
    val plannedCount = plannedOutfits.size
    when {
        plannedCount == 0 -> recommendations.add("Start planning your week for a stress-free morning routine")
        plannedCount < 3 -> recommendations.add("Plan a few more outfits to stay ahead of your week")
        plannedCount >= 5 -> recommendations.add("Great job! Your week is almost completely planned")
    }

    // Style variety recommendations
    val usedItems = plannedOutfits.values.flatMap { it.items }
    val itemFrequency = usedItems.groupBy { it }.mapValues { it.value.size }
    val overusedItems = itemFrequency.filter { it.value > 2 }

    if (overusedItems.isNotEmpty()) {
        recommendations.add("Try mixing in some different pieces to add variety to your looks")
    }

    return recommendations.take(3) // Limit to top 3 recommendations
}

// Color coordination helper
@Composable
fun ColorCoordinationTip(
    currentTheme: FashionTheme
) {
    val colorTips = listOf(
        "Neutral colors like black, white, and beige work with everything",
        "Try the 60-30-10 rule: 60% neutral, 30% secondary, 10% accent color",
        "Monochromatic outfits in different shades look sophisticated",
        "Complementary colors create striking, balanced looks",
        "When in doubt, add a pop of color with accessories"
    )

    val randomTip = remember { colorTips.random() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = currentTheme.primary.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Face,
                contentDescription = null,
                tint = currentTheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                FashionText(
                    text = "Color Tip",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = currentTheme.primary
                )

                FashionText(
                    text = randomTip,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// Quick action shortcuts
@Composable
fun QuickActions(
    onPlanOutfit: () -> Unit,
    onViewAnalytics: () -> Unit,
    onShuffleWeek: () -> Unit,
    currentTheme: FashionTheme
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            QuickActionCard(
                icon = Icons.Filled.Add,
                title = "Plan",
                subtitle = "New Outfit",
                onClick = onPlanOutfit,
                color = currentTheme.accent
            )
        }

        item {
            QuickActionCard(
                icon = Icons.Filled.Star,
                title = "Analytics",
                subtitle = "View Stats",
                onClick = onViewAnalytics,
                color = MaterialTheme.colorScheme.tertiary
            )
        }

        item {
            QuickActionCard(
                icon = Icons.Filled.Refresh,
                title = "Shuffle",
                subtitle = "Mix It Up",
                onClick = onShuffleWeek,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            FashionText(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )

            FashionText(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// 3. ADD THE MISSING SuggestionCard COMPOSABLE
@Composable
fun SuggestionCard(
    title: String,
    description: String,
    imageUrl: String,
    currentTheme: FashionTheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Apply suggestion */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                FashionText(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FashionText(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = { /* Apply suggestion */ },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(currentTheme.accent.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Apply",
                    tint = currentTheme.accent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            FashionText(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            FashionText(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    currentTheme: FashionTheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = currentTheme.accent,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            FashionText(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            FashionText(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun FashionStat(value: String, label: String, icon: ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        FashionText(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        FashionText(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

// Theme Selection Dialog
@Composable
fun ThemeSelectionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    viewModel: UserPreferencesViewModel,
    userPreferences: UserPreferences,
    currentTheme: FashionTheme
) {
    val themes = listOf(FashionTheme.Chic, FashionTheme.Rose, FashionTheme.Elegant, FashionTheme.Luxury)
    val coroutineScope = rememberCoroutineScope()

    if (showDialog) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    FashionText(
                        text = "Choose Your Style",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FashionText(
                        text = "Select a theme that matches your fashion style",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    themes.forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch {
                                        viewModel.saveSelectedTheme(theme.name)
                                        onDismiss()
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                            colors = listOf(theme.primary, theme.accent)
                                        )
                                    )
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                FashionText(
                                    text = theme.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                FashionText(
                                    text = when (theme.name) {
                                        "Chic Black" -> "Classic and timeless"
                                        "Rose Gold" -> "Feminine and elegant"
                                        "Elegant Navy" -> "Professional and sophisticated"
                                        "Luxury Purple" -> "Rich and luxurious"
                                        else -> "Beautiful theme"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }

                            if (theme.name == userPreferences.selectedTheme) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = theme.accent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        if (theme != themes.last()) {
                            Divider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    FashionButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentTheme.accent
                        )
                    ) {
                        FashionText("Done", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: UserPreferencesViewModel,
    userPreferences: UserPreferences,
    currentTheme: FashionTheme,
    onLogout: () -> Unit = {} // ADD THIS PARAMETER
) {
    val coroutineScope = rememberCoroutineScope()
    var userName by remember { mutableStateOf(userPreferences.userName) }
    var userEmail by remember { mutableStateOf(userPreferences.userEmail) }
    var isEditing by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) } // ADD THIS

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                FashionText(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Profile avatar
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(currentTheme.primary, currentTheme.accent)
                            )
                        )
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (userName.isNotEmpty()) {
                        FashionText(
                            text = userName.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 48.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Profile info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FashionText(
                                text = "Personal Information",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            IconButton(
                                onClick = {
                                    if (isEditing) {
                                        coroutineScope.launch {
                                            viewModel.saveUserName(userName)
                                            viewModel.saveUserEmail(userEmail)
                                        }
                                    }
                                    isEditing = !isEditing
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(currentTheme.accent.copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    imageVector = if (isEditing) Icons.Filled.Check else Icons.Filled.Edit,
                                    contentDescription = if (isEditing) "Save" else "Edit",
                                    tint = currentTheme.accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (isEditing) {
                            OutlinedTextField(
                                value = userName,
                                onValueChange = { userName = it },
                                label = { FashionText("Name") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = currentTheme.accent,
                                    focusedLabelColor = currentTheme.accent
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = userEmail,
                                onValueChange = { userEmail = it },
                                label = { FashionText("Email") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Email,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = currentTheme.accent,
                                    focusedLabelColor = currentTheme.accent
                                )
                            )
                        } else {
                            ProfileInfoRow(
                                icon = Icons.Filled.Person,
                                label = "Name",
                                value = if (userPreferences.userName.isNotEmpty()) userPreferences.userName else "Not set"
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            ProfileInfoRow(
                                icon = Icons.Filled.Email,
                                label = "Email",
                                value = if (userPreferences.userEmail.isNotEmpty()) userPreferences.userEmail else "Not set"
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            ProfileInfoRow(
                                icon = Icons.Filled.DateRange,
                                label = "Member Since",
                                value = "May 2025"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // App settings
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        FashionText(
                            text = "App Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Theme selector
                        SettingsItem(
                            icon = Icons.Filled.Face,
                            title = "Theme",
                            subtitle = userPreferences.selectedTheme,
                            onClick = { showThemeDialog = true },
                            currentTheme = currentTheme
                        )

                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // Language selector
                        SettingsItem(
                            icon = Icons.Filled.Star,
                            title = "Language",
                            subtitle = availableLanguages.find { it.code == userPreferences.selectedLanguage }?.name ?: "English",
                            onClick = { showLanguageDialog = true },
                            currentTheme = currentTheme
                        )

                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // Dark mode toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null,
                                tint = currentTheme.accent,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                FashionText(
                                    text = "Dark Mode",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                FashionText(
                                    text = "Adjust app appearance",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }

                            Switch(
                                checked = userPreferences.darkModeEnabled,
                                onCheckedChange = {
                                    coroutineScope.launch {
                                        viewModel.saveDarkModeEnabled(it)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = currentTheme.accent,
                                    checkedTrackColor = currentTheme.accent.copy(alpha = 0.5f)
                                )
                            )
                        }

                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // Notifications toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = null,
                                tint = currentTheme.accent,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                FashionText(
                                    text = "Notifications",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                FashionText(
                                    text = "Style alerts and reminders",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }

                            Switch(
                                checked = userPreferences.notificationsEnabled,
                                onCheckedChange = {
                                    coroutineScope.launch {
                                        viewModel.saveNotificationsEnabled(it)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = currentTheme.accent,
                                    checkedTrackColor = currentTheme.accent.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Fashion stats
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = currentTheme.accent.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        FashionText(
                            text = "Your Fashion Stats",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            FashionStat("25", "Items", Icons.Filled.Face)
                            FashionStat("8", "Outfits", Icons.Filled.Favorite)
                            FashionStat("3", "Favorites", Icons.Filled.Star)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ADD LOGOUT SECTION
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        FashionText(
                            text = "Account",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Logout Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLogoutDialog = true }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ExitToApp,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                FashionText(
                                    text = "Sign Out",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Red
                                )

                                FashionText(
                                    text = "Sign out of your account",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }

                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Theme Dialog
        if (showThemeDialog) {
            ThemeSelectionDialog(
                showDialog = showThemeDialog,
                onDismiss = { showThemeDialog = false },
                viewModel = viewModel,
                userPreferences = userPreferences,
                currentTheme = currentTheme
            )
        }

        // Language Dialog
        if (showLanguageDialog) {
            LanguageSelectionDialog(
                showDialog = showLanguageDialog,
                onDismiss = { showLanguageDialog = false },
                viewModel = viewModel,
                userPreferences = userPreferences,
                currentTheme = currentTheme
            )
        }

        // ADD LOGOUT CONFIRMATION DIALOG
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    FashionText(
                        "Sign Out",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    FashionText(
                        "Are you sure you want to sign out? You'll need to sign in again to access your wardrobe.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                },
                confirmButton = {
                    FashionButton(
                        onClick = {
                            showLogoutDialog = false
                            coroutineScope.launch {
                                viewModel.logout()
                                onLogout()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        FashionText("Sign Out", color = Color.White)
                    }
                },
                dismissButton = {
                    FashionButton(
                        onClick = { showLogoutDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        FashionText("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}
// Language Selection Dialog
@Composable
fun LanguageSelectionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    viewModel: UserPreferencesViewModel,
    userPreferences: UserPreferences,
    currentTheme: FashionTheme
) {
    val coroutineScope = rememberCoroutineScope()

    if (showDialog) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    FashionText(
                        text = "Select Language",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FashionText(
                        text = "Choose your preferred language",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    availableLanguages.forEach { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch {
                                        viewModel.saveSelectedLanguage(language.code)
                                        onDismiss()
                                    }
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = currentTheme.accent,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            FashionText(
                                text = language.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            if (language.code == userPreferences.selectedLanguage) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = currentTheme.accent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        if (language != availableLanguages.last()) {
                            Divider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    FashionButton(
                        onClick = { onDismiss() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentTheme.accent
                        )
                    ) {
                        FashionText("Done", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}