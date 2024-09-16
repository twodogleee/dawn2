package com._54year.dawn2.auth.config;

import jakarta.servlet.Filter;
import lombok.Getter;
import org.springframework.security.web.DefaultSecurityFilterChain;

import java.util.List;

/**
 * 自定义过滤器设置
 *
 * @author Andersen
 */
@Getter
public class DawnSecurityFilterSetting {
    /**
     * 框架的过滤器链
     */
    private DefaultSecurityFilterChain filterChain;
    /**
     * 过滤器链集合
     */
    private List<Filter> filterList;

    private DawnSecurityFilterSetting() {

    }

    private DawnSecurityFilterSetting(DefaultSecurityFilterChain filterChain) {
        this.filterList = filterChain.getFilters();
        this.filterChain = filterChain;
    }

    /**
     * 初始化过滤器设置
     *
     * @param filterChain 过滤器链
     * @return 过滤器设置
     */
    public static DawnSecurityFilterSetting init(DefaultSecurityFilterChain filterChain) {
        return new DawnSecurityFilterSetting(filterChain);
    }

    /**
     * 添加到指定过滤器后面
     *
     * @param filter      要添加进来的过滤器
     * @param afterFilter 指定过滤器
     * @return 过滤器设置
     */
    public DawnSecurityFilterSetting addFilterAfter(Filter filter, Class<? extends Filter> afterFilter) {
        return this.addFilterAtOffsetOf(filter, 1, afterFilter);
    }

    /**
     * 添加到指定过滤器前面
     *
     * @param filter       要添加进来的过滤器
     * @param beforeFilter 指定过滤器
     * @return 过滤器设置
     */
    public DawnSecurityFilterSetting addFilterBefore(Filter filter, Class<? extends Filter> beforeFilter) {
        return this.addFilterAtOffsetOf(filter, 0, beforeFilter);
    }

    /**
     * 添加过滤器
     *
     * @param filter           要添加进来的过滤器
     * @param offset           过滤器位置
     * @param registeredFilter 指定过滤器
     * @return 过滤器设置
     */
    private DawnSecurityFilterSetting addFilterAtOffsetOf(Filter filter, int offset, Class<? extends Filter> registeredFilter) {
        Integer registeredFilterOrder = this.findOrder(registeredFilter);
        if (registeredFilterOrder == null) {
            throw new IllegalArgumentException("The Filter class " + registeredFilter.getName() + "not registered");
        } else {
            //addFilterBefore 则放到当前filter的位置
            //addFilterBefore 则放到当前filter后面的位置
            int order = registeredFilterOrder + offset;
            this.filterList.add(order, filter);
            return this;
        }
    }

    /**
     * 寻找指定过滤器下标
     *
     * @param registeredFilter 指定过滤器
     * @return 过滤器下标
     */
    private Integer findOrder(Class<? extends Filter> registeredFilter) {
        //只要类名一致 则判定为他
        for (int i = 0; i < filterList.size(); i++) {
            Filter filter = filterList.get(i);
            if (filter.getClass().getName().equals(registeredFilter.getName())) {
                return i;
            }
        }
        return null;
    }
}
