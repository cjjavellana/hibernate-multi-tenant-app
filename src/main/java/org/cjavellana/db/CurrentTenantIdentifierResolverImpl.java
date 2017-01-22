package org.cjavellana.db;


import org.hibernate.context.spi.CurrentTenantIdentifierResolver;


public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        return null;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
