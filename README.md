"Order Management System"

This project is to develop a simple Order State Management System, where we maintain the state of the Order and return the latest state or all the states of the Order, along with Trade event which gets triggered automatically when a new order is successfully created. I have provided a basic REST api for submitting the command and quering the state of the order from the system.

Domain Overview -
In this OMS, a client can enter a command to create a new Order. On each order, user can amend the order or cancel the order. The state of the Order is determined by the values passed in the input (New, Amend, Cancel) and also Trade event is triggered for new accepted Order and listens to Order events and changes states accordingly.
User can query the latest order/trade state by its OrderId and also history of order/trade states.

High level Architecture Design:




