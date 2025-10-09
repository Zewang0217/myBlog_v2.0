package org.Zewang.myBlog.controller.admin;


import java.util.Collection;
import org.Zewang.myBlog.common.ApiResponse;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 缓存监控端点
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/09 19:32
 */

@RestController
@RequestMapping("/api/cache")
@PreAuthorize("hasRole('ADMIN')")
public class CacheController {

    private final CacheManager cacheManager;

    public CacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @DeleteMapping("/clear")
    public ApiResponse<String> clearAllCache() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        cacheNames.forEach(name -> {
            if (cacheManager.getCache(name) != null) {
                cacheManager.getCache(name).clear();
            }
        });
        return ApiResponse.success("所有缓存已清除");
    }


}
