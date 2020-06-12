package com.baza.track.io.core;


public interface ResourceIds {
    boolean knownIdName(String name);

    int idFromName(String name);

    String nameForId(int id);
}
