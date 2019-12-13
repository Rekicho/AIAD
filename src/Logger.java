import jade.core.AID;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.nio.file.StandardOpenOption;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Logger {

  public static enum LoggerPhase {
    INITIAL_PLACE, ROUND_PLACE, ROUND_ATTACK, ROUND_DEFEND, ROUND_FIGHT, ROUND_FORTIFY,
  }

  int nRounds;
  int nPlayers;
  ArrayList<Player> players;
  HashMap<Integer, Round> rounds;
  HashMap<String, Country> countries;
  HashMap<String, Continent> continents;
  HashMap<String, HashMap<String, Integer>> initialDisposal;
  
  public class Player {
    String name;
    String localName;

    public Player(final String name, final String localName) {
      this.name = name;
      this.localName = localName;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj != null && obj instanceof Player) {
        final Player s = (Player) obj;
        return name.equals(s.name) && localName.equals(s.localName);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }
  }

  public class Round {
    HashMap<Key, ArrayList<Move>> moves;
    HashMap<String, Country> countries;
    HashMap<String, Continent> continents;

    public Round() {
      moves = new HashMap<Key, ArrayList<Move>>();
      continents = new HashMap<String, Continent>();
      countries = new HashMap<String, Country>();
    }

    public void addPlace(final int round, final Player player, final LoggerPhase phase, final String country) {
      final Key key = new Key(round, player, phase);

      if (!moves.containsKey(key))
        moves.put(key, new ArrayList<Move>());

      moves.get(key).add(new Place(player, country));
    }

    public void addAttack(final int round, final Player player, final LoggerPhase phase, final String attack) {
      final Key key = new Key(round, player, phase);

      if (!moves.containsKey(key))
        moves.put(key, new ArrayList<Move>());

      final String[] params = attack.split(" ");
      moves.get(key).add(new Attack(player, params[0], params[1], Integer.parseInt(params[2])));
    }

    public void appendDefendInfo(final int round, final Player player, final LoggerPhase phase, final String defend) {
      final Key key = new Key(round, player, phase);

      //if (!moves.containsKey(key))
        //System.out.println("ERROR: Defend must exist with an attack");

      final String[] params = defend.split(" ");
      final Attack attack = (Attack) moves.get(key).get(moves.get(key).size() - 1);
      attack.setDefenderInfo(new Player(params[0], params[1]), Integer.parseInt(params[2]));
    }

    public void appendFightInfo(final int round, final Player player, final LoggerPhase phase, final String fight) {
      final Key key = new Key(round, player, phase);

      //if (!moves.containsKey(key))
        //System.out.println("ERROR: Fight must exist with an attack");

      final String[] params = fight.split(" ");
      final Attack attack = (Attack) moves.get(key).get(moves.get(key).size() - 1);
      attack.setFightInfo(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
    }

    public void addFortify(final int round, final Player player, final LoggerPhase phase, final String fortify) {
      final Key key = new Key(round, player, phase);

      if (!moves.containsKey(key))
        moves.put(key, new ArrayList<Move>());

      final String[] params = fortify.split(" ");
      moves.get(key).add(new Fortify(player, params[0], params[1], Integer.parseInt(params[2])));
    }

    public void setMap(final HashMap<String, Continent> continents, final HashMap<String, Country> countries) {
      this.continents = continents;
      this.countries = countries;
    }

    class Key {
      int round;
      Player player;
      LoggerPhase phase;

      public Key(final int round, final Player player, final LoggerPhase phase) {
        this.round = round;
        this.player = player;
        this.phase = phase;
      }

      @Override
      public boolean equals(final Object obj) {
        if (obj != null && obj instanceof Key) {
          final Key s = (Key) obj;
          return round == s.round && player.equals(s.player) && phase.toString().equals(s.phase.toString());
        }
        return false;
      }

      @Override
      public int hashCode() {
        return (round + player.name + phase.toString()).hashCode();
      }
    }

    abstract class Move {
    }

    class Attack extends Move {
      Player attacker, defender;
      String attackerCountry, defenderCountry;
      int attackerArmies, defenderArmies;
      int attackerLosses, defenderLosses;

      Attack(final Player attacker, final String attackerCountry, final String defenderCountry,
          final int attackerArmies) {
        this.attacker = attacker;
        this.attackerCountry = attackerCountry;
        this.defenderCountry = defenderCountry;
        this.attackerArmies = attackerArmies;
      }

      public void setDefenderInfo(final Player defender, final int defenderArmies) {
        this.defender = defender;
        this.defenderArmies = defenderArmies;
      }

      public void setFightInfo(final int attackerLosses, final int defenderLosses) {
        this.attackerLosses = attackerLosses;
        this.defenderLosses = defenderLosses;
      }
    }

    class Place extends Move {
      Player placer;
      String country;
      int armies;

      Place(final Player placer, final String country) {
        this.placer = placer;
        this.country = country;
        this.armies = 1;
      }
    }

    class Fortify extends Move {
      Player fortifier;
      String helperCountry, helpedCountry;
      int armiesTransfered;

      Fortify(final Player fortifier, final String helperCountry, final String helpedCountry,
          final int armiesTransfered) {
        this.fortifier = fortifier;
        this.helpedCountry = helpedCountry;
        this.helperCountry = helperCountry;
        this.armiesTransfered = armiesTransfered;
      }
    }
  }

  public Logger() {
    this.nRounds = 0;
    this.nPlayers = 0;
    this.players = new ArrayList<Player>();
    this.rounds = new HashMap<Integer, Round>();
    this.initialDisposal = new HashMap<>();
  }

  public void addPlayer(final String name, final String localName) {
    this.nPlayers++;
    players.add(new Player(name, localName));
  }

  public void addMap(final HashMap<String, Continent> continents, final HashMap<String, Country> countries) {
    this.continents = continents;
    this.countries = countries;
  }

  private Player getPlayer(final String name, final String localName) {
    for (int i = 0; i < nPlayers; i++)
      if (players.get(i).equals(new Player(name, localName))) {
        return players.get(i);
      }

    return null;
  }

  public void addRoundMove(final int round, final String name, final String localName, final LoggerPhase phase,
      final String move) {
    if (!rounds.containsKey(round))
      rounds.put(round, new Round());

    // System.out.println(round + " " + localName + " " + phase + " " + move);
    switch (phase) {
    case INITIAL_PLACE:
    case ROUND_PLACE:
      rounds.get(round).addPlace(round, getPlayer(name, localName), phase, move);
      break;
    case ROUND_ATTACK:
      rounds.get(round).addAttack(round, getPlayer(name, localName), phase, move);
      break;
    case ROUND_DEFEND:
      rounds.get(round).appendDefendInfo(round, getPlayer(name, localName), LoggerPhase.ROUND_ATTACK, move);
      break;
    case ROUND_FIGHT:
      rounds.get(round).appendFightInfo(round, getPlayer(name, localName), LoggerPhase.ROUND_ATTACK, move);
      break;
    case ROUND_FORTIFY:
      rounds.get(round).addFortify(round, getPlayer(name, localName), phase, move);
      break;
    default:
      break;
    }
  }

  public void addRoundMap(final int round, final HashMap<String, Continent> continents,
      final HashMap<String, Country> countries) {
    if (!rounds.containsKey(round))
      rounds.put(round, new Round());

    rounds.get(round).setMap(continents, countries);
    nRounds = rounds.size();
  }

  private void saveInformation(JSONObject header) {
    header.put("nPlayers", nPlayers);
    header.put("nRounds", nRounds);

    JSONArray playersObject = new JSONArray();
    int i = 0;
    for (Player p : players) {
      i++;
      JSONObject player = new JSONObject();
      player.put("name", p.localName);
      player.put("id", p.localName.charAt(p.localName.length() -1));
      playersObject.add(player);
    }
    
    header.put("players", playersObject);

    //saveMap(header);
  }

  public void saveInitialMapInformation(JSONObject object) {
    Iterator it = this.initialDisposal.entrySet().iterator();
    JSONArray continentsObject = new JSONArray();

    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();

      Iterator it2 = ((HashMap<String, Integer>) pair.getValue()).entrySet().iterator();
      JSONObject continent = new JSONObject();
      continent.put("name", (String) pair.getKey());

      while (it2.hasNext()) {
        Map.Entry pair2 = (Map.Entry) it2.next();
        continent.put((String) pair2.getKey(), (Integer) pair2.getValue());
      }
      continentsObject.add(continent);
    }
    object.put("continents", continentsObject);
  }

  public void saveBorders(HashMap<String, Country> cBorders, JSONArray bordersArray) {
    Iterator it = cBorders.entrySet().iterator();

    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();
      bordersArray.add(((Country) pair.getValue()).getName());
    }
  }

  public void saveCountries(HashMap<String, Country> cCountries, JSONArray countriesArray) {
    Iterator it = cCountries.entrySet().iterator();

    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();
      Country country = (Country) pair.getValue();
      JSONObject countryObject = new JSONObject();

      countryObject.put("name", country.getName());
      countryObject.put("armies", country.getArmies());
      countryObject.put("owner", country.getOwner().getLocalName());

      JSONArray bordersArray = new JSONArray();
      saveBorders(country.borders, bordersArray);
      countryObject.put("borders", bordersArray);

      countriesArray.add(countryObject);
    }
  }

  public void saveContinents(JSONArray continentsArray) {
    Iterator it = continents.entrySet().iterator();

    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();
      Continent continent = (Continent) pair.getValue();
      JSONObject continentObject = new JSONObject();

      continentObject.put("name", continent.name);
      continentObject.put("bonus", continent.bonus);
      continentObject.put("nCountries", continent.countries.size());

      JSONArray countriesArray = new JSONArray();
      saveCountries(continent.countries, countriesArray);
      continentObject.put("countries", countriesArray);

      continentsArray.add(continentObject);
    }
  }

  public void saveMap(JSONObject header) {

    JSONObject map = new JSONObject();
    map.put("nContinets", continents.size());
    map.put("nCountries", countries.size());

    JSONArray continentsArray = new JSONArray();
    saveContinents(continentsArray);
    map.put("Continents", continentsArray);

    header.put("map", map);
  }

  public void saveRound(int roundID, Round round, JSONObject object) {
    object.put("id", roundID);

    Iterator it = round.moves.entrySet().iterator();

    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();
      // (Move) pair.getValue();

    }

  }

  public void saveRounds(JSONObject object) {
    Iterator it = rounds.entrySet().iterator();

    JSONArray roundsArray = new JSONArray();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();
      JSONObject round = new JSONObject();
      saveRound((Integer) pair.getKey(), (Round) pair.getValue(), round);
      roundsArray.add(round);
    }

    object.put("rounds", roundsArray);
  }

  public void addInitialMapInformation(HashMap<String, HashMap<String, Integer>> info) {
    this.initialDisposal = info;
  }

  public void saveGame(String winner) {
    JSONObject object = new JSONObject();

    saveInformation(object);

    saveInitialMapInformation(object);
    // saveRounds(object);

    object.put("winner", winner);

    //System.out.println("Save Game Finished!");

    String result = "";
    int nSmart = 0, nBasic = 0;

    for(Player player:players) {
      if(player.name.split("Risk")[0].equals("Smart")) 
        nSmart++;
      else
        nBasic++;
    }

    result += nSmart + "," + nBasic + "," + nRounds + "," + winner.split("Risk")[0] + "\n";

    // Save object
    try {
      //Files.write(Paths.get("Logs/auxiliar.json"), object.toJSONString().getBytes());
      Files.write(Paths.get("Logs/risk.csv"), result.getBytes(), StandardOpenOption.APPEND);
    } catch (Exception e) {
      //System.out.println("ERROR: " + e.toString());
    }
  }
}
