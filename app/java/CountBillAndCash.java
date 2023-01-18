package com.pixhunter.friendsbills;


import static com.pixhunter.friendsbills.Variables.progressChangedValue;

import android.os.Build;

import com.pixhunter.friendsbills.entities.Cash;
import com.pixhunter.friendsbills.entities.Friend;
import com.pixhunter.friendsbills.entities.Pack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that helps to count and to show all stats to pack fragment
 */
public class CountBillAndCash {

    String resultDelta = "";
    String resultDeltaRes = "";
    String resultFriendBills = "";

    List<Friend> friends;

    //set float format for number result
    DecimalFormat df = new DecimalFormat("######.##");

    //collect all friends
    CountBillAndCash(List<Friend> friends) {
        this.friends = friends;
    }

    /** This method returns all existing friend names
     * @return String friend names
     */
    public String giveFriendsNames() {
        String result = "";
        for (int i = 0; i < friends.size(); i++) {
            Friend friend = friends.get(i);
            if (!friend.getName().isEmpty()) {
                result += "  " + friend.getName() + "\n";
            }
        }
        return result;
    }

    /** This method returns all existing friend names
     * @return String friend names
     */
    public String giveFriendBills() {
        String result = "";
        float count = 0;
        for (int i = 0; i < friends.size(); i++) {
            Friend friend = friends.get(i);
            if (!friend.getName().isEmpty()) {
                count += friend.getBill();
                result += df.format(friend.getBill() * progressChangedValue * 0.01) + "\n";
            }
        }
        resultFriendBills = df.format(count * progressChangedValue * 0.01);;
        return result;
    }

    /** This method gives all cashes for dedicated friend
     * @return String - result with cashes values and count each of them
     */

    public String giveFriendsCash() {
        String result = "";
        float delta = 0;
        for (int i = 0; i < friends.size(); i++) {
            Friend friend = friends.get(i);
            if (!friend.getName().isEmpty()) {
                ArrayList<Cash> cashes = friend.getCashList();
                Optional<Pack> resultPack = baggagePack(cashes, friend.getBill() * progressChangedValue * 0.01F);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (resultPack.isPresent()) {
                        var packDelta = resultPack.get().delta;
                        resultDelta += df.format(packDelta * ((packDelta == 0) ? 1 : -1)) + "\n";
                        result += parsePack(resultPack.get().pack, friend) + "\n";
                        delta += resultPack.get().delta;
                    } else {
                        resultDelta += "0\n";
                        result +="\n";
                    }
                }
            }
        }
        resultDeltaRes = df.format((delta != 0) ? (delta * -1) : (0));
        return result;
    }

    /** Pack the baggage - method to found optimal cashes of existing for dedicated friend
     *
     * @param cashes - all dedicated friend cashes
     * @param allBill - the bill of friend
     * @return - optional pack - the result of cashes and delta for it between bill and cashes
     */
    public Optional<Pack> baggagePack(ArrayList<Cash> cashes, float allBill) {
        ArrayList<Float> allCashMas = new ArrayList<Float>();
        //create new mass with all cashes include count
        for (int i = 0 ; i < cashes.size(); i ++) {
            int count = cashes.get(i).getCount();
            for (int j = 0; j < count; j++) {
                allCashMas.add(cashes.get(i).getCash());
            }
        }

        //sort array
        for (int i = 0; i + 1 < allCashMas.size(); i++) {
            for (int j = 0; j + 1 < allCashMas.size() - i; j++) {

                if (allCashMas.get(j + 1) > allCashMas.get(j)) {
                    float m = allCashMas.get(j + 1);
                    allCashMas.set(j + 1, allCashMas.get(j));
                    allCashMas.set(j, m);
                }
            }
        }

        ArrayList<Pack> optimal = new ArrayList<>();

        optimal.add(new Pack());
        packageRun(optimal, allCashMas, 0, allBill);
        optimal = getOptimalPacks(optimal);

        ArrayList<Pack> result = new ArrayList<>(optimal);
        Optional<Pack> bestResult = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bestResult = Optional.empty();
        }

        //sort all results - first will be the best
        for (int i = 0; i < result.size(); i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (bestResult.isPresent()) {
                    if (bestResult.get().delta > result.get(i).delta) {
                        bestResult = Optional.of(result.get(i));
                    }
                } else {
                    bestResult = Optional.of(result.get(i));
                }
            }
        }
        return bestResult;
    }

    /**
     * Optimal cashes search by recursion
     * @param packs - all packs list array
     * @param allCashMas - all cashes list array
     * @param k - index of first elem of array
     * @param price - rest of money
     * @return - array of best packs
     */
    ArrayList<Pack> packageRun(ArrayList<Pack> packs, ArrayList<Float> allCashMas, int k, float price) {
        int m = packs.size() - 1;
        float delta = price;
        if (delta <= 0) return packs;
        for (int j = k; j < allCashMas.size(); j++) {

            //delta more then new elem
            if (allCashMas.get(j) < delta) {
                // if dedicated elem the last in array
                if (j == allCashMas.size() - 1) {
                    String lessPackBefore = packs.get(m).getPack();

                    Pack lessPack = new Pack();
                    lessPack.updatePack(lessPackBefore + " " + allCashMas.get(j));
                    lessPack.setDelta(delta - allCashMas.get(j));
                    packs.set(m, lessPack);

                    return packs;
                //delta less than new elem
                } else {
                    String lessPackBefore = packs.get(m).getPack();

                    Pack lessPack = new Pack();
                    lessPack.updatePack(packs.get(m).getPack() + " " + allCashMas.get(j));
                    lessPack.setDelta(delta - allCashMas.get(j));
                    packs.set(m, lessPack);

                    //try get another elems
                    for (int i = j + 1; i < allCashMas.size(); i++) {
                        Pack lessPackTer = new Pack();
                        lessPackTer.updatePack(lessPackBefore + " " + allCashMas.get(j));
                        lessPackTer.setDelta(delta - allCashMas.get(j));
                        packs.add(lessPackTer);
                        //recursion jump with new delta like dedicated elem is good
                        packs = packageRun(packs, allCashMas, i, delta - allCashMas.get(j));

                        if (packs.get(packs.size() - 1).delta == 0) return packs;
                        if (packs.get(packs.size() - 1).delta * -1 < allCashMas.get(allCashMas.size()-1)) {
                            ArrayList<Pack> newList = new ArrayList<>();
                            newList.add(packs.get(packs.size() - 1));
                            return newList;
                        }

                        Pack lessPackVoi = new Pack();
                        lessPackVoi.updatePack(lessPackBefore);
                        lessPackVoi.setDelta(delta);
                        packs.add(lessPackVoi);
                        //recursion jump with old delta like dedicated elem not ok
                        packs = packageRun(packs, allCashMas, i, delta);

                        m = packs.size() - 1;
                        if (packs.get(packs.size() - 1).delta == 0) return packs;
                        if (packs.get(packs.size() - 1).delta * -1 < allCashMas.get(allCashMas.size()-1)) {
                            ArrayList<Pack> newList = new ArrayList<>();
                            newList.add(packs.get(packs.size() - 1));
                            return newList;
                        }
                    }
                    Pack lessPackW = new Pack();
                    lessPackW.updatePack(lessPackBefore + " " + allCashMas.get(j));
                    lessPackW.setDelta(delta - allCashMas.get(j));
                    packs.set(m, lessPackW);
                }
                delta -= allCashMas.get(j);
            //delta equals elem
            } else if (allCashMas.get(j) == delta) {
                String lessPackBefore = packs.get(m).getPack();

                Pack lessPack = new Pack();

                lessPack.updatePack(lessPackBefore);
                lessPack.setDelta(delta);
                packs.add(lessPack);

                if (packs.get(packs.size() - 1).delta == 0) return packs;
                m = packs.size() - 1;

                Pack newPack = new Pack();
                newPack.updatePack(lessPackBefore + " " + allCashMas.get(j));
                newPack.setDelta(delta - allCashMas.get(j));
                packs.set(m, newPack);

                return packageRun(packs, allCashMas, ++j, delta);
            } else {
                String lessPackBefore = packs.get(m).getPack();

                Pack lessPack = new Pack();
                lessPack.updatePack(lessPackBefore + " " + allCashMas.get(j));
                lessPack.setDelta(delta - allCashMas.get(j));
                packs.set(m, lessPack);

                if (packs.get(packs.size() - 1).delta == 0) return packs;

                if (packs.get(packs.size() - 1).delta * -1 < allCashMas.get(allCashMas.size()-1)) {
                    ArrayList<Pack> newList = new ArrayList<>();
                    newList.add(packs.get(packs.size() - 1));
                    return newList;
                }
                m = packs.size() - 1;

                Pack newPack = new Pack();
                newPack.updatePack(lessPackBefore);
                newPack.setDelta(delta);
                packs.add(newPack);

                return packageRun(packs, allCashMas, ++j, delta);
            }
        }
        return packs;
    }

    /**
     * Get optimal pak of existing
     * @param optimal - packs list
     * @return - sorted packs
     */
    ArrayList<Pack> getOptimalPacks(ArrayList<Pack> optimal) {
        ArrayList<Pack> negativePack = new ArrayList<>();
        Pack thisPack;
        for (int i = 0; i < optimal.size(); i++) {
            thisPack = optimal.get(i);
            if (thisPack.getDelta() < 0) {
                negativePack.add(thisPack);
            } else if (thisPack.getDelta() == 0) {
                negativePack.clear();
                negativePack.add(thisPack);
                return negativePack;
            }
        }

        for (int i = 0; i < negativePack.size() - 1; i++) {
            for (int j = 0; j < negativePack.size() - i - 1; j++){
                Pack leftPack = negativePack.get(j);
                Pack ridthPack = negativePack.get(j + 1);

                if (leftPack.getDelta() > ridthPack.getDelta()) {
                    negativePack.set(j, ridthPack);
                    negativePack.set(j+1, leftPack);
                }
            }
        }
        return negativePack;
    }

    /**
     *  Create result of cashes into rows
     */
    String parsePack(String pack, Friend friend) {
        String result = "";
        ArrayList<Cash> existCash = new ArrayList<>();
        Pattern p = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher m = p.matcher(pack);
        Optional<Cash> newCash = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            newCash = Optional.empty();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            newCash = Optional.empty();
        }
        while (m.find()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                newCash = existCash.stream().filter(packer -> packer.getName().equals(m.group())).findFirst();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (!newCash.isPresent()) {
                    existCash.add(new Cash(m.group(), 1, friend));
                } else {
                    existCash.stream().filter(packer -> packer.getName().equals(m.group())).findFirst().ifPresent(Cash::incCount);
                }
            }
        }

        for (int i = 0; i < existCash.size(); i++) {
            float thisValue = existCash.get(i).getCash();

            float deltaVal = thisValue - (int)thisValue;
            if (deltaVal > 0) {
                result += thisValue + "x" + existCash.get(i).getCount() + " ";
            } else {
                result += (int)thisValue + "x" + existCash.get(i).getCount() + " ";
            }
        }
        System.out.println("==> " + result);
        return result;
    }

}
