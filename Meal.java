package mealplanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

enum Category {breakfast, lunch, dinner}
enum Weekdays {MONDAY("Monday"), TUESDAY("Tuesday"), WEDNESDAY("Wednesday"), THURSDAY("Thursday"),
    FRIDAY("Friday"), SATURDAY("Saturday"), SUNDAY("Sunday");
    public String label;
    Weekdays(String label) {
        this.label = label;
    }
}

public class Meal {
    private Category category;
    private String name;
    private List<String> ingredients;

    public Meal(String category, String name, String ingredients) {
        this.category = Category.valueOf(category.toLowerCase());
        this.name = name;
        this.ingredients = Arrays.stream(ingredients.split(","))
                .map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));

    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    @Override
    public String toString() {
        StringBuilder ingredientString = new StringBuilder();
        for(String ingredient : this.ingredients) {
            ingredientString.append(ingredient).append("\n");
        }
        return
                "\nName: " + name + "\n" +
                "Ingredients:\n" +
                ingredientString;
    }
}
