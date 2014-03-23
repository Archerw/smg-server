package org.smg.server.database;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.smg.server.database.models.Player;
import org.smg.server.database.models.Player.PlayerProperty;
import org.smg.util.AccessSignatureUtil;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class DatabaseDriver {
  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  
  /**
   * Returns a list of entities given a query by kind (ex: DEVELOPER), property (ex: EMAIL),
   * and the string query (ex: "foo@bar.com").
   */
  public static List<Entity> queryByProperty(String kind, String property, Object query) {
    Filter filter = new FilterPredicate(property, FilterOperator.EQUAL, query);
    Query q = new Query(kind).setFilter(filter);
    List<Entity> result = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
    
    return result;
  }

  public static void insertMatchEntity(JSONObject match) throws JSONException {
    Transaction txn = datastore.beginTransaction();
    //Key key = KeyFactory.createKey("Match", match.getInt("matchId"));
    Entity entity = new Entity("Match", match.getInt("matchId"));
    entity.setProperty("matchId", match.getInt("matchId"));
    entity.setProperty("gameId", match.getInt("gameId"));
    String playerIds = match.getJSONArray("playerIds").toString();
    entity.setUnindexedProperty("playerIds", playerIds);
    entity.setProperty("playerThatHasTurn", match.getInt("playerThatHasTurn"));
    entity.setUnindexedProperty("gameOverScores", match.getJSONObject("gameOverScores").toString());
    entity.setProperty("gameOverReason", match.getString("gameOverReason"));
    entity.setUnindexedProperty("history", match.getJSONArray("history").toString());
    datastore.put(entity);
    txn.commit();
  }
  //MODEL NAME DEFINE
  private static final String PLAYER = "PLAYER";

  //PLAYER MODEL MODIFY BLACKLIST
  private static List<String> playerPropertyBlackList = Arrays.asList(
      PlayerProperty.EMAIL.toString(),
      PlayerProperty.PLAYERID.toString()
      );

  private static Object insertPlayerMutex = new Object();
  //TODO: Use Specific Mutex for each player login
  private static Object loginMutex = new Object();

  public static int test() {
    return 0;
  }

  /**
   * save player's information to database (insert or update)
   * If it is to insert a profile, playerId should not be given
   * return "SUCCESS:"+playerId+":"+accessSignature if insert success
   * return "EMAIL_EXITSTS" if email already existed
   * 
   * If it is to update profile, playerId should be given in {@code player}
   * return "UPDATED_PLAYER" if updated success
   * return "WRONG_ACCESS_SIGNATURE" if update fail due to accessSignature authentication fail.
   * return "WRONG_PLAYER_ID" if update fail due to no such id found.
   * 
   * @param player
   * @return
   */
  public static String savePlayer(Player player) {
    if (player.getAllProperties().containsKey(PlayerProperty.PLAYERID.toString())) {
      //Update profile
      return updatePlayer(player);
    } else {
      //Insert profile, should use a mutex
      return insertPlayer(player);
    }
  }

  private static String updatePlayer(Player player) {
    Map<String, String> playerInfo = player.getAllProperties();
    long playerId;
    try {
      //PlayerId should be convert to long
      playerId = Long.parseLong(playerInfo.get(PlayerProperty.PLAYERID.toString()));
    } catch (NumberFormatException e1) {
      return "WRONG_PLAYER_ID";
    }
    Key playerKey = KeyFactory.createKey(PLAYER, playerId);
    try {
      Entity playerDB = datastore.get(playerKey);
      String as = (String) playerDB.getProperty(PlayerProperty.ACCESSSIGNATURE.toString());
      String asLocal = playerInfo.containsKey(PlayerProperty.ACCESSSIGNATURE.toString()) ?
        playerInfo.get(PlayerProperty.ACCESSSIGNATURE.toString()):"";
      if (asLocal != "" && as.equals(asLocal)) {
        for (String key: playerInfo.keySet()){
          if (playerPropertyBlackList.contains(key)) {
            continue;
          }
          playerDB.setProperty(key, playerInfo.get(key));
        }
        datastore.put(playerDB);
        return "UPDATED_PLAYER";
      } else {
        return "WRONG_ACCESS_SIGNATURE";
      }
    } catch (EntityNotFoundException e) {
      return "WRONG_PLAYER_ID";
    }
  }

  private static String insertPlayer(Player player) {
    Map<String, String> playerInfo = player.getAllProperties();
    Entity playerDB = new Entity(PLAYER);
    for (String key: playerInfo.keySet()){
      playerDB.setProperty(key, playerInfo.get(key));
    }
    long playerId;
    final String EMAIL = PlayerProperty.EMAIL.toString();
    Filter emailFilter = new FilterPredicate(EMAIL,Query.FilterOperator.EQUAL,
        playerInfo.get(EMAIL));
    Query q = new Query(PLAYER).setFilter(emailFilter);
    FetchOptions fo = FetchOptions.Builder.withChunkSize(1);
    synchronized (insertPlayerMutex) {
      PreparedQuery pq = datastore.prepare(q);
      if (pq.countEntities(fo) > 0) {
        return "EMAIL_EXITSTS";
      }
      Key playerKey = datastore.put(playerDB);
      playerId = playerKey.getId();

      String accessSignature = AccessSignatureUtil.getAccessSignature(String.valueOf(playerId));
      playerDB.setProperty(PlayerProperty.ACCESSSIGNATURE.toString(), accessSignature);
      datastore.put(playerDB);
      return "SUCCESS:"+playerId+":"+accessSignature;
    }
  }

  /**
   * get a played by its id. If player is not exist, throw EntityNotFoundException. 
   * @param playerId
   * @return
   * @throws EntityNotFoundException 
   */
  public static Player getPlayerById(long playerId) throws EntityNotFoundException{
    Player player = new Player();
    Key playerKey = KeyFactory.createKey(PLAYER, playerId);
    Entity playerDB = datastore.get(playerKey);
    Map<String,Object> properties = playerDB.getProperties();
    for (String key: properties.keySet()) {
      PlayerProperty p = PlayerProperty.findByValue(key);
      if (p != null && p != PlayerProperty.HASHEDPASSWORD) {
        player.setProperty(p, (String)properties.get(key));
      }
    }
    player.setProperty(PlayerProperty.PLAYERID, String.valueOf(playerId));
    return player;
  }

  /**
   * login a player by id and password
   * If login success, update access signature and return
   * If login fail, throw EntityNotFoundException for WRONG_PLAYER_ID
   * @param playerId
   * @param originalPassword
   * @return {@code email+' '+accessSignature} if login success or "WRONG_PASSWORD" if 
   * password incorrect
   * @throws EntityNotFoundException if WRONG_PLAYER_ID
   */
  public static String[] loginPlayer(long playerId, String originalPassword) throws EntityNotFoundException {
    Key playerKey = KeyFactory.createKey(PLAYER, playerId);
    synchronized (loginMutex) {
      Entity playerDB = datastore.get(playerKey);
      String hashedPassword = AccessSignatureUtil.getHashedPassword(originalPassword);
      if (playerDB.getProperty(PlayerProperty.HASHEDPASSWORD.toString()).equals(hashedPassword)) {

        String accessSignature = AccessSignatureUtil.getAccessSignature(String.valueOf(playerId));
        String email = (String) playerDB.getProperty(PlayerProperty.EMAIL.toString());
        playerDB.setProperty(PlayerProperty.ACCESSSIGNATURE.toString(), accessSignature);
        datastore.put(playerDB);
        return new String[] {email, accessSignature};

      } else {
        return new String[] {"WRONG_PASSWORD"};
      }
    }
  }
  
  /**
   * Delete a player by id and access signature
   * If delete success, return DELETE_PLAYER
   * If delete failed due to wrong access signature return WRONG_ACCESS_SIGNATURE
   * @param playerId
   * @param accessSignature
   * @return
   * @throws EntityNotFoundException if WRONG_PLAYER_ID
   */
  public static String deletePlayer(long playerId, String accessSignature) throws EntityNotFoundException {
    Key playerKey = KeyFactory.createKey(PLAYER, playerId);
    synchronized (loginMutex) {
      Entity playerDB = datastore.get(playerKey);
      if (playerDB.getProperty(PlayerProperty.ACCESSSIGNATURE.toString()).equals(accessSignature)) {
        datastore.delete(playerKey);
        return "DELETED_PLAYER";
      } else {
        return "WRONG_ACCESS_SIGNATURE";
      }
    }
  }
}