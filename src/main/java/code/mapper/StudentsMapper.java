package code.mapper;

import code.cache.MyBatisCustomizeRedisCache;
import code.entity.Students;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ethereal
 */
@Mapper
@CacheNamespace(implementation= MyBatisCustomizeRedisCache.class,eviction= MyBatisCustomizeRedisCache.class)
public interface StudentsMapper extends BaseMapper<Students> {}
