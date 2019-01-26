import sqlite3

from flask import g, current_app
import os


def init_db():
    """Create a new database if it doesn't exist yet."""
    db = get_db()

    with current_app.open_resource('schema.sql') as f:
        db.executescript(f.read().decode('utf8'))


def drop_db(db_file):
    os.remove(db_file)


def get_db():
    db = getattr(g, 'database', None)
    if db is None:
        db_file = current_app.config['DATABASE']
        db = g.database = sqlite3.connect(db_file)
    db.row_factory = sqlite3.Row
    db.execute("PRAGMA foreign_keys = ON;")
    return db


def close_db(exception=None):
    db = g.pop('database', None)
    if db is not None:
        db.close()


def get_product_db():
    db = getattr(g, 'product_database', None)
    if db is None:
        db_file = current_app.config['PRODUCT_DATABASE']
        db = g.database = sqlite3.connect(db_file)
    db.row_factory = sqlite3.Row
    db.execute("PRAGMA foreign_keys = ON;")
    return db


def close_product_db():
    db = g.pop('product_database', None)
    if db is not None:
        db.close()