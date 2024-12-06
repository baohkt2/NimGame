// encoding=UTF-8
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author ASUS
 */
public class PlayerModel {

    private String Name;
    private Boolean isMachine;
    private Boolean canTake;

    public PlayerModel(){
    }

    public PlayerModel(Boolean isMachine) {
        this.isMachine = isMachine;
    }

    public PlayerModel(String Name, Boolean isMachine) {
        this.Name = Name;
        this.isMachine = isMachine;
    }

    public PlayerModel(String Name, Boolean isMachine, Boolean canTake) {
        this.Name = Name;
        this.isMachine = isMachine;
        this.canTake = canTake;
    }

    public String getName() {
        return Name;
    }

    public Boolean getIsMachine() {
        return isMachine;
    }

    public void setName(String Name) {
        this.Name = this.Name + " " + Name;
    }

    public void setIsMachine(Boolean isMachine) {
        this.isMachine = isMachine;
    }

    public Boolean getCanTake() {
        return canTake;
    }

    public void setCanTake(Boolean canTake) {
        this.canTake = canTake;
    }

}
