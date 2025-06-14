package com.ubanillx.smartclassbackendcommon.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * SQL 工具
*/
public class SqlUtils {

    /**
     * 升序
     */
    public static final String SORT_ORDER_ASC = "asc";

    /**
     * 降序
     */
    public static final String SORT_ORDER_DESC = "desc";

    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }

}