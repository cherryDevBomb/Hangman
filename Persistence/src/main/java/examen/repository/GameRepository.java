package examen.repository;

import model.Game;
import model.GameDetails;
import model.GamePlayerWrapper;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class GameRepository implements IGameRepository {

    private static SessionFactory sessionFactory;

    public GameRepository() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            System.out.println("Exception " + e);
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }


    @Override
    public void saveGame(Game game) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(game);
            session.getTransaction().commit();
        }
    }


    @Override
    public void updateGame(Game game) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(game);
            session.getTransaction().commit();
        }
    }


    @Override
    public Game findGame(int id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Game game = session.get(Game.class, id);
            session.getTransaction().commit();
            return game;
        }
        catch (HibernateException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    @SuppressWarnings("unchecked")
    public List<Game> getAllGames() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            List<Game> games = session.createCriteria(Game.class).list();
            session.getTransaction().commit();
            return games;
        }
        catch (HibernateException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public GameDetails getDetailsOfPlayer(Game game, String playerId) {
        List<GameDetails> gameDetails = game.getDetails();
        for (GameDetails gd : gameDetails) {
            if (gd.getPlayer().equals(playerId)) {
                return gd;
            }
        }
        return null;
    }


    @Override
    public GamePlayerWrapper getWrappedDetailsOfPlayer(Game game, String playerId) {
        GameDetails detail = getDetailsOfPlayer(game, playerId);
        if (detail != null) {
            //convert won
            boolean won = false;
            if (detail.getResult() == 1)
                won = true;
            //convert tried letters to list
            List<Character> tried = new ArrayList<>();
            if (detail.getTriedLetters() != null) {
                char[] triedChar = detail.getTriedLetters().toCharArray();
                for (char aChar : triedChar) {
                    tried.add(aChar);
                }
            }
            //convert guessed letters to list
            List<Character> guessed = new ArrayList<>();
            if (detail.getGuessedLetters() != null) {
                char[] guessedChar = detail.getTriedLetters().toCharArray();
                for (char aChar : guessedChar) {
                    guessed.add(aChar);
                }
            }
            return new GamePlayerWrapper(playerId, game.getId(), tried, guessed, won);
        }
        return null;
    }
}
