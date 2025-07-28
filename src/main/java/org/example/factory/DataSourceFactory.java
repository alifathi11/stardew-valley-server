package org.example.factory;

import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

public class DataSourceFactory {

    public static DataSource createDataSource() {
        SQLiteDataSource ds = new SQLiteDataSource();
        ds.setUrl("jdbc:sqlite:game.db");
        return ds;
    }
}
