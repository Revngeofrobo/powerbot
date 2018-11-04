package SecurityStronghold;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;


import javax.swing.*;
import java.awt.*;


@Script.Manifest(name = "Security Stronghold Fighter", description = "Kills Flesh Crawlers, Giant Spiders, Minotaurs, or Zombies at the Security Stronghold", properties="Author = Revngeofstr")

public class SecurityStronghold extends PollingScript<ClientContext> implements PaintListener {

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



    //PathStuff
    private final static int Entrance = 20790;
    private final static int Portal = 20786; //Right after Entrance (not needed for mintars
    private final static int Portal2 = 19005; //For Spiders
    private final static int Ladder1 = 20785; //After portal 1
    private final static int Ladder2 = 19004; //For Spiders
    private boolean AtStronghold = false;

    /* Old Stuck Tile Stuff
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
    */

    public static final Tile[] PathToBank = {new Tile(3214, 3425, 0), new Tile(3211, 3425, 0),
            new Tile(3208, 3426, 0), new Tile(3205, 3428, 0),
            new Tile(3202, 3428, 0), new Tile(3199, 3429, 0),
            new Tile(3196, 3429, 0), new Tile(3193, 3429, 0),
            new Tile(3190, 3430, 0), new Tile(3187, 3430, 0),
            new Tile(3184, 3431, 0), new Tile(3183, 3434, 0),
            new Tile(3183, 3437, 0)};



    public static final Tile[] PathToEntrance = {new Tile(3185, 3438, 0), new Tile(3183, 3435, 0),
            new Tile(3182, 3432, 0), new Tile(3179, 3432, 0), new Tile(3176, 3431, 0), new Tile(3173, 3429, 0),
            new Tile(3170, 3429, 0), new Tile(3167, 3428, 0), new Tile(3164, 3426, 0), new Tile(3161, 3424, 0),
            new Tile(3158, 3423, 0), new Tile(3155, 3420, 0), new Tile(3152, 3417, 0), new Tile(3149, 3417, 0),
            new Tile(3146, 3417, 0), new Tile(3143, 3417, 0), new Tile(3140, 3417, 0), new Tile(3137, 3417, 0),
            new Tile(3134, 3417, 0), new Tile(3131, 3417, 0), new Tile(3128, 3416, 0), new Tile(3125, 3416, 0),
            new Tile(3122, 3418, 0), new Tile(3119, 3418, 0), new Tile(3116, 3419, 0), new Tile(3113, 3419, 0),
            new Tile(3110, 3419, 0), new Tile(3107, 3420, 0), new Tile(3104, 3420, 0), new Tile(3101, 3420, 0),
            new Tile(3098, 3420, 0), new Tile(3095, 3420, 0), new Tile(3092, 3420, 0), new Tile(3089, 3420, 0),
            new Tile(3086, 3420, 0), new Tile(3083, 3422, 0), new Tile(3080, 3423, 0)};

    public static final Tile[] PathToLadder1 = {new Tile(1914, 5222, 0), new Tile(1911, 5222, 0),
            new Tile(1908, 5221, 0), new Tile(1905, 5221, 0)};

    public static final Tile[] PathToDoor1Flesh = {new Tile(2044, 5236, 0), new Tile(2041, 5235, 0),
            new Tile(2044, 5234, 0), new Tile(2046, 5231, 0), new Tile(2043, 5230, 0),
            new Tile(2043, 5227, 0), new Tile(2045, 5224, 0), new Tile(2045, 5221, 0),
            new Tile(2044, 5218, 0), new Tile(2043, 5215, 0), new Tile(2040, 5213, 0),
            new Tile(2038, 5210, 0), new Tile(2038, 5207, 0), new Tile(2037, 5204, 0)};

    public static final Tile[] PathToDoor2Flesh = {new Tile(2037, 5200, 0), new Tile(2040, 5199, 0),
            new Tile(2042, 5202, 0), new Tile(2045, 5202, 0), new Tile(2045, 5199, 0)};

    public static final Tile[] PathToZombies = {new Tile(2036, 5244, 0), new Tile(2033, 5243, 0),
            new Tile(2033, 5240, 0), new Tile(2031, 5237, 0), new Tile(2031, 5234, 0)};

    public static final Tile[] PathToSpidersDoor1 = {new Tile(2123, 5252, 0), new Tile(2126, 5252, 0),
            new Tile(2129, 5253, 0), new Tile(2132, 5255, 0)};

    public static final Tile[] PathToSpiders = {new Tile(2133, 5260, 0), new Tile(2133, 5263, 0),
            new Tile(2131, 5266, 0), new Tile(2129, 5269, 0)};

    public static final Tile LadderTile = new Tile(1859, 5243, 0);



    private void Heal() {
        Item FoodID = ctx.inventory.select().name(Food).poll();
        FoodID.interact("Eat");
        Condition.sleep(1000);
    }

    private boolean InFightZone(){
        return (ctx.players.local().contains()); //Add Boounds here based on monster
    }

    private boolean InBank(){
        return (ctx.players.local().contains()); //Add Boounds here for the bank
    }

    private boolean ShouldAttack() {
        return (!ctx.players.local().interacting().valid() && InFightZone() && HasFood());
    } //Add if in region Fight to ShouldAttack for each monster

    private boolean HasFood() {
        return (ctx.inventory.select().name(Food).count() > 0);
    }

    private boolean NeedFood() {
        return (ctx.combat.health() < HealthToEat);
    }

    private boolean CanCast() {
        return (ctx.skills.level(Constants.SKILLS_MAGIC) > 24 && ctx.inventory.select().id(LawRune).count() > 0 && ctx.inventory.select().id(FireRune).count() > 0 && ctx.inventory.select().id(AirRune).count() > 2);
    }
    private boolean HasTab() {
        return (ctx.inventory.select().id(Tab).count() > 0);
    }

    private void Cast() {
        ctx.magic.cast(Magic.Spell.VARROCK_TELEPORT);
        Condition.sleep(3000);
    }
    private void UseTab() {
        ctx.inventory.select().id(Tab).action("Use");
        Condition.sleep(3000);
    }

    private boolean ShouldLeave(){
        return  (!HasFood() && InFightZone());
    }

    private boolean NeedBank() {
        return (!HasFood() && !InFightZone());
    }


    private boolean BackToFightZone() {
        return (HasFood() && !InFightZone());
    }




    private void Leave() {
        if (CanCast()) {
            Cast();
        }else if (HasTab()){
            UseTab();
        }else{
            OpenDoor();
            Logout();
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

    public void OpenDoor() {
        final GameObject DoorToOpen = ctx.objects.select().id(Door).nearest().poll();

        if (DoorToOpen.inViewport()) {
            DoorToOpen.interact("Open");
            Condition.sleep(800);
        }else{
            ctx.camera.turnTo(DoorToOpen);
            Condition.sleep(800);
            DoorToOpen.interact("Open");
            Condition.sleep(800);
        }
    }


    /*Stuck Tile problem fixed by walking
    public boolean Stuck(){
        return (ctx.players.local().tile().equals(StuckTile1) || ctx.players.local().tile().equals(StuckTile2) || ctx.players.local().tile().equals(StuckTile3)
                || ctx.players.local().tile().equals(StuckTile4) || ctx.players.local().tile().equals(StuckTile5) || ctx.players.local().tile().equals(StuckTile6)
                || ctx.players.local().tile().equals(StuckTile7) || ctx.players.local().tile().equals(StuckTile8) || ctx.players.local().tile().equals(StuckTile9)
                || ctx.players.local().tile().equals(StuckTile10) || ctx.players.local().tile().equals(StuckTile11) || ctx.players.local().tile().equals(StuckTile12)
                || ctx.players.local().tile().equals(StuckTile13) || ctx.players.local().tile().equals(StuckTile14)) || ctx.players.local().tile().equals(StuckTile15)
                || ctx.players.local().tile().equals(StuckTile16);
    }
    */

    public void Logout() {
        ctx.game.logout();
        Condition.sleep(1000);
        if (!ctx.game.loggedIn()) {
            ctx.controller.stop();
        }
    }


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


    private State state() {

        if (NeedFood())
            return State.Eat;

        if(ShouldAttack())
            return State.Attack;

        if (ShouldLeave())
            return State.Leave;

        if (NeedBank() && !InBank())
            return State.WalkToBank;

        if (NeedBank() && InBank())
            return State.Bank;

        if(BackToFightZone())
            return State.WalkToMonster;
    }




    @Override
    public void poll() {

        final State state = state();

        // now we want a switch statement checking our current state
        switch (state) {

            // now we make a case for each state we have
            case Eat:
                Heal();
                break;
            case Attack:
                Attack();
                break;
            case Leave:
                Leave();
                break;
            case Bank:

                break;
            case WalkToBank:

                break;
            case WalkToMonster:

                break;

                default:
                break;
        }
    }




    private enum State {
        WalkToBank, Bank, WalkToMonster, Eat, Attack, Leave
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

