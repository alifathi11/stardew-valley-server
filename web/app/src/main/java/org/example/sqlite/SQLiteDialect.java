package org.example.sqlite;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;
import org.hibernate.service.ServiceRegistry;

public class SQLiteDialect extends Dialect {

    public SQLiteDialect() {
        super();
    }

    @Override
    protected void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.registerColumnTypes(typeContributions, serviceRegistry);
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new SQLiteIdentityColumnSupport();
    }

    @Override
    public boolean hasAlterTable() {
        return false;
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public String getDropForeignKeyString() {
        return ""; // SQLite doesn't support DROP CONSTRAINT
    }

    @Override
    public String getAddForeignKeyConstraintString(String cn, String[] fk, String t, String[] pk, boolean rpk) {
        return ""; // SQLite doesn't support ADD CONSTRAINT
    }

    @Override
    public String getAddPrimaryKeyConstraintString(String constraintName) {
        return ""; // SQLite doesn't support ADD CONSTRAINT
    }

    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }

    @Override
    public boolean supportsCascadeDelete() {
        return false;
    }
}
