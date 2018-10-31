package SecurityStrongholdFighter;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;


import javax.swing.*;
import java.awt.*;


@Script.Manifest(name = "Security Stronghold Fighter", description = "Kills Flesh Crawlers, Giant Spiders, Minotaurs, or Zombies at the Security Stronghold", properties="Author = Revngeofstr")

public class SecurityStrongholdFighter extends PollingScript<ClientContext> implements PaintListener {

    private String Food = "Tuna";
    private String MonsterToFight = "Zombie";
    private int StartExp = 0;
    private int CurrentStat =0;
    private int HealthToEat = 20;
    private final static int Tab = 8007;
    private final static int Door[] = {17100, 23654, 19207};
    private final static int LawRune = 563;
    private final static int FireRune = 554;
    private final static int AirRune = 556;


    private static final Tile StuckTile1 = new Tile(2027, 5239, 0);
    private static final Tile StuckTile2 = new Tile(2026, 5239, 0);
    private static final Tile StuckTile3 = new Tile(1976, 5184, 0);
    private static final Tile StuckTile4 = new Tile(1976, 5184,0);
    private static final Tile StuckTile5 = new Tile(2045, 5195, 0);
    private static final Tile StuckTile6 = new Tile(2046, 5195, 0);
    private static final Tile StuckTile7 = new Tile(2036, 5185, 0);
    private static final Tile StuckTile8 = new Tile(2036, 5186,0);
    private static final Tile StuckTile9 = new Tile(2132, 5259, 0);
    private static final Tile StuckTile10 = new Tile (2133, 5259, 0);
    private static final Tile StuckTile11 = new Tile(2138, 5263, 0);
    private static final Tile StuckTile12 = new Tile (2138, 5262, 0);
    private static final Tile StuckTile13 = new Tile(2133, 5279, 0);
    private static final Tile StuckTile14 = new Tile (2132, 5279, 0);
    private static final Tile StuckTile15 = new Tile(2032, 5227, 0);
    private static final Tile StuckTile16 = new Tile (2031, 5227, 0);


    public void Heal() {
        Item FoodID = ctx.inventory.select().name(Food).poll();
        FoodID.interact("Eat");
        Condition.sleep(1000);
    }

    public boolean ShouldAttack() {
        return (!ctx.players.local().interacting().valid() &&  !Stuck());
    }

    public boolean HasFood() {

        return (ctx.inventory.select().name(Food).count() > 0);
    }

    public boolean NeedFood() {

        return (ctx.combat.health() < HealthToEat);
    }

    public boolean CanCast() {
        return (ctx.skills.level(Constants.SKILLS_MAGIC) > 24 && ctx.inventory.select().id(LawRune).count() > 0 && ctx.inventory.select().id(FireRune).count() > 0 && ctx.inventory.select().id(AirRune).count() > 2);
    }
    public boolean HasTab() {

        return (ctx.inventory.select().id(Tab).count() > 0);
    }

    public void Cast() {
        ctx.magic.cast(Magic.Spell.VARROCK_TELEPORT);
        Condition.sleep(10000);
        Logout();
    }
    public void UseTab() {
        ctx.inventory.select().id(Tab).action("Use");
        Condition.sleep(10000);
        Logout();
    }

    public void Leave() {
        if (CanCast()) {
            Cast();
        }else if (HasTab()){
            UseTab();
        }else{
            OpenDoorToLeave();
        }

    }

    public void Attack() {
        final Npc MonsterToAttack = ctx.npcs.select().name(MonsterToFight).select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                final Actor i = npc.interacting();
                return  i.equals(ctx.players.local()) || (!i.valid() && !npc.inCombat());
            }
        }).nearest().poll();
        if (MonsterToAttack.inViewport()){
            MonsterToAttack.interact("Attack");
            if (ctx.players.local().inMotion()){
                Condition.sleep(400);
            }
            Condition.sleep(700);
        } else {
            ctx.camera.turnTo(MonsterToAttack);
            MonsterToAttack.interact("Attack");
            if (ctx.players.local().inMotion()){
                Condition.sleep(400);
            }
            Condition.sleep(700);
        }
    }

    public void OpenDoorToLeave() {
        final GameObject DoorToLeave = ctx.objects.select().id(Door).nearest().poll();

        if (DoorToLeave.inViewport()) {
            DoorToLeave.interact("Open");
            Condition.sleep(3000);
            Logout();
        }else{
            ctx.camera.turnTo(DoorToLeave);
            Condition.sleep(800);
            DoorToLeave.interact("Open");
            Condition.sleep(3000);
            Logout();
        }
    }

    //Temp fix to the mis-click the door problem
    public boolean Stuck(){
        return (ctx.players.local().tile().equals(StuckTile1) || ctx.players.local().tile().equals(StuckTile2) || ctx.players.local().tile().equals(StuckTile3)
                || ctx.players.local().tile().equals(StuckTile4) || ctx.players.local().tile().equals(StuckTile5) || ctx.players.local().tile().equals(StuckTile6)
                || ctx.players.local().tile().equals(StuckTile7) || ctx.players.local().tile().equals(StuckTile8) || ctx.players.local().tile().equals(StuckTile9)
                || ctx.players.local().tile().equals(StuckTile10) || ctx.players.local().tile().equals(StuckTile11) || ctx.players.local().tile().equals(StuckTile12)
                || ctx.players.local().tile().equals(StuckTile13) || ctx.players.local().tile().equals(StuckTile14)) || ctx.players.local().tile().equals(StuckTile15)
                || ctx.players.local().tile().equals(StuckTile16);
    }

    public void Logout() {
        ctx.game.logout();
        Condition.sleep(1000);
        if (!ctx.game.loggedIn()) {
            ctx.controller.stop();
        }
    }

    //Impliment method for if stuck to re-open door
    //Walking from bank to fight zone




    @Override
    public void start() {
        //User Options
        String UserOptionsAttack[] = {"Zombie", "Minotaur", "Flesh Crawler", "Giant spider"};
        String UserChoiceAttack = "" + (String) JOptionPane.showInputDialog(null, "Monster to Fight?", "Stronghold", JOptionPane.PLAIN_MESSAGE, null, UserOptionsAttack, UserOptionsAttack[1]);
        String UserOptionsFood[] = {"Lobster", "Tuna", "Swordfish", "Monkfish"};
        String UserChoiceFood = "" + (String) JOptionPane.showInputDialog(null, "Food to Eat?", "Stronghold", JOptionPane.PLAIN_MESSAGE, null, UserOptionsFood, UserOptionsFood[1]);
        String CombatOptions[] = {"Attack", "Strength", "Defense", "Ranged", "Magic" };
        String UserChoiceCombat = "" + (String) JOptionPane.showInputDialog(null, "What stat are you training?", "Stronghold", JOptionPane.PLAIN_MESSAGE, null, CombatOptions, CombatOptions[1]);

        //MonsterToAttack
        MonsterToFight = UserChoiceAttack;

        //FoodToUse
        Food = UserChoiceFood;

        HealthToEat = Random.nextInt(19, 29);


        //Combat Training check
        if (UserChoiceCombat.equals("Strength")){
            StartExp = ctx.skills.experience(Constants.SKILLS_STRENGTH);
            CurrentStat = Constants.SKILLS_STRENGTH;
        }else if (UserChoiceCombat.equals("Attack")){
            StartExp = ctx.skills.experience(Constants.SKILLS_ATTACK);
            CurrentStat = Constants.SKILLS_ATTACK;
        }else if (UserChoiceCombat.equals("Defense")){
            StartExp = ctx.skills.experience(Constants.SKILLS_DEFENSE);
            CurrentStat = Constants.SKILLS_DEFENSE;
        }else if (UserChoiceCombat.equals("Ranged")) {
            StartExp = ctx.skills.experience(Constants.SKILLS_RANGE);
            CurrentStat = Constants.SKILLS_RANGE;
        }else if (UserChoiceCombat.equals("Magic")) {
            StartExp = ctx.skills.experience(Constants.SKILLS_MAGIC);
            CurrentStat = Constants.SKILLS_MAGIC;
        }

    }



    @Override
    public void poll() {
        if (HasFood()){
            if (NeedFood()) {
                Heal();
            } else if (ShouldAttack()) {
                Attack();
            }else if (Stuck()){
                Logout();
            }
        }else{
            Leave();
        }
    }

    @Override
    public void repaint(Graphics graphics) {
        long milli = this.getTotalRuntime();
        long second = (milli/1000) % 60;
        long minutes = (milli/(1000*60)% 60);
        long hours = (milli/(1000*60*60)% 24);


        int ExpGained = ctx.skills.experience(CurrentStat) - StartExp;

        Graphics2D g = (Graphics2D)graphics;
        g.setColor(new Color(0,0,0,180));
        g.fillRect(0,0,175,100);
        g.setColor(new Color(255,255,255));
        g.drawRect(0,0,175,100);

        g.drawString("Security Stronghold Fighter", 20, 20);
        g.drawString("Run Time: " + String.format("%02d:%02d:%02d", hours, minutes, second), 20, 40);
        g.drawString("Exp Gained: " + ExpGained,20,60);
        g.drawString("Exp/Hour: " + (int)(ExpGained*(3600000D/milli)),20,80);

    }
}

