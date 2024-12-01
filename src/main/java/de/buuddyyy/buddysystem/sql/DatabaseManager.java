package de.buuddyyy.buddysystem.sql;

import de.buuddyyy.buddysystem.configs.MainConfig;
import de.buuddyyy.buddysystem.sql.entities.EnderChestEntity;
import de.buuddyyy.buddysystem.sql.entities.HomeEntity;
import de.buuddyyy.buddysystem.sql.entities.PlayerEntity;
import de.buuddyyy.buddysystem.sql.entities.WarpEntity;
import jakarta.persistence.Query;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DatabaseManager implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());

    private SessionFactory sessionFactory;
    @Getter private Session session;

    private final MainConfig config;

    public DatabaseManager(MainConfig config) {
        this.config = config;
    }

    public void openConnection() {
        if (sessionFactory != null) {
            return;
        }

        final String driverClass = config.getString("sql.driverClass");
        final String url = config.getString("sql.url");
        final String username = config.getString("sql.username");
        final String password = config.getString("sql.password");
        final boolean showSql = config.getBoolean("sql.showSql");
        final boolean formatSql = config.getBoolean("sql.formatSql");
        final String commandSql = config.getString("sql.commandSql");

        final Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", driverClass);
        configuration.setProperty("hibernate.connection.url", url);
        configuration.setProperty("hibernate.connection.username", username);
        configuration.setProperty("hibernate.connection.password", password);
        configuration.setProperty("hibernate.show_sql", String.valueOf(showSql));
        configuration.setProperty("hibernate.format_sql", String.valueOf(formatSql));
        configuration.setProperty("hibernate.hbm2ddl.auto", commandSql);

        configuration.addAnnotatedClass(EnderChestEntity.class);
        configuration.addAnnotatedClass(HomeEntity.class);
        configuration.addAnnotatedClass(PlayerEntity.class);
        configuration.addAnnotatedClass(WarpEntity.class);

        final ServiceRegistry sr = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        this.sessionFactory = configuration.buildSessionFactory(sr);
        this.session = this.sessionFactory.openSession();
    }

    public <T> T queryResult(Class<T> clazz, final String sql, final Map<String, Object> parameters) {
        Collection<T> collection = this.queryResults(clazz, sql, parameters);
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        return ((ArrayList<T>) collection).getFirst();
    }

    public <T> Collection<T> queryResults(Class<T> clazz, final String sql, final Map<String, Object> parameters) {
        Query query = this.session.createNativeQuery(sql, clazz);
        parameters.forEach(query::setParameter);
        return query.getResultList();
    }

    public void insertEntity(Object entityObject) {
        executeInTransaction(() -> session.persist(entityObject));
    }

    public void updateEntity(Mergeable entityObject) {
        executeInTransaction(() -> {
            var toMergedObj = session.merge(entityObject);
            toMergedObj.toMerge(entityObject);
        });
    }

    public void deleteEntity(Object entityObject) {
        executeInTransaction(() -> session.remove(entityObject));
    }

    @Override
    public void close() {
        if (session != null && session.isOpen()) {
            session.close();
        }
        if (sessionFactory != null && sessionFactory.isOpen()) {
            sessionFactory.close();
        }
    }

    private void executeInTransaction(Runnable runnable) {
        var transaction = this.session.beginTransaction();
        try {
            runnable.run();
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null && transaction.isActive())
                transaction.rollback();
            LOGGER.log(Level.SEVERE, "Database transaction failed.", ex);
            throw new IllegalStateException("Database transaction failed.", ex);
        }
    }

}
