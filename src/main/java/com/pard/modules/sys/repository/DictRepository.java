package com.pard.modules.sys.repository;

import com.pard.common.datatables.DataTablesRepository;
import com.pard.modules.sys.entity.Dict;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by wawe on 17/5/3.
 */
public interface DictRepository extends DataTablesRepository<Dict, String> {
    @Query(value = "select distinct d.type from Dict d order by d.type asc")
    List findDistinctDictType();

    @Query(value = "select max(d.sort) from Dict d where d.type = ?1")
    int findMaxSortByType(String type);

    @Query(value = "from Dict d where d.type = ?1 order by d.sort asc")
    List<Dict> findByType(String type);
}
