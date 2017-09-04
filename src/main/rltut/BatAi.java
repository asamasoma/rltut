package rltut;

public class BatAi extends CreatureAi {

    public BatAi(Creature creature) {
        super(creature);
    }

    @Override
    public void onUpdate() {
        wander();
        wander();
    }

}
