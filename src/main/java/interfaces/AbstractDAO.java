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

    public AbstractDAO() {
        try{
            this.connection = UtilsDB.getConnection();
        } catch (Exception ex) {
            System.getLogger(AbstractDAO.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
    }

    public void closeConnection() {
        UtilsDB.closeConnection(connection);
    }
    
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
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        
        return null;
    }

    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM " + getTableName();
        List<T> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        
        return list;
    }

    @Override
    public void deleteById(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumn() + " = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("Nenhum registro encontrado com o ID fornecido.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
    
}