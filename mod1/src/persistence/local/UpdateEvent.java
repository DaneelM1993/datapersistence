/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistence.local;

import sun.awt.geom.AreaOp.AddOp;

/**
 *
 * @author Wizard1993
 */
public class UpdateEvent extends java.util.EventObject{
    public int Id;
    public enum State{
        add,
        remove,
        update
    }
    State state;
    public UpdateEvent(Object source,Integer ID,State state) {
        super(source);
        this.Id=ID;
        this.state=state;
    }

    public int getId() {
        return Id;
    }

    public State getState() {
        return state;
    }




}
