package mealplanner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DbClient {
    private final DataSource dataSource;

    public DbClient(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void run(String update) {
        try (Connection con = dataSource.getConnection();
             Statement statement = con.createStatement()
        ) {
            statement.executeUpdate(update);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Meal select(String query) {
        List<Meal> meals = selectForList(query);
        if (meals.size() == 1) {
            return meals.get(0);
        } else if (meals.isEmpty()) {
            return null;
        } else {
            throw new IllegalStateException("Query returned more than one object");
        }
    }

    public List<Meal> selectForList(String query) {
        List<Meal> meals = new ArrayList<>();

        try (Connection con = dataSource.getConnection();
             Statement statement = con.createStatement();
             ResultSet result = statement.executeQuery(query);
        ) {
            while (result.next()) {
                String ingredients = "";
                String category = result.getString("category");
                String name = result.getString("meal");
                int meal_id = result.getInt("meal_id");
                try (Connection connection = dataSource.getConnection();
                     Statement statement1 = connection.createStatement();
                     ResultSet ingredientSet = statement1.executeQuery("SELECT ingredient FROM ingredients WHERE meal_id = " + meal_id)) {
                    while (ingredientSet.next()) {
                        ingredients += ingredientSet.getString("ingredient") + ", ";
                    }
                }
                meals.add(new Meal(category, name, ingredients));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meals;
    }
}
