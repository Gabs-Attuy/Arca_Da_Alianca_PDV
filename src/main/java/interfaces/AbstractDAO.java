/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.UtilsDB;

/**
 *
 * @author gabs
 * @param <T>
 * @param <ID>
 */
public abstract class AbstractDAO<T, ID> implements GenericDAO<T, ID> {

    protected Connection connection;

    public AbstractDAO() {}
    
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;

    protected abstract String getTableName();

    protected abstract String getIdColumn();

    @Override
    public abstract void save(T entity);
    
    @Override
    public abstract void update(T entity);
    
    @Override
    public T findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumn() + " = ?";
        try (Connection connection = UtilsDB.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }

    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        List<T> list = new ArrayList<>();
        try (Connection connection = UtilsDB.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return list;
    }
    
    @Override
    public List<T> findAllPaged(int page, int size) {
        String sql = "SELECT * FROM " + getTableName() + " LIMIT ? OFFSET ?";
        List<T> list = new ArrayList<>();
        int offset = page * size;

        try (Connection connection = UtilsDB.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, size);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToEntity(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
    
    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM " + getTableName();

        try (Connection connection = UtilsDB.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }
    
    @Override
    public void deleteById(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumn() + " = ?";
        
        try (Connection connection = UtilsDB.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("Nenhum registro encontrado com o ID fornecido.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}