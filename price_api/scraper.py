import datetime
import random

from bs4 import BeautifulSoup
import requests
import re
import base64
import sqlite3
import time

def init_db():
    """Create a new database if it doesn't exist yet."""
    db = sqlite3.connect("products.db")
    db.row_factory = sqlite3.Row

    with open('schema.sql', 'r') as f:
        db.executescript(f.read())
    return db

def parse_colruyt_price_page(url=None, branch=None):
    if not url:
        url = "https://colruyt.collectandgo.be/cogo/nl/branch/" + str(branch) + "/"

    page = requests.get(url)
    soup = BeautifulSoup(page.text, "html.parser")

    try:
        category = soup.find("h1").text
        product_divs = soup.find_all("div", class_=re.compile("^product products__item _bArticle"))
    except:
        return [], ""


    products = {}

    for product in product_divs:
        try:
            name = product.find("div", class_="product__name").text.lower().strip()
            name += " " + product.find("div", class_="product__description").text.strip()
            name = name.strip()

            unit = product.find("span", class_="product__unit").text.strip()
            weight = product.find("div", class_="product__weight").text.strip()
            match1 = re.match('^([0-9])kg', weight)
            match2 = re.match('.*([0-9],[0-9])kg', weight)
            match6 = re.match('([0-9])x([0-9]{2,3})g.*', weight)
            match3 = re.match('.*([0-9]{3})g.*', weight)
            match4 = re.match('.*([0-9]{2})g.*', weight)
            match5 = re.match('.*(stuk|st).*', weight)

            if weight.lower() == "los":
                weight = 1000 # weight in grams
            elif match1:
                weight = int(match1.group(1))*1000
            elif match2:
                weight = float(match2.group(1).replace(",", "."))*1000
            elif match6:
                weight = int(match6.group(1)) * int(match6.group(2))
            elif match3:
                weight = int(match3.group(1))
            elif match4:
                weight = int(match4.group(1))
            elif match5:
                weight = None
            else:
                weight=1000
            
            product_info = product.find("div", class_="product__price-unit")
            encoded_price = product.find("span", class_="displayPrice1").text
            m = re.match("setPrice\('(.*)',", encoded_price)
            price = base64.b64decode(m.group(1)).decode('utf-8')

            products[name] = {'name': name, 'price': float(price.replace(",", ".")), 'weight': weight, 'datetime': datetime.datetime.now()}
        except Exception as e:
            print(e)
    return products, category


if __name__ == '__main__':
    db = init_db()
    cursor = db.cursor()

    for i in range(1812,1900):
        products, category = parse_colruyt_price_page(branch=i)
        rows = [('colruyt', product['name'], product['price'], product['weight'], product['datetime']) for product in products.values()]
        print("{}:'{}'".format(i, category.strip()))
        if len(rows) > 0:
            cursor.executemany("INSERT INTO products(store, name, price, weight, datetime) VALUES (?,?,?,?,?)", rows)
            db.commit()

        time.sleep(random.randint(0, 10000)/1000)

