package server;

import model.Game;
import model.GameDetails;
import model.Player;
import examen.repository.GameRepository;
import examen.repository.PlayerRepository;
import services.GameException;
import services.IGameServices;
import services.IObserver;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerImpl implements IGameServices {

    private PlayerRepository playerRepository;
    private GameRepository gameRepository;
    private Map<String, IObserver> loggedPlayers;
    private Map<String, IObserver> waitingPlayers;
    private Map<String, IObserver> playingPlayers;
    private Game activeGame;
    private Map<Game, String> currentTurns;
    private List<String> words;
    private Set<Character> allGuessed;


    public ServerImpl(PlayerRepository pr, GameRepository gr) {
        this.playerRepository = pr;
        this.gameRepository = gr;
        this.loggedPlayers = new ConcurrentHashMap<>();
        this.waitingPlayers = new ConcurrentHashMap<>();
        this.playingPlayers = new ConcurrentHashMap<>();
        this.currentTurns = new ConcurrentHashMap<>();
        this.words = new ArrayList<>(Arrays.asList("coding", "coffee", "pizza", "laptop"));
        this.allGuessed = new HashSet<>();
    }


    /**
     * method called when a player logs in
     * @param player who pressed START
     * @param client observer corresponding to player
     */
    public synchronized void login(Player player, IObserver client) throws GameException {
        boolean found = playerRepository.findByUsernameAndPassword(player.getUsername(), player.getPassword());
        if (found) {
            if(loggedPlayers.get(player.getUsername()) != null)
                throw new GameException("Player already logged in.");
            loggedPlayers.put(player.getUsername(), client);
            //unlimited number of players; one press on "START" starts game
            waitingPlayers.put(player.getUsername(), client);
        }
        else
            throw new GameException("Authentication failed.");
    }


    /**
     * method called when a player presses START
     * @param player who pressed START
     * @param client observer corresponding to player
     */
    public synchronized void gameStartPressed(Player player, IObserver client) {
        //unlimited number of players; one press on "START" starts game
        Set<String> playing = playingPlayers.keySet();
        Set<String> waiting = waitingPlayers.keySet();
        if (playing.isEmpty() && waiting.size() > 1) {
            int lastId = 0;
            List<Game> previousGames = gameRepository.getAllGames();
            if (previousGames.size() > 0) {
                lastId = previousGames.get(previousGames.size() - 1).getId();
            }
            Random r = new Random();
            int pos = r.nextInt(words.size());
            Game newGame = new Game(lastId + 1, words.get(pos));
            activeGame = newGame;
            for (String u : waiting) {
                GameDetails playerDetails = new GameDetails(u, 0);
                playerDetails.setTriedLetters("");
                playerDetails.setGuessedLetters("");
                newGame.getDetails().add(playerDetails);
                startGame(u);
            }
            gameRepository.saveGame(newGame);
            loadPlayersForClients(newGame);
            currentTurns.put(newGame, player.getUsername());
            refreshClients(newGame, false, "");
        }
        else {
            putInWaiting(player.getUsername());
        }
    }


    /**
     * method that starts the game for a specific player
     * @param username of player to start game
     */
    private void startGame(String username) {
        ExecutorService executor= Executors.newFixedThreadPool(5);
        IObserver client = loggedPlayers.get(username);
        if (client != null)
            executor.execute(() -> {
                System.out.println("Started game for " + username);
                try {
                    client.gameOn(activeGame.getWord());
                    playingPlayers.put(username, client);
                    waitingPlayers.remove(username);
                }
                catch (GameException | RemoteException e) {
                    System.err.println("Error starting game " + e);
                }
            });
        executor.shutdown();
    }


    /**
     * notifies all players of a game to load their opponents
     * @param game specified game
     */
    private void loadPlayersForClients(Game game) {
        ExecutorService executor= Executors.newFixedThreadPool(5);
        for (GameDetails gd : game.getDetails()) {
            String playerId = gd.getPlayer();
            IObserver client = loggedPlayers.get(playerId);
            if (client != null)
                executor.execute(() -> {
                    System.out.println("Loading opponents for " + playerId);
                    try {
                        client.loadAllPlayers();
                    } catch (GameException | RemoteException e) {
                        System.err.println("Error loading players " + e);
                    }
                });
        }
        executor.shutdown();
    }


    /**
     * puts the player in waiting state if there is an ongoing game or not enough users are logged in
     * @param username of player
     */
    private void putInWaiting(String username) {
        ExecutorService executor= Executors.newFixedThreadPool(5);
        IObserver client = loggedPlayers.get(username);
        if (client != null)
            executor.execute(() -> {
                System.out.println(username + "in waiting state");
                waitingPlayers.put(username, client);
                try {
                    client.waitForGame();
                }
                catch (GameException | RemoteException e) {
                    System.err.println("Error notifying client " + e);
                }
            });
        executor.shutdown();
    }


    /**
     * gets all players taking part in the current game
     * @return usernames of players
     */
    @Override
    public synchronized List<String> getPlayersOfCurrentGame() {
        List<String> players = new ArrayList<>();
        for (GameDetails gd : activeGame.getDetails()) {
            players.add(gd.getPlayer());
        }
        return players;
    }


    /**
     * gets all players of a specified game
     * @param game game
     * @return usernames of players
     */
    private synchronized List<String> getPlayersOfGame(Game game) {
        List<String> players = new ArrayList<>();
        for (GameDetails gd : game.getDetails()) {
            players.add(gd.getPlayer());
        }
        return players;
    }


    /**
     * get GameDetails of specified player for the current game
     */
    private synchronized GameDetails getCurrentGameDetailsForPlayer(Player player) {
        for (GameDetails gd : activeGame.getDetails()) {
            if (gd.getPlayer().equals(player.getUsername())) {
                return gd;
            }
        }
        return null;
    }


    /**
     * notifies players that they should update view
     * @param game current game
     * @param guessed specifies if suggested letter was guessed or not
     * @param input suggested letter
     */
    private void refreshClients(Game game, boolean guessed, String input) {
        List<String> allPlayers = getPlayersOfGame(game);

        ExecutorService executor= Executors.newFixedThreadPool(5);
        for (GameDetails gd : game.getDetails()) {
            String playerId = gd.getPlayer();
            IObserver client = loggedPlayers.get(playerId);
            if (client != null)
                executor.execute(() -> {
                    System.out.println("Refreshing view for " + playerId);
                    try {
                        if (guessed)
                            client.refresh(currentTurns.get(game), true, input);
                        else
                            client.refresh(currentTurns.get(game), false, input);
                    } catch (GameException | RemoteException e) {
                        System.err.println("Error refreshing view" + e);
                    }
                });
        }
        int index = allPlayers.indexOf(currentTurns.get(game)) + 1;
        if (index >= allPlayers.size()) {
            index = 0;
        }
        String nextPlayer = allPlayers.get(index);
        currentTurns.put(game, nextPlayer);
        executor.shutdown();
    }


    /**
     * game logic for a turn
     * @param player who does the turn
     * @param input suggested letter
     */
    @Override
    public void verifyTurn(Player player, String input) {
        GameDetails details = getCurrentGameDetailsForPlayer(player);
        details.setTriedLetters(details.getTriedLetters() + input);
        //check if guessed
            if (activeGame.getWord().contains(input)) {
                allGuessed.add(input.charAt(0));
                details.setGuessedLetters(details.getGuessedLetters() + input);
                refreshClients(activeGame, true, input);

                //check if player won or not
                int totalNr = getCharSetFromString(activeGame.getWord()).size();
                int guessedNr = allGuessed.size();
                if (totalNr == guessedNr) {
                    gameWon(player);
                }
            }
            else {
                details.setAttempts(details.getAttempts() + 1);
                refreshClients(activeGame, false, input);
            }
            gameRepository.updateGame(activeGame);
    }


    /**
     * updates game status
     * @param player: player that won
     */
    @Override
    public synchronized void gameWon(Player player) {
        activeGame.setWinner(player.getUsername());
        GameDetails details = getCurrentGameDetailsForPlayer(player);
        details.setResult(1);
        gameRepository.updateGame(activeGame);
        notifyGameOver(activeGame);
    }


    /**
     * notifies players that game is over
     */
    private void notifyGameOver(Game game) {
        ExecutorService executor= Executors.newFixedThreadPool(5);
        for (GameDetails gd : game.getDetails()) {
            String playerId = gd.getPlayer();
            IObserver client = loggedPlayers.get(playerId);
            if (client != null)
                executor.execute(() -> {
                    System.out.println("Notifying game over " + playerId);
                    try {
                        client.gameOver(game.getWinner());
                    } catch (GameException | RemoteException e) {
                        System.err.println("Error Notifying game over " + e);
                    }
                });
        }
        executor.shutdown();
    }


    /**
     * reassigns player to corresponding group after a game has ended
     */
    @Override
    public synchronized void gameEnded(Player player) {
        playingPlayers.remove(player.getUsername());
        waitingPlayers.put(player.getUsername(), loggedPlayers.get(player.getUsername()));
    }


    /**
     * method called when a player logs out
     */
    @Override
    public void logout(Player player) {
        loggedPlayers.remove(player.getUsername());
        waitingPlayers.remove(player.getUsername());
        playingPlayers.remove(player.getUsername());
    }


    /**
     * helper function to extract set of Characters from String
     */
    private Set<Character> getCharSetFromString(String string) {
        List<Character> characterList = new ArrayList<Character>();
        char[] arrayChar = string.toCharArray();
        for (char aChar : arrayChar)
        {
            characterList.add(aChar);
        }
        return new HashSet<Character>(characterList);
    }

}
