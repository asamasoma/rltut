package rltut.screens;

import rltut.Creature;
import rltut.Item;

public class ExamineScreen extends InventoryBasedScreen {

    public ExamineScreen(Creature player) {
        super(player);
    }

    @Override
    protected String getVerb() {
        return "examine";
    }

    @Override
    protected boolean isAcceptable(Item item) {
        return true;
    }

    @Override
    protected Screen use(Item item) {
        String article = "aeiou".contains(item.name().subSequence(0, 1)) ? "an " : "a "; // extract to grammar helper
        player.notify("It's " + article + item.name() + "." + item.details());
        return null;
    }
}
