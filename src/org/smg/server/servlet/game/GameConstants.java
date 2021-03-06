package org.smg.server.servlet.game;

import org.smg.server.util.NamespaceUtil;
/**
 * Constant used for game component including 1. Game Entity name used in datastore
 * 2. Error/Success Msg 3. Other 4. Statistics table 5.Statistics properties
 * @author kanghuan
 *
 */
		
public final class GameConstants {
	  private GameConstants() { }  // Prevent instantiation/subclassing
	
	  // Game entity
	  public static final String PICS = "pics";
	  public static final String SCREENSHOT = "screenshots";
	  public static final String ICON = "icon";
	  public static final String GAME_NAME = "gameName";
	  public static final String DESCRIPTION = "description";
	  public static final String URL = "url";
	  public static final String GAME_ID = "gameId" ;
	  public static final String HAS_TOKENS = "hasTokens";
	  public static final String POST_DATE = "postDate";
	  public static final String PLAYER_ID = "playerId";
	  public static final String GAME_META_INFO = NamespaceUtil.VERSION+"gameMetaInfo";
	  public static final String USER_ID = "userId";
	  // Error messages
	  static final String JSON_RECEIVED = "json_received";
	  static final String DETAILS  = "details";
	  static final String ERROR = "error";
	  static final String GAME_EXISTS = "GAME_EXISTS";
	  static final String WRONG_GAME_ID = "WRONG_GAME_ID";
	  static final String URL_ERROR = "INVALID_URL_PATH_ERROR";
	  static final String WRONG_PLAYER_ID = "WRONG_PLAYER_ID";
	  static final String WRONG_RATING = "WRONG_RATING";
	  static final String INVALID_JSON = "INVALID_JSON";
	  static final String NO_MATCH_RECORD = "NO_MATCH_RECORDS";
	  
	  // Success messages
	  static final String SUCCESS = "success";
	  static final String DELETED_GAME = "DELETED_GAME";
	  static final String UPDATED_GAME = "UPDATED_GAME";
	  
	  // Other
	  static final String ALL = "all";
	  static final String STATS = "stats";
	  static final String CURRENT_GAMES = "currentGames";
	  
	  // Statistics table
	  public static final String GAME_STATISTICS = NamespaceUtil.VERSION + "GAME_STATISTICS";
	  // Statistics properties
	  public static final String AUTHORIZED = "authorized";
	  public static final String UPDATED = "updated";
	  public static final String HIGH_SCORE = "highScore";
	  public static final String SCORE = "score";
	  public static final String RATING = "rating";
	  public static final String RATING_COUNT = "ratingCount";
	  public static final String AVERAGE_RATING = "averageRating";
	  public static final String ZERO_STRING = "0";
	  public static final String FINISHED_GAMES = "finishedGames";
	  public static final String PLAYERS = "players";
	  public static final String FIRST_NAME = "firstname";
	  public static final String LAST_NAME = "lastname";
	  public static final String NICKNAME = "nickname";
	  public static final String TOKENS = "tokens";
	  public static final String TOKEN = "token";
	  public static final String EMAIL = "email";
	  public static final String RESULT = "result";
	  public static final String DATE = "date";
	  public static final String TOKENCHANGE = "tokenChange";
	  public static final String OPPONENTIDS = "opponentIds";
	  public static final String WIN = "win";
	  public static final String LOST = "lost";
	  public static final String DRAW = "draw";
	  public static final String DEVELOPER_LOWER = "developer";
	  public static final String RANK = "RANK";
	}
