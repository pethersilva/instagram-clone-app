# Plano de Desenvolvimento — Android Instagram-like
> Kotlin · Jetpack Compose · MVVM + Clean Architecture · Testes Unitários

---

## Visão geral

Aplicação Android nativa que consome a API do `ms-instagram-clone`, exibindo um feed de imagens com suporte a likes, comentários e perfil de usuário. A arquitetura segue **MVVM + Clean Architecture** com separação clara de camadas, UI declarativa em **Jetpack Compose** e cobertura de testes unitários em todas as camadas de negócio.

---

## Stack tecnológica

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin 1.9+ |
| UI | Jetpack Compose (BOM 2024.x) |
| Arquitetura | MVVM + Clean Architecture |
| Injeção de dependência | Hilt |
| Navegação | Navigation Compose |
| Networking | Retrofit + OkHttp + Gson |
| Async | Kotlin Coroutines + Flow |
| Paginação | Paging 3 (cursor-based) |
| Cache de imagem | Coil |
| Armazenamento local | Room + DataStore |
| Testes unitários | JUnit 5 + MockK + Turbine |
| Testes de UI | Compose Testing |
| Build | Gradle (Kotlin DSL) |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 (Android 15) |

---

## Arquitetura de camadas

```
app/
└── src/main/kotlin/com/app/instagramclone/
    ├── domain/          # Kotlin puro — zero Android
    ├── data/            # Repositórios, API, Room
    ├── presentation/    # ViewModels, UI State
    └── ui/              # Composables, Screens, Navigation
```

### Regra de dependência
```
ui → presentation → domain ← data
```

A camada `domain` não conhece Android, Retrofit, Room nem Compose.
Cada camada tem seus próprios modelos de dados com Mappers entre elas.

### Modelos por camada

```
NetworkModel (data)  →  Mapper  →  DomainModel (domain)  →  Mapper  →  UiModel (presentation)
```

---

## Estrutura de pacotes detalhada

```
com/app/instagramclone/
│
├── domain/
│   ├── model/
│   │   ├── Post.kt
│   │   ├── User.kt
│   │   ├── Comment.kt
│   │   └── FeedPage.kt
│   ├── repository/
│   │   ├── PostRepository.kt        (interface)
│   │   ├── UserRepository.kt        (interface)
│   │   └── CommentRepository.kt     (interface)
│   └── usecase/
│       ├── feed/
│       │   └── GetFeedUseCase.kt
│       ├── post/
│       │   ├── GetPostUseCase.kt
│       │   ├── LikePostUseCase.kt
│       │   └── UnlikePostUseCase.kt
│       ├── comment/
│       │   ├── GetCommentsUseCase.kt
│       │   └── CreateCommentUseCase.kt
│       └── user/
│           ├── GetUserUseCase.kt
│           └── GetCurrentUserUseCase.kt
│
├── data/
│   ├── remote/
│   │   ├── api/
│   │   │   ├── FeedApiService.kt
│   │   │   ├── PostApiService.kt
│   │   │   ├── CommentApiService.kt
│   │   │   └── UserApiService.kt
│   │   └── model/
│   │       ├── PostNetworkModel.kt
│   │       ├── UserNetworkModel.kt
│   │       └── CommentNetworkModel.kt
│   ├── local/
│   │   ├── dao/
│   │   │   ├── PostDao.kt
│   │   │   └── UserDao.kt
│   │   ├── entity/
│   │   │   ├── PostEntity.kt
│   │   │   └── UserEntity.kt
│   │   └── AppDatabase.kt
│   ├── repository/
│   │   ├── PostRepositoryImpl.kt    (implementa interface do domain)
│   │   ├── UserRepositoryImpl.kt
│   │   └── CommentRepositoryImpl.kt
│   ├── paging/
│   │   └── FeedPagingSource.kt
│   └── mapper/
│       ├── PostMapper.kt
│       ├── UserMapper.kt
│       └── CommentMapper.kt
│
├── presentation/
│   ├── feed/
│   │   ├── FeedViewModel.kt
│   │   └── FeedUiState.kt
│   ├── detail/
│   │   ├── PostDetailViewModel.kt
│   │   └── PostDetailUiState.kt
│   ├── profile/
│   │   ├── ProfileViewModel.kt
│   │   └── ProfileUiState.kt
│   └── auth/
│       ├── AuthViewModel.kt
│       └── AuthUiState.kt
│
├── ui/
│   ├── navigation/
│   │   ├── AppNavGraph.kt
│   │   └── AppDestinations.kt
│   ├── screen/
│   │   ├── feed/
│   │   │   ├── FeedScreen.kt
│   │   │   └── components/
│   │   │       ├── PostCard.kt
│   │   │       ├── PostCardPlaceholder.kt
│   │   │       └── LikeButton.kt
│   │   ├── detail/
│   │   │   ├── PostDetailScreen.kt
│   │   │   └── components/
│   │   │       └── CommentItem.kt
│   │   ├── profile/
│   │   │   ├── ProfileScreen.kt
│   │   │   └── components/
│   │   │       └── ProfilePostGrid.kt
│   │   └── auth/
│   │       └── LoginScreen.kt
│   └── theme/
│       ├── Theme.kt
│       ├── Color.kt
│       └── Typography.kt
│
└── di/
    ├── NetworkModule.kt
    ├── DatabaseModule.kt
    ├── RepositoryModule.kt
    └── UseCaseModule.kt
```

---

## Telas e navegação

```
LoginScreen
    │
    └─► FeedScreen (home)
            │
            ├─► PostDetailScreen
            │       └─► CommentsSheet (bottom sheet)
            │
            └─► ProfileScreen
                    └─► PostDetailScreen
```

### Rotas de navegação

```kotlin
sealed class AppDestinations(val route: String) {
    object Feed       : AppDestinations("feed")
    object PostDetail : AppDestinations("post/{postId}")
    object Profile    : AppDestinations("profile/{userId}")
    object Login      : AppDestinations("login")
}
```

---

## Descrição de cada tela

### FeedScreen
- `LazyColumn` com `PagingItems<Post>`
- Pull-to-refresh com `rememberPullRefreshState`
- Cada item renderiza `PostCard` com: avatar, username, imagem, caption, botões de like e comentário
- Footer com spinner de carregamento e botão de retry em caso de erro
- Navegação para `PostDetailScreen` ao clicar em qualquer post

### PostDetailScreen
- Imagem em tela cheia com `AsyncImage`
- Seção de caption, likes e botão de like
- Lista de comentários (`LazyColumn`)
- Campo de texto fixo no rodapé para novo comentário
- Estados: `Loading`, `Success`, `Error`

### ProfileScreen
- Header com avatar, nome, bio e contadores
- Grid de posts do usuário com `LazyVerticalGrid`
- Clique em post navega para `PostDetailScreen`

### LoginScreen
- Campos de username e senha
- Botão de login que chama `AuthViewModel.login()`
- Redirecionamento para `FeedScreen` em caso de sucesso

---

## UI States

Cada ViewModel expõe um `StateFlow<UiState>` com sealed class:

```kotlin
// Exemplo: FeedUiState
sealed class FeedUiState {
    object Loading : FeedUiState()
    data class Success(
        val posts: Flow<PagingData<Post>>
    ) : FeedUiState()
    data class Error(
        val message: String
    ) : FeedUiState()
}

// Exemplo: PostDetailUiState
sealed class PostDetailUiState {
    object Loading : PostDetailUiState()
    data class Success(
        val post: Post,
        val comments: List<Comment>,
        val likedByMe: Boolean
    ) : PostDetailUiState()
    data class Error(val message: String) : PostDetailUiState()
}
```

---

## Paginação cursor-based com Paging 3

```kotlin
// FeedPagingSource.kt
class FeedPagingSource(
    private val api: FeedApiService
) : PagingSource<String, Post>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Post> {
        return try {
            val response = api.getFeed(
                cursor = params.key,
                limit = params.loadSize
            )
            LoadResult.Page(
                data = response.data.map { it.toDomain() },
                prevKey = null,
                nextKey = response.nextCursor
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Post>): String? = null
}

// FeedRepository.kt (domain — interface)
interface FeedRepository {
    fun getFeedStream(): Flow<PagingData<Post>>
}

// PostRepositoryImpl.kt (data — implementação)
class FeedRepositoryImpl(
    private val api: FeedApiService
) : FeedRepository {
    override fun getFeedStream(): Flow<PagingData<Post>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { FeedPagingSource(api) }
        ).flow
}

// GetFeedUseCase.kt (domain)
class GetFeedUseCase(
    private val repository: FeedRepository
) {
    operator fun invoke(): Flow<PagingData<Post>> =
        repository.getFeedStream()
}

// FeedViewModel.kt (presentation)
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase
) : ViewModel() {

    val feedPagingData: Flow<PagingData<Post>> =
        getFeedUseCase()
            .cachedIn(viewModelScope)
}
```

---

## Armazenamento local (Room + DataStore)

### Room — cache do feed
```kotlin
@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val mediaUrl: String,
    val caption: String?,
    val likesCount: Int,
    val commentsCount: Int,
    val createdAt: Long
)

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPosts(): PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Query("DELETE FROM posts")
    suspend fun clearAll()
}
```

### DataStore — token JWT e preferências
```kotlin
class AuthDataStore(private val dataStore: DataStore<Preferences>) {

    companion object {
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
        val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
    }

    val jwtToken: Flow<String?> = dataStore.data.map { it[JWT_TOKEN] }

    suspend fun saveToken(token: String) {
        dataStore.edit { it[JWT_TOKEN] = token }
    }

    suspend fun clearToken() {
        dataStore.edit { it.remove(JWT_TOKEN) }
    }
}
```

---

## Testes unitários

### Estratégia de cobertura

| Camada | O que testar | Ferramentas |
|---|---|---|
| Use Cases | Lógica de negócio, resultados esperados, erros | JUnit 5 + MockK |
| ViewModels | Estados emitidos via StateFlow, eventos | JUnit 5 + MockK + Turbine |
| Repositories | Decisão cache vs rede, mapeamento | JUnit 5 + MockK |
| PagingSource | Primeira página, última página, erro de rede | JUnit 5 + Paging Testing |
| Mappers | Conversão correta entre modelos | JUnit 5 |

### Exemplo — teste do Use Case

```kotlin
class GetFeedUseCaseTest {

    private val repository = mockk<FeedRepository>()
    private lateinit var useCase: GetFeedUseCase

    @BeforeEach
    fun setup() {
        useCase = GetFeedUseCase(repository)
    }

    @Test
    fun `invoke retorna flow de PagingData do repositorio`() = runTest {
        val fakePagingData = PagingData.from(listOf(fakePost()))
        every { repository.getFeedStream() } returns flowOf(fakePagingData)

        val result = useCase()

        verify(exactly = 1) { repository.getFeedStream() }
        assertNotNull(result)
    }
}
```

### Exemplo — teste do ViewModel

```kotlin
class FeedViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getFeedUseCase = mockk<GetFeedUseCase>()
    private lateinit var viewModel: FeedViewModel

    @BeforeEach
    fun setup() {
        val fakePagingData = PagingData.from(listOf(fakePost()))
        every { getFeedUseCase() } returns flowOf(fakePagingData)
        viewModel = FeedViewModel(getFeedUseCase)
    }

    @Test
    fun `feedPagingData emite dados do use case`() = runTest {
        viewModel.feedPagingData.test {
            val item = awaitItem()
            assertNotNull(item)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

### Exemplo — teste do PagingSource

```kotlin
class FeedPagingSourceTest {

    private val api = mockk<FeedApiService>()

    @Test
    fun `load retorna primeira pagina com nextCursor`() = runTest {
        coEvery {
            api.getFeed(cursor = null, limit = 20)
        } returns FeedResponse(
            data = listOf(fakePostNetwork()),
            nextCursor = "cursor_abc",
            hasMore = true
        )

        val pagingSource = FeedPagingSource(api)
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.data.size)
        assertEquals("cursor_abc", page.nextKey)
        assertNull(page.prevKey)
    }

    @Test
    fun `load retorna nextKey null na ultima pagina`() = runTest {
        coEvery {
            api.getFeed(cursor = "cursor_final", limit = 20)
        } returns FeedResponse(
            data = listOf(fakePostNetwork()),
            nextCursor = null,
            hasMore = false
        )

        val pagingSource = FeedPagingSource(api)
        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = "cursor_final",
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        val page = result as PagingSource.LoadResult.Page
        assertNull(page.nextKey)
    }

    @Test
    fun `load retorna Error em caso de IOException`() = runTest {
        coEvery {
            api.getFeed(cursor = null, limit = 20)
        } throws IOException("Sem internet")

        val pagingSource = FeedPagingSource(api)
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(null, 20, false)
        )

        assertTrue(result is PagingSource.LoadResult.Error)
    }
}
```

### Exemplo — teste do Mapper

```kotlin
class PostMapperTest {

    @Test
    fun `PostNetworkModel mapeia corretamente para Post de dominio`() {
        val network = PostNetworkModel(
            id = "abc123",
            author = AuthorNetworkModel("u1", "pether.silva", null),
            mediaUrl = "https://s3.amazonaws.com/img.jpg",
            caption = "Foto de teste",
            likesCount = 10,
            commentsCount = 2,
            createdAt = "2026-06-01T10:00:00Z"
        )

        val domain = network.toDomain()

        assertEquals("abc123", domain.id)
        assertEquals("pether.silva", domain.author.username)
        assertEquals(10, domain.likesCount)
        assertEquals("Foto de teste", domain.caption)
    }
}
```

---

## Configuração de Hilt (DI)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton
    fun provideOkHttp(authDataStore: AuthDataStore): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = runBlocking { authDataStore.jwtToken.first() }
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .build()

    @Provides @Singleton
    fun provideRetrofit(okHttp: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.instagramclone.com/api/v1/")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides @Singleton
    fun provideFeedRepository(api: FeedApiService): FeedRepository =
        FeedRepositoryImpl(api)
}
```

---

## Plano de implementação

### Fase 1 — Setup e fundação (Semana 1)
- [ ] Criar projeto Android com Android Studio (Kotlin, Compose, Gradle KTS)
- [ ] Configurar Hilt, Retrofit, Room, DataStore, Coil, Navigation Compose
- [ ] Criar `AppNavGraph` com as rotas principais
- [ ] Implementar `Theme.kt`, `Color.kt` e `Typography.kt`
- [ ] Criar entidades de domínio: `Post`, `User`, `Comment`
- [ ] Configurar CI básico com GitHub Actions (build + lint)

### Fase 2 — Autenticação (Semana 2)
- [ ] Implementar `LoginScreen` com Compose
- [ ] `AuthViewModel` + `AuthUiState`
- [ ] `AuthDataStore` para persistir JWT e userId
- [ ] Interceptor OkHttp para injetar token nas requisições
- [ ] Redirecionar para Feed após login bem-sucedido
- [ ] Testes unitários do `AuthViewModel`

### Fase 3 — Feed com paginação (Semana 3)
- [ ] Implementar `FeedApiService` e `FeedResponse`
- [ ] `FeedPagingSource` com cursor-based pagination
- [ ] `FeedRepositoryImpl` e `GetFeedUseCase`
- [ ] `FeedViewModel` com `cachedIn(viewModelScope)`
- [ ] `FeedScreen` com `LazyColumn` + `PagingItems`
- [ ] `PostCard` com imagem (Coil), avatar, caption, likes
- [ ] Pull-to-refresh
- [ ] Testes: `FeedPagingSourceTest`, `GetFeedUseCaseTest`, `FeedViewModelTest`

### Fase 4 — Post Detail e Likes (Semana 4)
- [ ] `PostDetailScreen` com imagem, comentários e campo de texto
- [ ] `PostDetailViewModel` + `PostDetailUiState`
- [ ] `LikePostUseCase` e `UnlikePostUseCase`
- [ ] Animação de like (coração pulsante com `animateFloatAsState`)
- [ ] Atualização otimista do contador de likes (sem esperar resposta do servidor)
- [ ] Testes dos Use Cases de like/unlike

### Fase 5 — Comentários (Semana 5)
- [ ] `GetCommentsUseCase` e `CreateCommentUseCase`
- [ ] Lista de comentários no `PostDetailScreen`
- [ ] Campo de texto com envio de comentário
- [ ] Atualização do `commentsCount` após envio
- [ ] Testes dos Use Cases de comentário

### Fase 6 — Perfil e cache local (Semana 6)
- [ ] `ProfileScreen` com grid de posts
- [ ] `GetUserUseCase` e `ProfileViewModel`
- [ ] Cache offline do feed com Room + `RemoteMediator`
- [ ] Skeleton loading com `PostCardPlaceholder`
- [ ] Testes do `ProfileViewModel`

### Fase 7 — Qualidade e finalização (Semana 7)
- [ ] Revisar cobertura de testes (meta: 80%+ nas camadas domain e data)
- [ ] Testes de UI com Compose Testing nas telas principais
- [ ] Configurar ProGuard/R8 com regras para Retrofit e Gson
- [ ] Lint e Detekt para análise estática
- [ ] Revisar acessibilidade dos Composables (contentDescription, semantics)
- [ ] Gerar APK de release assinado

---

## Considerações de performance

- **`cachedIn(viewModelScope)`** no ViewModel preserva o estado do feed durante rotação de tela.
- **`key = pagingItems.itemKey { it.id }`** no `LazyColumn` garante animações suaves e recomposições eficientes.
- **`collectAsStateWithLifecycle()`** para coletar Flows — para automaticamente quando o app vai para background.
- **Coil** cancela requisições de imagem automaticamente quando o item sai da tela (RecyclerView/LazyColumn lifecycle-aware).
- **Atualização otimista** para likes: atualiza o estado local imediatamente, desfaz em caso de erro de rede.
