package code.cache;

// Copyright (c) 2024, NoCodeNoLife-cloud. All rights reserved.
// Author: NoCodeNoLife-cloud
// stay hungry，stay foolish

// Lettuce is a high-performance, extensible Java client library for interacting with Redis databases.
// It provides an asynchronous and responsive API, as well as rich features and flexible configuration options that make it easy for developers to communicate with Redis.
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Objects;

/**
 * The type Redis connection util.
 */
@Slf4j
public class RedisConnectionPool {
	private static final String REDIS_PASSWORDS = "";
	private static final String REDIS_HOST = "localhost";
	private static final String REDIS_POTS = "6379";
	private static final String REDIS_DATABASE = "0";
	private static final String REDIS_ADDRESS = "redis://" + REDIS_PASSWORDS + "@" + REDIS_HOST + ":" + REDIS_POTS + "/" + REDIS_DATABASE;
	private static final RedisURI REDIS_URI = RedisURI.create(REDIS_ADDRESS);
	private static final RedisClient REDIS_CLIENT = RedisClient.create(REDIS_URI);
	private static final int MAX_IDLE = 10;
	private static final int MIN_IDLE = 1;
	private static final int MAX_TOTAL = 10;
	private static final boolean TEST_ON_BORROW = true;
	/**
	 * connection management<br>
	 * No synchronization scheme, local thread<br>
	 * Each open thread corresponds to a redis connection<br>
	 */
	private static final ThreadLocal<StatefulRedisConnection<byte[], byte[]>> REDIS_CONNECTION_THREAD_LOCAL = new ThreadLocal<>();
	/**
	 * Object pooling and reuse: GenericObjectPool allows you to pool objects so that objects can be fetched from the pool when needed and returned to the pool when use is complete.
	 * <p>This avoids the overhead of frequently creating and destroying objects, improving performance and resource utilization.
	 */
	private static final GenericObjectPool<StatefulRedisConnection<byte[], byte[]>> pool;

	static {// Initialize when class is loaded
		// Object pool configuration class
		GenericObjectPoolConfig<StatefulRedisConnection<byte[], byte[]>> config = new GenericObjectPoolConfig<>();
		// Set Redis connection pool parameters
		config.setMaxIdle(MAX_IDLE);
		config.setMinIdle(MIN_IDLE);
		config.setMaxTotal(MAX_TOTAL);
		config.setTestOnBorrow(TEST_ON_BORROW);
		// ByteArrayCodec is a codec for serializing and deserializing byte arrays. It implements the RedisCodec interface, which defines how Redis commands and responses are serialized and deserialized.
		pool = ConnectionPoolSupport.createGenericObjectPool(
				() -> REDIS_CLIENT.connect(new ByteArrayCodec()), config
		);
	}

	private RedisConnectionPool() {}

	/**
	 * Get a Redis connection from the connection pool.<br>
	 * If there is no connection available in the thread local, a new connection is created and added to the thread local.
	 *
	 * @return the Redis connection from the connection pool
	 */
	public static StatefulRedisConnection<byte[], byte[]> getConnection() {
		// get connection
		StatefulRedisConnection<byte[], byte[]> connection = REDIS_CONNECTION_THREAD_LOCAL.get();
		// no connection
		if (Objects.isNull(connection)) {
			// Create a new redis connection
			connection = build();
			// reserved for future use
			REDIS_CONNECTION_THREAD_LOCAL.set(connection);
		}
		return connection;
	}

	/**
	 * Builds a StatefulRedisConnection object from the connection pool.
	 *
	 * @return a StatefulRedisConnection object from the connection pool, or null if an exception occurs
	 */
	private static StatefulRedisConnection<byte[], byte[]> build() {
		try {
			// If there are any available objects in the object pool, it immediately returns an object.
			// If there are no objects available in the object pool, and the pool has not reached the maximum number of objects, it creates a new object and returns it to the caller.
			// If there are no available objects in the object pool and the pool has reached its maximum object limit, the borrowObject method blocks until an object is available.
			// While waiting, it tries to create a new object in the pool. If the wait times out (if the timeout parameter is set), or if an error occurs during the wait, the borrowObject method throws an exception.
			return pool.borrowObject();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Close the connection pool
	 */
	public static void close() {
		// get connection
		StatefulRedisConnection<byte[], byte[]> connection = REDIS_CONNECTION_THREAD_LOCAL.get();
		if (!Objects.isNull(connection)) {
			connection.close();
			REDIS_CONNECTION_THREAD_LOCAL.remove();
		}
	}
}