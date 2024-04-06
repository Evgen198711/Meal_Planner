package mealplanner;

import java.util.List;

public interface MealDao {
    List<Meal> findAll();
    Meal findById(int id) ;
    void add(Meal meal);
    void update(Meal meal);
    void deleteById(int id);
}


