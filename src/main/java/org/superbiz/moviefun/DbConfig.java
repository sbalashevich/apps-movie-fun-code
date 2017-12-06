package org.superbiz.moviefun;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Bean
    public DataSource albumsDataSource(
            @Value("${moviefun.datasources.albums.url}") String url,
            @Value("${moviefun.datasources.albums.username}") String username,
            @Value("${moviefun.datasources.albums.password}") String password
    ) {
        return getDataSource(url, username, password);
    }

    @Bean
    public DataSource moviesDataSource(
            @Value("${moviefun.datasources.movies.url}") String url,
            @Value("${moviefun.datasources.movies.username}") String username,
            @Value("${moviefun.datasources.movies.password}") String password
    ) {
        return getDataSource(url, username, password);
    }

    private DataSource getDataSource(String url, String username, String password) {
        HikariDataSource wrapper = new HikariDataSource();
        wrapper.setJdbcUrl(url);
        wrapper.setUsername(username);
        wrapper.setPassword(password);
        return wrapper;
    }


    @Bean
    public HibernateJpaVendorAdapter getJpaAdapter(){
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(true);
        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        return adapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumEntityManager(@Qualifier("albumsDataSource") DataSource dataSource, HibernateJpaVendorAdapter adapter){
        return getLocalContainerEntityManagerFactoryBean(dataSource, adapter, "org.superbiz.moviefun.albums", "AlbumEntityManager");
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean moviesEntityManager(@Qualifier("moviesDataSource")DataSource dataSource, HibernateJpaVendorAdapter adapter){
        return getLocalContainerEntityManagerFactoryBean(dataSource, adapter, "org.superbiz.moviefun.movies", "MoviesEntityManager");
    }

    private LocalContainerEntityManagerFactoryBean getLocalContainerEntityManagerFactoryBean(DataSource dataSource, HibernateJpaVendorAdapter adapter, String s, String moviesEntityManager) {
        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setDataSource(dataSource);
        entityManager.setJpaVendorAdapter(adapter);
        entityManager.setPackagesToScan(s);
        entityManager.setPersistenceUnitName(moviesEntityManager);
        return entityManager;
    }

    @Bean
    public PlatformTransactionManager moviesTransactionManager(@Qualifier("moviesEntityManager") EntityManagerFactory
                                                                              entityManagerFactory){
        JpaTransactionManager manager = new JpaTransactionManager(entityManagerFactory);
        manager.setPersistenceUnitName("moviesTransactionManager");
        return manager;
    }

    @Bean
    public PlatformTransactionManager albumTransactionManager(@Qualifier("albumEntityManager") EntityManagerFactory
                                                                              entityManagerFactory){
        JpaTransactionManager manager = new JpaTransactionManager(entityManagerFactory);
        manager.setPersistenceUnitName("albumEntityManager");
        return manager;
    }


}
