package org.logica.cns_workshop.communication;

public interface SmileyVocabulary {

    // conecpts
    public static final String LOCATED = "located";
    public static final String DOOR = "door";
    public static final String SMILEY = "smiley";
    public static final String ROOM = "room";

    // predicates
    public static final String NOTIFY_MOVE = "movedTo";
    public static final String NOTIFY_LOCATION = "locatedAt";
    public static final String NOTIFY_REACHEDDOOR = "reachedDoor";
    public static final String YOURNEIGHBOUR = "yourneighbour";
    public static final String ASK_WHEREISDOOR = "whereIsDoor";

    // properties/terms
    public static final String SENDER = "sender";
    public static final String SUBJECT = "subject";
    public static final String AID = "AID";
    public static final String X_POS = "x";
    public static final String Y_POS = "y";
    public static final String X_MOVE = "xmove";
    public static final String Y_MOVE = "ymove";
    public static final String COLOR = "color";
    public static final String FOUNDDOOR = "founddoor";
    public static final String FOUNDSELF = "foundMySelf";
}
