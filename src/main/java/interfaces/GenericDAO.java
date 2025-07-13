package interfaces;

import java.util.List;

/**
 *
 * @author gabs
 * @param <T>
 * @param <ID>
 */
public interface GenericDAO<T, ID> {
    T findById(ID id);
    List<T> findAll();
    List<T> findAllPaged(int page, int size);
    int countAll();
    void save(T entity);
    void update(T entity);
    void deleteById(ID id);
}