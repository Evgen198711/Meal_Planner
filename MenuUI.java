package mealplanner;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuUI {
 
    Scanner sc;

    DbMealDao data;
    String input;

    public MenuUI() {
        sc = new Scanner(System.in);
//
        data = new DbMealDao();
    }

    public void start() {
        mainMenu();
    }

    public void mainMenu() {
        loop:
        while (true) {
            System.out.println();
            System.out.println("What would you like to do (add, show, plan, save, exit)?");

            String action = sc.nextLine();

            switch (action) {
                case "add":
                    add();
                    break;
                case "show":
                    show();
                    break;
                case "plan":
                    plan();
                    break;
                case "save":
                    save();
                    break;
                case "exit":
                    System.out.println("Bye!");
                    break loop;

            }
        }
    }

    private void add() {

        String category;
        String name;
        String ingredients;

        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");

        while (true) {
            input = sc.nextLine();
            if (input.matches("breakfast|lunch|dinner")) {
                category = input;
                break;
            } else {
                System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            }
        }

        System.out.println("Input the meal's name:");

        while (true) {
            input = sc.nextLine();
            if (input.matches("[a-zA-Z]+\\s?[a-zA-Z]+")) {
                name = input;
                break;
            } else {
                System.out.println("Wrong format. Use letters only!");
            }
        }

        System.out.println("Input the ingredients:");

        while (true) {
            input = sc.nextLine();
            if (input.matches("([a-zA-Z]+\\s?[a-zA-Z]+,\\s?)*[a-zA-Z]+\\s?[a-zA-Z]+")) {
                ingredients = input;
                break;
            } else {
                System.out.println("Wrong format. Use letters only!");
            }
        }

        Meal completedMeal = new Meal(category, name, ingredients);

        data.add(completedMeal);
        System.out.println("The meal has been added!");

    }

    private void plan() {
        data.clearTable();
        for (int i = 0; i < Weekdays.values().length; i++) {
            System.out.println(Weekdays.values()[i].label);
            for (int j = 0; j < Category.values().length; j++) {
                List<Meal> foundMeal = data.findByCategory(String.valueOf(Category.values()[j]));
                foundMeal.stream().map(Meal::getName).sorted().forEach(System.out::println);
                System.out.printf("Choose the %s for %s from the list above:\n", String.valueOf(Category.values()[j]),
                        Weekdays.values()[i].label);

                while(true) {
                    input = sc.nextLine();

                    if (foundMeal.stream().noneMatch(s -> input.equalsIgnoreCase(s.getName()))) {
                        System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                        continue;
                    }
                    break;
                }
                data.addToPlan(Weekdays.values()[i].label, data.findByName(input));
            }
            System.out.printf("Yeah! We planned the meals for %s.\n\n", Weekdays.values()[i].label);
        }

        for (int i = 0; i < Weekdays.values().length; i++) {
            System.out.println(Weekdays.values()[i].label);
            for(int j = 0; j < Category.values().length; j++) {
                System.out.println(String.valueOf(Category.values()[j]).substring(0,1).toUpperCase() + String.valueOf(Category.values()[j]).substring(1) + ": " + data.findByCategoryAndWeekday("plan", String.valueOf(Category.values()[j]), Weekdays.values()[i].label).getName());
            }
        }
    }

    private void save() {
        List<Meal> saveMeals = data.findAll();
        if(saveMeals.isEmpty()) {
            System.out.println("Unable to save. Plan your meals first.");
            return;
        }
        List<String> ingredients = new ArrayList<>();
        for(Meal meal : saveMeals) {
            ingredients.addAll(meal.getIngredients());
        }

        System.out.println("Input a filename:");
        input = sc.nextLine().trim();
        try (PrintWriter writer = new PrintWriter(input)) {
            for (int i = 0; i < ingredients.size(); i++) {
                int count = 0;
                for (int j = ingredients.size()-1; j > i; j--) {
                    if(ingredients.get(i).equals(ingredients.get(j))) {
                        count++;
                        ingredients.remove(j);
                    }
                }
                writer.print(ingredients.get(i));
                if(count > 0) {
                    writer.println(" x" + ++count);
                } else {
                    writer.println();
                }

            }
            System.out.println("Saved!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void show() {

//        System.out.println();
        System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");

        while (true) {
            input = sc.nextLine();
            if (input.matches("breakfast|lunch|dinner")) {

                if(data.findByCategory(input).isEmpty()) {
                    System.out.println("No meals found.");
                    break;
                }

                System.out.printf("Category: %s\n", input);

                data.findByCategory(input).forEach(System.out::print);
                break;
            } else {
                System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            }
        }


    }
}
