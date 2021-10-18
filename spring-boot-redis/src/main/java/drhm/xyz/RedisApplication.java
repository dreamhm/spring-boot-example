package drhm.xyz;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
public class RedisApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
        System.out.println("启动成功");
    }

    public SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RedisApplication.class);
    }

    @RestController
    @RequestMapping("/redis")
    public class RedisController {

        @Resource
        private RedisService redisService;

        @GetMapping("/echo")
        public String echo() {
            return "echo";
        }

        @GetMapping("/setRedis")
        public String setRedis() {
            redisService.setCacheObject("hm", "echo-hm-value");
            return "redis-存入成功";
        }

        @GetMapping("/getRedis")
        public String getRedis() {
            return redisService.getCacheObject("hm");
        }

        @GetMapping("/setList")
        public String setList(){
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                list.add("echo: " + i);
            }
            redisService.setCacheList("key:list", list);
            redisService.setCacheListRightPush("key:list", "echo: 1", "echo: right");
            redisService.setCacheListLeftPush("key:list", "echo: 1", "echo: left");
            return "redis-list-存入成功";
        }

        @GetMapping("/getList")
        public List<String> getList(){
            return redisService.getCacheList("key:list");
        }

        @GetMapping("/setListLeft")
        public long setListLeft(){
            return redisService.setCacheListLeftPush("key:list", "echo: 1", "echo: left");
        }

        @GetMapping("/setListRight")
        public long setListRight(){
            return redisService.setCacheListRightPush("key:list", "echo: 1", "echo: right");
        }

        @GetMapping("/setListSize")
        public long setListSize(){
            return redisService.setCacheListSize("key:list");
        }

        @GetMapping("/updCacheListSet")
        public String updCacheListSet(){
            redisService.setCacheListSet("key:list", 1, "echo: set");
            return "redis-update-更改缓存";
        }

        @GetMapping("/delCacheList")
        public String delCacheList(){
            redisService.delCacheList("key:list");
            return "redis-deleteList-删除缓存";
        }

        @GetMapping("/deleteObject")
        public String deleteObject(){
            redisService.deleteObject("key:list");
            return "redis-delete-object-删除缓存";
        }

    }

    @Configuration
    public class RedisAutoConfig extends CachingConfigurerSupport {

        private Logger logger = LoggerFactory.getLogger(RedisAutoConfig.class);

        /*@Configuration
        public class JedisConf {
            @Value("${spring.redis.host}")
            private String host;
            @Value("${spring.redis.port}")
            private Integer port;
            @Value("${spring.redis.password}")
            private String password;
            @Value("${spring.redis.database}")
            private Integer database;

            @Value("${spring.redis.jedis.pool.max-active}")
            private Integer maxActive;
            @Value("${spring.redis.jedis.pool.max-idle}")
            private Integer maxIdle;
            @Value("${spring.redis.jedis.pool.max-wait}")
            private Long maxWait;
            @Value("${spring.redis.jedis.pool.min-idle}")
            private Integer minIdle;

            @Bean
            public JedisPoolConfig jedisPoolConfig() {
                JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                // 最大空闲数
                jedisPoolConfig.setMaxIdle(maxIdle);
                // 链接池的最大数据库连接数
                jedisPoolConfig.setMaxTotal(maxActive);
                // 最大建立链接等待时间
                jedisPoolConfig.setMaxWaitMillis(maxWait);
                //
                jedisPoolConfig.setMinIdle(minIdle);
                return jedisPoolConfig;
            }

            @Bean
            public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
                JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);

                RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
                redisStandaloneConfiguration.setHostName("127.0.0.1");
                redisStandaloneConfiguration.setPort(6379);
                redisStandaloneConfiguration.setDatabase(0);
                redisStandaloneConfiguration.setPassword(RedisPassword.of(password));

                JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
                jedisClientConfiguration.connectTimeout(Duration.ofMillis(1800));
                return jedisConnectionFactory;
            }
        }*/

        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();

            template.setConnectionFactory(redisConnectionFactory);
            Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
            ObjectMapper om = new ObjectMapper();
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
            jackson2JsonRedisSerializer.setObjectMapper(om);
            StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
            // key采用String的序列化方式
            template.setKeySerializer(stringRedisSerializer);
            // hash的key也采用String的序列化方式
            template.setHashKeySerializer(stringRedisSerializer);
            // value序列化方式采用jackson
            template.setValueSerializer(jackson2JsonRedisSerializer);
            // hash的value序列化方式采用jackson
            template.setHashValueSerializer(jackson2JsonRedisSerializer);
            template.afterPropertiesSet();
            return template;
        }
    }


    @Component
    public class RedisService {

        @Resource
        private RedisTemplate redisTemplate;

        public void setCacheObject(final String key, final Object value) {
            redisTemplate.opsForValue().set(key, value);
        }

        public <T> T getCacheObject(final String key) {
            ValueOperations<String, T> operation = redisTemplate.opsForValue();
            return operation.get(key);
        }

        public <T> void setCacheList(final String key, final List<T> dataList) {
            redisTemplate.opsForList().rightPushAll(key, dataList);
        }

        public <T> List<T> getCacheList(final String key) {
            ListOperations<String, T> operations = redisTemplate.opsForList();
            return operations.range(key, 0, -1);
        }

        public <T> long setCacheListLeftPush(final String key, final String var1, final String value) {
            return redisTemplate.opsForList().leftPush(key, var1, value);
        }

        public <T> long setCacheListRightPush(final String key, final String var1, final String value) {
            return redisTemplate.opsForList().rightPush(key, var1, value);
        }

        public <T> long setCacheListSize(final String key) {
            return redisTemplate.opsForList().size(key);
        }

        public <T> void setCacheListSet(final String key, final int index, final String value) {
            redisTemplate.opsForList().set(key, index, value);
        }

        public Long delCacheList(final String key){
            return redisTemplate.opsForList().remove(key, 0, -1);
        }

        public Boolean deleteObject(final String key){
            return redisTemplate.delete(key);
        }
    }
}
