# Proposal project Mobile Development

We will develop an android-only application written in Java, using Android Studio.

# General idea

The app will generate a new meal plan every week, along with a shopping list containing the required ingredients for those meals. 

**Features**

1. **Meals**   
   The user can get random meals or he can maintain a meal list himself (or both). This will require a server/API (so that the app can get a list of meals to choose from).

2. **Portions**   
   The user can enter the amount of portions that he wants for every meal. The app will then take this into consideration when making the shopping list.

3. **Budgetting**   
   The user can decide on a weekly amount of money that he wants to spend on food. The app will then take this into consideration when selecting meals. This will require scraping some website periodically and carefully (e.g. colruyt: https://colruyt.collectandgo.be/cogo/nl/home) for prices. This will require a server/API, since we can't let every app scrape the colruyt website for all prices.

4. **Calorie tracking/planning**   
   The user can enter a daily calorie limit and nutrition requirements (at least this much protein, ...). The app will then take this data into consideration when selecting meals. This feature might be useful for people on a specific diet. There are public (and free) APIs available for this. This doesn't really require a server/API, but a server/API could be useful to get around API user limits (i.e. if all app users send requests to our server and if our server queries the nutrition API and also maintains a cache of nutrition data, then we can simply pretend to be one user and not have to pay since most APIs are only paid after a certain number of users).

5. **Shared shopping list**   
   The user can "share" a shopping list with other people.  Everyone with access to the list can then mark items as bought. Users should also be able to add items to the shopping list (so the user doesn't need to download/use another shopping list app for other items he needs). This will require a server/API so the shopping lists can be synchronized. The shopping lists will be shared using *QR-codes*.
