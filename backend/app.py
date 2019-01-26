from flask import Flask
from db import init_db, close_db

from meal import meal
from shopping_list import shopping_list


def create_app():
    app = Flask(__name__)
    app.register_blueprint(meal)
    app.register_blueprint(shopping_list)
    app.config.from_object('settings.Config')

    with app.app_context():
        init_db()

    app.teardown_appcontext(close_db)

    return app

if __name__ == '__main__':
    app = create_app()
    app.run(host='localhost')
