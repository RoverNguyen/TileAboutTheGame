package states;

import entities.EntityManager;
import entities.creatures.*;
import entities.creatures.npc.*;
import game.Handler;
import gfx.Assets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import settings.Settings;
import sounds.Sound;
import worlds.World;

public class GameState extends State{


    public static World[] world = new World[5];
    public static EntityManager entityManager;
    public static int playerCurrentHealth;
    public static double playerCurrentSpeed;

    public GameState(Handler handler){
        super(handler);
        world[1] = new World(handler, "res/worlds/world1.txt");
        world[2] = new World(handler, "res/worlds/world2.txt");
        world[3] = new World(handler, "res/worlds/world3.txt");
        world[4] = new World(handler, "res/worlds/world4.txt");
        world[0] = world[1];
        handler.setWorld(world[0], true);

        entityManager = world[0].getEntityManager();

        stateSound = Sound.main;
        handler.getSoundManager().addSound(stateSound);
        stateSound.setCycleCount(MediaPlayer.INDEFINITE);

        //create NPCs
        world[1].getEntityManager().addEntity(new Guard(handler, "Tao có súng\nđây nè...",300, 660));
        world[1].getEntityManager().addEntity(new Guard(handler, 170, 660));
        world[1].getEntityManager().addEntity(new Jack(handler, 500, 660));
        world[1].getEntityManager().addEntity(new Jill(handler, 600, 660));
        world[1].getEntityManager().addEntity(new Monk(handler, 300, 800));
        world[1].getEntityManager().addEntity(new Grandma(handler, 900, 1100));
        world[1].getEntityManager().addEntity(new Farmer(handler, 1200, 950));
        world[1].getEntityManager().addEntity(new BlueHat(handler, 1300, 350));
        world[1].getEntityManager().addEntity(new GreenHair(handler, 1200, 350));
        world[1].getEntityManager().addEntity(new FemaleGuard(handler, 1460, 350));
        world[1].getEntityManager().addEntity(new FemaleGuard(handler, 1460, 500));


        //enemies in world 2
        for(int i = 0; i < 3; ++i){
            world[2].getEntityManager().addEntity(new Skeleton(handler, 550 + 55 * i, 1050));
            world[2].getEntityManager().addEntity(new Slime(handler, 150 + 45 * i, 650));
            world[2].getEntityManager().addEntity(new Skeleton(handler, 150 + 55 * i, 700));

            world[2].getEntityManager().addEntity(new Skeleton(handler, 1200 + 55 * i, 150));
            world[2].getEntityManager().addEntity(new Slime(handler, 1350 + 45 * i, 250));
            world[2].getEntityManager().addEntity(new Skeleton(handler, 1100 + 55 * i, 650));

            world[2].getEntityManager().addEntity(new Skeleton(handler, 750 + 55 * i, 300));
            world[2].getEntityManager().addEntity(new Slime(handler, 900 + 45 * i, 400));
        }

        for(int i = 0; i < 3; ++i){
            world[3].getEntityManager().addEntity(new Skeleton(handler, 550 + 55 * i, 650));
            world[3].getEntityManager().addEntity(new Witch(handler, 150 + 45 * i, 650));
            world[3].getEntityManager().addEntity(new Skeleton(handler, 150 + 55 * i, 700));

            world[3].getEntityManager().addEntity(new Skeleton(handler, 1000 + 55 * i, 450));
            world[3].getEntityManager().addEntity(new Witch(handler, 950 + 45 * i, 400));
            world[3].getEntityManager().addEntity(new Slime(handler, 1100 + 55 * i, 550));

        }



        //enemies in world 4
        world[4].getEntityManager().addEntity(new Boss(handler, 600, 600));
    }

    @Override
    public void tick() {
        if(!Settings.IS_MUTE)
            stateSound.play();

        world[0].tick();
        checkPause();
        checkWin();
    }

    public void checkPause(){
        if(handler.getKeyManager().isPause()){

            //Sounds off
            for(MediaPlayer mediaP : handler.getSoundManager().getSoundList()){
                mediaP.pause();
            }

            //Set pause state
            State.setState(new PauseState(handler));
        }
    }

    public void checkWin(){

        if(!handler.isWin()){
            return;
        }

        Settings.SCORES = 0;

        //Sounds off
        handler.getSoundManager().soundOff();

        //Set victory state
        handler.getMouseManager().setUiManager(null);
        State.setState(new VictoryState(handler));
    }
    @Override
    public void render(GraphicsContext g) {

        world[0].render(g);

        //DRAW SCORES
        g.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        g.setFill(Color.LAVENDER);
        g.fillRect(Settings.STAGE_WIDTH - 200, 0, 200, 30);
        g.setFill(Color.BLACK);
        g.fillText("Điểm số: " + Settings.SCORES, Settings.STAGE_WIDTH - 190, 22);

        //DRAW HEALTH BAR
        double percent = (double) handler.getWorld().getEntityManager().getPlayer().getHealth() /
                (double) handler.getWorld().getEntityManager().getPlayer().getMaxHealth();
        g.setFill(Color.BURLYWOOD);
        g.fillRoundRect(200, 553, 400,7, 15, 15);
        g.setFill(Color.RED);
        g.fillRoundRect(200, 553, percent * 400,7, 15, 15);
        g.setFont(Font.font("Verdana", FontWeight.BOLD, 7));
        g.setFill(Color.WHITE);
        g.fillText(handler.getWorld().getEntityManager().getPlayer().getHealth() + " / "
                + handler.getWorld().getEntityManager().getPlayer().getMaxHealth(), 380, 559);

        //DRAW SPELL COOL DOWN
        g.setFill(Color.BLACK);
        g.strokeOval(700, 520,40,40);
        g.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        int coolDown;
        if(Player.spellCoolDown - Player.spellTimer < 0){
            g.setFill(Color.GREEN);
            g.fillOval(700, 520, 40, 40);
        } else {
            coolDown = (int) Math.ceil((double) (Player.spellCoolDown - Player.spellTimer)/1000);
            g.fillText(coolDown + "s", 707,546);
        }
        g.setFont(Font.font("Verdana", FontWeight.BOLD, 7));
        g.setFill(Color.web("#e2fbff"));
        g.fillRoundRect(711, 555, 20,10, 10,10);
        g.setFill(Color.BLACK);
        g.fillText("Q", 718,562);
    }
}
