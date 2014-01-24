package com.dryerzinia.pokemon.obj;

public interface Actor {

    /*
     * Returns true if the actor did anything
     */
    public boolean act();

    /*
     * Drives character animation
     */
    public void update(int deltaTime);

}
