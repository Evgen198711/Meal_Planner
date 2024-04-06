package mealplanner;

import org.postgresql.ds.PGPoolingDataSource;

import java.util.List;

public class DbMealDao implements MealDao {

    private static final String CREATE_TABLE_MEALS = "CREATE TABLE IF NOT EXISTS meals(" +
            "meal_id INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY, " +
            "category VARCHAR(30), " +
            "meal VARCHAR(30));";
    private static final String CREATE_TABLE_INGREDIENTS = "CREATE TABLE IF NOT EXISTS ingredients(" +
            "ingredient_id INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY, " +
            "ingredient VARCHAR(30), " +
            "meal_id INTEGER REFERENCES meals(meal_id));";
    private static final String CREATE_TABLE_PLAN = "CREATE TABLE IF NOT EXISTS plan(" +
            "weekday VARCHAR(30), " +
            "category VARCHAR(30), " +
            "meal VARCHAR(30), " +
            "meal_id INTEGER REFERENCES meals(meal_id))";
    private static final String SELECT_ALL_MEALS_FROM_PLAN = "SELECT * FROM plan";
    private static final String SELECT_ALL_INGREDIENTS = "SELECT * FROM ingredients";
    private static final String SELECT_MEAL_BY_CATEGORY = "SELECT * FROM meals WHERE category = '%s'";
    private static final String SELECT_MEAL_BY_TABLE_AND_BY_CATEGORY_AND_BY_WEEKDAY = "SELECT * FROM %s WHERE category = '%s' AND weekday = '%s'";
    private static final String SELECT_MEAL_BY_NAME = "SELECT * FROM meals WHERE meal = '%s'";
    private static final String SELECT_INGREDIENT_BY_MEAL_ID = "SELECT * FROM ingredients WHERE meal_id = %d";
    private static final String INSERT_MEAL = "INSERT INTO meals (category, meal) VALUES ('%s', '%s')";
    private static final String INSERT_MEAL_TO_PLAN = "INSERT INTO plan (weekday, category, meal, meal_id) VALUES ('%s', '%s', '%s', (SELECT meal_id FROM meals WHERE meal = '%s'))";
    private static final String INSERT_INGREDIENT = "INSERT INTO ingredients (meal_id, ingredient) VALUES((SELECT MAX(meal_id) FROM meals), '%s')";
    private static final String UPDATE_DATA = "UPDATE meals SET meal " +
            " = %s WHERE meal_id = %d";
    private static final String DELETE_DATA = "DELETE FROM meals WHERE meal_id = %d";

    private final DbClient dbClient;

    public DbMealDao() {
        PGPoolingDataSource dSource = new PGPoolingDataSource();
        dSource.setDatabaseName("meals_db");
        dSource.setServerName("localhost");
        dSource.setPortNumber(5432);
        dSource.setUser("postgres");
        dSource.setPassword("1111");

        this.dbClient = new DbClient(dSource);

        dbClient.run(CREATE_TABLE_MEALS);
        dbClient.run(CREATE_TABLE_INGREDIENTS);
        dbClient.run(CREATE_TABLE_PLAN);
    }


    public List<Meal> findByCategory(String category) {
        return dbClient.selectForList(String.format(SELECT_MEAL_BY_CATEGORY, category));
    }

    
    public Meal findByCategoryAndWeekday(String table, String category, String weekday) {
        return dbClient.select(String.format(SELECT_MEAL_BY_TABLE_AND_BY_CATEGORY_AND_BY_WEEKDAY,table, category, weekday));
    }

    @Override
    public List<Meal> findAll() {
        return dbClient.selectForList(SELECT_ALL_MEALS_FROM_PLAN);
    }

    @Override
    public Meal findById(int id) {
        return null;
    }

    public Meal findByName(String name) {
        return dbClient.select(String.format(SELECT_MEAL_BY_NAME, name));
    }

    @Override
    public void add(Meal meal) {

        dbClient.run(String.format(INSERT_MEAL, meal.getCategory(), meal.getName()));

        for (String ingredient : meal.getIngredients()) {
            dbClient.run(String.format(INSERT_INGREDIENT, ingredient));
        }
    }

    public void addToPlan(String weekDay, Meal meal) {
        dbClient.run(String.format(INSERT_MEAL_TO_PLAN, weekDay, meal.getCategory(), meal.getName(), meal.getName()));
    }


    @Override
    public void update(Meal meal) {

    }

    public void clearTable() {
        dbClient.run("DELETE FROM plan");
    }

    @Override
    public void deleteById(int id) {

    }
}
