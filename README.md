# Friend_bills

This app helps to count bills, cashes and change from sales receipt when there are one, two or more people.

## What does this app cans:

* Collect friends in horizontal list
* Friends have different animal names and contains image each of them on ico and background for easy navigation
* Show all history of imput data (bills and cashes) for each friend with all summ of them
* Statistics of all friends data that shows name, total sum of bill, all needed cashes, total change for each friend and for all of them

App in Google Play:
<br>https://play.google.com/store/apps/details?id=com.pixhunter.friendsbills

## Entities

* `Bill` - bills for each friend, has `Friend` information, bill float value
* `Cash` - cashes for each friend, has `Friend` information, cash float value and int count of cash
* `Friend` - friend information, has summ of all bills, cashes for this frend, also lists of all bills and cashes and current animal name
* `Friendfragment` - entity for open tab, fragment that shows friend information and information based on Sqlite data (friend name, comment, lists and total summ of bills and cashes)
* `Pack` - helping class for friends statistics, has delta (differense between total friend bill and cashes) and info about those cashes if they exists

## Schema of dependencies of application activities
 ```
 *            Main Activity
 *         ________|_______
 *        |               |
 *      Pack        Friend Fragment
 *                        |
 *                     ___|____
 *                    |       |
 *                  Bill    Cash
 ```


<br>The hurdest part was to reliase algoritm of choice of optimal algorithm to find cashes to have minimal change for each bills

The algoritm realised in `CountBillAndCash`
All existing cashes append to list and descending sorted. 
Then use recursive method when we compare each element of list with dedicated sum of all bills only for one friend. 
If new element less then summ we call those method again. 
New list is old list without those element and with new summ which is the multiplicity of the previous sum and the current element.
Also we call those method with old list without those element but with old summ.
Then new list sorted by delta and we get the smaler of them. Those entity is the best for dedicated friend.

*NOTE - cashes that has summ les than total bills for friend will not be shown and not considered a solution to the problem*

<br>Recursion repeats while:
* element less than summ
* difference between summ and dedicated element less than last element of sorted list of cashes
* list ends but summ more then difference dedicated element and summ 

<br>


![123](https://user-images.githubusercontent.com/88940110/213296508-706da9c2-f054-4a62-b48e-8ccb1ed00719.jpg)
