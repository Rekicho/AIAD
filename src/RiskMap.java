import jade.core.AID;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import src.Continent;
import src.Country;

public class RiskMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private final HashMap<String, Country> countries;
    private final HashMap<String, Continent> continents;

    public RiskMap() {
        continents = new HashMap<String, Continent>();
        countries = new HashMap<String, Country>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("res/countries.txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split("->");

                if (!continents.containsKey(parts[0]))
                    continents.put(parts[0], new Continent(parts[0]));

                countries.put(parts[1], new Country(parts[1]));
                continents.get(parts[0]).addCountry(countries.get(parts[1]));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[Risk Game Agent] Could not load countries");
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader("res/borders.txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split("->");
                countries.get(parts[0]).addBorder(countries.get(parts[1]));
                countries.get(parts[1]).addBorder(countries.get(parts[0]));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[Risk Game Agent] Could not load borders");
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader("res/bonus.txt"));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split("->");
                continents.get(parts[0]).setBonus(Integer.parseInt(parts[1]));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[Risk Game Agent] Could not load bonus");
        }
    }

    public HashMap<String, Continent> getContinents() {
        return continents;
    }

    public HashMap<String, Country> getCountries() {
        return countries;
    }

    @Override
    public String toString() {
        String res = "";

        Iterator it = continents.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            res += ((Continent) pair.getValue()) + "\n";
        }

        return res;
    }

    public ArrayList<Country> unoccupiedCountries() {
        ArrayList<Country> unoccupiedCountries = new ArrayList();

        Iterator it = countries.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (((Country) pair.getValue()).isEmpty())
                unoccupiedCountries.add((Country) pair.getValue());
        }

        return unoccupiedCountries;
    }

    public boolean placeIfValid(AID player, String country) {
        if (unoccupiedCountries().size() != 0) {
            if (countries.get(country).getOwner() != null)
                return false;

            countries.get(country).setOwner(player);
            countries.get(country).setArmies(1);

            return true;
        }

        if (countries.get(country).getOwner().equals(player)) {
            countries.get(country).setArmies(countries.get(country).getArmies() + 1);
            return true;
        }

        return false;
    }

    public boolean placeIfValidList(AID player, String placementList) {
        String[] placements = placementList.split(",");

        for (int i = 0; i < placements.length; i++)
            if (!placeIfValid(player, placements[i])) {
                for (int j = 0; j < i; j++)
                    countries.get(placements[i]).setArmies(countries.get(placements[i]).getArmies() - 1);

                return false;
            }

        return true;
    }

    public boolean checkGameOver() {

        Iterator it = countries.entrySet().iterator();

        AID player = null;

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            AID owner = ((Country) pair.getValue()).getOwner();

            if (player == null)
                player = owner;
            if (!player.equals(owner))
                return false;
        }

        return true;
    }

    public int controledCountries(AID player) {
        int res = 0;

        Iterator it = countries.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (((Country) pair.getValue()).getOwner().equals(player))
                res++;
        }

        return res;
    }

    public int controledContinentsBonus(AID player) {
        int res = 0;

        Iterator it = continents.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            res += ((Continent) pair.getValue()).getBonusIfControlled(player);
        }

        return res;
    }

    public int calculateArmiesToPlace(AID player) {
        return (((int) controledCountries(player) / 3) > 3 ? ((int) controledCountries(player) / 3) : 3)
                + controledContinentsBonus(player);
    }

    public boolean checkValidAttack(AID player, String arguments) {
        String[] args = arguments.split(" ");

        return countries.get(args[1]).getOwner().equals(player) && countries.get(args[1]).getArmies() >= 2
                && countries.get(args[1]).getArmies() >= Integer.parseInt(args[3])
                && !countries.get(args[2]).getOwner().equals(player)
                && countries.get(args[1]).getBorders().containsKey(args[2]);
    }

    public boolean checkValidFortify(AID player, String arguments) {
        String[] args = arguments.split(" ");

        // 0 - localName, 1-helper, 2-selected, 3-armies
        return countries.get(args[1]).getOwner().equals(player) &&
                countries.get(args[2]).getOwner().equals(player) &&
                countries.get(args[1]).getArmies() >= Integer.parseInt(args[3]) + 1;
    }

    public String resolveAttack(String arguments) {
        Country attacking = countries.get(arguments.split(" ")[1]);
        Country defending = countries.get(arguments.split(" ")[3]);
        int attackingArmies = Integer.parseInt(arguments.split(" ")[2]);
        int defendingArmies = Integer.parseInt(arguments.split(" ")[4]);

        Integer[] attackingDice = new Integer[attackingArmies];
        Integer[] defendingDice = new Integer[defendingArmies];

        Random dice = new Random();

        for (int i = 0; i < attackingArmies; i++)
            attackingDice[i] = dice.nextInt(6) + 1;

        for (int i = 0; i < defendingArmies; i++)
            defendingDice[i] = dice.nextInt(6) + 1;

        Arrays.sort(attackingDice, Collections.reverseOrder());
        Arrays.sort(defendingDice, Collections.reverseOrder());

        int lostAttacking = 0;
        int lostDefending = 0;

        for (int i = 0; i < Math.min(attackingArmies, defendingArmies); i++) {
            if (defendingDice[i] >= attackingDice[i])
                lostAttacking++;

            else
                lostDefending++;
        }

        attacking.setArmies(attacking.getArmies() - lostAttacking);
        defending.setArmies(defending.getArmies() - lostDefending);

        if (defending.isEmpty()) {
            defending.setOwner(attacking.getOwner());
            defending.setArmies(attackingArmies - lostAttacking);
            attacking.setArmies(attacking.getArmies() - (attackingArmies - lostAttacking));
        }

        return attacking.getName() + " " + attackingArmies + " -" + lostAttacking + " " + defending.getName() + " -"
                + lostDefending;
    }

    public void fight(String attackingName, int attackingArmies, int lostAttacking, String defendingName,
            int lostDefending) {
        Country attacking = countries.get(attackingName);
        Country defending = countries.get(defendingName);

        attacking.setArmies(attacking.getArmies() + lostAttacking);
        defending.setArmies(defending.getArmies() + lostDefending);

        if (defending.isEmpty()) {
            defending.setOwner(attacking.getOwner());
            defending.setArmies(attackingArmies + lostAttacking);
            attacking.setArmies(attacking.getArmies() - (attackingArmies + lostAttacking));
        }
    }

    public boolean stillPlaying(AID player) {
        return controledCountries(player) != 0;
    }

    public void doFortify(String arguments) {
        String[] args = arguments.split(" ");

        Country helper = countries.get(args[1]);
        Country selected = countries.get(args[2]);
        int armies = Integer.parseInt(args[3]);

        helper.setArmies(helper.getArmies()-armies);
        selected.setArmies(selected.getArmies()+armies);
    }

    public ArrayList<AID> possibleAlliances(AID player) {
        ArrayList<AID> possibleAlliances = new ArrayList<AID>();

        Iterator it = countries.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (!((Country) pair.getValue()).getOwner().equals(player))
                possibleAlliances.add(((Country) pair.getValue()).getOwner());
        }

        it = countries.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            if(!((Country) pair.getValue()).getOwner().equals(player))
                continue;

            Iterator ti = ((Country) pair.getValue()).getBorders().entrySet().iterator();

            while(it.hasNext()) {
                Map.Entry pairpair = (Map.Entry) it.next();
                Country country = (Country) pairpair.getValue();
                if (country.getOwner().equals(player))
                    possibleAlliances.remove(country.getOwner());
            }
        }

        return possibleAlliances;
    }

    public boolean checkAllianceWin(AID player1, AID player2) {
        Iterator it = countries.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (!((Country) pair.getValue()).getOwner().equals(player1) && !((Country) pair.getValue()).getOwner().equals(player2))
                return false;
        }

        return true;
    }
}