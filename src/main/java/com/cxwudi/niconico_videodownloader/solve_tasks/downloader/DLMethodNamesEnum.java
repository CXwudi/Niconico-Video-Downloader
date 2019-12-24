package com.cxwudi.niconico_videodownloader.solve_tasks.downloader;

import java.util.HashMap;

/**
 * to indicate any available way to download a Vocaloid PV.
 * in another word, each concrete class of {@link AbstractVideoDownloader} has a correspond value of this enum
 *
 * @author CX无敌
 */
public enum DLMethodNamesEnum {
    YOUTUBE_DL("youtube-dl"), IDM("idm");

    private String name;
    DLMethodNamesEnum(String name){
        this.name = name;
    }

    private static final HashMap<String, DLMethodNamesEnum> map;
    static {
        map = new HashMap<>();
        for (var method: DLMethodNamesEnum.values()) {
            map.put(method.getName(), method);
        }
    }

    public static DLMethodNamesEnum getMethodByName(String name){
        return map.get(name);
    }

    public String getName() {
        return name;
    }
}
