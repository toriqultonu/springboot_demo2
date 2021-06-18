package com.koshailimited.demo2.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DaoClass {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //create a table
    public void createTable(){
        var query = "CREATE TABLE users(id SERIAL PRIMARY KEY, name varchar(255) NOT NULL, city VARCHAR(255))";
        int update = this.jdbcTemplate.update(query);
        System.out.println(update);
    }

    
}
