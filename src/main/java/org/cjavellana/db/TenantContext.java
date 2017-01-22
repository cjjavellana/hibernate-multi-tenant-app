package org.cjavellana.db;

/**
 * Holds
 */
public class TenantContext {

    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setTenant(String tenantId) {
        threadLocal.set(tenantId);
    }

    public static String getTenant() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
