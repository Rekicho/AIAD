package src;

import java.util.ArrayList;
import java.util.HashMap;

import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONString;

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

  public class Player {
    String name;
    String localName;

    public Player(final String name, final String localName) {
      this.name = name;
      this.localName = localName;
    }

    public boolean equals(final Object obj) {
      if (obj != null && obj instanceof Player) {
        final Player s = (Player) obj;
        return name.equals(s.name) && localName.equals(s.localName);
      }
      return false;
    }

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

      if (!moves.containsKey(key))
        System.out.println("ERROR: Defend must exist with an attack");

      final String[] params = defend.split(" ");
      final Attack attack = (Attack) moves.get(key).get(moves.get(key).size() - 1);
      attack.setDefenderInfo(new Player(params[0], params[1]), Integer.parseInt(params[2]));
    }

    public void appendFightInfo(final int round, final Player player, final LoggerPhase phase, final String fight) {
      final Key key = new Key(round, player, phase);

      if (!moves.containsKey(key))
        System.out.println("ERROR: Fight must exist with an attack");

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

    System.out.println(round + " " + localName + " " + phase + " " + move);
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

  public void saveGame() {

  }
}
