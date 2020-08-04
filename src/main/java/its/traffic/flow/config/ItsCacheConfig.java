//package its.traffic.flow.config;
//
//import com.github.benmanes.caffeine.cache.CaffeineSpec;
//import org.ntt.cache.MultiCacheManager;
//import org.ntt.web.RequParameter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.cache.CacheProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.CachingConfigurerSupport;
//import org.springframework.cache.interceptor.KeyGenerator;
//import org.springframework.cache.interceptor.SimpleKeyGenerator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.util.StringUtils;
//
//import javax.annotation.Resource;
//import java.lang.reflect.Method;
//import java.text.SimpleDateFormat;
//import java.time.Duration;
//import java.util.Date;
//
//
///**
// * @desc 缓存配置
// * @author huangyong
// * @date 2018-10-31
// */
//@Configuration
//@EnableConfigurationProperties({CacheProperties.class})
//public class ItsCacheConfig extends CachingConfigurerSupport {
//
//    //是否使用一级缓存
//    //@Value("${spring.cache.used.caffeine:false}")
//    private boolean usedCaffeineCache=false;
//
//    //是否使用二级缓存
//    @Value("${spring.cache.used.redis:false}")
//    private boolean usedRedisCache;
//
//    @Resource
//    private CacheProperties cacheProperties;
//
//    @Bean
//    @Primary
//    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
//        boolean allowNullValues=cacheProperties.getRedis().isCacheNullValues();
//        MultiCacheManager multiCacheManager=new MultiCacheManager(allowNullValues,usedCaffeineCache,usedRedisCache,redisConnectionFactory);
//        setCaffeineCacheSetting(multiCacheManager);
//        setRedisCacheSetting(multiCacheManager);
//        return multiCacheManager;
//    }
//
//    private void setCaffeineCacheSetting(MultiCacheManager multiCacheManager) {
//
//        String specification = this.cacheProperties.getCaffeine().getSpec();
//
//        if (StringUtils.hasText(specification)) {
//            //设置CaffeineCache配置参数
//            multiCacheManager.setCaffeineParams(CaffeineSpec.parse(specification));
//        }
//    }
//
//    private void setRedisCacheSetting(MultiCacheManager multiCacheManager) {
//        String keyPrefix=this.cacheProperties.getRedis().getKeyPrefix();
//        //this.cacheProperties.getRedis().isUseKeyPrefix();
//        boolean userPrefix=false;
//        Duration timeToLive= this.cacheProperties.getRedis().getTimeToLive();
//        //设置RedisCache配置参数
//        multiCacheManager.setRedisParams(keyPrefix,timeToLive,userPrefix);
//    }
//
//    @Override
//    public KeyGenerator keyGenerator() {
//        return new SimpleKeyGenerator(){
//            private final SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH/mm/ss");
//            @Override
//            public Object generate(Object target, Method method, Object... params) {
//                String keyPrefix=cacheProperties.getRedis().getKeyPrefix();
//                char sp = ':';
//                StringBuilder strBuilder = new StringBuilder();
//                strBuilder.append(keyPrefix);
//                strBuilder.append(sp);
//                // 类名
//                strBuilder.append(target.getClass().getSimpleName());
//                strBuilder.append(sp);
//                // 方法名
//                strBuilder.append(method.getName());
//                strBuilder.append(sp);
//                strBuilder.append("(");
//                strBuilder.append(buildString(params));
//                strBuilder.append(")");
//                return strBuilder.toString();
//            }
//
//            private String buildString(Object... params){
//                StringBuilder strBuilder = new StringBuilder();
//                for(Object param:params){
//                    if(param==null){
//                        continue;
//                    }
//                    else if(param instanceof RequParameter){
//                        strBuilder.append(((RequParameter)param).toString());
//                    }
//                    else if(param instanceof Date){
//                        strBuilder.append(format.format((Date)param));
//                    }
//                    else if(param.getClass().isArray()){
//                        Object[] array=(Object[])param;
//                        if(array.length==0){
//                            continue;
//                        }
//                        strBuilder.append("[");
//                        strBuilder.append(buildString(array));
//                        strBuilder.append("]");
//                    }
//                    else{
//                        strBuilder.append(param);
//                    }
//                    strBuilder.append(",");
//                }
//                return strBuilder.toString();
//            }
//        };
//    }
//}
//
///**设置默认的一级缓存配置
// * initialCapacity=[integer]: 初始的缓存空间大小
// * maximumSize=[long]: 缓存的最大条数
// * maximumWeight=[long]: 缓存的最大权重
// * expireAfterAccess=[duration]: 最后一次写入或访问后经过固定时间过期
// * expireAfterWrite=[duration]: 最后一次写入后经过固定时间过期
// * refreshAfterWrite=[duration]: 创建缓存或者最近一次更新缓存后经过固定的时间间隔，刷新缓存
// * weakKeys: 打开key的弱引用
// * weakValues：打开value的弱引用
// * softValues：打开value的软引用
// * recordStats：开发统计功能
// * 注:
// * expireAfterWrite和expireAfterAccess同事存在时，以expireAfterWrite为准。
// * maximumSize和maximumWeight不可以同时使用
// * weakValues和softValues不可以同时使用
// * */
