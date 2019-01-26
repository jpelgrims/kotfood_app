from flask import Flask, Blueprint, jsonify, request
from db import get_db, get_product_db

import random
import json

meal = Blueprint('meal_plan_api', __name__)

random_meals = []


with open("meals.json") as f:
    data = json.load(f)
    random_meals = data


def get_product_price(product_name):
    db = get_product_db()
    cursor = db.cursor()
    cursor.execute("SELECT price FROM products WHERE lower(name) LIKE '%{}%' LIMIT 1".format(product_name))
    fetch = cursor.fetchone()
    product_price = float(fetch[0]) if fetch is not None else 0
    return product_price


# Returns a list of 7 random meals generated from given list of meals and within given parameters
def generate_meal_plan(budget, calorie_limit, portions):
    meal_plan = random.sample(random_meals, 7)

    for i in range(len(meal_plan)):
        for p in range(len(meal_plan[i]["ingredients"])):
            price = get_product_price(meal_plan[i]["ingredients"][p]["name"])
            meal_plan[i]["ingredients"][p]["price"] = price

    return meal_plan


# Function to get a shopping list from a list of meals
def get_shopping_list(meals, portions=1):
    ingredients = {}

    for m in meals:
        meal_ingredients = m["ingredients"]

        for ingredient in meal_ingredients:

            if ingredient["name"] in ingredients.keys():
                ingredients[ingredient["name"]]["amount"] += ingredient["amount"]
            else:
                ingredients[ingredient["name"]] = ingredient

            price = get_product_price(ingredient["name"])
            ingredients[ingredient["name"]]["price"] = price

    return list(ingredients.values())


@meal.route('/meal_plan', methods=['GET'])
def meal_plan_endpoint():
    user_id = request.args.get('id')

    if user_id:
        # Default values
        budget = 50
        portions = 1
        calorie_limit = 1000

        try:
            budget = int(request.args.get('budget'))
            portions = int(request.args.get('portions'))
            calorie_limit = int(request.args.get('calorie_limit'))
        except Exception as e:
            print(e)

        # Create user
        connection = get_db()
        cursor = connection.cursor()
        cursor.execute("INSERT OR IGNORE INTO user(id) VALUES(?)", (user_id,))

        # Generate meal plan
        meal_plan = generate_meal_plan(budget, calorie_limit, portions)

        # Generate shopping list
        shopping_list = get_shopping_list(meal_plan, portions)

        cursor.execute("DELETE FROM shopping_list WHERE id=(SELECT shopping_list_id FROM user_has_shopping_list WHERE user_id=?)", (user_id,))
        cursor.execute("DELETE FROM user_has_shopping_list WHERE user_id=?", (user_id,))
        cursor.execute("INSERT INTO shopping_list (budget, calorie_limit, portions) VALUES (?,?,?)", (budget, calorie_limit, portions))
        shopping_list_id = cursor.lastrowid
        cursor.execute("INSERT INTO user_has_shopping_list (user_id, shopping_list_id) VALUES(?,?)", (user_id, shopping_list_id))

        for ingredient in shopping_list:
            cursor.execute("INSERT OR IGNORE INTO ingredient(name, measurement, amount, unit, price) VALUES(?,?,?,?,?)", (ingredient["name"], ingredient["measurement"], ingredient["amount"], ingredient.get("unit", ""), ingredient["price"]))
            cursor.execute("INSERT INTO shopping_list_has_ingredients (shopping_list_id, ingredient_name) VALUES (?,?)", (shopping_list_id, ingredient["name"]))

        connection.commit()
        cursor.close()

        return jsonify(meal_plan)
    else:
        return jsonify({"error": "No user id in url parameters"})
