# Лабораторная работа №2
## Реализация разделяемого хранилища данных с использованием Redis

### Содержание

[1. Постановка задачи](#task)

[2. Решение](#implementation)

[3. Выводы](#conclusion)

## <a id="task" style="color: lightgrey">1. Постановка задачи

- #### Пройти интерактивный redis tutorial
- #### Запустить redis
- #### Доработать приложение из предыдущей лабораторной работы таким образом, чтобы счетчик входящих запросов хранился в redis.
- #### Доработать приложение, создав соответствующий HTTP REST API, чтобы в redis использовалась структура Redis Sorted Set. Самый распространенный пример: список игроков, набравших максимальное число очков в игре (Gaming Leaderboards) [3,4]

## <a id="implementation" style="color: lightgrey">2. Решение</a>

Для работы было использовано веб-приложение, разработанное в рамках лабораторных работы №1.

Для использования Redis были реализованы файлы:

- **Конфигурация RedisConfig**
```java
@Configuration
public class RedisConfig {

    @Bean
    public JedisConnectionFactory jedisConnectionFactory()
    {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericToStringSerializer<>(Integer.class));

        return template;
    }
}
```

- **Сервис ClickService**
```java
@Service
public class ClickService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ZSetOperations<String, String> zSetOperations;

    private static final String KEY = "leaderboard";

    public ClickService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    public void initUserIfAbsent(String username) {
        Double score = zSetOperations.score(KEY, username);
        if (score == null) {
            zSetOperations.add(KEY, username, 0.0);
        }
    }

    public void incrementClick(String username) {
        zSetOperations.incrementScore(KEY, username, 1);
    }

    public int getClicks(String username) {
        Double score = zSetOperations.score(KEY, username);
        return score != null ? score.intValue() : 0;
    }

    public List<Map.Entry<String, Integer>> getTopUsers(int limit) {
        Set<ZSetOperations.TypedTuple<String>> set = zSetOperations.reverseRangeWithScores(KEY, 0, limit - 1);
        if (set == null) return List.of();

        return set.stream()
                .map(e -> Map.entry(e.getValue(), e.getScore().intValue()))
                .toList();
    }
}
```

- **Контроллер CounterController**
```java
@Controller
public class CounterController {

    private final ClickService clickService;

    public CounterController(ClickService clickService) {
        this.clickService = clickService;
    }

    @GetMapping("/")
    public String home(@CookieValue(value = "username", required = false) String username, Model model) {
        if (username == null || username.isEmpty()) {
            return "login";
        }

        long clicks = clickService.getClicks(username);
        model.addAttribute("username", username);
        model.addAttribute("clicks", clicks);
        model.addAttribute("leaders", clickService.getTopUsers(10));
        return "index";
    }

    @PostMapping("/set-name")
    public String setName(@RequestParam String username, HttpServletResponse response) {
        Cookie cookie = new Cookie("username", username);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 дней
        response.addCookie(cookie);

        // создаём запись в Redis если новой нет
        clickService.initUserIfAbsent(username);

        return "redirect:/";
    }

    @PostMapping("/click")
    public String click(@CookieValue("username") String username) {
        clickService.incrementClick(username);
        return "redirect:/";
    }
}
```

- **Модель User**
```java
public class User implements Serializable {
    private int score = 0;
    private int id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public Integer getScore() {
        return score;
    }

    public void addScore() {
        this.score = score+1;
    }

    public void setName(String name) {
        this.name = name;
    }

}
```

Данное приложение представляет собой игру-кликер, с таблицей лидеров по кликам. При первом входе пользователь вводит свое имя, после этого оно сохраняется в Cookies.

Для работы приложения, с помощью Docker был запущен Redis, была использована следующая команда:
```cmd
docker run --name my-redis -p 6379:6379 -d redis
```

## <a id="conclusion" style="color: lightgrey">3. Выводы</a>