# Paleo Delights

This is a demo primarily demonstrating the use of Firebase's features. This app is meant to be used together with Paleo Delights Rider which I shall be developing later. 

Paleo Delights is a fictional paleo meals home delivery service based in the city of Kuantan in Malaysia. Menu items are mostly taken from PaleoHacks website. 

## Stacks / templates used:

1. [Material Dialogues](https://github.com/afollestad/material-dialogs) for creating a dialogue box in the app with minimal code.

2. [Notify](https://github.com/Karn/notify) for creating a push notification within the app with minimal code.

3. Firebase Firestore for storing of menu item data as well as submitted orders.

4. Firebase Authentication for setting up phone authentication.

5. Firebase Cloud Storage for storing images of menu items.

6. Navigation Drawer template as provided by Android Studio. 

## Walkthrough 

![image](https://user-images.githubusercontent.com/40174427/83353722-267f2280-a387-11ea-8aad-3c39827fe34d.png)

Upon starting up the app, the user is first greeted by a phone number registration screen. Firebase Authentication UI is used to display the screen, with some changes to the theme to match the coloring scheme of the app. The user phone number is then stored in the app for later on.

![image](https://user-images.githubusercontent.com/40174427/83354036-2bdd6c80-a389-11ea-9003-609a8eb5f4a7.png)

Once the user's phone number has been registered the user is then given access to the app. The Navigation Drawer template provided by Android Studio is used to as the basis for the app. 

![image](https://user-images.githubusercontent.com/40174427/83354055-431c5a00-a389-11ea-971f-7fa9655e9e37.png)

In the menu items screen the user can browse through a selection of paleo diet meals to order, separated into categories of foods, drinks and appetizers. The menu items are stored and referenced from a database set up in Firebase Firestore.

![image](https://user-images.githubusercontent.com/40174427/83354071-57605700-a389-11ea-8dbb-d2fe0ae7346f.png)

Tapping on any of the menu items opens up a screen detailing the selection and the user can then opt to place orders and add to cart.

![image](https://user-images.githubusercontent.com/40174427/83354081-6a732700-a389-11ea-936a-bbba4f98df1b.png)

Once orders are placed the user is then returned to the menu items screen with amount marker labels added to the menu items. The user can proceed to tap on any other menu items, place orders, return the menu items screen and repeat as desired.

![image](https://user-images.githubusercontent.com/40174427/83354101-87a7f580-a389-11ea-8904-487e6564e149.png)

Upon tapping the Checkout button at the bottom of the menu items screen, which only appears if at least one menu item had been added to cart, the user is then brought to the process payment screen. 

The user is required to enter their credit card information in order to make a purchase, but since this is only a demo the fields are left empty.

The user is also required to enter the address. The app by design does not detect the user's location automatically due to the inaccuracy present in the location-finding service. Instead the address is left blank at first requiring the user either entering manually, or tap at the Get Location button.

![image](https://user-images.githubusercontent.com/40174427/83354117-9bebf280-a389-11ea-8ded-39d4d663911c.png)

Once the Get Location button from the previous screen is tabbed, the user is brought to a screen displaying the user's closest approximate location. Since the location is not guaranteed to be 100 percent accurate, the result is displayed in an EditText which the user can tap and make changes before finally confirming location and returning to the process payment screen.

![image](https://user-images.githubusercontent.com/40174427/83354130-b02fef80-a389-11ea-86d7-8f27dd9eff3a.png)



![image](https://user-images.githubusercontent.com/40174427/83354143-c2119280-a389-11ea-8d3e-c3aac78fd1db.png)

![image](https://user-images.githubusercontent.com/40174427/83354169-e2d9e800-a389-11ea-836e-acf87c510d10.png)
