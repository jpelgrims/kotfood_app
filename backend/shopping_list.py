from flask import Flask, Blueprint, jsonify, request
from db import get_db

shopping_list = Blueprint('shopping_list_api', __name__, template_folder='templates')


@shopping_list.route('/purchase', methods=['POST'])
def mark_as_purchased():
    user_id = request.form.get("user_id")
    ingredient_name = request.form.get("ingredient_name")
    status = 0
    try:
        status = int(request.form.get("ingredient_status"))
        if status > 1 or status < 0:
            status = 0
    except:
        pass

    db = get_db()
    cursor = db.cursor()
    cursor.execute("UPDATE shopping_list_has_ingredients SET purchased=? WHERE ingredient_name=? AND shopping_list_id IN (SELECT shopping_list_id FROM user_has_shopping_list WHERE user_id=?)", (status, ingredient_name, user_id))
    db.commit()
    cursor.close()
    return ""


def get_shopping_list_price(ingredients):
    return sum(ingredient['price'] for ingredient in ingredients)


@shopping_list.route('/shopping_list', methods=['GET'])
def shopping_list_endpoint():
    user_id = request.args.get('id')

    db = get_db()
    cursor = db.cursor()

    cursor.execute("SELECT i.name, i.measurement, i.unit, SUM(i.amount) AS amount, i.price, (SELECT MAX(purchased) FROM shopping_list_has_ingredients WHERE ingredient_name=i.name AND shopping_list_id IN (SELECT shopping_list_id FROM user_has_shopping_list WHERE user_id=?)) AS purchased "
                   "FROM ingredient AS i JOIN shopping_list_has_ingredients AS s ON i.name = s.ingredient_name "
                   "WHERE shopping_list_id IN (SELECT shopping_list_id FROM user_has_shopping_list WHERE user_id=?) "
                   "GROUP BY name, measurement, unit", (user_id,user_id))
    ingredients = [dict(item) for item in cursor.fetchall()]

    cursor.execute("SELECT COUNT(*) FROM user_has_shopping_list WHERE shopping_list_id IN (SELECT shopping_list_id FROM user_has_shopping_list WHERE user_id=?)", (user_id,))
    shared = 1 if cursor.fetchone()[0] > 1 else 0
    cursor.close()

    shopping_list = {
        "items": ingredients,
        "estimated_price": get_shopping_list_price(ingredients),
        "shared": shared
    }

    return jsonify(shopping_list)


@shopping_list.route('/shopping_list', methods=['POST'])
def share_shopping_list():
    generator_id = request.form.get("generator_id")
    scanner_id = request.form.get("scanner_id")

    db = get_db()
    cursor = db.cursor()
    cursor.execute("SELECT shopping_list_id FROM user_has_shopping_list WHERE user_id=?", (generator_id,))
    shopping_list_id = int(cursor.fetchone()[0])

    if shopping_list_id:
        cursor.execute("INSERT INTO user_has_shopping_list(user_id, shopping_list_id) VALUES (?,?)", (scanner_id,shopping_list_id))
        db.commit()

    cursor.close()
    return ""