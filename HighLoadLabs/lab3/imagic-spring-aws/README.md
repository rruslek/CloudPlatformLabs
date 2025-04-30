# Лабораторная работа №3
## Реализация очередей задач с использованием очередей Redis

### Содержание

[1. Постановка задачи](#task)

[2. Решение](#implementation)

[3. Выводы](#conclusion)

## <a id="task" style="color: lightgrey">1. Постановка задачи

- #### Запустить redis
- #### Реализовать веб-сервис, который с неравномерной скоростью записывает сообщения в очередь. Например, загружает данные из файла и записывает их в очередь.
- #### Реализовать веб-сервис, который с определенной задержкой обрабатывает данные в очереди.

## <a id="implementation" style="color: lightgrey">2. Решение</a>

Для работы было разработано веб-приложение, которое использует Redis для шифрования/дешифрования текста с помощью алгоритма AES.

Были реализованы следующие файлы:

- **Конфигурация RedisConfig**
```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer()); //Важно для сериализации объектов
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
```

- **Контроллер ProducerController**
```java
@Controller
public class ProducerController {

    private static final String ENCRYPTION_QUEUE = "encryption_queue";
    private static final String DECRYPTION_QUEUE = "decryption_queue";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/")
    public String showForm() {
        return "index";
    }

    @PostMapping("/encrypt")
    public String encryptText(@RequestParam("text") String text, @RequestParam("key") String key, Model model) throws InterruptedException {
        String requestId = UUID.randomUUID().toString();
        CryptoRequest request = new CryptoRequest(text, key, requestId);
        System.out.println(requestId);

        Random random = new Random();
        Thread.sleep(random.nextInt(300));

        redisTemplate.opsForList().leftPush(ENCRYPTION_QUEUE, request);
        model.addAttribute("message", "Текст отправлен на шифрование.");
        model.addAttribute("requestId", requestId);
        return "process";
    }

    @PostMapping("/decrypt")
    public String decryptText(@RequestParam("text") String text, @RequestParam("key") String key, Model model) throws InterruptedException {
        String requestId = UUID.randomUUID().toString();
        CryptoRequest request = new CryptoRequest(text, key, requestId);

        Random random = new Random();
        Thread.sleep(random.nextInt(300));

        redisTemplate.opsForList().leftPush(DECRYPTION_QUEUE, request);
        model.addAttribute("message", "Текст отправлен на дешифрование.");
        model.addAttribute("requestId", requestId);
        return "process";
    }

    @GetMapping("/result")
    public String getResult(@RequestParam("requestId") String requestId, Model model) {
        CryptoResponse response = (CryptoResponse) redisTemplate.opsForValue().get(requestId);

        model.addAttribute("result", response.getResultText());
        model.addAttribute("message", "Зашифрованный текст:");

        return "result";

    }
}
```

- **Сервис ConsumerService**
```java
@Service
public class ConsumerService {

    private static final String ENCRYPTION_QUEUE = "encryption_queue";
    private static final String DECRYPTION_QUEUE = "decryption_queue";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Scheduled(fixedRate = 1000)
    public void processEncryptionRequest() throws InterruptedException {
        CryptoRequest request = (CryptoRequest) redisTemplate.opsForList().rightPop(ENCRYPTION_QUEUE);
        if (request != null) {
            String textToEncrypt = request.getText();
            String key = request.getKey();
            String requestId = request.getRequestId();
            try {
                String encryptedText = encrypt(textToEncrypt, key);
                CryptoResponse response = new CryptoResponse(encryptedText, requestId);
                redisTemplate.opsForValue().set(requestId, response);
                System.out.println("Encryption Request ID: " + requestId + ", Encrypted: " + encryptedText);

            } catch (Exception e) {
                System.err.println("Error encrypting: " + e.getMessage());
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    public void processDecryptionRequest() throws InterruptedException {
        CryptoRequest request = (CryptoRequest) redisTemplate.opsForList().rightPop(DECRYPTION_QUEUE);

        if (request != null) {
            String textToDecrypt = request.getText();
            String key = request.getKey();
            String requestId = request.getRequestId();

            try {
                String decryptedText = decrypt(textToDecrypt, key);
                CryptoResponse response = new CryptoResponse(decryptedText, requestId);
                redisTemplate.opsForValue().set(requestId, response);

                System.out.println("Decryption Request ID: " + requestId + ", Decrypted: " + decryptedText);

            } catch (Exception e) {
                System.err.println("Error decrypting: " + e.getMessage());
            }
        }
    }


    private String encrypt(String strToEncrypt, String secret) throws Exception {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e);
            throw e;
        }
    }


    private String decrypt(String strToDecrypt, String secret) throws Exception {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e);
            throw e;
        }
    }
}
```

- **Модель CryptoRequest**
```java
public class CryptoRequest implements Serializable {
    private String text;
    private String key;
    private String requestId;

    public CryptoRequest(String text, String key, String requestId) {
        this.text = text;
        this.key = key;
        this.requestId = requestId;
    }

    public String getText() {
        return text;
    }

    public String getKey() {
        return key;
    }

    public String getRequestId() {
        return requestId;
    }

}
```

- **Модель CryptoResponse**
```java
public class CryptoResponse implements Serializable {
    private String resultText;
    private String requestId;

    public CryptoResponse(String resultText, String requestId) {
        this.resultText = resultText;
        this.requestId = requestId;
    }

    public String getResultText() {
        return resultText;
    }

    public String getRequestId() {
        return requestId;
    }
}
```

Алгоритм работы приложения:
- Пользователь вводит текст для шифрования/дешифровки 
- Данные с неравномерной задержкой добавляются в Redis Queue с помощью контроллера
- Сервис с определенной задержкой забирает их из Redis Queue
- Пользователь получает результат шифрования/дешифровки

## <a id="conclusion" style="color: lightgrey">3. Выводы</a>