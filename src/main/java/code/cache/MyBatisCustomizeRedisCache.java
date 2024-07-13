package code.cache;

// Copyright (c) 2024, NoCodeNoLife-cloud. All rights reserved.
// Author: NoCodeNoLife-cloud
// stay hungry，stay foolish
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.springframework.util.SerializationUtils;

import java.util.Objects;

/**
 * The type My redis cache.
 * <p>If you want to use redis as a cache for mysql, you need to add it in the mapper configuration file of mybatis
 * <p><cache type="*.MyBatisCustomizeRedisCache"/>
 */
@Slf4j
public class MyBatisCustomizeRedisCache implements Cache {
	private final StatefulRedisConnection<byte[], byte[]> connection = RedisConnectionPool.getConnection();
	private final String id;

	public MyBatisCustomizeRedisCache(String id) {
		log.info("[set cache id] id = {}", id);
		this.id = id;
	}

	@Override
	public String getId() {
		log.info("[get cache] id = {}", this.id);
		return this.id;
	}

	@Override
	public void putObject(Object key, Object value) {
		log.info("[Cache data write] key = {}, value = {}", key, value);
		this.connection.sync().set(SerializationUtils.serialize(key), SerializationUtils.serialize(value));
	}

	@Override
	@SuppressWarnings("deprecation")
	public Object getObject(Object key) {
		byte[] data = this.connection.sync().get(SerializationUtils.serialize(key));
		if (Objects.isNull(data)) {
			return null;
		}
		Object value = SerializationUtils.deserialize(data);
		log.info("[Cache data read] key = {}, value = {}", key, value);
		return value;
	}

	@Override
	public Object removeObject(Object key) {
		log.info("[delete cache data] key = {}", key);
		return this.connection.sync().del(SerializationUtils.serialize(key));
	}

	@Override
	public void clear() {
		log.info("[clear cache data]");
		this.connection.sync().flushdb();
	}

	@Override
	public int getSize() {
		int size = this.connection.sync().dbsize().intValue();
		log.info("[get cache size] size = {}", size);
		return size;
	}
}